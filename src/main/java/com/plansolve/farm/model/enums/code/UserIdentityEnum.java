package com.plansolve.farm.model.enums.code;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum UserIdentityEnum implements CodeEnum {
    NORMOL(0,"普通用户"),
    MEMBER(1,"社员"),
    CAPTAIN(2,"队长"),
    PROPRIETER(3,"社长");

    private Integer code; // 用户状态

    private String message; // 相关状态详情

    UserIdentityEnum(Integer code, String message) {
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
