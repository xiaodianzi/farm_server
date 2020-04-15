package com.plansolve.farm.model.database;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/

@Entity
@Data
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idAddress; // 主键（数据库自增）

    @Column(nullable = false, length = 16)
    private String province; // 省

    @Column(nullable = false, length = 16)
    private String city; // 市

    @Column(nullable = false, length = 16)
    private String county; // 区县

    @Column(length = 16)
    private String town; // 乡镇

    @Column(nullable = false)
    private Double latitude; // 纬度

    @Column(nullable = false)
    private Double longitude; // 经度

    private String detail; // 详情

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createDate; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updateDate; // 更新时间
}
