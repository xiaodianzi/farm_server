package com.plansolve.farm.model.enums.state;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农田状态码
 **/

public enum BidOperatorStateEnum implements StateEnum {
    APPLYING("applying","申请中"),
    PASSED("passed","已通过"),
    REFUSED("refused","已拒绝"),
    FINISHED("finished","已完成"),
    CANCELED("canceled","已取消");

    private String state; // 被邀请农机手状态

    private String message; // 相关状态详情

    BidOperatorStateEnum(String state, String message) {
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
