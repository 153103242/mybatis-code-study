package com.yc.study;

import com.yc.study.mapper.StudentMapper;
import org.apache.ibatis.executor.*;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @date 2020/8/21 14:50:41
 * @description
 */
public class ExecutorTest {
    // mybatis的配置类
    private static Configuration configuration;
    // mabatis的事务
    private static Transaction transaction;
    private static SqlSessionFactory sessionFactory;
    private static MappedStatement ms;

    /**
     * 初始化mybatis配置
     */
    @Before
    public void init() {
        // 构建会话工厂创建器
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        // 获取配置文件输入流
        // 构建会话工厂
        sessionFactory = sqlSessionFactoryBuilder.build(ExecutorTest.class.getResourceAsStream("/mybatis-config.xml"));
        // 获得configuration
        configuration = sessionFactory.getConfiguration();
        // 获得会话
        SqlSession sqlSession = sessionFactory.openSession(true);
        // 获得连接
        Connection connection = sqlSession.getConnection();
        // 获得jdbc事务工厂
        JdbcTransactionFactory jdbcTransactionFactory = new JdbcTransactionFactory();
        // 通过连接创建事务
        transaction = jdbcTransactionFactory.newTransaction(connection);
        // 创建sql映射
        ms = configuration.getMappedStatement("com.yc.study.mapper.StudentMapper.getStudentById");
    }

    /**
     * 简单执行器测试
     * ms:sql映射
     * parameter：动态sql的属性
     * rowBounds：分页
     * resultHandler：结果集处理
     * boundsql：sql映射里的动态sql
     *
     * 每次都会创建一个新的预处理(PrepareStatement)
     */
    @Test
    public void simpleTest() throws SQLException {
        SimpleExecutor executor = new SimpleExecutor(configuration, transaction);
        List<Object> list1 = executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(10));
        List<Object> list2 = executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(10));
        System.out.println(list1.get(0));
        System.out.println(list2.get(0));


    }

    /**
     * 可重用执行器测试
     * 利用了PrepareStatement的只编译一次sql，传入不同的属性，就可以多次使用的特性（编译过一次sql的PrepareStatement，再对其设置参数便可以多次使用）
     * 相同的sql只进行一次预处理。（添加PrepareStatement的缓存）
     * @throws SQLException
     */
    @Test
    public void reuseTest() throws SQLException {
        ReuseExecutor executor = new ReuseExecutor(configuration, transaction);
        List<Object> list1 = executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(10));
        List<Object> list2 = executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(10));
        System.out.println(list1.get(0));
        System.out.println(list2.get(0));
    }

    /**
     * 批处理执行器测试
     * 批处理提交修改,必须执行flushStatements才能生效
     * @throws SQLException
     */
    @Test
    public void batchTest() throws SQLException {
        BatchExecutor executor = new BatchExecutor(configuration, transaction);
        MappedStatement setName = configuration.getMappedStatement("com.yc.study.mapper.StudentMapper.setName");

        Map<String,Object> params = new HashMap();
        params.put("id",1);
        params.put("name","修改后的YCcccccccccc111");
        executor.doUpdate(setName,params);//设置1
        executor.doUpdate(setName,params);//设置2

        Map<String,Object> params2 = new HashMap();
        params2.put("id",2);
        params2.put("name","修改后的YCcccccccccc222");
        executor.doUpdate(setName,params2);//设置3

        executor.doFlushStatements(false);//刷新
        // executor.commit(true);
    }

    /**
     * 二级缓存执行器测试
     * 提交之后才更新
     */
    @Test
    public void cacheTest() throws SQLException {
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, transaction);
        CachingExecutor cachingExecutor = new CachingExecutor(simpleExecutor);
        cachingExecutor.query(ms,1,RowBounds.DEFAULT,SimpleExecutor.NO_RESULT_HANDLER);
        cachingExecutor.commit(true);
        cachingExecutor.query(ms,1,RowBounds.DEFAULT,SimpleExecutor.NO_RESULT_HANDLER);
    }

    /**
     * sqlSession全流程测试
     */
    @Test
    public void sqlSessionTest(){
        SqlSession sqlSession = sessionFactory.openSession(true);

        Object o = sqlSession.selectOne("com.yc.study.mapper.StudentMapper.getStudentById", 1);
        System.out.println(o);
        
    }


    /**
     * 使用重用执行器
     * 相同的sql就会重用
     */
    @Test
    public void sessionReuseTest(){
        SqlSession sqlSession = sessionFactory.openSession(ExecutorType.REUSE, true);

        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        mapper.getStudentById(1);
        mapper.getStudentById(1);
    }

    /**
     * 使用批处理执行器
     * 问题1：如何才能使用同一个jdbcStatement?
     *  1.同一个sql 2.同一个MappedStatement 3.必须是连续的
     * 问题2：一起提交的吗？
     * 如果是同一个jdbcStatement一起提交，不同的statement遍历BatchResultList挨个提交
     *
     */
    @Test
    public void sessionBatchTest(){
        SqlSession sqlSession = sessionFactory.openSession(ExecutorType.BATCH, true);

        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        mapper.insertStudent(222,"芜湖1");
        mapper.setName(1,"1111");
        mapper.setName(2,"2222");

        List<BatchResult> batchResults = sqlSession.flushStatements();


    }
}

