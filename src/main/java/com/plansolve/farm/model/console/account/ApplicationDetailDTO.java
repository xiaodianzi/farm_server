package com.plansolve.farm.model.console.account;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/3/14
 * @Description:
 **/

@Data
public class ApplicationDetailDTO {

    private String transactionApplicationNo; // 申请ID

    private String transactionId; // 微信交易号

    private String transactionType; // 审核类型 初审、复核、财务打款

    private String applicationType; // 审核类型 初审、复核、财务打款

    private String applicationTime; // 审核时间

    private BigDecimal applicationMoney; // 审核金额

    private Boolean applicationResult; // 审核结果

    private String adminName; // 审核人

    private String adminMobile; // 审核人电话

    private String applicationDetail; // 审核备注

}
