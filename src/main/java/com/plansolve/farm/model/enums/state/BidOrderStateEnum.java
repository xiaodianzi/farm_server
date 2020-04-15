package com.plansolve.farm.model.enums.state;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农田状态码
 **/

public enum BidOrderStateEnum implements StateEnum {
    WAITING("waiting", "接单中..."),
    WORKING("working", "作业中..."),
    FINISHED("finished", "作业完成"),
    CANCELED("canceled", "已取消"),
    DELETED("deleted", "已删除");

    private String state; // 订单状态

    private String message; // 相关状态详情

    BidOrderStateEnum(String state, String message) {
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
