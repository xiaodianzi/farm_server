package com.plansolve.farm.model.client.account;

import com.plansolve.farm.model.database.account.Bank;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: 高一平
 * @Date: 2019/2/22
 * @Description:
 **/
@Data
public class BankCardDTO {

    private Long idBankCard;

    private String userMobile;

    private String bankCardState; // 银行卡状态（不做物理删除）

    @NotBlank(message = "银行卡号不能为空")
    private String bankCardNo; // 银行卡号

    @NotBlank(message = "持卡人姓名不能为空")
    private String bankCardHolder; // 持卡人姓名

    @NotNull(message = "是否是公司账户")
    private Boolean isCompany; // 是否是公司账户

    @NotBlank(message = "开户行信息不能为空")
    private String bankInfo; // 开户行信息

    private String reserveMobile; // 持卡人预留银行手机号码

    @NotBlank(message = "银行类型不能为空")
    private String bankType; // 银行类型

    @NotBlank(message = "卡片类型不能为空")
    private String cardType; // 卡片类型

    private Bank bank; // 银行信息

}
