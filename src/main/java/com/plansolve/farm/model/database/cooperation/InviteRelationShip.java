package com.plansolve.farm.model.database.cooperation;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Andrew
 * @Date: 2018/6/12
 * @Description: 合作社成员关系表
 */

@Data
@Entity
public class InviteRelationShip implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idRelationShip; //主键（数据库自增)

    @Column(nullable = false)
    private Integer idCooperation; //合作社id

    private Integer idTeam; //所属小队id

    @Column(nullable = false)
    private Long idInviter; //关联人id

    @Column(nullable = false)
    private Long idProprieter; //社长id

    @Column(nullable = false)
    private String inviterState; //关联人状态

    private String inviterRole; //关联人角色

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createTime; //创建时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date permitTime; //加入合作社时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime; //更新时间

    private String remark; //备注

}
