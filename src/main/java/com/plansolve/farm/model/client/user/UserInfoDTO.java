package com.plansolve.farm.model.client.user;

import com.plansolve.farm.model.database.Machinery;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

public class UserInfoDTO implements Serializable {

    @NotBlank(message = "用户名不能为空")
    private String nickname; // 用户昵称

    @NotBlank(message = "手机号码不能为空")
    @Size(min = 11, max = 11, message = "请输入正确的手机号码")
    private String mobile; // 手机号码

    private String userState; // 用户状态

    private Boolean isFarmer; // 是否是种植户

    private Boolean isOperator; // 是否是农机手

    private String avatar; // 用户头像地址

    private List<FarmlandDTO> farmlands; //土地信息

    private List<Machinery> machinerys; //农机信息

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<FarmlandDTO> getFarmlands() {
        return farmlands;
    }

    public void setFarmlands(List<FarmlandDTO> farmlands) {
        this.farmlands = farmlands;
    }

    public List<Machinery> getMachinerys() {
        return machinerys;
    }

    public void setMachinerys(List<Machinery> machinerys) {
        this.machinerys = machinerys;
    }

    @Override
    public String toString() {
        return "UserInfoDTO{" +
                "nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", userState='" + userState + '\'' +
                ", isFarmer=" + isFarmer +
                ", isOperator=" + isOperator +
                ", avatar='" + avatar + '\'' +
                ", farmlands=" + farmlands +
                ", machinerys=" + machinerys +
                '}';
    }
}
