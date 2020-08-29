package com.yc.study.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * @author admin
 * @date 2020/8/26 17:24:49
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements java.io.Serializable {
    private Integer id;
    private String name;
    private String age;
    private String sex;
    private String email;
    private String phone_number;
    private String create_time;

}
