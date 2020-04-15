package com.plansolve.farm.model.client.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.util.Date2LongSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date applicationTime; // 审核时间

    private BigDecimal applicationMoney; // 审核金额

    private Boolean applicationResult; // 审核结果

    private String adminName; // 审核人

    private String adminMobile; // 审核人电话

    private String applicationDetail; // 审核备注

}
