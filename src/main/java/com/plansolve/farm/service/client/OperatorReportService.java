package com.plansolve.farm.service.client;

import com.google.gson.JsonObject;
import com.plansolve.farm.model.client.order.OperatorDTO;
import com.plansolve.farm.model.client.order.OrderOperatorDTO;
import com.plansolve.farm.model.client.order.ReportDTO;
import com.plansolve.farm.model.database.order.CompletionReport;
import com.plansolve.farm.model.database.user.User;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/3
 * @Description:
 **/
public interface OperatorReportService {

    /**
     * 发送完工报告
     *
     * @param user
     * @param reportDTO
     * @return
     */
    public CompletionReport sentReport(User user, ReportDTO reportDTO);

    /**
     * 社长或小队长发送完工报告
     *
     * @param user
     * @param orderNo
     * @return
     */
    public void endReport(User user, String orderNo);

    /**
     * 获取工作人员完工报告
     *
     * @param orderNo
     * @param mobile
     * @return
     */
    public ReportDTO getOperatorReport(String orderNo, String mobile);

    /**
     * 获取该订单相关完工报告
     *
     * @param orderNo
     * @return
     */
    public List<CompletionReport> getOperatorReports(String orderNo);

    /**
     * 获取工作人员完工报告
     *
     * @param idUserOrder
     * @param idUser
     * @return
     */
    public CompletionReport getOperatorReport(Long idUserOrder, Long idUser);

    /**
     * 根据主键查询
     *
     * @param idCompletionReport
     * @return
     */
    public CompletionReport getOperatorReport(Long idCompletionReport);

    /**
     * 获取指定订单完工报告
     *
     * @param orderNo
     * @param user
     * @return
     */
    public List<ReportDTO> getOrderOperatorReports(String orderNo, User user);

    /**
     * 获取当前工作人员的完工情况
     * 社长则为全社的完工情况
     *
     * @param orderNo
     * @param user
     * @return
     */
    public JsonObject getOperatorReportDetail(String orderNo, User user);

    /**
     * 获取当前订单完工情况
     *
     * @param orderNo
     * @return
     */
    public List<OperatorDTO> getSimpleOperatorReport(String orderNo);

    /**
     * 获取订单工作人员简要
     *
     * @param orderNo
     * @return
     */
    public List<OrderOperatorDTO> getSimpleOperatorDetails(String orderNo, User user);

}
