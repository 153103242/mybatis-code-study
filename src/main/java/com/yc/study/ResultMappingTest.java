package com.yc.study;

import com.yc.study.entity.Blog;
import com.yc.study.entity.Student;
import com.yc.study.mapper.BlogMapper;
import com.yc.study.mapper.StudentMapper;
import org.apache.ibatis.session.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author admin
 * @date 2020/8/27 15:55:45
 * @description
 */
public class ResultMappingTest {
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
     * 测试简单映射的结果集映射
     */
    @Test
    public void testSimpleMapping(){
        Student studentById = mapper.getStudentById(1);
        System.out.println(studentById);
    }
    /**
     * 测试返回自动映射
     */
    @Test
    public void testResultSetAutoMapping(){
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Blog blog = mapper.selectBlogByBlogId(1);
    }
}
