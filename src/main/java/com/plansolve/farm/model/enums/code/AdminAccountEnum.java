package com.plansolve.farm.model.enums.code;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum AdminAccountEnum implements CodeEnum {
    PLATFORM_USER_ACCOUNT(0,"平台账户-用户资金中转"),
    PLATFORM_Fill(-1,"平台账户-订单支付差价");

    private Integer code; // 用户状态

    private String message; // 相关状态详情

    AdminAccountEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
