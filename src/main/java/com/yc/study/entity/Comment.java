package com.yc.study.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 * @date 2020/8/26 17:24:31
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements java.io.Serializable {

    private String id;
    private Blog blog;
    private String body;
    private User user;
}
