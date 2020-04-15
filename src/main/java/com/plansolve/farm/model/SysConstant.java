package com.plansolve.farm.model;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/
public class SysConstant {

    public static final String MOBILE = "MOBILE"; // 手机号码
    public static final String CAPTCHA = "CAPTCHA"; // 验证码
    public static final String EXPIRE_TIME = "EXPIRE_TIME"; // 过期时间
    public static final String VALIDATION_MOBILE = "VALIDATION_MOBILE"; // 验证码验证成功手机号码
    public static final String VALIDATION_EXPIRE_TIME = "VALIDATION_EXPIRE_TIME"; // 验证码验证成功过期时间
    public static final String CURRENT_USER = "CURRENT_USER"; // 当前用户
    public static final String USER_TOKEN = "USER_TOKEN"; // 当前用户登录标识
    public static final String WX_MP_OAUTH2_ACCESS_TOKEN = "WX_MP_OAUTH2_ACCESS_TOKEN"; // 当前微信用户TOKEN标识

    /******************************************相关文件保存地址******************************************/
    public static final String USER_AVATAR = "user/avatar/"; // 用户头像
    public static final String FARMLAND_PICTURE = "farmland/"; // 农田地貌图片保存地址
    public static final String MACHINERY_PICTURE = "machinery/"; // 农田地貌图片保存地址
    public static final String COOPERATION_PICTURE = "cooperation/"; //合作社图片保存地址

    public static final String NEWS_ICON_PICTURE = "news/icon/"; // 新闻标题图片保存地址
    public static final String NEWS_DETAIL_PICTURE = "news/detail/"; // 新闻详情图片保存地址
    public static final String NEWS_DETAIL_BANNER_PICTURE = "news/banner/"; // 新闻详情banner图片保存地址
    public static final String NEWS_DETAIL_URL = "${projectUrl}/wx/mp/news/detail?idNews="; // 新闻标题图片保存地址
    public static final String CEREALS_REAL_TIME_PRICES_PICTURE = "cereals/realTimePrices/"; // 实时粮价图片保存地址
    public static final String CEREALS_REAL_TIME_PRICES_DETAIL_PICTURE = "cereals/realTimePrices/detail/"; // 实时粮价图片保存地址
    /******************************************相关文件保存地址******************************************/

    /******************************************农机相关设置******************************************/
    public static final String MACHINERY_STRING_SEPARATOR = "/";
    public static final String MACHINERY_DRAGGING = "machinery_dragging";
    public static final String MACHINERY_TYPE = "machinery_type";
    /******************************************农机相关设置******************************************/

    /******************************************土地相关设置******************************************/
    public static final String FARM_STRING_SEPARATOR = "/";
    public static final String CROP = "crop";
    /******************************************土地相关设置******************************************/

    /******************************************管理员相关设置******************************************/
    public static final String ADMIN_STRING_SEPARATOR = "/";
    public static final String ADMIN_TAG = "ADMIN";
    /******************************************管理员相关设置******************************************/

    /******************************************农作物图片保存地址******************************************/
    public static final String OPENCV_PHOTES_IMAGE = "opencv/photos/";//农作物原图
    public static final String OPENCV_MODEL_IMAGE = "opencv/models/";//待匹配模板图片
    public static final String OPENCV_DNN_IMAGE = "opencv/dnn/";//特征点图片
    /******************************************农作物图片保存地址******************************************/

    public static final String OPENCV_RICE_TYPE = "rice";//水稻
    public static final String OPENCV_CORN_TYPE = "corn";//玉米

    /******************************************积分缓存相关设置******************************************/
    public static final String SCORE_CACHE_NAME = "scoreCache";
    public static final Integer INITIAL_USER_SCORE = 0;
    public static final Integer INITIAL_DONE_TIMES = 0;
    //完成后所得的有效积分
    public static final Integer SCORE_ONCE_TIME = 1;
    public static final Integer AUTH_USER_VALID_SCORE = 4;
    public static final Integer AUTH_FARMLAND_VALID_SCORE = 3;
    public static final Integer AUTH_MACHINERY_VALID_SCORE = 3;
    public static final Integer SIGN_IN_VALID_SCORE = 1;
    public static final Integer SHARE_APP_VALID_SCORE = 1;
    public static final Integer CREATE_ORDER_OFFLINE_SCORE = 10;
    public static final Integer CREATE_ORDER_WXPAYED_SCORE = 20;
    public static final Integer ACCEPT_ORDER_VALID_SCORE = 5;
    public static final Integer DIAGNOSE_VALID_SCORE = 1;
    //积分任务时效性
    public static final Integer AUTH_VALID_TIMES = 3;//全部认证次数3
    public static final Integer SIGN_IN_VALID_TIMES = 1;//日上限1次
    public static final Integer SHARE_VALID_TIMES = 100;//次数不限
    public static final Integer CREATE_ORDER_VALID_TIMES = 2;//月上限2次
    public static final Integer ACCEPT_ORDER_VALID_TIMES = 100;//次数不限
    public static final Integer DIAGNOSE_VALID_TIMES = 5;//上限5次
    //积分任务名称
    public static final String SIGN_IN_RULE_NAME = "签到";
    public static final String AUTH_RULE_NAME = "认证";
    public static final String SHARE_APP_RULE_NAME = "分享";
    public static final String CREATE_ORDER_RULE_NAME = "农机需求订单";
    public static final String ACCEPT_ORDER_RULE_NAME = "接单作业订单";
    public static final String DIAGNOSE_RULE_NAME = "病虫害诊断";
    //已完成积分缓存名称后缀
    public static final String SIGN_IN_DONE_TIME_SUFFIX = "_DONE_SIGN_IN_TIMES";
    public static final String AUTH_DONE_TIME_SUFFIX = "_DONE_AUTH_TIMES";
    public static final String SHARE_APP_DONE_TIME_SUFFIX = "_DONE_SHARE_APP_TIMES";
    public static final String CREATE_ORDER_DONE_TIME_SUFFIX = "_DONE_CREATE_ORDER_TIMES";
    public static final String ACCEPT_ORDER_DONE_TIME_SUFFIX = "_DONE_ACCEPT_ORDER_TIMES";
    public static final String DIAGNOSE_DONE_TIME_SUFFIX = "_DONE_DIAGNOSE_TIMES";
    //今日积分缓存名称后缀
    public static final String TODAY_SCORE_SUFFIX = "_TODAY_SCORE";
    //积分规则缓存名称后缀
    public static final String SIGN_IN_CACHE_KEY_SUFFIX = "_SIGN_IN";
    public static final String AUTH_CACHE_KEY_SUFFIX = "_AUTH";
    public static final String SHARE_APP_CACHE_KEY_SUFFIX = "_SHARE_APP";
    public static final String CREATE_ORDER_CACHE_KEY_SUFFIX = "_CREATE_ORDER";
    public static final String ACCEPT_ORDER_CACHE_KEY_SUFFIX = "_ACCEPT_ORDER";
    public static final String DIAGNOSE_CACHE_KEY_SUFFIX = "_DIAGNOSE";
    //积分任务url
    public static final String SIGN_IN_URL = "/plansolve/farm/score/manager/signPointTask";
    public static final String AUTH_URL = "/plansolve/farm/score/manager/authPointTask";
    public static final String SHARE_APP_URL = "/plansolve/farm/score/manager/shareAppPointTask";
    public static final String CREATE_ORDER_URL = "/plansolve/farm/score/manager/createOrderPointTask";
    public static final String ACCEPT_ORDER_URL = "/plansolve/farm/score/manager/acceptOrderPointTask";
    public static final String DIAGNOSE_URL = "/plansolve/farm/score/manager/diagnosePointTask";
    public static final String VOID_URL = "javascript:void(0)";
    //积分标签提示符
    public static final String SIGN_IN_LABLE = "signIn";
    public static final String AUTH_URL_LABLE = "auth";
    public static final String SHARE_APP_LABLE = "shareApp";
    public static final String CREATE_ORDER_LABLE = "createOrder";
    public static final String ACCEPT_ORDER_LABLE = "acceptOrder";
    public static final String DIAGNOSE_URL_LABLE = "diagnose";
    //认证信息类型
    public static final String AUTH_USER_TYPE = "auth_user";
    public static final String AUTH_FARMLAND_TYPE = "auth_farmland";
    public static final String AUTH_MACHINERY_TYPE = "auth_machinery";
    public static final String AUTH_ALL_TYPE = "auth_all";

    /******************************************农学院相关设置******************************************/
    //消息发布的有效时间
    public static final String GRAIN_WEEK_VALID = "weekValid";
    public static final String GRAIN_HALFMONTH_VALID = "halfMonthValid";
    public static final String GRAIN_MONTH_VALID = "monthValid";
    public static final String GRAIN_QUARTER_VALID = "quarterValid";
    public static final String GRAIN_HALFYEAR_VALID = "halfYearValid";
    public static final String GRAIN_YEAR_VALID = "yearValid";
    public static final String GRAIN_LONG_TERM_VALID = "longTermValid";
    //粮食购销的信息类型
    public static final String GRAIN_BUY_INFORMATION_TYPE = "grainBuy";
    public static final String GRAIN_SALE_INFORMATION_TYPE = "grainSale";
    public static final String GRAIN_ALL_INFORMATION_TYPE = "grainAll";
    //农作物种类
    public static final String RICE_TYPE = "rice";//水稻
    public static final String CORN_TYPE = "corn";//玉米
    public static final String SORGHUM_TYPE = "sorghum";//高粱
    public static final String WHEAT_TYPE = "wheat";//小麦
    public static final String SOYBEAN_TYPE = "soybean";//大豆
    //病虫害种类
    public static final String RICE_ILLNESS_TYPE = "sd_illness";//水稻病害
    public static final String RICE_INSECT_TYPE = "sd_pest";//水稻虫害
    public static final String CORN_ILLNESS_TYPE = "ym_illness";//玉米病害
    public static final String CORN_INSECT_TYPE = "ym_pest";//玉米虫害
    public static final String SORGHUM_ILLNESS_TYPE = "gl_illness";//高粱病害
    public static final String SORGHUM_INSECT_TYPE = "gl_pest";//高粱虫害
    public static final String WHEAT_ILLNESS_TYPE = "xm_illness";//小麦病害
    public static final String WHEAT_INSECT_TYPE = "xm_pest";//小麦虫害
    public static final String SOYBEAN_ILLNESS_TYPE = "dd_illness";//大豆病害
    public static final String SOYBEAN_INSECT_TYPE = "dd_pest";//大豆虫害

}
