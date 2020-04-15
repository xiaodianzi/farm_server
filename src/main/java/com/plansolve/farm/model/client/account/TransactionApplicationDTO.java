package com.plansolve.farm.model.client.account;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/3/11
 * @Description:
 **/

@Data
public class TransactionApplicationDTO {

    private String TransactionApplicationNo;

    private String applicationType; // 申请类型--提现、付款

    private String applicationState; // 申请进度--创建、初审、复核

    private String createTime; // 申请创建时间

    private String userOrderNo; // 相关订单号

    private BigDecimal money; // 涉及金额

    private BankCardDTO bankCard; //相关银行卡

}
