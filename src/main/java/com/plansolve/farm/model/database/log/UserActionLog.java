package com.plansolve.farm.model.database.log;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/6/11
 * @Description:
 **/
@Data
@Entity
public class UserActionLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idUserActionLog;

    private Long idUser; // 用户ID

    private String platform; // 平台 app-客户端/mp-公众号/applet-小程序

    private String module; // 模块 用户/合作社/订单

    private String url; // 访问URL

    private String ip; // 用户IP地址

    private String args; // 用户传入参数

    private Date actionTime; // 动作发生时间

    private String result; // 返回结果

}
