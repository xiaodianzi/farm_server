package com.plansolve.farm.model.client;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Andrew
 * @Date: 2018/8/21
 * @Description: 农机手作业统计表
 */
public class StatisticsOperatorsDTO implements Serializable {

    private String operatorName; //姓名

    private String teamName; //小队名称

    private Integer finishedOrderNum; //结算单数

    private Float farmlandAcreage; //作业面积

    private BigDecimal income; //收入

    private String startTime; //作业时间

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getFinishedOrderNum() {
        return finishedOrderNum;
    }

    public void setFinishedOrderNum(Integer finishedOrderNum) {
        this.finishedOrderNum = finishedOrderNum;
    }

    public Float getFarmlandAcreage() {
        return farmlandAcreage;
    }

    public void setFarmlandAcreage(Float farmlandAcreage) {
        this.farmlandAcreage = farmlandAcreage;
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
