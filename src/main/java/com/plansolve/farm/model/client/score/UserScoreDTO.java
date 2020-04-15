package com.plansolve.farm.model.client.score;

import lombok.Data;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2018/3/22
 * @Description: 积分任务返回数据结果
 **/
@Data
public class UserScoreDTO implements Serializable {

    private Integer latestScore;//最新积分

    private Integer todayScore;//今日积分

//    private List<ScoreRuleDTO> scoreDTOList;//积分规则数据

    private ScoreRuleDTO sign;//签到积分数据

    private ScoreRuleDTO auth;//认证积分数据

    private ScoreRuleDTO shareApp;//分享app积分数据

    private ScoreRuleDTO creatOrder;//下单积分数据

    private ScoreRuleDTO acceptOrder;//接单积分数据

    private ScoreRuleDTO diagnose;//诊断积分数据

}
