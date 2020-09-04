package com.yc.study.mapper;

import com.yc.study.entity.User;
import com.yc.study.plugin.Page;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author admin
 * @date 2020/8/27 14:07:35
 * @description
 */
@CacheNamespace
public interface UserMapper {

    User findUser(@Param("id")Integer id ,@Param("name")String name,@Param("age")String age);

    List<User> findUsersByIds(@Param("list")List<String> list);

    @Insert("insert into test.t_user value (#{id},#{name},#{age},#{sex},#{email},#{phone_number},#{create_time})")
    void insertUser(User user);

    @Select("select * from test.t_user")
    List<User> selectUsersByPagePlugin(Page page);

    List<User> findUserByInterceptor(@Param("user")User user,Page page);
}