package com.plansolve.farm.model.client;

import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2018/8/21
 * @Description: 订单作业统计表
 */
public class StatisticsOrderDTO implements Serializable {

    private String month; //月份

    private String operatorName; //姓名

    private String userOrderNo; //订单编号

    private String teamName; //小队名称

    private String machineType; //农机类型

    private String cropName; //作物名称

    private String address; //作业地址

    private Float farmlandAcreage; //作业面积

    private Integer period; //作业周期

    private String startTime; //作业时间

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getUserOrderNo() {
        return userOrderNo;
    }

    public void setUserOrderNo(String userOrderNo) {
        this.userOrderNo = userOrderNo;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getFarmlandAcreage() {
        return farmlandAcreage;
    }

    public void setFarmlandAcreage(Float farmlandAcreage) {
        this.farmlandAcreage = farmlandAcreage;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

}
