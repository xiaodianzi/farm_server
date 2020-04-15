package com.plansolve.farm.model.database.user;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户详细信息
 **/

@Data
@Entity
public class UserInfo implements Serializable {
    @Id
    @Column(updatable = false)
    private Long idUser; // 主键

    @Column(nullable = false, updatable = false, length = 16)
    private String realname; // 真实姓名

    @Column(nullable = false, updatable = false, length = 18)
    private String idCardNo; // 证件号码

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date verifyTime; // 验证时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday; // 出生日期

    private String sex; // 性别

    private String email; // 邮箱

    private String qq; // QQ号
}
