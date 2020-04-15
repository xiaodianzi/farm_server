package com.plansolve.farm.model.console.account;

import com.plansolve.farm.util.HtmlUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/13
 * @Description:
 **/

@Data
public class TransactionApplicationDTO {

    private Long idTransactionApplication;

    private String transactionApplicationNo;

    private Integer idUser; // 申请人

    private String username; // 申请人

    private String mobile; // 申请人

    private String applicationType; // 申请类型--提现、付款

    private String applicationState; // 申请进度--创建、初审、复核

    private String applicationCreateTime; // 申请创建时间

    private String applicationUpdateTime; // 申请更新时间

    private Integer idUserOrder; // 相关订单号

    private String userOrderNo; // 相关订单号

    private Integer idBankCard; // 相关银行卡

    private String bankInfo; // 开户行信息

    private String bankCardNo; // 银行卡号

    private BigDecimal money; // 涉及金额

    private List<ApplicationDetailDTO> details;

    private String button; // 相关操作

    public String getButton() {
        return HtmlUtil.getButtonHtml(false, "/plansolve/manger/application/detail?idTransactionApplication=" + idTransactionApplication,
                "", "primary", "详情");
    }
}
