package com.yc.study;

import com.yc.study.entity.User;
import com.yc.study.mapper.BlogMapper;
import com.yc.study.mapper.StudentMapper;
import com.yc.study.mapper.UserMapper;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author admin
 * @date 2020/8/31 14:05:07
 * @description
 */
public class DynamicSqlTest {
    private static Configuration configuration;
    private static SqlSessionFactory sqlSessionFactory;
    private static SqlSession sqlSession;
    private static UserMapper mapper;

    @Before
    public void init() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        sqlSessionFactory = sqlSessionFactoryBuilder.build(DynamicSqlTest.class.getResourceAsStream("/mybatis-config.xml"));
        configuration = sqlSessionFactory.getConfiguration();
        //取消默认的懒加载的触发方法
        configuration.setLazyLoadTriggerMethods(new HashSet<>());
        sqlSession = sqlSessionFactory.openSession(ExecutorType.REUSE, true);
        mapper = sqlSession.getMapper(UserMapper.class);
    }


    /**
     * 测试脚本语法树的if节点
     */
    @Test
    public void testSqlNode(){
        User user = new User();
        user.setId(1);
        user.setName("2");
        user.setAge("3");
        //动态sql的上下文环境
        DynamicContext dynamicContext = new DynamicContext(configuration,user);
        //静态节点逻辑
        new StaticTextSqlNode("select * from test.t_user").apply(dynamicContext);
        //IF节点逻辑
        IfSqlNode ifSqlNode1 = new IfSqlNode(new StaticTextSqlNode("and id=#{id}"), "id!=null");
        IfSqlNode ifSqlNode2 = new IfSqlNode(new StaticTextSqlNode("and age=#{age}"), "age!=null");
        IfSqlNode ifSqlNode3 = new IfSqlNode(new StaticTextSqlNode("and name=#{name}"), "name!=null");
        //用mixedSqlNode包装
        MixedSqlNode mixedSqlNode = new MixedSqlNode(Arrays.asList(ifSqlNode1, ifSqlNode2, ifSqlNode3));
        //where节点逻辑
        WhereSqlNode whereSqlNode = new WhereSqlNode(configuration, mixedSqlNode);
        whereSqlNode.apply(dynamicContext);

        //从动态sql的环境获取处理好的sql语句
        System.out.println(dynamicContext.getSql());
    }

    /**
     * 测试foreach
     */
    @Test
    public void foreachTest(){
        List<User> usersByIds = mapper.findUsersByIds(Arrays.asList("1", "2", "3"));
    }
}
