package com.plansolve.farm.model.client.score;

import lombok.Data;

/**
 * @Author: andrew
 * @Date: 2018/6/1
 * @Description:
 **/
@Data
public class ScoreIndexDTO {

    private Long idScoreRule;// id

    private String ruleName; // 规则名称

    private Integer increaseScore; // 规则积分

    private String memo;  // 规则描述

    private String taskLabel; //  任务标签

    private String ruleType;// 规则类型（注册、登录、分享等类型，同一规则类型可以有不同名称的规则，只需遍历控制有效规则）

    private String doneTask; //  已完成的任务

    private String availableTask; // 可完成的任务

    private Integer doneTimes;//已完成次数

    private Integer availableTimes;//上限次数

    private String doneLabel;//已完成标签

    private String rangeType; // 生效周期（按日生效、按周生效、按月生效、按年生效，一次性有效，永久有效）

    private Integer actNum; // 生效条件（相同动作，第几次生效，给予积分）

    private String validTimes; // 有效次数（前几次给予积分）

    private String scoreTaskUrl;// 跳转url

    private Boolean isValid;// 是否有效

    private String button;// 编辑按钮

    public ScoreIndexDTO() {
    }

    /**
     * 编辑：<a href="#editScoreForm" onclick="editScoreForm()" class="btn btn-xs btn-primary" data-toggle="modal">编辑</a>
     */
    public ScoreIndexDTO(Long idScoreRule, String ruleName, Integer increaseScore,
                        String memo, String taskLabel, String ruleType, String availableTask, String rangeType,
                        Integer actNum, String validTimes, Boolean isValid) {
        this.idScoreRule = idScoreRule;
        this.ruleName = ruleName;
        this.increaseScore = increaseScore;
        this.memo = memo;
        this.taskLabel = taskLabel;
        this.ruleType = ruleType;
        this.availableTask = availableTask;
        this.rangeType = rangeType;
        this.actNum = actNum;
        this.validTimes = validTimes;
        this.isValid = isValid;
        this.button = "<a href=\"#editScoreForm\" onclick=\"editScoreForm('" + ruleName + "','" + increaseScore + "','" + memo + "','" + taskLabel + "','" + ruleType + "','" + availableTask + "','" + rangeType + "','" + actNum + "','" + validTimes + "','" + isValid + "','" + idScoreRule + "')\" class=\"btn btn-xs btn-primary\" data-toggle=\"modal\">编辑</a>";
    }

}
