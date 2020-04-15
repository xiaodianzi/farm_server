package com.plansolve.farm.model;

/**
 * @Author: Andrew
 * @Date: 2019/5/23
 * @Description:
 **/
public class PromotionActivityConstant {

    public static final Boolean ACTIVITY_LOTTERY_SWITCH = false; //优惠活动开关标志

    public static final Boolean SCORE_SWITCH = false; //积分活动开关标志

    public static final String SCORE_VALID_TIME = "2019-08-31 23:59:59"; //积分活动截止时间

    public static final String LOTTERY_START_TIME = "2019-06-01 00:00:00"; //抽奖活动截止日期

    public static final String LOTTERY_END_TIME = "2019-06-15 23:59:59"; //抽奖活动截止日期

    public static final String FARMLAND_ACREAGE_LIMIT = "1000"; //优惠季活动土地亩数限制

    public static final Integer INITIAL_PLAYER_AMOUNT = 0;

    public static final String DRONE_ACTIVITY_NAME = "超值优惠季"; //无人机优惠季活动备注信息

    public static final String LOTTERY_ACTIVITY_NAME = "幸运大抽奖";

    public static final String DRONE_ACTIVITY_REMARK = "超值优惠季，报名成功。"; //无人机优惠季活动备注信息

    public static final Integer LOTTERY_WINNER_NUMBER = 2;

    public static final String LOTTERY_ACTIVITY_REMARK = "幸运大抽奖，报名成功。"; //幸运大抽奖活动

    public static final String LOTTERY_WINNER_PRIZE = "现金大奖"; //幸运大抽奖活动

    public static final String LOTTERY_WINNER_REMARK = "幸运大抽奖，锦鲤就是您。"; //幸运大抽奖活动

    /*月有效积分次数全局常量*/
    public static final Integer SIGN_IN_MONTH_VALID_TIMES = 15; //签到月上限次数为15次，即15分

    public static final Integer SHARE_APP_MONTH_VALID_TIMES = 10; //有效分享月上限次数为10次，即10分

    public static final Integer CREATE_ORDER_MONTH_VALID_TIMES = 2; //有效下单月上限次数为2次

    public static final Integer DIAGNOSE_VALID_TIMES = 5; //病虫害诊断上限次数为5次

}
