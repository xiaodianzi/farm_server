package com.plansolve.farm.model.client.activity;

import lombok.Data;

/**
 * @Author: andrew
 * @Date: 2018/6/1
 * @Description:
 **/
@Data
public class PromotionActivityDTO {

    private Long idPromotionActivity;

    private String pictureUrl; // 活动图片

    private String activityName; // 活动名称

    private String description; // 活动描述（对规则进行简单描述，使客户端用户快速理解该规则）

    private String activityType; // 活动类型（同一类型下可以有多个不同的优惠活动）

    private Integer maxPlayers; // 人数限制

    private Float maxAcreage; // 土地面积限制

    private String startTime; // 开始时间

    private String endTime; // 结束时间

    private String contacts; // 联系方式

    private String addressDetail; //活动地点

    private Boolean isValid; // 该规则是否可用（APP页面只显示有效的活动）

    private String button;// 编辑按钮

    public PromotionActivityDTO() {
    }

    /**
     * 编辑：<a href="#editScoreForm" onclick="editScoreForm()" class="btn btn-xs btn-primary" data-toggle="modal">编辑</a>
     */
    public PromotionActivityDTO(Long idPromotionActivity, String pictureUrl, String activityName,
                                String description, String activityType, Integer maxPlayers, Float maxAcreage,
                                String startTime, String endTime, String contacts, String addressDetail, Boolean isValid) {
        this.idPromotionActivity = idPromotionActivity;
        this.pictureUrl = pictureUrl;
        this.activityName = activityName;
        this.description = description;
        this.activityType = activityType;
        this.maxPlayers = maxPlayers;
        this.maxAcreage = maxAcreage;
        this.startTime = startTime;
        this.endTime = endTime;
        this.contacts = contacts;
        this.addressDetail = addressDetail;
        this.isValid = isValid;
        this.button = "<a href=\"#editActivityForm\" onclick=\"editActivityForm('" + idPromotionActivity + "','" +pictureUrl + "','" + activityName + "','" + description + "','" + activityType + "','" + maxPlayers + "','" + maxAcreage + "','" + startTime + "','" +  endTime + "','" + contacts + "','"  + addressDetail + "','" + isValid + "')\" class=\"btn btn-xs btn-primary\" data-toggle=\"modal\">编辑</a>";
    }

}
