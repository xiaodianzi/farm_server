package com.plansolve.farm.model.client.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.util.Date2LongSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/22
 * @Description:
 **/
@Data
public class AccountDTO {

    private String userMobile;

    private String accountState; // 账户状态

    private Boolean passwordSetting; // 是否设置提现密码

    private BigDecimal accountBalance; // 用户余额

    private Boolean haveBankCard; // 是否绑定银行卡、

    private List<BankCardDTO> bankCards;

}
