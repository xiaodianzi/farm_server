package com.plansolve.farm.model.enums.type;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum LogTypeEnum implements TypeEnum {
    CREATE("create","添加"), UPDATE("update","修改"),DELETE("delete","删除"), FREEZE("freeze","冻结"), VERIFY("verify","验证通过");

    private String type; // 日志类型

    private String message; // 类型说明

    LogTypeEnum(String type, String message) {
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
