package com.plansolve.farm.model.client;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Andrew
 * @Date: 2018/8/21
 * @Description: 合作社作业统计表
 */
public class StatisticsCooperationDTO implements Serializable {

    private String teamName; //小队名称

    private Integer workingOrderNum; //作业单数

    private Integer commitOrderNum; //提交单数

    private Integer finishedOrderNum; //结算单数

    private BigDecimal income; //收入

    private String startTime; //作业时间

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getWorkingOrderNum() {
        return workingOrderNum;
    }

    public void setWorkingOrderNum(Integer workingOrderNum) {
        this.workingOrderNum = workingOrderNum;
    }

    public Integer getCommitOrderNum() {
        return commitOrderNum;
    }

    public void setCommitOrderNum(Integer commitOrderNum) {
        this.commitOrderNum = commitOrderNum;
    }

    public Integer getFinishedOrderNum() {
        return finishedOrderNum;
    }

    public void setFinishedOrderNum(Integer finishedOrderNum) {
        this.finishedOrderNum = finishedOrderNum;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

}
