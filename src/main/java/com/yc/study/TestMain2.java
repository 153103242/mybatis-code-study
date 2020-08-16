package com.yc.study;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 如何保证会话之外的数据更新
 *  会话1 ：       查询name为1的（事务1）                   查询name为1的（事务1）
 *  会话2 ：                      添加name为1的
 *
 *  如果会话1中不添加事务，则
 * @author admin
 * @date 2020/8/15 13:36:13
 * @description
 */
public class TestMain2 {

    static {
        try {
            ConnectUtil.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static Object lock = new Object();

    public static Thread run(Runnable runnable) {
        Thread thread1 = new Thread(runnable);
        thread1.start();
        return thread1;
    }

    public static void main(String[] args) {

        Thread t1 = run(() -> {
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //2.插入记录
            ConnectUtil.insert(555, "小帅哥YC1111");
        });

        Thread t2=run(()->{
            try {
                Connection connection = ConnectUtil.getConnection();
                //如果不是同一个事务，就能查询到结果,即自动提交为true
                //如果是同一个事务，就看不到其他事务的结果，即自动提交为false
                connection.setAutoCommit(false);
                //1.第一次读取
                ConnectUtil.select("小帅哥YC1111",connection);

                //释放锁
                synchronized (lock){
                    lock.notify();
                }
                Thread.sleep(500);

                //3.第二次读取
                ConnectUtil.select("小帅哥YC1111",connection);


                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
