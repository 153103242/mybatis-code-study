package com.yc.study.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 * @date 2020/8/26 17:24:31
 * @description
 */
public class Comment implements java.io.Serializable {

    private String id;
    private Blog blog;
    private String body;
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment() {
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", blog=" + blog +
                ", body='" + body + '\'' +
                ", user=" + user +
                '}';
    }

    public Comment(String id, Blog blog, String body, User user) {
        this.id = id;
        this.blog = blog;
        this.body = body;
        this.user = user;
    }
}
