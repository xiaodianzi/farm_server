package com.plansolve.farm.model.client.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.model.client.CooperationDTO;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.util.Date2LongSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/20
 * @Description:
 **/
public class BidOrderOperatorDTO {

    @NotBlank(message = "订单单号不可为空")
    private String bidOrderNo; // 竞价订单单号

    private UserDTO user; // 竞价订单申请人

    @NotBlank(message = "申请人姓名不可为空")
    private String username; // 申请人姓名

    @NotBlank(message = "申请人手机号码不可为空")
    private String userMobile; // 申请人手机号码

    @NotBlank(message = "申请人位置不可为空")
    private String address; // 申请人位置

    @NotNull(message = "申请人位置（经度）不能为空")
    private Double latitude; // 申请人位置（经度）

    @NotNull(message = "申请人位置（纬度）不能为空")
    private Double longitude; // 申请人位置（纬度）

    private String operatorState; // 申请人状态

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date startTime; // 订单开始时间

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date endTime; // 订单结束时间

    @NotBlank(message = "农机类型不能为空")
    private String machineryType; // 农机类型

    @NotNull(message = "农机数量不能为空")
    private Integer machineryNum; // 所需农机数量

    @NotNull(message = "农机作业能力不能为空")
    private Float machineryAbility; // 农机作业能力

    @NotNull(message = "可作业面积不能为空")
    private Float acreage; // 可作业面积

    @NotNull(message = "竞价价钱不能为空")
    private BigDecimal price; // 竞价价钱

    private UserDTO invited; // 邀请人

    private CooperationDTO cooperation; // 合作社

    private Boolean isCooperative; // 是否协同作业

    public String getBidOrderNo() { return bidOrderNo; }

    public void setBidOrderNo(String bidOrderNo) { this.bidOrderNo = bidOrderNo; }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getOperatorState() {
        return operatorState;
    }

    public void setOperatorState(String operatorState) {
        this.operatorState = operatorState;
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

    public Float getAcreage() {
        return acreage;
    }

    public void setAcreage(Float acreage) {
        this.acreage = acreage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public UserDTO getInvited() {
        return invited;
    }

    public void setInvited(UserDTO invited) {
        this.invited = invited;
    }

    public CooperationDTO getCooperation() {
        return cooperation;
    }

    public void setCooperation(CooperationDTO cooperation) {
        this.cooperation = cooperation;
    }

    public Boolean getCooperative() {
        return isCooperative;
    }

    public void setCooperative(Boolean cooperative) {
        isCooperative = cooperative;
    }

    @Override
    public String toString() {
        return "BidOrderOperatorDTO{" +
                "bidOrderNo=" + bidOrderNo +
                ", user=" + user +
                ", username='" + username + '\'' +
                ", userMobile='" + userMobile + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", operatorState='" + operatorState + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", machineryType='" + machineryType + '\'' +
                ", machineryNum=" + machineryNum +
                ", machineryAbility=" + machineryAbility +
                ", acreage=" + acreage +
                ", price=" + price +
                ", invited=" + invited +
                ", cooperation=" + cooperation +
                ", isCooperative=" + isCooperative +
                '}';
    }
}
