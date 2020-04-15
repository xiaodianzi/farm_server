package com.plansolve.farm.model.client.score;

import lombok.Data;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2018/3/22
 * @Description: 积分任务返回数据结果
 **/
@Data
public class ScoreRuleDTO implements Serializable {

    private Integer taskScore;//任务积分

    private Integer doneTimes;//已完成次数

}
