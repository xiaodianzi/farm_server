package com.plansolve.farm.model.database.user;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/8/30
 * @Description:
 **/

@Data
@Entity
public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idFeedback; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Long idUser; // 用户

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime; // 创建时间

    @Column(nullable = false, updatable = false)
    private String detail; // 意见详情

}
