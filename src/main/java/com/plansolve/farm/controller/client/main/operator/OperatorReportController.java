package com.plansolve.farm.controller.client.main.operator;

import com.google.gson.JsonObject;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.order.OrderDTO;
import com.plansolve.farm.model.client.order.OrderSimpleReportDTO;
import com.plansolve.farm.model.client.order.OrderOperatorDTO;
import com.plansolve.farm.model.client.order.ReportDTO;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.OperatorReportService;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.BigDecimalUtil;
import com.plansolve.farm.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/3
 * @Description:
 **/

@RestController
@RequestMapping(value = "/farm/operator/order")
public class OperatorReportController {

    @Autowired
    private OperatorReportService reportService;
    @Autowired
    private OrderService orderService;

    /**
     * 发送完工报告
     *
     * @param report
     * @return
     */
    @PostMapping(value = "/sentReport")
    public Result sentReport(@Valid ReportDTO report) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        reportService.sentReport(user, report);
        return ResultUtil.success(report);
    }

    /**
     * 社长或小队长发送完工报告并确认完工
     *
     * @return
     */
    @PostMapping(value = "/endReport")
    public Result endReport(String orderNo) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        reportService.endReport(user, orderNo);
        UserOrder userOrder = orderService.getUserOrder(orderNo);
        OrderDTO orderDTO = orderService.loadDTO(userOrder);
        return ResultUtil.success(orderDTO);
    }

    /**
     * 根据完工报告，统计索要金额总数
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/getDemandPriceAmount")
    public Result getDemandPriceAmount(String orderNo) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);

        OrderSimpleReportDTO dto = new OrderSimpleReportDTO();
        List<OrderOperatorDTO> operators = reportService.getSimpleOperatorDetails(orderNo, user);
        BigDecimal demandPriceAmount = new BigDecimal(0);
        Integer operatorNum = 1;
        Integer reportedNum = 0;
        if (operators != null && operators.size() > 0) {
            operatorNum = operators.size();
            for (OrderOperatorDTO operator : operators) {
                if (operator.getIsReported()) {
                    reportedNum = reportedNum + 1;
                }
                if (operator.getDemandPrice() != null
                        && BigDecimalUtil.moreThan(operator.getDemandPrice(), new BigDecimal(0))) {
                    demandPriceAmount = demandPriceAmount.add(operator.getDemandPrice());
                }
            }
        }
        dto.setDemandPriceAmount(demandPriceAmount);
        dto.setOperatorNum(String.valueOf(operatorNum));
        dto.setReportedOperatorNum(String.valueOf(reportedNum));
        dto.setOperators(operators);
        return ResultUtil.success(dto);
    }

    /**
     * 获取指定工作人员完工报告
     *
     * @param orderNo 订单号
     * @param mobile  该工作人员手机号码
     * @return
     */
    @PostMapping(value = "/getOperatorReport")
    public Result getOperatorReport(String orderNo, String mobile) {
        ReportDTO operatorReport = reportService.getOperatorReport(orderNo, mobile);
        return ResultUtil.success(operatorReport);
    }

    /**
     * 获取完工详情
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/getReportDetail")
    public Result getReportDetail(String orderNo) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        JsonObject reportDetail = reportService.getOperatorReportDetail(orderNo, user);
        return ResultUtil.success(reportDetail.toString());
    }

}
