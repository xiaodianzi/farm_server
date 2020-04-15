package com.plansolve.farm.model.client.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.util.Date2LongSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/8/14
 * @Description:
 **/
public class BidOrderDTO implements Serializable {

    private String bidOrderNo; // 竞价订单编号

    private String bidOrderState; // 竞价订单状态

    private UserDTO createBy; // 下单人

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime; // 下单时间

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date startTime; // 作业开始时间

    private String farmlandAddress; // 农田地址

    private Float arceage; // 农田面积

    private String cropName; // 农作物种类

    private String machineryType; // 农机类型

    private Integer machineryNum; // 农机数量

    private Integer period; // 作业周期

    private String detail; // 详情

    public String getBidOrderNo() {
        return bidOrderNo;
    }

    public void setBidOrderNo(String bidOrderNo) {
        this.bidOrderNo = bidOrderNo;
    }

    public String getBidOrderState() {
        return bidOrderState;
    }

    public void setBidOrderState(String bidOrderState) {
        this.bidOrderState = bidOrderState;
    }

    public UserDTO getCreateBy() {
        return createBy;
    }

    public void setCreateBy(UserDTO createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getFarmlandAddress() {
        return farmlandAddress;
    }

    public void setFarmlandAddress(String farmlandAddress) {
        this.farmlandAddress = farmlandAddress;
    }

    public Float getArceage() {
        return arceage;
    }

    public void setArceage(Float arceage) {
        this.arceage = arceage;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getMachineryType() {
        return machineryType;
    }

    public void setMachineryType(String machineryType) {
        this.machineryType = machineryType;
    }

    public Integer getMachineryNum() {
        return machineryNum;
    }

    public void setMachineryNum(Integer machineryNum) {
        this.machineryNum = machineryNum;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "BidOrderDTO{" +
                "bidOrderNo='" + bidOrderNo + '\'' +
                ", bidOrderState='" + bidOrderState + '\'' +
                ", createBy=" + createBy +
                ", createTime=" + createTime +
                ", startTime=" + startTime +
                ", farmlandAddress='" + farmlandAddress + '\'' +
                ", arceage=" + arceage +
                ", cropName='" + cropName + '\'' +
                ", machineryType='" + machineryType + '\'' +
                ", machineryNum=" + machineryNum +
                ", period=" + period +
                ", detail='" + detail + '\'' +
                '}';
    }
}
