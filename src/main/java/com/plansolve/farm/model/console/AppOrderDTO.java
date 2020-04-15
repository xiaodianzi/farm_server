package com.plansolve.farm.model.console;

import com.plansolve.farm.util.HtmlUtil;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/4/9
 * @Description:
 **/
@Data
public class AppOrderDTO {

    private Long idUserOrder; // 主键（数据库自增）

    private String userOrderNo; // 订单编号

    private String userOrderState; // 订单状态

    private String createByDetail; // 下单人

    private String createTime; // 下单时间

    private String updateTime; // 更新时间

    private String target; // 下单给   1、平台    2、自身所在合作社   3、电话查找农机手（恰好是社长，则可认为下单给该合作社）

    private Float arce; // 面积（亩数）

    private String cropName; // 作物名称

    private String startTime; // 开始时间

    private String machineryType; // 所需农机类型

    private Integer machineryNum; // 所需农机数量

    private Integer period; // 工作周期（工作几天）

    private BigDecimal price; // 下单金额（单价）

    private BigDecimal amountPayable; // 应付金额

    private String guideName; // 引路人名称

    private String guideMobile; // 引路人联系方式

    private String assemblyAddress; // 集合地址

    private String receiveByDetail; // 接单人

    private Boolean isCooperative; // 作业方式

    private String reportedByDetail; // 完工提交人

    private String detail; // 备注

    private String button;

    public String getButton() {
        return HtmlUtil.getButtonHtml(false, "/plansolve/manger/app/user/appOrderDetail?idUserOrder=" + idUserOrder,
                "", "primary", "详情");
    }

}
