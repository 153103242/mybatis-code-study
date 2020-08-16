package com.yc.study;

import com.yc.study.entity.Student;
import com.yc.study.mapper.StudentMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.executor.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @date 2020/8/14 10:17:33
 * @description
 */
public class TestMain {

    // mybatis的配置类
    private static Configuration configuration;
    // mabatis的事务
    private static Transaction transaction;
    private static SqlSessionFactory sessionFactory;

    @Before
    public void init(){
        //构建会话工厂创建器
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        //获取配置文件输入流
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = Resources.getResourceAsStream("mybatis-config.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //构建会话工厂
         sessionFactory = sqlSessionFactoryBuilder.build(resourceAsStream);
        //获得configuration
        configuration = sessionFactory.getConfiguration();
        //获得会话
        SqlSession sqlSession = sessionFactory.openSession();
        //获得连接
        Connection connection = sqlSession.getConnection();
        //获得jdbc事务工厂
        JdbcTransactionFactory jdbcTransactionFactory = new JdbcTransactionFactory();
        //通过连接创建事务
        transaction = jdbcTransactionFactory.newTransaction(connection);
    }


    /**
     * 测试简单执行器
     * 简单执行器无论如何都会预编译
     * @throws SQLException
     */
    @Test
    public void testSimpleExecutor() throws SQLException {
        //获得简单执行器
        SimpleExecutor  simpleExecutor = new SimpleExecutor(configuration,transaction);
        //映射器
        MappedStatement mappedStatement = configuration.getMappedStatement("com.yc.study.mapper.StudentMapper.getStudentById");
        //执行简单执行器的查询（映射器，属性，是否分页，结果集处理器，sql语句）
        List<Student> studentList = simpleExecutor.doQuery(mappedStatement, 1, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, mappedStatement.getBoundSql(1));
        System.out.println(studentList.get(0));
        simpleExecutor.doQuery(mappedStatement, 1, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, mappedStatement.getBoundSql(1));
    }


    /**
     * 测试可重用执行器
     * 相同代码不会预编译
     * @throws SQLException
     */
    @Test
    public void testReuseExecutor() throws SQLException {
        //获得可重用执行器
        ReuseExecutor reuseExecutor = new ReuseExecutor(configuration,transaction);
        //映射器
        MappedStatement mappedStatement = configuration.getMappedStatement("com.yc.study.mapper.StudentMapper.getStudentById");
        //执行简单执行器的查询（映射器，属性，是否分页，结果集处理器，sql语句）
        List<Student> studentList = reuseExecutor.doQuery(mappedStatement, 1, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, mappedStatement.getBoundSql(1));
        System.out.println(studentList.get(0));
        reuseExecutor.doQuery(mappedStatement, 1, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, mappedStatement.getBoundSql(1));
    }

    /**
     * 测试批处理处理器
     * 只针对修改操作
     * 需要手动刷新
     *
     * @throws SQLException
     */
    @Test
    public void testBatchExecutor() throws SQLException {
        //获得批处理执行器
        BatchExecutor batchExecutor = new BatchExecutor(configuration,transaction);
        //映射器
        MappedStatement mappedStatement = configuration.getMappedStatement("com.yc.study.mapper.StudentMapper.setName");
        //*****查询也会预处理
        Map<String,Object> params = new HashMap<>();
        params.put("id",1);
        params.put("name","小帅哥YC1111");
        batchExecutor.doUpdate(mappedStatement,params);
        batchExecutor.doUpdate(mappedStatement,params);
        //手动刷新
        batchExecutor.commit(true);
    }


    /**
     * simpleExecutor 、 reuseExecutor、 batchExecutor都实现了BaseExecutor
     * 基础执行器 里实现了 执行器的接口
     * 基础执行器的query和update都封装了一级缓存
     * doquery和doupdate都交给子类实现
     * @throws SQLException
     */
    @Test
    public void testBaseExecutor() throws SQLException {
        Executor executor = new SimpleExecutor(configuration,transaction);
        MappedStatement ms = configuration.getMappedStatement("com.yc.study.mapper.StudentMapper.getStudentById");
        executor.query(ms,1,RowBounds.DEFAULT,Executor.NO_RESULT_HANDLER);
        executor.query(ms,1,RowBounds.DEFAULT,Executor.NO_RESULT_HANDLER);

    }


    /**
     *     二级缓存执行器，实现了执行器，完成了自己的二级缓存的代码逻辑
     *     剩下的代码逻辑交给装饰的实现baseExecutor的执行器完成
     *     要注意的是：二级缓存是可以跨线程的，所以需要事务提交后，缓存中才有
     *                一级缓存是线程独有的，所以代码执行后事务还没提交就有
     *
     *      开启二级缓存需要在mapper.xml中的namespace下写<cache/>
     */


    @Test
    public void testCacheExecutor() throws SQLException {
        Executor executor = new SimpleExecutor(configuration,transaction);

        //装饰者模式，封装一个简单执行器
        Executor cacheExecutor = new CachingExecutor(executor);
        MappedStatement ms = configuration.getMappedStatement("com.yc.study.mapper.StudentMapper.getStudentById");
        cacheExecutor.query(ms,1,RowBounds.DEFAULT,Executor.NO_RESULT_HANDLER);
        cacheExecutor.commit(true);
        cacheExecutor.query(ms,1,RowBounds.DEFAULT,Executor.NO_RESULT_HANDLER);
        cacheExecutor.query(ms,1,RowBounds.DEFAULT,Executor.NO_RESULT_HANDLER);


    }

    /**
     * sqlsession的执行流程
     * sessionFactory.openSession(ExecutorType.REUSE,true);可以指定cacheingExecutor的装饰者的类型
     */
    @Test
    public void testSqlSession(){
        SqlSession sqlSession = sessionFactory.openSession(ExecutorType.REUSE,true);
        List<Object> objects = sqlSession.selectList("com.yc.study.mapper.StudentMapper.getStudentById");
        System.out.println(objects.get(0));

    }

    /**
     * 测试一级缓存，默认一级缓存是开启的
     * 命中条件：
     * 1.必须是同一个会话（会话级的缓存）
     * 2.方法名必须一致，类名必须一致（staementId必须一致）
     * 3.RowBounds 返回行必须一致
     * 4.sql语句 参数一致
     */
    @Test
    public void testFirstCache(){
        SqlSession sqlSession = sessionFactory.openSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);//动态代理

        //同一个会话下执行查询同样的查询语句----》命中缓存
        // Student student = mapper.getStudentById(1);
        // Student student2 = mapper.getStudentById(1);

        //不同会话下查询同样的查询语句----------》未命中
        // Student student = mapper.getStudentById(1);
        // Student student2 = sessionFactory.openSession().getMapper(StudentMapper.class).getStudentById(1);

        //同个会话下查询语句相同但是方法名不同的语句--->未命中
        //同个会话下如果类名不一致，但方法名一致也是未命中
        //Student student = mapper.getStudentById(1); //statementId --》com.yc.study.mapper.StudentMapper.getStudentById
        //Student student2 = mapper.getStudentById2(1);//statementId --》com.yc.study.mapper.StudentMapper.getStudentById2

        //同个会话下查询相同的语句，增加了分页的选项
        //未添加分页----------------------------->命中
        // Student student = mapper.getStudentById(1);
        // List<Object> objects = sqlSession.selectList("com.yc.study.mapper.StudentMapper.getStudentById", 1);
        // Student student2 = (Student) objects.get(0);
        //添加分页------------------------------>未命中
        Student student = mapper.getStudentById(1);//在这个方法中查询的时候，添加的是默认分页RowBounds.DEFAULT,所以即使添加了默认分页也可以命中缓存
        List<Object> objects = sqlSession.selectList("com.yc.study.mapper.StudentMapper.getStudentById", 1,new RowBounds(0,1));
        Student student2 = (Student) objects.get(0);

        System.out.println(student==student2);
        
    }


    /*
     * 测试一级缓存命中的行为和配置
     * 命中条件：
     * 1.没有手动清空   清空clearLocalCache
     * 2.没有手动提交   清空clearLocalCache
     * 3.没有手动回滚   清空clearLocalCache
     * 4.没有update操作   清空clearLocalCache，只要调用update就清空，不管你的sql语句是什么
     * 5.查询语句上没有添加 @Options(flushCache = Options.FlushCachePolicy.TRUE)
     * 6.缓存的作用域不是statement
     */
    @Test
    public void testFirstCacheAction(){

        SqlSession sqlSession = sessionFactory.openSession(false);
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);//动态代理
        Student student = mapper.getStudentById(1);
        //其他条件相同，中间清除缓存----------》未命中
        // sqlSession.clearCache();
        //其他条件相同，手动提交----------》未命中
        // sqlSession.commit();
        //其他条件相同，事务回滚----------》未命中
        // sqlSession.rollback();
        //其他条件相同，添加更新操作，但不提交--->未命中
        // mapper.setName(5,"555");
        //其他条件相同，查询语句上添加@Options(flushCache = Options.FlushCachePolicy.TRUE)
        // Student student = mapper.getStudentById2(1);
        // Student student2 = mapper.getStudentById2(1);

        Student student2 = mapper.getStudentById(1);
        System.out.println(student == student2);
    }

    /**
     * 测试spring整合mybatis 一级缓存失效
     *
     * 问题导致：mapper.方法 ---->Creating a new SqlSession
     *          MapperProxy(动态代理接口)->SqlSessionTemplate(会话模板)->SqlSessionProxy(会话代理类)->SqlSessionInterceptor(会话拦截器)->sqlsessionFactory(会话工厂)
     * 解决分析：在SqlSessionInterceptor中的invoke方法中的getSqlSession如果有事务，就会取得同一个会话、
     * SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
*    * SqlSession session = sessionHolder(executorType, holder);
*    * if (session != null) {
*    *   return session;
*    * }
     *    我们需要在创建mapper前添加事务
     *     DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) context.getBean("txManager");
     *     TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
     */
    @Test
    public void testSpringMybatis(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        //创建事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) context.getBean("txManager");
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        //创建Mapper代理类
        StudentMapper bean = context.getBean(StudentMapper.class);

        Student student = bean.getStudentById(1);
        Student student2 = bean.getStudentById(1);
        System.out.println(student==student2);
        //提交事务
        transactionManager.commit(status);
    }



}
