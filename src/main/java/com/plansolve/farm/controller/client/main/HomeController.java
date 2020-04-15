package com.plansolve.farm.controller.client.main;

import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.PageDTO;
import com.plansolve.farm.model.client.order.*;
import com.plansolve.farm.model.client.user.FarmlandDTO;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserAppVersion;
import com.plansolve.farm.repository.user.UserAppVersionRepository;
import com.plansolve.farm.service.client.*;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/3
 * @Description:
 **/
@RestController
@RequestMapping(value = "/farm/user")
public class HomeController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OperatorReportService reportService;

    @Autowired
    private DictService dictService;
    @Autowired
    private FarmlandService farmlandService;

    /**
     * 农机手获取可接订单列表
     *
     * @param latitude
     * @param longitude
     * @return
     */
    @PostMapping(value = "/getWaitingOrders")
    public Result getWaitingOrders(@RequestParam(defaultValue = "0") Integer pageNo,
                                   @RequestParam(defaultValue = "20") Integer pageSize,
                                   Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new NullParamException("[经纬度不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            List<UserOrder> orders = orderService.getWaitingOrderList(user, latitude, longitude, pageNo, pageSize);
            List<OrderDTO> orderDTOS = orderService.loadDTOs(orders);
            if (orderDTOS != null && orderDTOS.size() > 0) {
                for (OrderDTO orderDTO : orderDTOS) {
                    if (orderDTO.getTarget().equals("我的合作社")) {
                        User createMan = userService.findByMobile(orderDTO.getCreateBy().getMobile());
                        if (createMan.getIdCooperation().equals(user.getIdCooperation())) {
                            orderDTO.setCannotGet("true");
                        } else {
                            orderDTO.setCannotGet("false");
                        }
                    } else {
                        orderDTO.setCannotGet("true");
                    }
                }
            }
            return ResultUtil.success(orderDTOS);
        }
    }

    /**
     * 农机手获取可接订单列表
     *
     * @param latitude
     * @param longitude
     * @return
     */
    @PostMapping(value = "/getWaitingOrders/simple")
    public Result getWaitingOrdersSimple(@RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "20") Integer pageSize,
                                         Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new NullParamException("[经纬度不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            List<UserOrder> orders = orderService.getWaitingOrderList(user, latitude, longitude, pageNo, pageSize);
            List<OrderListDTO> orderDTOS = new ArrayList<>();
            if (orders != null && orders.size() > 0) {
                for (UserOrder order : orders) {
                    OrderListDTO orderDTO = new OrderListDTO();
                    BeanUtils.copyProperties(order, orderDTO);
                    Farmland farmland = farmlandService.getFarmland(order.getIdFarmland());
                    FarmlandDTO farmlandDTO = farmlandService.loadDTO(farmland);
                    orderDTO.setAddress(farmlandDTO.getAddress());
                    User createMan = userService.findUser(order.getCreateBy());
                    orderDTO.setCreateMan(createMan.getNickname());
                    if (orderDTO.getTarget().equals("我的合作社")) {
                        if (createMan.getIdCooperation().equals(user.getIdCooperation())) {
                            orderDTO.setCannotGet("true");
                        } else {
                            orderDTO.setCannotGet("false");
                        }
                    } else {
                        orderDTO.setCannotGet("true");
                    }
                    orderDTOS.add(orderDTO);
                }
            }
            return ResultUtil.success(orderDTOS);
        }
    }

    /**
     * 获取种植户订单
     *
     * @return
     */
    @PostMapping(value = "/farmer/getOrders")
    public Result getOrderWithFarmer() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<UserOrder> orders = orderService.getOrderWithFarmer(user);
        List<OrderDTO> orderDTOS = orderService.loadDTOs(orders);
        return ResultUtil.success(orderDTOS);
    }

    /**
     * 查询用户订单
     *
     * @param identity
     * @param orderState
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/getUserOrders")
    public Result getUserOrders(Integer identity, @RequestParam(required = false) String orderState,
                                @RequestParam(defaultValue = "0") Integer pageNo,
                                @RequestParam(defaultValue = "20") Integer pageSize) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        Page<UserOrder> orderPage;
        if (identity.equals(0)) {
            orderPage = orderService.getUserOrders(user, true, orderState, pageNo, pageSize);
        } else if (identity.equals(1)) {
            orderPage = orderService.getUserOrders(user, false, orderState, pageNo, pageSize);
        } else {
            throw new ParamErrorException("[用户身份错误]");
        }
        PageDTO dto = new PageDTO();
        dto.setTotal(orderPage.getTotalElements());
        List<OrderDTO> dtos = orderService.loadDTOs(orderPage.getContent());
        dto.setRows(dtos);
        return ResultUtil.success(dto);
    }

    /**
     * 查询用户协同作业订单
     *
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/getUserCooperativeOrders")
    public Result getUserCooperativeOrders(@RequestParam(required = false) String state,
                                           @RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "20") Integer pageSize) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        PageDTO<UserOrder> cooperativeOrder = orderService.getUserCooperativeOrder(user, state, pageNo, pageSize);
        PageDTO dto = new PageDTO();
        dto.setTotal(cooperativeOrder.getTotal());
        List<OrderDTO> dtos = orderService.loadDTOs(cooperativeOrder.getRows());
        dto.setRows(dtos);
        return ResultUtil.success(dto);
    }

    /**
     * 获取农机手订单
     *
     * @return
     */
    @PostMapping(value = "/operator/getOrders")
    public Result getOrderWithOperator() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<UserOrder> orders = orderService.getOrderWithOperator(user);

        List<OrderDTO> orderDTOS = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (UserOrder order : orders) {
                OrderDTO orderDTO = orderService.loadDTO(user, order);
                orderDTOS.add(orderDTO);
            }
        }
        return ResultUtil.success(orderDTOS);
    }

    /**
     * 获取指定订单
     *
     * @param orderNo 订单号
     * @return
     */
    @PostMapping(value = "/getUserOrder")
    public Result getUserOrder(String orderNo) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        UserOrder userOrder = orderService.getUserOrder(orderNo);
        OrderDTO orderDTO = orderService.loadDTO(user, userOrder);
        return ResultUtil.success(orderDTO);
    }

    /**
     * 根据订单号码，查询订单工作人员完工报告
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/getOrderOperatorReport")
    public Result getOrderOperatorReport(String orderNo) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<ReportDTO> reports = reportService.getOrderOperatorReports(orderNo, user);
        return ResultUtil.success(reports);
    }

    /**
     * 获取订单简单完工情况
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/getOrderSimpleReports")
    public Result getOrderSimpleReports(String orderNo) {
        List<OperatorDTO> simpleOperatorReport = reportService.getSimpleOperatorReport(orderNo);
        return ResultUtil.success(simpleOperatorReport);
    }

    /**
     * 获取用户指定订单变更日志
     *
     * @param orderNo
     * @param mobile
     * @return
     */
    @PostMapping(value = "/order/getUserOrderChangeLogs")
    public Result getUserOrderChangeLogs(String orderNo, String mobile) {
        User user = userService.findByMobile(mobile);
        if (user == null) {
            throw new ParamErrorException("[查询不到该用户]");
        }
        List<OrderChangeLogDTO> userOrderChangeLog = orderService.getUserOrderChangeLog(user, orderNo);
        return ResultUtil.success(userOrderChangeLog);
    }

    /**
     * 获取字典表中设置
     *
     * @param key
     * @return
     */
    @PostMapping(value = "/getDictValue")
    public Result getDictValue(String key) {
        return ResultUtil.success(dictService.getValue(key));
    }

}
