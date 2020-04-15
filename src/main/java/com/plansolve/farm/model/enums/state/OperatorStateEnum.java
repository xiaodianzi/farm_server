package com.plansolve.farm.model.enums.state;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农田状态码
 **/

public enum OperatorStateEnum implements StateEnum {
    INVITED("invited","被邀请"),
    ACCEPTED("accepted","已接受"),
    FINISHED("finished","已完成"),
    REFUSED("refused","已拒绝"),
    CANCELED("canceled","已取消");

    private String state; // 被邀请农机手状态

    private String message; // 相关状态详情

    OperatorStateEnum(String state, String message) {
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
