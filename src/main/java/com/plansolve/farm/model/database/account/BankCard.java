package com.plansolve.farm.model.database.account;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/2/19
 * @Description: 用户银行卡信息
 **/
@Entity
@Data
public class BankCard implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idBankCard;

    @Column(updatable = false, nullable = false)
    private Long idUser; // 持卡人ID

    @Column(nullable = false)
    private String bankCardState; // 银行卡状态（不做物理删除）

    @Column(updatable = false, nullable = false)
    private String bankCardNo; // 银行卡号

    @Column(updatable = false, nullable = false)
    private String bankCardHolder; // 持卡人姓名

    @Column(nullable = false)
    private Boolean isCompany; // 是否是公司账户

    @Column(updatable = false, nullable = false)
    private String bankInfo; // 开户行信息

    private String reserveMobile; // 持卡人预留银行手机号码

    @Column(updatable = false, nullable = false)
    private String bankType; // 银行类型

    @Column(updatable = false, nullable = false)
    private String cardType; // 卡片类型

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updateTime;

}
