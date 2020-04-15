package com.plansolve.farm.model.enums.type;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum AccountLogTypeEnum implements TypeEnum {
    CREATE("create","创建账户"),
    FROZEN("frozen","冻结账户"),
    RECHARGE("recharge","用户充值"),
    WITHDRAW("withdraw","用户提现"),
    INCOME("income","账户收款"),
    EXPENSE("expense","账户付款"),
    WX_INCOME("wx_income","微信账户收款"),
    WX_EXPENSE("wx_expense","微信账户付款"),
    BANKCARD_INCOME("bankcard_income","银行卡账户收款"),
    BANKCARD_EXPENSE("bankcard_expense","银行卡账户付款");

    private String type; // 账户状态

    private String message; // 相关状态详情

    AccountLogTypeEnum(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
