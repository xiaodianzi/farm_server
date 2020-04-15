package com.plansolve.farm.model.enums.type;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum ApplicationTypeEnum implements TypeEnum {
    CHECK("check","初审"),
    RECHECK("recheck","复核"),
    PAYMENT("payment","付款");

    private String type; // 账户状态

    private String message; // 相关状态详情

    ApplicationTypeEnum(String type, String message) {
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
