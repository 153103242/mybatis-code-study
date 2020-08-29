package com.yc.study;

import com.yc.study.entity.Blog;
import com.yc.study.mapper.BlogMapper;
import com.yc.study.mapper.StudentMapper;
import org.apache.ibatis.session.*;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

/**
 * @author admin
 * @date 2020/8/29 14:24:40
 * @description
 */
public class LazyTest {

    private static Configuration configuration;
    private static SqlSessionFactory sqlSessionFactory;
    private static SqlSession sqlSession;
    private static BlogMapper mapper;

    @Before
    public void init() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        sqlSessionFactory = sqlSessionFactoryBuilder.build(SecondCacheTest.class.getResourceAsStream("/mybatis-config.xml"));
        configuration = sqlSessionFactory.getConfiguration();
        sqlSession = sqlSessionFactory.openSession(ExecutorType.REUSE, true);
        mapper = sqlSession.getMapper(BlogMapper.class);
    }


    /**
     * 获得懒加载的动态对象后，设置了懒加载的属性
     * 再去执行get操作，还能执行懒加载操作吗?
     *
     * 执行过set操作后就自动关闭了懒加载
     */
    @Test
    public void lazySetTest(){
        Blog blog = mapper.selectBlogByBlogIdByLazy(1);
        // blog.setComments(new ArrayList<>());
        // System.out.println(blog.getComments().size());
    }

    /**
     * 序列化---》字节码---》反序列化成对象
     *
     *
     * 只有jdk序列化之后依然可以进行，传输给第三方，或者用第三方序列化都不行
     * 注：需要设置configurationFactory指定configuration构造器
     */
    @Test
    public void lazySerializableTest(){
        Blog blog = mapper.selectBlogByBlogIdByLazy(1);
        //序列化
        System.out.println("开始序列化");
        byte[] bytes = writeObject(blog);
        System.out.println("开始反序列化");
        Blog o = (Blog) readObject(bytes);
        System.out.println("反序列化完成");
        System.out.println(o.getComments().size());


    }
    @Test
    public void serializableWriteReplace(){
        Bean bean = new Bean(1,"1");
        byte[] bytes = writeObject(bean);
        Bean o = (Bean) readObject(bytes);
        System.out.println(o.id);
        
        
    }

    public static class Bean implements Serializable{
        public Integer id;
        public String name;

        public Bean() {
        }

        public Bean(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        //在序列化时，重写对象
        protected final Object writeReplace() throws ObjectStreamException{
            return new Bean(7888,"mm");
        }
        //在反序列化是，重写对象
        protected final Object readResolve() throws ObjectStreamException{
            System.out.println(this.id);
            return new Bean(9999,"ss");
        }
        
    }
    public static class ConfigurationFactory{
        public static Configuration getConfiguration(){ return configuration; }
    }
    private static byte[] writeObject(Object o){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
             oos.writeObject(o);
             oos.flush();
            byte[] bytes = bos.toByteArray();
            oos.close();
            bos.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Object readObject(byte[] bytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object o = ois.readObject();
            ois.close();
            bis.close();
            return o ;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
