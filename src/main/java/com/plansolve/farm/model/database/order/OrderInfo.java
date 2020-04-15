package com.plansolve.farm.model.database.order;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/4/25
 * @Description:
 **/
@Data
@Entity
public class OrderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idOrderInfo;

    private Long idUserOrder; // 订单

    private Integer idMachineryParentType;

    private Integer idMachineryType;

    private String orderState; // 订单状态

    private Long idUser; // 关联人

    private Integer identity; // 关联人身份 0-下单人，1-接单人，2-作业人

    private String mobile; // 作业人联系方式

    private String operatorState; // 作业状态

    private Boolean cooperativeOrder; // 作业方式

    private Long idAddress; // 订单地址

    private String province; // 省

    private String city; // 市

    private String county; // 区县

    private String town; // 乡镇

    private Double latitude; // 纬度

    private Double longitude; // 经度

    private String detail; // 详情

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime; // 下单时间

}
