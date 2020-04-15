package com.plansolve.farm.model.enums.code;

/**
 * @Author: 高一平
 * @Date: 2018/8/14
 * @Description:
 **/
public enum OperatorIdentityEnum implements CodeEnum {
    PRORPIETER(2,"社长"),
    CAPTAIN(1,"队长"),
    MEMBER(0,"社员");

    private Integer code; // 用户状态

    private String message; // 相关状态详情

    OperatorIdentityEnum(Integer code, String message) {
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