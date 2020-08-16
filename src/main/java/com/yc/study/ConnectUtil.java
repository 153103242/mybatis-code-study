package com.yc.study;

import com.yc.study.entity.Student;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @date 2020/8/15 13:42:19
 * @description
 */
public class ConnectUtil {

    private static SqlSessionFactory sessionFactory;
    private static SqlSession sqlSession;
    static {
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
        //默认开启自动提交，即不是同一个事务
        sqlSession = sessionFactory.openSession(true);
    }
    public static Connection getConnection(){
        return sqlSession.getConnection();
    }
    public static void insert(Integer id ,String name ){
        System.out.println("******************更新结果***********************");
        //新开一个会话，开启自动提交
        SqlSession sqlSession = sessionFactory.openSession(true);
        Map<String,Object> params= new HashMap<>();
        params.put("id",id);
        params.put("name",name);
        sqlSession.update("com.yc.study.mapper.StudentMapper.insertStudent",params);
        sqlSession.close();
    }
    public static void select(String name,Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from t_student where name=?");
        statement.setString(1,name);
        ResultSet resultSet = statement.executeQuery();
        System.out.println("******************查询结果***********************");
        
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String c_name =resultSet.getString("name");
            System.out.println(id+" "+c_name);
        }
    }
}
