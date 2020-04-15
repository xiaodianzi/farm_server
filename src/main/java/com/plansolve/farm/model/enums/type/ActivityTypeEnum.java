package com.plansolve.farm.model.enums.type;

/**
 * @Author: ANDREW
 * @Date: 2019/5/27
 * @Description: 优惠活动类型
 **/

public enum ActivityTypeEnum implements TypeEnum {

    //规则类型
    CREATE_ORDER_ACTIVITY("create_order_activity","下单优惠活动"),

    ACCEPT_ORDER_ACTIVITY("accept_order_activity","接单优惠活动"),

    SCORE_EXCHANGE_ACTIVITY("score_exchange_activity","兑换积分活动"),

    SCORE_EARN_ACTIVITY("score_earn_activity","获取积分活动"),

    LOTTERY_ACTIVITY("lottery_activity","抽奖优惠活动");

    private String type; // 积分类型

    private String message; // 类型描述

    ActivityTypeEnum(String type, String message) {
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
