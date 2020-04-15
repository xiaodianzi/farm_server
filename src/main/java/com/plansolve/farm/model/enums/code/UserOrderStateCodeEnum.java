package com.plansolve.farm.model.enums.code;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 订单状态码
 * WAITING("waiting", "待接单/待抢单"),
 * CONFIRMING("confirming", "待农机手确认"),
 * WORKING("working", "待作业"),
 * CHECKING("checking", "待验收"),
 * PAYMENT("payment", "待支付"),
 * PREPAID("prepaid", "已支付"),
 * RECEIPT("receipt", "确认收款"),
 * FINISHED("finished", "已完成"),
 * CANCELED("canceled", "已取消"),
 * OVERDUE("overdue", "已过期"),
 * DELETED("deleted", "已删除");
 **/

public enum UserOrderStateCodeEnum implements CodeEnum {
    WAITING(0, "waiting"),
    CONFIRMING(1, "confirming"),
    WORKING(2, "working"),
    CHECKING(3, "checking"),
    PAYMENT(4, "payment"),
    PREPAID(5, "prepaid"),
    RECEIPT(6, "receipt"),
    FINISHED(9, "finished"),
    CANCELED(-1, "canceled"),
    DELETED(-2, "deleted"),
    OVERDUE(-3, "overdue");

    private Integer code; // 用户状态
    private String message; // 相关状态详情

    UserOrderStateCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static UserOrderStateCodeEnum getByMessage(String message) {
        for (UserOrderStateCodeEnum each : UserOrderStateCodeEnum.class.getEnumConstants()) {
            if (message.equals(each.getMessage())) {
                return each;
            }
        }
        return null;
    }
}
