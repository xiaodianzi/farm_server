package com.plansolve.farm.model.enums.type;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum PageTypeEnum implements TypeEnum {
    MAIN("main","首页"),
    INDENT("indent","订单"),
    MANAGE("manage","管理"),
    MINE("mine","我的");

    private String type; // 日志类型

    private String message; // 类型说明

    PageTypeEnum(String type, String message) {
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
