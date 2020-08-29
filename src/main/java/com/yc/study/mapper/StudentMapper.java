package com.yc.study.mapper;

import com.yc.study.cache.DiskCache;
import com.yc.study.entity.Student;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.mapping.StatementType;

import java.sql.Statement;
import java.util.List;

/**
 * @author admin
 * @date 2020/8/14 10:09:03
 * @description
 */
//开启二级缓存
// @CacheNamespace(implementation = DiskCache.class,properties ={@Property(name = "cachePath", value ="C:\\Users\\admin\\IdeaProjects\\mybatis-code\\mybatis-study\\target\\classes\\com\\yc\\study\\cache\\" )})
// @CacheNamespace(size = 10)
// @CacheNamespace(readWrite = false)
@CacheNamespace
public interface StudentMapper {


    @Select("select * from t_student where id =#{id}")
    // @Options(flushCache = Options.FlushCachePolicy.TRUE)//清空缓存
    Student getStudentById2(@Param("id") Integer id);

    // @Options(useCache = false)//关闭二级缓存
    @Select("select * from t_student where id =#{id}")
    // @Options(statementType = StatementType.STATEMENT)
    Student getStudentById(@Param("id") Integer id);

    @Select({"select * from t_student where name like concat('%',#{name},'%')"})
    List<Student> getStudentLikeName(@Param("name") String name);


    @Select("select * from t_student where id =#{id} and name=#{student.name}")
        // @Options(statementType = StatementType.STATEMENT)
    Student getStudentByIdAndStudent(@Param("id") Integer id,@Param("student")Student student);

    @Select("select * from t_student where id =#{suibiantianshenme}")
        // @Options(statementType = StatementType.STATEMENT)
    Student getStudentByIdNoParam(Integer id);

    @Select("select * from t_student where id =#{arg0} and name=#{arg1}")
    Student getStudentByIdAandNameNoParam(Integer id,String name);

    @Select("select * from t_student where id =#{id} and name=#{name}")
    Student getStudentByIdAandName(@Param("id")Integer id,@Param("name")String name);

    Student getStudentByIdByxml(@Param("id") Integer id);

    @Select("select * from t_student where name =#{name}")
    List<Student> getStudentByName(@Param("name")String name);

    //可以通过注解来映射
    @Update("update t_student set name=#{name} where id =#{id}")
    int setName(@Param("id") Integer id,@Param("name") String name);

    @Insert("insert into t_student values(#{id},#{name})")
    int insertStudent(@Param("id") Integer id,@Param("name") String name);

    Student getStudentByIdAutoMapping (@Param("id") Integer id);

}
