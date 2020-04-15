package com.plansolve.farm.model.client.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/8/10
 * @Description: 合作社订单统计类
 */
public class StatisticsDTO implements Serializable {

    private List<OrderDTO> orderDTOS; //我的统计列表

    private List<OrderStatisticsDTO> orderStatisticsDTOS; //合作社统计列表

    private BigDecimal amount; //总金额

    private float totalArea; //总亩数

    private Integer orderNum; //总单数

    public List<OrderDTO> getOrderDTOS() {
        return orderDTOS;
    }

    public void setOrderDTOS(List<OrderDTO> orderDTOS) {
        this.orderDTOS = orderDTOS;
    }

    public List<OrderStatisticsDTO> getOrderStatisticsDTOS() {
        return orderStatisticsDTOS;
    }

    public void setOrderStatisticsDTOS(List<OrderStatisticsDTO> orderStatisticsDTOS) {
        this.orderStatisticsDTOS = orderStatisticsDTOS;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public float getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(float totalArea) {
        this.totalArea = totalArea;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "StatisticsDTO{" +
                "orderDTOS=" + orderDTOS +
                ", orderStatisticsDTOS=" + orderStatisticsDTOS +
                ", amount=" + amount +
                ", totalArea=" + totalArea +
                ", orderNum=" + orderNum +
                '}';
    }

}
