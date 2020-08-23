package com.yc.study;

import com.yc.study.entity.Student;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author admin
 * @date 2020/8/21 10:12:40
 * @description
 */
public class JDBCTest {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    private static Connection conn = null;


    /**
     * 初始化连接
     */
    @Before
    public void init() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将结果集封装成bean添加到list中
     *
     * @param rs          结果集
     * @param studentList 要添加的list
     * @throws SQLException
     */
    private static void addResultToList(ResultSet rs, List<Student> studentList) throws SQLException {
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String name = rs.getString("name");
            studentList.add(new Student(id, name));
        }
    }

    /**
     * 简单执行一遍jdbc流程
     * debug查看具体流程
     */
    @Test
    public void test1() {
        String sql = "select * from test.t_student  where id = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 1);
            rs = stmt.executeQuery();
            List<Student> studentList = new ArrayList<>();
            addResultToList(rs, studentList);
            studentList.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { //关闭结果集对象
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) { // 关闭数据库操作对象
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) { // 关闭数据库连接对象
                try {
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 编写SQL注入
     */
    private static void writeSQL(String name) {
        String sql = "select * from test.t_student where `name`='" + name + "'";
        System.out.println(sql);
        List<Student> studentList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            addResultToList(resultSet, studentList);
            studentList.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 防止sql注入
     */
    private static void writeBetterSQL(String name) {
        String sql = "select * from test.t_student where `name`= ? ";
        List<Student> studentList = new ArrayList<>();
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, name);
            System.out.println(statement);
            ResultSet resultSet = statement.executeQuery();
            addResultToList(resultSet, studentList);
            studentList.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试sql注入
     */
    @Test
    public void testSQL() {
        String name = "dsad' or '1'='1";
        // writeSQL(name);
        // String name = "a2";
        writeBetterSQL(name);
    }
}
