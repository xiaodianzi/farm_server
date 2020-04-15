package com.plansolve.farm.model.client.score;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2018/3/22
 * @Description: 积分任务返回数据结果
 **/
@Data
public class ScoreTaskDTO implements Serializable {

    private String ruleName; // 规则名称

    private Integer latestScore;//最新积分

    private Integer todayScore;//今日积分

    private Integer doneTimes;//已完成次数

    private Integer availableTimes;//上限次数

    private Boolean result;// 完成任务结果

    private String resultInfo;//结果描述

}
