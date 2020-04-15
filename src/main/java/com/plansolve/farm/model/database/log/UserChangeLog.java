package com.plansolve.farm.model.database.log;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 记录用户账号变更情况（变更密码，变更手机号码，以及用户的新增验证冻结删除等）
 **/

@Data
@Entity
public class UserChangeLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idUserChangeLog; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Long idUser; // 用户主键

    @Column(nullable = false, updatable = false, length = 32)
    private String changeType; // 更改类型

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date changeTime; // 更改时间

    @Column(updatable = false)
    private String detail; // 变更详情

}
