package com.yc.study.mapper;

import com.yc.study.entity.Blog;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * @author admin
 * @date 2020/8/27 14:07:35
 * @description
 */
@CacheNamespace
public interface BlogMapper {

    Blog selectBlogByBlogId(@Param("blog_id")Integer id);
    Blog selectBlogByBlogIdByLazy(@Param("blog_id")Integer id);
}
