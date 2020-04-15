package com.plansolve.farm.model.enums.type;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 积分任务类型
 **/

public enum ScoreTypeEnum implements TypeEnum {

    //规则类型
    AUTH_TYPE("auth","完善信息"),

    SIGN_IN_TYPE("sign_in","每日签到"),

    SHARE_APP_TYPE("share_app","APP分享"),

    CREATE_ORDER_TYPE("create_order","发布订单"),

    ACCEPT_ORDER_TYPE("accept_order","接单作业"),

    DIAGNOSE_DISEASE_TYPE("diagnose_disease","农作物病虫害诊断"),

    // 生效周期
    VALID_ONCE("valid_once","一次性"),

    VALID_DATE("valid_date","日时效"),

    VALID_WEEK("valid_week","周时效"),

    VALID_MONTH("valid_month","月时效"),

    VALID_YEAR("valid_year","年时效"),

    // 积分变更类型
    SIGN_IN_SCORE_PLUS("sign_in_score_plus", "签到赚积分"),

    AUTH_USER_SCORE_PLUS("auth_user_score_plus", "用户信息认证赚积分"),

    AUTH_FARMLAND_SCORE_PLUS("auth_farmland_score_plus", "土地信息认证赚积分"),

    AUTH_MACHINERY_SCORE_PLUS("auth_machinery_score_plus", "农机信息认证赚积分"),

    SHARE_APP_SCORE_PLUS("share_app_score_plus", "分享APP赚积分"),

    CREATE_ORDER_SCORE_PLUS("create_order_score_plus", "下单赚积分"),

    ACCEPT_ORDER_SCORE_PLUS("accept_order_score_plus", "接单赚积分"),

    DIAGNOSE_SCORE_PLUS("diagnose_score_plus", "病害诊断赚积分"),

    SCORE_EXCHANGE_REDUCE("score_exchange_reduce", "兑换商品花积分"),

    SCORE_EXCHANGE_INCREASE("score_exchange_increase", "兑换商品赚积分");

    private String type; // 积分类型

    private String message; // 类型描述

    ScoreTypeEnum(String type, String message) {
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
