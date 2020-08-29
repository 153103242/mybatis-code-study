package com.yc.study.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @date 2020/8/26 17:21:42
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog implements java.io.Serializable {
    private int id;
    private String title;
    private User author;
    private String body;
    private List<Comment> comments;
    Map<String,String> labels;

}
