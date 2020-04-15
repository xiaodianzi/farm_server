package com.plansolve.farm.model;

/**
 * @Author: Andrew
 * @Date: 2019/5/8
 * @Description:
 **/
public class EncacheConstant {

    /*注册用户统计常量*/
    public static final String REGISTER_TIMELINE = "register_timeline"; //注册用户

    /*农作物类型订单统计常量*/
    public static final String RICE_TIMELINE = "rice_timeline"; //水稻订单

    public static final String CORN_TIMELINE = "corn_timeline"; //玉米订单

    public static final String SORGHUM_TIMELINE = "sorghum_timeline"; //高粱订单

    public static final String WHEAT_TIMELINE = "wheat_timeline"; //小麦订单

    public static final String SOYBEAN_TIMELINE = "soybean_timeline"; //大豆订单

    public static final String MUNGBEAN_TIMELINE = "mungbean_timeline"; //绿豆订单

    public static final String POTATO_TIMELINE = "potato_timeline"; //土豆订单

    /*农机类型订单统计常量*/
    public static final String PLOUGH_TIMELINE = "plough_timeline"; //耕整地机订单

    public static final String SEED_TIMELINE = "seed_timeline"; //播种订单

    public static final String FERTILIZATION_TIMELINE = "fertilization_timeline"; //施肥订单

    public static final String PROTECTION_TIMELINE = "protection_timeline"; //植保订单

    public static final String HARVEST_TIMELINE = "harvest_timeline"; //收获订单

    /*作业类型订单统计常量*/
    public static final String INDEPENDENT_WORK = "independent_work"; //独立作业

    public static final String COLLABORATIVE_WORK = "collaborative_work"; //协同作业

    /*订单状态统计常量*/
    public static final String WAITING_ORDER = "waiting_order"; //待接单

    public static final String CONFIRMING_ORDER = "confirming_order"; //待确认

    public static final String WORKING_ORDER = "working_order"; //作业中

    public static final String PAYED_ORDER = "payed_order"; //已支付

    public static final String FINISH_ORDER = "finish_order"; //已完成

    public static final String OVERDUE_ORDER = "overdue_order"; //已过期

}
