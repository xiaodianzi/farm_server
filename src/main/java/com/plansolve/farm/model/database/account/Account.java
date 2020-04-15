package com.plansolve.farm.model.database.account;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/2/19
 * @Description: 用户账户信息
 * 1、设置idUser为0的账户，为平台资金冻结账户，存储所有处理中的用户资金
 * 2、设置idUser为1的账户，为平台差价账户，存储所有平台差价
 * 3、若以后有需求优惠券等抵现活动，需单独开设账户，并对相应账户记录资金流水
 **/
@Entity
@Data
public class Account implements Serializable {

    @Id
    @Column(updatable = false)
    private Long idUser;

    @Column(nullable = false)
    private BigDecimal accountBalance; // 用户余额

    private String withdrawPassword; // 提现密码

    @Column(nullable = false)
    private Boolean passwordSetting; // 是否设置提现密码

    @Column(nullable = false)
    private String accountState; // 账户状态

    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date accountOpeningTime; // 开户时间

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date accountUpdateTime; // 账户更新时间

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date accountBalanceChangeTime; // 账户更新时间

}
