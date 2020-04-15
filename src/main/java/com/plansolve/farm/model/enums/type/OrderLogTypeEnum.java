package com.plansolve.farm.model.enums.type;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum OrderLogTypeEnum implements TypeEnum {
    CREATE("create","新增订单"),
    UPDATE("update","修改订单"),
    DELETE("delete","删除订单"),
    GET_IT("get_it","接单成功"),
    CONFIRMED("confirmed","农机手确认"),
    WORKER_ADD("worker_add","添加农机手"),
    WORKER_CUT("worker_cut","减少农机手"),
    REPORT("report","提交完工报告"),
    WORK_OVER("work_over","该订单作业完成"),
    DEMAND_PRICE("demand_price","添加索要订单金额"),
    WORK_FINAL_ACCEPTANCE("work_final_acceptance","该订单作业验收完成"),
    ORDER_PAY("order_pay","该订单支付完成"),
    CONFIRM_RECEIPT("confirm_receipt","该订单确认收款"),
    FINISHED("finished","订单完成"),
    OVERDUE("overdue","订单过期"),
    CANCEL("cancel","取消订单");

    private String type; // 日志类型

    private String message; // 类型说明

    OrderLogTypeEnum(String type, String message) {
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
