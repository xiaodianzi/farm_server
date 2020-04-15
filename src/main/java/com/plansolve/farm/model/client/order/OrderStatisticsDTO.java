package com.plansolve.farm.model.client.order;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Andrew
 * @Date: 2018/8/10
 * @Description: 合作社订单统计类
 */
public class OrderStatisticsDTO implements Serializable {

    private String userName;

    private String mobile;

    private String avatar; // 用户头像地址

    private String month; // 月份

    private Float area; // 作业面积（亩数）

    private Integer orderNum; // 参与订单数

    private BigDecimal income; //收入

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Float getArea() {
        return area;
    }

    public void setArea(Float area) {
        this.area = area;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "OrderStatisticsDTO{" +
                "userName='" + userName + '\'' +
                ", month='" + month + '\'' +
                ", area=" + area +
                ", orderNum=" + orderNum +
                ", income=" + income +
                '}';
    }

}
