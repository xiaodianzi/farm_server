package com.plansolve.farm.model.database.console;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 主控台用户
 **/
@Entity
@Data
public class AdminUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idAdminUser;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userState;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Boolean isSuperAdmin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updateTime;

}
