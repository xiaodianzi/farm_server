package com.plansolve.farm.model.client.activity;

import com.plansolve.farm.model.client.user.UserDTO;
import lombok.Data;
import java.util.List;

/**
 * @Author: andrew
 * @Date: 2018/6/1
 * @Description:
 **/
@Data
public class PromotionActivityAppDTO {

    private Long idPromotionActivity;

    private String pictureUrl; // 活动图片

    private String activityName; // 活动名称

    private String description; // 活动描述（对规则进行简单描述，使客户端用户快速理解该规则）

    private Float totalAcreage; // 累计土地面积

    private Integer playerAmount; // 最新的参与人数

    private List<UserDTO> players; //参与人员姓名集合

    private String contacts; // 联系方式

    private String startTime; // 开始时间

    private String endTime; // 结束时间

    private Long deadline; // 截止时间毫秒数

    private String addressDetail; //活动地点

    private Boolean joined; // 是否已参加

}
