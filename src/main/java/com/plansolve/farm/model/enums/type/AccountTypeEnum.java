package com.plansolve.farm.model.enums.type;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum AccountTypeEnum implements TypeEnum {
    WALLET("wallet","托管之家钱包"),
    WECHAT("wechat","微信钱包"),
    BANKCARD("bankcard","银行卡");

    private String type; // 账户状态

    private String message; // 相关状态详情

    AccountTypeEnum(String type, String message) {
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
