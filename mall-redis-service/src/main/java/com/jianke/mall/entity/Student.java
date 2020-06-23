package com.jianke.mall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {

    private Long id;

    private String name;

    private Long score;

    private Date createDate;

    public Student(Long id, String name, Long score) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.createDate = new Date();
    }

}
