package com.yc.study.mapper;

import com.yc.study.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author admin
 * @date 2020/8/14 10:09:03
 * @description
 */
public interface StudentMapper {

    Student getStudentById(@Param("id") Integer id);

    @Select("select * from t_student where id =#{id}")
    // @Options(flushCache = Options.FlushCachePolicy.TRUE)//清空缓存
    Student getStudentById2(@Param("id") Integer id);

    @Select("select * from t_student where name =#{name}")
    List<Student> getStudentByName(@Param("name")String name);

    //可以通过注解来映射
    @Update("update t_student set name=#{name} where id =#{id}")
    int setName(@Param("id") Integer id,@Param("name") String name);

    @Insert("insert into t_student values(#{id},#{name})")
    int insertStudent(@Param("id") Integer id,@Param("name") String name);
}