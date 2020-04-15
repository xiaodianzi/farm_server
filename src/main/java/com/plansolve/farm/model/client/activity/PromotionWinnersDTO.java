package com.plansolve.farm.model.client.activity;

import lombok.Data;

/**
 * @Author: Andrew
 * @Date: 2019/5/28
 * @Description: 优惠活动报名人员封装类
 */
@Data
public class PromotionWinnersDTO {

    private Long idPromotionWinners; // 主键

    private String activityName; // 活动名称

    private String winnerName; // 中奖者姓名

    private String prize; //奖品

    private String createTime; // 中奖时间

}
