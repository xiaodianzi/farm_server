package com.plansolve.farm.model.client.user;

import com.plansolve.farm.model.client.CooperationInfoDTO;
import com.plansolve.farm.model.client.TeamDTO;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

@Data
public class UserDTO {

    @NotBlank(message = "用户名不能为空")
    private String nickname; // 用户昵称

    @NotBlank(message = "手机号码不能为空")
    @Size(min = 11, max = 11, message = "请输入正确的手机号码")
    private String mobile; // 手机号码

    private String userState; // 用户状态

    private Boolean isFarmer; // 是否是种植户

    private Boolean isOperator; // 是否是农机手

    private String avatar; // 用户头像地址

    private String registId;  // 消息推送id

    private Integer identity; //  用户身份（0-普通用户、1-社员、2-队长、3-社长）

    private String cooperationName; //  合作社名称

    private CooperationInfoDTO cooperationInfoDTO; // 用户所加入的合作社

    private TeamDTO teamDTO; // 用户所加入小队

    private boolean isSignedToday;// 今日是否签到

    public Boolean getFarmer() {
        return isFarmer;
    }

    public void setFarmer(Boolean farmer) {
        isFarmer = farmer;
    }

    public Boolean getOperator() {
        return isOperator;
    }

    public void setOperator(Boolean operator) {
        isOperator = operator;
    }

    public boolean isSignedToday() {
        return isSignedToday;
    }

    public void setSignedToday(boolean signedToday) {
        isSignedToday = signedToday;
    }

}
