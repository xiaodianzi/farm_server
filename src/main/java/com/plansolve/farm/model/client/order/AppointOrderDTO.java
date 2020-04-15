package com.plansolve.farm.model.client.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.util.Date2LongSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/8/2
 * @Description:
 **/
public class AppointOrderDTO {

    private String orderNo; // 订单号

    private String orderState; // 订单状态

    private UserDTO createBy; // 下单人

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime; // 下单时间

    @JsonSerialize(using = Date2LongSerializer.class)
    @NotNull(message = "作业开始时间不能为空")
    private Date operateCreateTime; // 作业开始时间

    @JsonSerialize(using = Date2LongSerializer.class)
    @NotNull(message = "作业结束时间不能为空")
    private Date operateEndTime; // 作业结束时间

    @NotBlank(message = "农机类型名称不能为空")
    private String machineryType; // 农机类型

    @NotNull(message = "农机数量不能为空")
    private Integer machineryNum; // 农机数量

    @NotNull(message = "作业能力不能为空")
    private Float machineryAbility; // 作业能力

    @NotNull(message = "所在位置（经度）不能为空")
    private Double latitude; // 位置（经度）

    @NotNull(message = "所在位置（纬度）不能为空")
    private Double longitude; // 位置（纬度）

    @NotBlank(message = "所在位置地址详情不能为空")
    private String address; // 地址详情

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
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

    public Date getOperateCreateTime() { return operateCreateTime; }

    public void setOperateCreateTime(Date operateCreateTime) { this.operateCreateTime = operateCreateTime; }

    public Date getOperateEndTime() { return operateEndTime; }

    public void setOperateEndTime(Date operateEndTime) { this.operateEndTime = operateEndTime; }

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

    public Float getMachineryAbility() {
        return machineryAbility;
    }

    public void setMachineryAbility(Float machineryAbility) {
        this.machineryAbility = machineryAbility;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
