package com.plansolve.farm.model.enums.state;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农田状态码
 **/

public enum MachineryStateEnum implements StateEnum {
    NORMOL("normal","正常状态"),DELETED("deleted","已删除"),FROZEN("forzen","已冻结");

    private String state; // 用户状态

    private String message; // 相关状态详情

    MachineryStateEnum(String state, String message) {
        this.state = state;
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }
}
