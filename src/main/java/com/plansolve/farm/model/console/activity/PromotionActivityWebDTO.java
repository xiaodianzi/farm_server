package com.plansolve.farm.model.console.activity;

import lombok.Data;

/**
 * @Author: andrew
 * @Date: 2018/6/1
 * @Description: 优惠活动DTO封装类
 **/
@Data
public class PromotionActivityWebDTO {

    private Long idPromotionPlayer;

    private Long idPromotionWinners;

    private String activityName; //活动名称

    private String playerName; //参与人员姓名

    private String mobile; //手机

    private Float totalAcreage; //累计土地面积

    private String prize; //奖品

    private String createTime; // 参与时间

}
