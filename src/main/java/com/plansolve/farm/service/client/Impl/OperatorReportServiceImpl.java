package com.plansolve.farm.service.client.Impl;

import com.google.gson.JsonObject;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.client.order.OrderOperatorDTO;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.client.order.OperatorDTO;
import com.plansolve.farm.model.client.order.ReportDTO;
import com.plansolve.farm.model.database.cooperation.Team;
import com.plansolve.farm.model.database.log.OrderChangeLog;
import com.plansolve.farm.model.database.order.CompletionReport;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.code.OperatorIdentityEnum;
import com.plansolve.farm.model.enums.state.OperatorStateEnum;
import com.plansolve.farm.model.enums.type.OrderLogTypeEnum;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.repository.log.OrderChangeLogRepository;
import com.plansolve.farm.repository.order.CompletionReportRepository;
import com.plansolve.farm.repository.order.OrderOperatorRepository;
import com.plansolve.farm.repository.order.UserOrderRepository;
import com.plansolve.farm.service.client.*;
import com.plansolve.farm.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/3
 * @Description:
 **/
@Service
public class OperatorReportServiceImpl implements OperatorReportService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderOperatorService operatorService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private CompletionReportRepository reportRepository;
    @Autowired
    private OrderChangeLogRepository logRepository;
    @Autowired
    private OrderOperatorRepository orderOperatorRepository;
    @Autowired
    private UserOrderRepository userOrderRepository;

    /**
     * 发送完工报告
     *
     * @param user
     * @param reportDTO
     * @return
     */
    @Override
    @Transactional
    public CompletionReport sentReport(User user, ReportDTO reportDTO) {
        UserOrder order = orderService.getUserOrder(reportDTO.getOrderNo());
        // 查询该工作人员
        OrderOperator orderOperator = operatorService.getOperator(user.getIdUser(), order.getIdUserOrder());
        if (orderOperator == null) {
            throw new NullParamException("[该用户不是该单工作人员]");
        } else {
            // 提交完工报告
            CompletionReport completionReport = reportRepository.findByIdUserOrderAndIdUser(order.getIdUserOrder(), user.getIdUser());
            if (completionReport == null) {
                completionReport = new CompletionReport();
            }
            BeanUtils.copyProperties(reportDTO, completionReport);
            completionReport.setIdUserOrder(order.getIdUserOrder());
            completionReport.setIdUser(user.getIdUser());
            completionReport = saveReport(completionReport, user);

            // 关联完工报告
            if (orderOperator.getIdCompletionReport() == null) {
                orderOperator.setOperatorState(OperatorStateEnum.FINISHED.getState());
                orderOperator.setIdCompletionReport(completionReport.getIdCompletionReport());
                orderOperatorRepository.save(orderOperator);
            }
            return completionReport;
        }
    }

    /**
     * 社长或小队长发送完工报告
     * 1、查询订单是否存在
     * 2、查询当前用户是否是该单接单人
     * 是：
     * 1、不是社长：提交报告完成订单
     * 2、是社长：校验订单工作人员是否完工报告提交完毕
     * 否：
     * 1、校验订单接单人是否是相关社长
     * 2、校验用户身份，是否是社长，是否是小队长
     * 3、校验订单工作人员是否完工报告提交完毕
     *
     * @param user
     * @param orderNo
     * @return
     */
    @Override
    @Transactional
    public void endReport(User user, String orderNo) {
        // 1、查询订单是否存在
        UserOrder order = orderService.getUserOrder(orderNo);
        // 2、查询当前用户是否是该单接单人
        Boolean isProprieter = cooperationService.proprieter(user);
        if (order.getReceiveBy().equals(user.getIdUser())) {
            finishedReport(user, order);
        } else {
            // 1、校验订单接单人是否是相关社长
            User proprieter = cooperationService.findProprieterByUser(user);
            if (proprieter != null) {
                if (order.getReceiveBy().equals(proprieter.getIdUser()) == false) {
                    throw new PermissionException("[非法操作！该单接单人不是该用户所在合作社社长]");
                } else {
                    Boolean isCaptain = cooperationService.captain(user);
                    // 2、校验用户身份，是否是社长，是否是小队长
                    if (isProprieter || isCaptain) {
                        finishedReport(user, order);
                    } else {
                        throw new PermissionException("[该用户既不是社长也不是队长，无权做此操作]");
                    }
                }
            } else {
                throw new PermissionException("[非法操作！该单接单人不是该用户所在合作社社长]");
            }
        }
    }

    /**
     * 获取工作人员完工报告
     *
     * @param orderNo
     * @param mobile
     * @return
     */
    @Override
    public ReportDTO getOperatorReport(String orderNo, String mobile) {
        UserOrder order = orderService.getUserOrder(orderNo);
        User user = userService.findByMobile(mobile);
        if (user != null) {
            OrderOperator operator = operatorService.getOperator(user.getIdUser(), order.getIdUserOrder());
            if (operator != null) {
                if (operator.getIdCompletionReport() != null && operator.getIdCompletionReport() > 0) {
                    CompletionReport report = reportRepository.findByIdCompletionReport(operator.getIdCompletionReport());
                    ReportDTO reportDTO = new ReportDTO();
                    BeanUtils.copyProperties(report, reportDTO);
                    reportDTO.setOrderNo(orderNo);
                    return reportDTO;
                } else {
                    return null;
                }
            } else {
                throw new NullParamException("[该用户不是该单工作人员]");
            }
        } else {
            throw new NullParamException("[该用户不存在]");
        }
    }

    /**
     * 获取该订单相关完工报告
     *
     * @param orderNo
     * @return
     */
    @Override
    public List<CompletionReport> getOperatorReports(String orderNo) {
        UserOrder order = orderService.getUserOrder(orderNo);
        List<CompletionReport> completionReports;
        if (order != null) {
            completionReports = reportRepository.findByIdUserOrder(order.getIdUserOrder());
            if (completionReports == null) {
                completionReports = new ArrayList<>();
            }
        } else {
            completionReports = new ArrayList<>();
        }
        return completionReports;
    }

    /**
     * 获取工作人员完工报告
     *
     * @param idUserOrder
     * @param idUser
     * @return
     */
    @Override
    public CompletionReport getOperatorReport(Long idUserOrder, Long idUser) {
        return reportRepository.findByIdUserOrderAndIdUser(idUserOrder, idUser);
    }

    /**
     * 根据主键查询
     *
     * @param idCompletionReport
     * @return
     */
    @Override
    public CompletionReport getOperatorReport(Long idCompletionReport) {
        return reportRepository.findByIdCompletionReport(idCompletionReport);
    }

    /**
     * 获取指定订单完工报告
     *
     * @param orderNo
     * @param user
     * @return
     */
    @Override
    public List<ReportDTO> getOrderOperatorReports(String orderNo, User user) {
        UserOrder userOrder = orderService.getUserOrder(orderNo);
        if (userOrder.getCreateBy().equals(user.getIdUser())
                || userOrder.getReceiveBy().equals(user.getIdUser())) {
            List<ReportDTO> reports = getOrderOperatorReports(userOrder);
            return reports;
        } else {
            OrderOperator operator = operatorService.getOperator(user.getIdUser(), userOrder.getIdUserOrder());
            if (operator != null) {
                User proprieter = userService.findUser(userOrder.getReceiveBy());
                if (proprieter.getIdCooperation().equals(user.getIdCooperation())) {
                    boolean captain = cooperationService.captain(user);
                    if (captain) {
                        if (userOrder.getReportedBy() != null && userOrder.getReportedBy() > 0 && userOrder.getReportedBy().equals(user.getIdUser())) {
                            List<ReportDTO> reports = getOrderOperatorReports(userOrder);
                            return reports;
                        } else {
                            CompletionReport completionReport = reportRepository.findByIdUserOrderAndIdUser(userOrder.getIdUserOrder(), user.getIdUser());
                            ReportDTO reportDTO = loadDTO(completionReport, orderNo);
                            return Arrays.asList(reportDTO);
                        }
                    } else {
                        CompletionReport completionReport = reportRepository.findByIdUserOrderAndIdUser(userOrder.getIdUserOrder(), user.getIdUser());
                        ReportDTO reportDTO = loadDTO(completionReport, orderNo);
                        return Arrays.asList(reportDTO);
                    }
                } else {
                    throw new PermissionException("[该用户不是该合作社社员]");
                }
            } else {
                throw new PermissionException("[该用户不是该单工作人员]");
            }
        }
    }

    /**
     * 封装传输对象
     *
     * @param report
     * @param orderNo
     * @return
     */
    private ReportDTO loadDTO(CompletionReport report, String orderNo) {
        ReportDTO reportDTO = new ReportDTO();
        BeanUtils.copyProperties(report, reportDTO);
        reportDTO.setOrderNo(orderNo);
        reportDTO.setUser(userService.findUser(report.getIdUser(), false));
        return reportDTO;
    }

    /**
     * 获取当前工作人员的完工情况
     * 社长则为全社的完工情况
     *
     * @param orderNo
     * @param user
     * @return
     */
    @Override
    public JsonObject getOperatorReportDetail(String orderNo, User user) {
        UserOrder order = orderService.getUserOrder(orderNo);
        // 当前用户若为社长，则返回全部工作人员总数据，若不是，返回当前工作人员数据
        boolean proprieter = cooperationService.proprieter(user);
        /*// 无论是不是社长，返回全部数据
        boolean proprieter = true;*/
        CompletionReport report = null;
        if (user.getIdUser().equals(order.getCreateBy()) || proprieter) {
            List<CompletionReport> reports = reportRepository.findByIdUserOrder(order.getIdUserOrder());
            if (reports != null && reports.size() > 0) {
                report = new CompletionReport();
                report.setAcre(0f);
                report.setMachineryNum(0);
                report.setDetail("各社员备注详情：");
                for (CompletionReport completionReport : reports) {
                    if (report.getTaskStartTime() == null || report.getTaskStartTime().after(completionReport.getTaskStartTime())) {
                        report.setTaskStartTime(completionReport.getTaskStartTime());
                    }

                    if (report.getTaskEndTime() == null || report.getTaskEndTime().before(completionReport.getTaskEndTime())) {
                        report.setTaskEndTime(completionReport.getTaskEndTime());
                    }

                    report.setAcre(report.getAcre() + completionReport.getAcre());
                    report.setMachineryNum(report.getMachineryNum() + completionReport.getMachineryNum());
                    if (completionReport.getDetail() != null && completionReport.getDetail().isEmpty() == false) {
                        report.setDetail(report.getDetail() + " \n" + completionReport.getDetail());
                    }
                }
            }
        } else {
            report = reportRepository.findByIdUserOrderAndIdUser(order.getIdUserOrder(), user.getIdUser());
        }

        if (report != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("startTime", DateUtils.formatDate(report.getTaskStartTime(), "yyyy.MM.dd"));
            double distance = DateUtils.getDistanceOfTwoDate(report.getTaskStartTime(), report.getTaskEndTime());
            distance = distance + 1;
            jsonObject.addProperty("period", distance);
            jsonObject.addProperty("acre", report.getAcre());
            jsonObject.addProperty("machineryNum", report.getMachineryNum());
            jsonObject.addProperty("detail", report.getDetail());
            return jsonObject;
        } else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("startTime", "");
            jsonObject.addProperty("period", "0");
            jsonObject.addProperty("acre", "0");
            jsonObject.addProperty("machineryNum", "0");
            jsonObject.addProperty("detail", "");
            return jsonObject;
        }
    }

    /**
     * 获取当前订单完工情况
     *
     * @param orderNo
     * @return
     */
    @Override
    public List<OperatorDTO> getSimpleOperatorReport(String orderNo) {
        UserOrder userOrder = orderService.getUserOrder(orderNo);
        List<OrderOperator> orderOperators = operatorService.getOrderOperators(userOrder.getIdUserOrder());
        List<OperatorDTO> operatorDTOS = new ArrayList<>();
        if (orderOperators != null && orderOperators.size() > 0) {
            for (OrderOperator orderOperator : orderOperators) {
                User user = userService.findUser(orderOperator.getIdUser());
                Team team = cooperationService.queryTeamInfo(user);
                boolean proprieter = cooperationService.proprieter(user);
                boolean captain = cooperationService.captain(user);

                OperatorDTO operatorDTO = new OperatorDTO();
                operatorDTO.setUsername(user.getNickname());
                operatorDTO.setMobile(user.getMobile());
                if (team != null) {
                    operatorDTO.setTeamName(team.getTeamName());
                }
                if (proprieter) {
                    operatorDTO.setIdentity(OperatorIdentityEnum.PRORPIETER.getCode());
                } else if (captain) {
                    operatorDTO.setIdentity(OperatorIdentityEnum.CAPTAIN.getCode());
                } else {
                    operatorDTO.setIdentity(OperatorIdentityEnum.MEMBER.getCode());
                }
                if (orderOperator.getIdCompletionReport() != null && orderOperator.getIdCompletionReport() > 0) {
                    CompletionReport report = reportRepository.findByIdCompletionReport(orderOperator.getIdCompletionReport());
                    operatorDTO.setArce(report.getAcre());
                    operatorDTO.setReported(true);
                } else {
                    operatorDTO.setArce(0f);
                    operatorDTO.setReported(false);
                }
                operatorDTOS.add(operatorDTO);
            }
        }
        return operatorDTOS;
    }

    /**
     * 获取订单工作人员简要
     *
     * @param orderNo
     * @return
     */
    @Override
    public List<OrderOperatorDTO> getSimpleOperatorDetails(String orderNo, User user) {
        UserOrder userOrder = orderService.getUserOrder(orderNo);
        if (user.getIdUser().equals(userOrder.getCreateBy()) || user.getIdUser().equals(userOrder.getReceiveBy())) {
            List<OrderOperator> orderOperators = operatorService.getOrderOperators(userOrder.getIdUserOrder());

            List<OrderOperatorDTO> operatorDTOS = new ArrayList<>();
            if (orderOperators != null && orderOperators.size() > 0) {
                for (OrderOperator orderOperator : orderOperators) {
                    User operator = userService.findUser(orderOperator.getIdUser());

                    OrderOperatorDTO operatorDTO = new OrderOperatorDTO();
                    operatorDTO.setIdOperator(orderOperator.getIdOperator());
                    operatorDTO.setUsername(operator.getNickname());
                    operatorDTO.setMobile(operator.getMobile());

                    if (orderOperator.getIdCompletionReport() != null && orderOperator.getIdCompletionReport() > 0) {
                        CompletionReport report = reportRepository.findByIdCompletionReport(orderOperator.getIdCompletionReport());
                        operatorDTO.setAcre(report.getAcre());
                        operatorDTO.setDemandPrice(report.getDemandPrice());
                        operatorDTO.setIsReported(true);
                    } else {
                        operatorDTO.setIsReported(false);
                    }
                    operatorDTOS.add(operatorDTO);
                }
            }
            return operatorDTOS;
        } else {
            throw new PermissionException("[该用户不是该单下单人或接单人，不可查询此数据");
        }
    }

    /**
     * 保存完工报告并生成相关日志
     *
     * @param report
     * @param user
     * @return
     */
    private CompletionReport saveReport(CompletionReport report, User user) {
        CompletionReport completionReport = reportRepository.findByIdUserOrderAndIdUser(report.getIdUserOrder(), report.getIdUser());
        if (completionReport != null) {
            report.setIdCompletionReport(completionReport.getIdCompletionReport());
        }
        report = reportRepository.save(report);

        // 添加相关日志
        OrderChangeLog log = new OrderChangeLog();
        log.setIdUserOrder(report.getIdUserOrder());
        log.setChangeType(OrderLogTypeEnum.REPORT.getType());
        log.setChangeTime(new Date());
        log.setChangeBy(user.getIdUser());
        log.setDetail("用户[" + user.getMobile() + "]提交完工报告，已完工汇报（" + report.getAcre() + "亩）");
        logRepository.save(log);
        return report;
    }

    /**
     * 完结订单
     *
     * @param user      完结人
     * @param order     完结订单
     * @return
     */
    private void finishedReport(User user, UserOrder order) {
        /*if (isLeader) {
            // 校验相关人员是否提交完工报告（除社长外）
            List<OrderOperator> orderOperators = orderOperatorRepository.findByIdUserOrderAndOperatorStateNotIn(order.getIdUserOrder(), Arrays.asList(OperatorStateEnum.REFUSED.getState(), OperatorStateEnum.CANCELED.getState()));

            for (OrderOperator orderOperator : orderOperators) {
                if (orderOperator.getOperatorState().equals(OperatorStateEnum.FINISHED.getState()) == false) {
                    if (orderOperator.getIdUser().equals(user.getIdUser()) == false) {
                        throw new PermissionException("[该单尚有工作人员未提交完工报告，不能执行订单完结操作]");
                    }
                }
            }
        }*/

        order.setUpdateTime(new Date());
        order.setUserOrderState(OrderStateEnum.CHECKING.getState());
        order.setReportedBy(user.getIdUser());
        order = userOrderRepository.save(order);

        // 生成日志
        OrderChangeLog log = new OrderChangeLog();
        log.setIdUserOrder(order.getIdUserOrder());
        log.setChangeBy(user.getIdUser());
        log.setChangeTime(new Date());
        log.setChangeType(OrderLogTypeEnum.WORK_OVER.getType());
        log.setDetail("订单[" + order.getUserOrderNo() + "]作业完成，由用户[" + user.getMobile() + "]确认提交");
        logRepository.save(log);
    }

    /**
     * 获取订单完工汇报
     *
     * @param userOrder
     * @return
     */
    private List<ReportDTO> getOrderOperatorReports(UserOrder userOrder) {
        List<CompletionReport> reports = reportRepository.findByIdUserOrder(userOrder.getIdUserOrder());
        List<ReportDTO> reportDTOS = new ArrayList<>();
        for (CompletionReport report : reports) {
            ReportDTO reportDTO = new ReportDTO();
            BeanUtils.copyProperties(report, reportDTO);
            reportDTO.setOrderNo(userOrder.getUserOrderNo());
            UserDTO user = userService.findUser(report.getIdUser(), false);
            reportDTO.setUser(user);
            reportDTOS.add(reportDTO);
        }
        return reportDTOS;
    }

}
