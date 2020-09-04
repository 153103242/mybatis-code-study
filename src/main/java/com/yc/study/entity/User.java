package com.yc.study.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 * @date 2020/8/26 17:24:49
 * @description
 */
public class User implements java.io.Serializable {
    private Integer id;
    private String name;
    private String age;
    private String sex;
    private String email;
    private String phone_number;
    private String create_time;

    public User() {
    }

    public User(Integer id, String name, String age, String sex, String email, String phone_number, String create_time) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.email = email;
        this.phone_number = phone_number;
        this.create_time = create_time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
