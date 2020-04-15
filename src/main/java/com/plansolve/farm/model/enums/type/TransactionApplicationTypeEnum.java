package com.plansolve.farm.model.enums.type;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum TransactionApplicationTypeEnum implements TypeEnum {
    PAYMENT("payment","支付"),
    WX_PAYMENT("wx_payment","微信支付"),
    WITHDRAW("withdraw","提现");

    private String type; // 账户状态

    private String message; // 相关状态详情

    TransactionApplicationTypeEnum(String type, String message) {
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
