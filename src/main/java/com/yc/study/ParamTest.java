package com.yc.study;

import com.yc.study.entity.Blog;
import com.yc.study.entity.Student;
import com.yc.study.mapper.BlogMapper;
import com.yc.study.mapper.StudentMapper;
import org.apache.ibatis.session.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author admin
 * @date 2020/8/25 11:31:21
 * @description
 */
public class ParamTest {
    private static Configuration configuration;
    private static SqlSessionFactory sqlSessionFactory;
    private static SqlSession sqlSession;
    private static StudentMapper mapper;

    @Before
    public void init() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        sqlSessionFactory = sqlSessionFactoryBuilder.build(SecondCacheTest.class.getResourceAsStream("/mybatis-config.xml"));
        configuration = sqlSessionFactory.getConfiguration();
        sqlSession = sqlSessionFactory.openSession(ExecutorType.REUSE, true);
        mapper = sqlSession.getMapper(StudentMapper.class);
    }

    /**
     * 测试单个参数，noParam
     */
    @Test
    public void testSingleNoParam(){
        mapper.getStudentByIdNoParam(1);
    }
    /**
     * 测试多个参数，noParam
     */
    @Test
    public void testMultiNoParam(){
        mapper.getStudentByIdAandNameNoParam(1,"hhh");
    }
    /**
     * 测试多个参数param
     */
    @Test
    public void testMulti(){
        mapper.getStudentByIdAandName(1,"hhh");
    }
    /**
     * 测试多个参数（包装类）param
     */
    @Test
    public void testMultiEntity(){
        Student student = mapper.getStudentByIdAndStudent(1, new Student(1, "hhh"));
        System.out.println(student);

    }

    /**
     * 测试返回多个resultSet
     */
    @Test
    public void testMultiResultSet(){
        List<Student> students = mapper.getStudentLikeName("发发发");
        System.out.println(students);

    }

}
