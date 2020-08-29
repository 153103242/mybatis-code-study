package com.yc.study;

import com.yc.study.entity.Blog;
import com.yc.study.entity.Comment;
import com.yc.study.entity.Student;
import com.yc.study.entity.User;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author admin
 * @date 2020/8/26 17:20:42
 * @description
 */
public class MetaTest {

    private static List<Comment> commentList = new ArrayList<>();
    @Before
    public void init(){
        commentList.add(new Comment("1",null,"111",new User(1,"user1","123","男", "123@qq.com","1","2020年8月27日 10:06:29")));
        commentList.add(new Comment("2",null,"222",new User(2,"user2","222","男", "222@qq.com","2","2020年8月27日 10:06:29")));
        commentList.add(new Comment("3",null,"333",new User(3,"user3","333","男", "3333@qq.com","3","2020年8月27日 10:06:29")));
    }

    /**
     * 1.直接操作属性
     * 2.操作子属性
     * 3.自动创建属性对象
     * 4.自动查找属性名，支持下划线转驼峰
     * 5.基于索引访问数组
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Test
    public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //基于反射
        // Object blog = new Blog();
        // Method setBody = blog.getClass().getDeclaredMethod("setBody", String.class);
        // setBody.invoke(blog,"1");
        // Blog blogInvoke = (Blog) blog;
        // System.out.println(blogInvoke.getBody());
        //基于metaObject装饰者模式
        Configuration configuration = new Configuration();
        Object blog = new Blog();
        MetaObject metaObject = configuration.newMetaObject(blog);

        /* 直接操作属性 */
        // metaObject.setValue("body","123");
        // System.out.println(metaObject.getValue("body"));

        /* 操作子属性,自动创建属性对象 */
        // metaObject.setValue("author.name","123");
        // System.out.println(metaObject.getValue("author"));

        /* 自动查找属性名，支持下划线转驼峰 */
        // String property = metaObject.findProperty("author.phone_number", true);
        // System.out.println(property);
        

        /* 操作集合、数组，需要先创建出来，往里面填值 */
        metaObject.setValue("comments",commentList);
        metaObject.getValue("comments[0].user.name");

        /* 操作Map可以通过 map名[key] 直接获得value,map只需要传入map对象不需要往里面填值，也会自动创建 */
        // metaObject.setValue("labels",new HashMap<>());
        // metaObject.setValue("labels[red]","666");
        // System.out.println(metaObject.getValue("labels[red]"));

    }
}
