package com.plansolve.farm.model.database.school;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/5/15
 * @Description:
 **/

@Entity
@Data
public class CerealsMassage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idCerealsMassage; // 主键（数据库自增）

    @Column(nullable = false)
    private Integer idCerealsMassageType; // 作物类型 玉米/水稻

    @Column(nullable = false)
    private String address; // 新闻显示城市 吉林/东北三省/全国

    @Column(nullable = false)
    private String title; // 新闻标题

    @Column(nullable = false)
    private String detail; // 文字详情

    @Column(nullable = false)
    private Boolean isValid; // 是否有效

    @Column(nullable = false)
    private Integer sno; // 序号

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createTime;

    @Column(nullable = false)
    private String source; // 新闻来源

    @Column(nullable = false)
    private Date releaseTime; // 新闻发布时间

    private String url; // 新闻详情页链接

}
