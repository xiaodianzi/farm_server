package com.plansolve.farm.model.database.school;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/3/29
 * @Description:
 **/

@Entity
@Data
public class News implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idNews; // 主键（数据库自增）

    private Integer idAdminUser; // 添加人

    @Column(nullable = false)
    private String address; // 新闻显示城市

    @Column(nullable = false)
    private String title; // 新闻标题

    @Column(nullable = false)
    private String picture; // 新闻相关图片

    @Column(nullable = false)
    private String sketch; // 新闻简述

    @Column(nullable = false)
    private String detail; // 文字详情

    @Column(nullable = false)
    private Boolean isValid; // 是否有效

    @Column(nullable = false)
    private Integer sno; // 序号

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime;

    @Column(nullable = false)
    private String source; // 新闻来源

    @Column(nullable = false)
    private Date releaseTime; // 新闻发布时间

    private String url; // 新闻详情页链接

    private String image; // 新闻详情页banner图片

}
