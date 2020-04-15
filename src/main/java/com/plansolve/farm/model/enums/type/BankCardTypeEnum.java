package com.plansolve.farm.model.enums.type;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

public enum BankCardTypeEnum implements TypeEnum {
    DC("DC","储蓄卡"),
    CC("CC","信用卡"),
    SCC("SCC","准贷记卡"),
    PC("PC","预付费卡");

    private String type; // 账户状态

    private String message; // 相关状态详情

    BankCardTypeEnum(String type, String message) {
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
