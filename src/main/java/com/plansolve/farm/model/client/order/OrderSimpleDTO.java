package com.plansolve.farm.model.client.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.util.Date2LongSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Andrew
 * @Date: 2018/8/10
 * @Description: 合作社订单统计类
 */
public class OrderSimpleDTO implements Serializable {

    private String userOrderNo; // 订单编号

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date startTime; // 订单开始时间

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date endTime; // 订单结束时间

    private Float arce; // 作业面积（亩数）

    private AddressDTO farmlandAddress; // 地块

    private BigDecimal price; // 单价

    private String createManNickname;

    private String createManMobile;

    private String createManAvatar;

    public String getUserOrderNo() {
        return userOrderNo;
    }

    public void setUserOrderNo(String userOrderNo) {
        this.userOrderNo = userOrderNo;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Float getArce() {
        return arce;
    }

    public void setArce(Float arce) {
        this.arce = arce;
    }

    public AddressDTO getFarmlandAddress() { return farmlandAddress; }

    public void setFarmlandAddress(AddressDTO farmlandAddress) { this.farmlandAddress = farmlandAddress; }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCreateManNickname() {
        return createManNickname;
    }

    public void setCreateManNickname(String createManNickname) {
        this.createManNickname = createManNickname;
    }

    public String getCreateManMobile() {
        return createManMobile;
    }

    public void setCreateManMobile(String createManMobile) {
        this.createManMobile = createManMobile;
    }

    public String getCreateManAvatar() {
        return createManAvatar;
    }

    public void setCreateManAvatar(String createManAvatar) {
        this.createManAvatar = createManAvatar;
    }

    @Override
    public String toString() {
        return "OrderSimpleDTO{" +
                "userOrderNo='" + userOrderNo + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", arce=" + arce +
                ", farmlandAddress=" + farmlandAddress +
                ", price=" + price +
                ", createManNickname='" + createManNickname + '\'' +
                ", createManMobile='" + createManMobile + '\'' +
                ", createManAvatar='" + createManAvatar + '\'' +
                '}';
    }
}
