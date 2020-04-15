package com.plansolve.farm.model.enums.type;

/**
 * @Author: ANDREW
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum GrainMarketTypeEnum implements TypeEnum {
    RICE("rice","水稻"),
    CORN("corn","玉米"),
    SORGHUM("sorghum","高粱"),
    WHEAT("wheat","小麦"),
    SOYBEAN("soybean","大豆"),
    GRAINBUY("grainBuy","收购"),
    GRAINSALE("grainSale","出售"),
    WEEKVALID("weekValid","一周"),
    HALFMONTHVALID("halfMonthValid","半个月"),
    MONTHVALID("monthValid","一个月"),
    QUARTERVALID("quarterValid","三个月"),
    HALFYEARVALID("halfYearValid","六个月"),
    YEARVALID("yearValid","一年"),
    LONGTERMVALID("longTermValid","长期有效");

    private String type; // 账户状态

    private String message; // 相关状态详情

    GrainMarketTypeEnum(String type, String message) {
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
