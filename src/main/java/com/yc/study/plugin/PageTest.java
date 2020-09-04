package com.yc.study.plugin;

import com.yc.study.NestedQueryTest;
import com.yc.study.entity.User;
import com.yc.study.mapper.UserMapper;
import org.apache.ibatis.session.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

/**
 * @author admin
 * @date 2020/9/2 13:31:42
 * @description
 */
public class PageTest {
    private static Configuration configuration;
    private static SqlSessionFactory sqlSessionFactory;
    @Before
    public void init() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        sqlSessionFactory = sqlSessionFactoryBuilder.build(NestedQueryTest.class.getResourceAsStream("/mybatis-config.xml"));
        configuration = sqlSessionFactory.getConfiguration();
        //取消默认的懒加载的触发方法
        configuration.setLazyLoadTriggerMethods(new HashSet<>());
    }

    /**
     * 插入测试数据
     */
    @Test
    public void batchAddUser(){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,true);
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        for(int i = 0 ; i < 150 ;i++){
            User user = new User(i+1000,i+"",i+"",i+"",i+"",i+"",i+"");
            mapper.insertUser(user);
        }
        sqlSession.flushStatements();
    }
    /**
     * 测试分页插件
     */
    @Test
    public void testPagePlugin(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Page page = new Page();
        page.setIndex(10);
        page.setSize(3);
        User user = new User();
        user.setName("1");
        List<User> users = mapper.findUserByInterceptor(user,page);

        System.out.println("总共数量："+page.getTotal());
        System.out.println("返回数量："+users.size());

    }

}
