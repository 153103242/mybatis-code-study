package com.yc.study;

import com.yc.study.entity.Student;
import com.yc.study.mapper.StudentMapper;
import org.apache.ibatis.session.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author admin
 * @date 2020/8/23 13:05:18
 * @description
 */
public class FirstCacheTest {


    private static SqlSessionFactory sessionFactory;

    @Before
    public void init() {
        // 构建会话工厂创建器
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        // 获取配置文件输入流
        // 构建会话工厂
        sessionFactory = sqlSessionFactoryBuilder.build(ExecutorTest.class.getResourceAsStream("/mybatis-config.xml"));

    }

    /**
     * 命中一级缓存的条件(运行参数)：
     * 1.sql和sql参数必须相同
     * 2.StatementId必须相同
     * 3.sqlsession必须相同
     * 4.rowBounds换行必须一致，默认不换行
     */
    @Test
    public void testRuntime(){
        SqlSession sqlSession = sessionFactory.openSession(ExecutorType.REUSE, true);
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);

        Student studentById = mapper.getStudentById(1);
        // Student studentById1 = mapper.getStudentById(2);   sql和sql参数必须相同
        // Student studentById1 = mapper.getStudentById2(1);    MappedStatementId必须相同
        /*Student studentById1 = sessionFactory.openSession(ExecutorType.REUSE, true)
                .getMapper(StudentMapper.class).getStudentById(1);   sqlsession必须相同 */
        List<Object> objects = sqlSession.selectList("com.yc.study.mapper.StudentMapper.getStudentById", 1, RowBounds.DEFAULT);//rowBounds换行必须一致
        System.out.println(studentById==objects.get(0));
    }

    /**
     * 命中一级缓存的条件(操作和配置)：
     * 1.不手动清空一级缓存
     * 2.未执行更新操作，更新操作为了保证数据一致性，也会清空一级缓存
     * 3.未调用flushCache=true的查询
     * 4.缓存作用域不是STATEMENT--->子查询一级缓存依旧有用
     */
    @Test
    public void testConfig(){
        SqlSession sqlSession = sessionFactory.openSession(ExecutorType.REUSE, true);
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);

        Student studentById = mapper.getStudentById(1);
        // sqlSession.clearCache();//未手动清空
        // mapper.setName(1,"hhh");//更新操作为了保证数据一致性，需要清空缓存
        Student studentById1 = mapper.getStudentById(1);
        System.out.println(studentById==studentById1);
    }
}
