package com.plansolve.farm.model.console.account;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @Author: ANDREW
 * @Date: 2019/4/10
 * @Description: 提现申请记录表
 **/

@Data
public class WithdrawApplicationDetailDTO {

    private String adminName; // 申请人

    private String applicationType; // 申请进度--创建、初审、复核

    private String applicationTime; // 审批时间

    private Boolean applicationResult; // 审核结果

    private BigDecimal applicationMoney; // 审批金额

    private String applicationDetail; // 审核备注

}
