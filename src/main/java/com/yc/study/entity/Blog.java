package com.yc.study.entity;

import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @date 2020/8/26 17:21:42
 * @description
 */
public class Blog implements java.io.Serializable {
    private int id;
    private String title;
    private User author;
    private String body;
    private List<Comment> comments;
    Map<String,String> labels;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author=" + author +
                ", body='" + body + '\'' +
                ", comments=" + comments +
                ", labels=" + labels +
                '}';
    }

    public Object writeReplace(){
        System.out.println("芜湖！");
        return null;
    }

    public Blog(int id, String title, User author, String body, List<Comment> comments, Map<String, String> labels) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.body = body;
        this.comments = comments;
        this.labels = labels;
    }

    public Blog() {
    }
}
