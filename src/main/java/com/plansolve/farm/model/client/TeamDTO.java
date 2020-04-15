package com.plansolve.farm.model.client;

import com.plansolve.farm.model.client.user.UserDTO;

import javax.validation.constraints.NotBlank;

/**
 * @Author: 高一平
 * @Date: 2018/6/8
 * @Description: 小队传输对象
 **/
public class TeamDTO {

    @NotBlank(message = "小队名称不能为空")
    private String teamName; // 小队名称

    private UserDTO captain; // 队长

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public UserDTO getCaptain() {
        return captain;
    }

    public void setCaptain(UserDTO captain) {
        this.captain = captain;
    }

    @Override
    public String toString() {
        return "TeamDTO{" +
                "teamName='" + teamName + '\'' +
                ", captain=" + captain +
                '}';
    }
}
