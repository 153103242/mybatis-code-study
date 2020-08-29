package com.yc.study;

import com.yc.study.entity.Student;
import com.yc.study.mapper.StudentMapper;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

/**
 * @author admin
 * @date 2020/8/24 10:40:01
 * @description
 */
public class SecondCacheTest {

    private static Configuration configuration;
    private static SqlSessionFactory sqlSessionFactory;
    @Before
    public void init() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        sqlSessionFactory = sqlSessionFactoryBuilder.build(SecondCacheTest.class.getResourceAsStream("/mybatis-config.xml"));
        configuration = sqlSessionFactory.getConfiguration();
    }

    /**
     * 测试二级缓存，查看责任链
     * 需要在mapper接口开启耳机缓存
     * 缓存id为mapper的全包名
     */
    @Test
    public void cacheTest1() {
        Cache cache = configuration.getCache("com.yc.study.mapper.StudentMapper");
        cache.putObject("1","1111");
        Object object = cache.getObject("1");
        System.out.println(object);
    }

    /**
     * 指定第三方最终实现的缓存
     * //@CacheNamespace(implementation = DiskCache.class,properties ={@Property(name = "cachePath", value ="C:\\Users\\admin\\IdeaProjects\\mybatis-code\\mybatis-study\\target\\classes\\com\\yc\\study\\cache\\" )})
     */
    @Test
    public void cacheTest2() {
        Cache cache = configuration.getCache("com.yc.study.mapper.StudentMapper");
        cache.putObject("1","1111");
        Object object = cache.getObject("1");
        System.out.println(object);
    }
    /**
     * 指定FIFO淘汰策略/LRU淘汰策略
     * //@CacheNamespace(eviction = FifoCache.class,size = 10)
     * //@CacheNamespace(size = 10)
     */
    @Test
    public void cacheTest3() {
        Cache cache = configuration.getCache("com.yc.study.mapper.StudentMapper");
        for(int i = 0 ; i < 12 ;i++){
            cache.putObject("cache"+i,i);
        }
        System.out.println(cache);
    }

    /**
     * 序列化操作
     * 多线程调用，防止操作同一个对象
     */
    @Test
    public void cacheTest4() {
        Cache cache = configuration.getCache("com.yc.study.mapper.StudentMapper");
        cache.putObject("student",new Student());
        Object student = cache.getObject("student");
        Object student1 = cache.getObject("student");
        System.out.println(student==student1);
    }

    /**
     * 二级缓存命中条件
     *  1.会话提交
     *  2.同一个sql和参数
     *  3.statementId相同
     *  4.rowBounds相同
     */
    @Test
    public void cacheTest5() {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        sqlSession.getConnection();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        Student studentById = mapper.getStudentById(1);
        // sqlSession.commit(true);
        // mapper.getStudentById2(1);
        // mapper.insertStudent(new Random().nextInt(100000),"发发发889");


        SqlSession sqlSession1 = sqlSessionFactory.openSession(true);
        StudentMapper mapper1 = sqlSession.getMapper(StudentMapper.class);
        Student studentById1 = mapper1.getStudentById(1);
    }

}
