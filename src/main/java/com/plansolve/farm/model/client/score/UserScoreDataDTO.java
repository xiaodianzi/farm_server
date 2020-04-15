package com.plansolve.farm.model.client.score;

import lombok.Data;

import java.util.List;

/**
 * @Author: andrew
 * @Date: 2018/6/1
 * @Description:
 **/
@Data
public class UserScoreDataDTO {

    private List<String> notices;// 通知

    private Integer latestScore; // 用户最新积分

    private Integer todayScore; // 今日积分

    private List<ScoreIndexDTO> scoreIndexDTOS;//积分任务信息

}
