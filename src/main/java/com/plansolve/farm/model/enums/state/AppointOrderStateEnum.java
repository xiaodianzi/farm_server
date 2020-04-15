package com.plansolve.farm.model.enums.state;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农田状态码
 **/

public enum AppointOrderStateEnum implements StateEnum {
    WAITING("waiting", "待派单"),
    SENDING("sending", "派单中"),
    FINISHED("finished", "派单完成"),
    CANCELED("canceled", "已取消"),
    DELETED("deleted", "已删除");

    private String state; // 订单状态

    private String message; // 相关状态详情

    AppointOrderStateEnum(String state, String message) {
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
