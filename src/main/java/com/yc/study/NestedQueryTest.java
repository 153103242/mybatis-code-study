package com.yc.study;

import com.yc.study.entity.Blog;
import com.yc.study.mapper.BlogMapper;
import org.apache.ibatis.session.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

/**
 * @author admin
 * @date 2020/8/30 15:56:29
 * @description
 */
public class NestedQueryTest {

    private static Configuration configuration;
    private static SqlSessionFactory sqlSessionFactory;
    private static SqlSession sqlSession;
    private static BlogMapper mapper;

    @Before
    public void init() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        sqlSessionFactory = sqlSessionFactoryBuilder.build(NestedQueryTest.class.getResourceAsStream("/mybatis-config.xml"));
        configuration = sqlSessionFactory.getConfiguration();
        //取消默认的懒加载的触发方法
        configuration.setLazyLoadTriggerMethods(new HashSet<>());
        sqlSession = sqlSessionFactory.openSession(ExecutorType.REUSE, true);
        mapper = sqlSession.getMapper(BlogMapper.class);
    }

    /**
     * 测试联合查询
     */
    @Test
    public void test1(){
        Blog blog = mapper.selectBlogByNestedQuery(1);

    }
}
