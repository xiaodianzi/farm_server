package com.plansolve.farm.model.enums.state;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum UserStateEnum implements StateEnum {
    NORMOL("normal","正常状态"),
    PENDING("pending","待审核"),
    AUDIT("audit","审核中"),
    AUDIT_FAILURE("audit_failure","审核失败"),
    DELETED("deleted","已删除"),
    FROZEN("frozen","已冻结");

    private String state; // 用户状态

    private String message; // 相关状态详情

    UserStateEnum(String state, String message) {
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
