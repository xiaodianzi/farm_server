package com.plansolve.farm.model.client.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.util.Date2LongSerializer;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/8/20
 * @Description:
 **/
public class OrderChangeLogDTO {

    @NotBlank(message = "订单编号不能为空")
    private String orderNo; // 订单编号

    private String changeType; // 变更类型

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date changeTime; // 变更时间

    private String changeBy; // 变更人

    private String detail; // 变更详情

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }

    public String getChangeBy() {
        return changeBy;
    }

    public void setChangeBy(String changeBy) {
        this.changeBy = changeBy;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "OrderChangeLogDTO{" +
                "orderNo='" + orderNo + '\'' +
                ", changeType='" + changeType + '\'' +
                ", changeTime=" + changeTime +
                ", changeBy='" + changeBy + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
