package com.plansolve.farm.service.client.order.impl;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.bo.order.OrderChangeLogDTO;
import com.plansolve.farm.model.bo.user.FarmlandBO;
import com.plansolve.farm.model.client.userOrder.OrderSimpleDTO;
import com.plansolve.farm.model.database.log.OrderChangeLog;
import com.plansolve.farm.model.database.order.CompletionReport;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.CropCodeEnum;
import com.plansolve.farm.model.enums.code.UserOrderStateCodeEnum;
import com.plansolve.farm.model.enums.state.OperatorStateEnum;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.model.enums.type.OrderLogTypeEnum;
import com.plansolve.farm.service.base.order.UserOrderBaseSelectService;
import com.plansolve.farm.service.base.order.UserOrderCompletionReportBaseSelectService;
import com.plansolve.farm.service.base.user.FarmlandBaseService;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.service.client.order.UserOrderClientSelectService;
import com.plansolve.farm.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description:
 **/
@Slf4j
@Service
public class UserOrderClientSelectServiceImpl implements UserOrderClientSelectService {

    @Autowired
    private UserOrderBaseSelectService orderSelectService;
    @Autowired
    private UserOrderCompletionReportBaseSelectService reportBaseSelectService;
    @Autowired
    private FarmlandBaseService farmlandBaseService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private UserService userService;

    @Override
    public Page<UserOrder> pageUserOrders(Page page, Integer identity, Integer state, User user) {
        Page<UserOrder> orderPage;
        if (identity.equals(0)) {
            // 种植户订单
            if (state.equals(0)) {
                orderPage = orderSelectService.pageFarmerOrder(page, user);
            } else if (state.equals(1)) {
                // 1 - 待接单/确认中（这个更合理，但应客户端限制，暂时不用）
//                orderPage = orderSelectService.pageFarmerOrder(page, Arrays.asList(OrderStateEnum.WAITING.getState(), OrderStateEnum.CONFIRMING.getState()), user);
                // 1 - 待接单
                orderPage = orderSelectService.pageFarmerOrder(page, OrderStateEnum.WAITING.getState(), user);
            } else if (state.equals(2)) {
                // 2 - 作业中（这个更合理，但应客户端限制，暂时不用）
//                orderPage = orderSelectService.pageFarmerOrder(page, OrderStateEnum.WORKING.getState(), user);
                // 2 - 确认中
                orderPage = orderSelectService.pageFarmerOrder(page, OrderStateEnum.CONFIRMING.getState(), user);
            } else if (state.equals(3)) {
                // 3 - 待验收（这个更合理，但应客户端限制，暂时不用）
//                orderPage = orderSelectService.pageFarmerOrder(page, OrderStateEnum.CHECKING.getState(), user);
                // 3 - 作业中
                orderPage = orderSelectService.pageFarmerOrder(page, OrderStateEnum.WORKING.getState(), user);
            } else if (state.equals(4)) {
                // 4 - 待支付（这个更合理，但应客户端限制，暂时不用）
//                orderPage = orderSelectService.pageFarmerOrder(page, OrderStateEnum.PAYMENT.getState(), user);
                // 4 - 待验收/待支付
                orderPage = orderSelectService.pageFarmerOrder(page, Arrays.asList(OrderStateEnum.CHECKING.getState(), OrderStateEnum.PAYMENT.getState()), user);
            } else if (state.equals(5)) {
                // 5 - 已完成
                orderPage = orderSelectService.pageFarmerOrder(page, Arrays.asList(OrderStateEnum.PREPAID.getState(), OrderStateEnum.RECEIPT.getState(), OrderStateEnum.FINISHED.getState()), user);
            } else {
                // 订单状态错误
                throw new ParamErrorException("[订单状态错误]");
            }
        } else if (identity.equals(1)) {
            // 农机手订单
            if (state.equals(0)) {
                orderPage = orderSelectService.pageOperatorOrderByOperator(page, user);
            } else if (state.equals(1)) {
                // 1 - 待接单 - 普通订单、协同作业订单
                List<UserOrder> userOrders = orderSelectService.listOperatorOrderByState(OrderStateEnum.WAITING.getState(), user);
                List<UserOrder> operatorOrders = orderSelectService.listOperatorOrderByOperatorState(OperatorStateEnum.INVITED.getState(), user);
                List<UserOrder> cooperationOrders = orderSelectService.listOperatorCooperationOrderByState(OrderStateEnum.WAITING.getState(), user);

                List<UserOrder> orders = new ArrayList<>();
                orders = mergeCollection(userOrders, operatorOrders);
                orders = mergeCollection(orders, cooperationOrders);
                // 若协同作业订单，未接受但订单已完成，需筛出去
                List<UserOrder> result = new ArrayList<>();
                for (UserOrder order : orders) {
                    if (order.getUserOrderState().equals(OrderStateEnum.WAITING.getState())
                            || order.getUserOrderState().equals(OrderStateEnum.WORKING.getState())) {
                        result.add(order);
                    }
                }

                orderPage = new Page<>();
                orderPage.setTotal((long) result.size());
                orderPage.setRows(result);
            } else if (state.equals(2)) {
                // 2 - 待确认 - 普通订单
                orderPage = orderSelectService.pageOperatorOrder(page, OrderStateEnum.CONFIRMING.getState(), user);
            } else if (state.equals(3)) {
                // 3 - 待作业 - 普通订单、协同作业已同意且未完成任务 作业进行中订单
                List<UserOrder> userOrders = orderSelectService.listOperatorOrderByState(OrderStateEnum.WORKING.getState(), user);
                List<UserOrder> operatorOrders = orderSelectService.listOperatorOrderByOperatorStates(Arrays.asList(OperatorStateEnum.ACCEPTED.getState(), OperatorStateEnum.FINISHED.getState()), user);
                List<UserOrder> orders = new ArrayList<>();
                orders = mergeCollection(userOrders, operatorOrders);

                List<UserOrder> result = new ArrayList<>();
                for (UserOrder order : orders) {
                    if (order.getUserOrderState().equals(OrderStateEnum.WORKING.getState())) {
                        result.add(order);
                    }
                }

                orderPage = new Page<>();
                orderPage.setTotal((long) result.size());
                orderPage.setRows(result);
            } else if (state.equals(4)) {
                // 4 - 验收中 - 普通订单、协同作业已同意且已提交完工报告 等待验收订单 / 待支付暂时也在此栏中
                List<UserOrder> userOrders = orderSelectService.listOperatorOrderByStates(Arrays.asList(OrderStateEnum.CHECKING.getState(), OrderStateEnum.PAYMENT.getState()), user);
                List<UserOrder> operatorOrders = orderSelectService.listOperatorOrderByOperatorState(OperatorStateEnum.FINISHED.getState(), user);
                List<UserOrder> orders = new ArrayList<>();
                orders = mergeCollection(userOrders, operatorOrders);

                List<UserOrder> result = new ArrayList<>();
                for (UserOrder order : orders) {
                    if (order.getUserOrderState().equals(OrderStateEnum.CHECKING.getState()) || order.getUserOrderState().equals(OrderStateEnum.PAYMENT.getState())) {
                        result.add(order);
                    }
                }

                orderPage = new Page<>();
                orderPage.setTotal((long) result.size());
                orderPage.setRows(result);
            } else if (state.equals(5)) {
                // 5 - 已完成 / 待确认收款也在此栏中
                orderPage = orderSelectService.pageOperatorOrder(page, Arrays.asList(OrderStateEnum.PREPAID.getState(), OrderStateEnum.RECEIPT.getState(), OrderStateEnum.FINISHED.getState()), user);
            } else {
                // 订单状态错误
                throw new ParamErrorException("[订单状态错误]");
            }
        } else {
            // 角色错误
            throw new ParamErrorException("[角色错误]");
        }
        return orderPage;
    }

    /**
     * 查询线上支付订单 TODO
     *
     * @param start
     * @param end
     * @param idUser
     * @return
     */
    @Override
    public List<UserOrder> listOnlinePaymentOrders(Date start, Date end, Long idUser) {
        List<UserOrder> orders = new ArrayList<>();
        List<UserOrder> orders1 = orderSelectService.listFarmerOnlinePaymentOrders(start, end, idUser);
        List<UserOrder> orders2 = orderSelectService.listOperatorOnlinePaymentOrders(start, end, idUser);
        if (orders1 != null) {
            orders.addAll(orders1);
        }
        if (orders2 != null) {
            orders.addAll(orders2);
        }
        return orders;
    }

    @Override
    public List<OrderChangeLogDTO> getUserOrderLog(String userOrderNo, User user) {
        UserOrder order = orderSelectService.getByUserOrder(userOrderNo);
        List<OrderChangeLog> logs = orderSelectService.listOrderChangeLog(order.getIdUserOrder(), user.getIdUser());

        List<OrderChangeLogDTO> dtos = new ArrayList<>();
        if (logs != null && logs.size() > 0) {
            for (OrderChangeLog log : logs) {
                OrderChangeLogDTO dto = new OrderChangeLogDTO();
                dto.setUsername(user.getNickname());
                dto.setMobile(user.getMobile());
                dto.setChangeType(log.getChangeType());
                dto.setChangeTime(DateUtils.formatDateTime(log.getChangeTime()));
                dto.setDetail(log.getDetail());

                if (log.getChangeType().equals(OrderLogTypeEnum.REPORT.getType())) {
                    CompletionReport completionReport = reportBaseSelectService.listCompletionReports(order.getIdUserOrder(), user.getIdUser());
                    dto.setAcreage(completionReport.getAcre());
                    dto.setDemandPrice(completionReport.getDemandPrice());
                }
                dtos.add(dto);
            }
        }
        return dtos;
    }

    /**
     * 封装客户端订单列表Item接口
     *
     * @param userOrder
     * @param user
     * @return
     */
    @Override
    public OrderSimpleDTO loadSimpleDTO(UserOrder userOrder, User user) {
        OrderSimpleDTO dto = new OrderSimpleDTO();
        dto.setUserOrderNo(userOrder.getUserOrderNo());
        dto.setMachineryTypeName(userOrder.getMachineryType());
        dto.setPrice(userOrder.getPrice());
        dto.setUserOrderState(UserOrderStateCodeEnum.getByMessage(userOrder.getUserOrderState()).getCode());

        User createMan = userService.findUser(userOrder.getCreateBy());
        dto.setCreateMan(createMan.getNickname());
        FarmlandBO farmland = farmlandBaseService.getFarmlandBO(userOrder.getIdFarmland());
        String address = "";
        if (farmland != null) {
            if (StringUtils.isNotBlank(farmland.getProvince())) {
                address = address + farmland.getProvince();
            }
            if (StringUtils.isNotBlank(farmland.getCity())) {
                address = address + farmland.getCity();
            }
            if (StringUtils.isNotBlank(farmland.getCounty())) {
                address = address + farmland.getCounty();
            }
            if (StringUtils.isNotBlank(farmland.getAddressDetail())) {
                address = address + farmland.getAddressDetail();
            }
        }
        dto.setAddress(address);
        if (dto.getAddress() == null || dto.getAddress().trim().equals("")) {
            dto.setAddress(userOrder.getAssemblyAddress());
        }

        dto.setStartTime(DateUtils.formatDate(userOrder.getStartTime(), "yyyy-MM-dd"));
        dto.setAcreage(userOrder.getArce());
        dto.setCreateTime(userOrder.getCreateTime());

        CropCodeEnum crop = CropCodeEnum.getByName(userOrder.getCropName());
        dto.setCrop(crop.getCode());
        dto.setCropImg(crop.getImg());

        dto.setCooperative(userOrder.getCooperative());
        if (user.getIdUser().equals(userOrder.getCreateBy())) {
            dto.setUserIdentity(0);
        } else if (user.getIdUser().equals(userOrder.getReceiveBy())) {
            dto.setUserIdentity(1);
        } else {
            List<OrderOperator> operators = orderSelectService.findOrderOperator(userOrder.getIdUserOrder(), user.getIdUser());
            if (operators != null && operators.size() > 0) {
                boolean captain = cooperationService.captain(user);
                if (captain) {
                    dto.setUserIdentity(3);
                } else {
                    dto.setUserIdentity(2);
                }

                // 判断工作任务状态，可有多个任务，优先级为被邀请-》已接受-》已完成-》已拒绝-》已取消
                for (OrderOperator operator : operators) {
                    if (operator.getOperatorState().equals(OperatorStateEnum.INVITED.getState())) {
                        dto.setOperatorState(0);
                    }
                }
                if (dto.getOperatorState() == null) {
                    for (OrderOperator operator : operators) {
                        if (operator.getOperatorState().equals(OperatorStateEnum.ACCEPTED.getState())) {
                            dto.setOperatorState(1);
                        }
                    }
                }
                if (dto.getOperatorState() == null) {
                    for (OrderOperator operator : operators) {
                        if (operator.getOperatorState().equals(OperatorStateEnum.FINISHED.getState())) {
                            dto.setOperatorState(2);
                        }
                    }
                }
                if (dto.getOperatorState() == null) {
                    for (OrderOperator operator : operators) {
                        if (operator.getOperatorState().equals(OperatorStateEnum.REFUSED.getState())) {
                            dto.setOperatorState(-1);
                        }
                    }
                }
                if (dto.getOperatorState() == null) {
                    for (OrderOperator operator : operators) {
                        if (operator.getOperatorState().equals(OperatorStateEnum.CANCELED.getState())) {
                            dto.setOperatorState(-2);
                        }
                    }
                }
            } else {
                dto.setUserIdentity(-1);
            }
        }

        if (user.getIdUser().equals(userOrder.getCreateBy())
                || user.getIdUser().equals(userOrder.getReceiveBy())
                || (userOrder.getUserOrderState().equals(OrderStateEnum.WAITING.getState()) && userOrder.getReceiveBy() == null)) {
            dto.setCanSee(true);
        } else {
            dto.setCanSee(false);
        }
        return dto;
    }

    @Override
    public List<OrderSimpleDTO> loadSimpleDTO(List<UserOrder> userOrders, User user) {
        List<OrderSimpleDTO> dtos = new ArrayList<>();
        if (userOrders != null && userOrders.size() > 0) {
            for (UserOrder userOrder : userOrders) {
                OrderSimpleDTO dto = loadSimpleDTO(userOrder, user);
                dtos.add(dto);
            }
        }
        return dtos;
    }

    private List<UserOrder> mergeCollection(List<UserOrder> orders1, List<UserOrder> orders2) {
        List<UserOrder> result = new ArrayList<>();
        result.addAll(orders1);
        for (UserOrder order2 : orders2) {
            boolean add = true;
            for (UserOrder order1 : orders1) {
                if (order2.getIdUserOrder().equals(order1.getIdUserOrder())) {
                    add = false;
                }
            }
            if (add) {
                result.add(order2);
            }
        }
        return result;
    }
}
