package com.plansolve.farm.model.database.user;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户基本信息
 **/

@Data
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idUser; // 主键（数据库自增）

    @Column(nullable = false, length = 16)
    private String nickname; // 用户昵称

    @Column(nullable = false, unique = true, length = 12)
    private String mobile; // 联系电话

    @Column(nullable = false, length = 128)
    private String password; // 密码

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date registTime; // 注册时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updateTime; // 注册时间

    @Column(nullable = false)
    private Boolean isFarmer; // 是否是种植户

    @Column(nullable = false)
    private Boolean isOperator; // 是否是农机手

    @Column(nullable = false, length = 32)
    private String userState; // 当前用户的状态

    private String avatar; // 用户头像地址

    private Integer idCooperation; // 用户所加入合作社

    private Integer idTeam; // 用户所加入小队

    private Long idAddress; // 用户所处位置

    @Column(length = 36)
    private String androidMAC; // 用户设备码

    private String registId; // 消息推送id

    public Boolean getFarmer() {
        return isFarmer;
    }

    public void setFarmer(Boolean farmer) {
        isFarmer = farmer;
    }

    public Boolean getOperator() {
        return isOperator;
    }

    public void setOperator(Boolean operator) {
        isOperator = operator;
    }

}
