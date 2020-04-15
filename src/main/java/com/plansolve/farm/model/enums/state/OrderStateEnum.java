package com.plansolve.farm.model.enums.state;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农田状态码
 **/

public enum OrderStateEnum implements StateEnum {
    WAITING("waiting", "待接单/待抢单"),
    CONFIRMING("confirming", "待农机手确认"),
    WORKING("working", "待作业"),
    CHECKING("checking", "待验收"),
    PAYMENT("payment", "待支付"),
    PREPAID("prepaid", "已支付"),
    RECEIPT("receipt", "确认收款"),
    FINISHED("finished", "已完成"),
    CANCELED("canceled", "已取消"),
    OVERDUE("overdue", "已过期"),
    DELETED("deleted", "已删除");

    private String state; // 订单状态

    private String message; // 相关状态详情

    OrderStateEnum(String state, String message) {
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
