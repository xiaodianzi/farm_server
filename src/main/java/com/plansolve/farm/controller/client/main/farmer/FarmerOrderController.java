package com.plansolve.farm.controller.client.main.farmer;

import com.plansolve.farm.controller.client.BaseController;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.order.GuideDTO;
import com.plansolve.farm.model.client.order.OrderDTO;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: 高一平
 * @Date: 2018/6/20
 * @Description: 下单人相关订单操作
 **/
@RestController
@RequestMapping(value = "/farm/farmer/order")
public class FarmerOrderController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(FarmerOrderController.class);
    @Autowired
    private OrderService orderService;

    /**
     * 农户创建订单
     * 需优化：
     * 订单的下单对象：1、所在合作社     2、手机号码查询农机手（社长）     3、平台
     * 下单遭拒绝或取消，可一键重新下单
     *
     * @param orderDTO   订单基本情况
     * @param guideDTO   领路人及集合信息
     * @param farmlandNo 订单相关农田
     * @return
     */
    @PostMapping(value = "/createOrder")
    public Result createOrder(@Valid OrderDTO orderDTO, @Valid GuideDTO guideDTO, String farmlandNo, @RequestParam(required = false) String mobile) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        orderDTO.setGuideDTO(guideDTO);
        UserOrder order = orderService.createOrder(orderDTO, farmlandNo, user, mobile);
        orderDTO = orderService.loadDTO(order);
        return ResultUtil.success(orderDTO);
    }

    /**
     * 一键重新下单给平台
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/repeatCreateOrder")
    public Result repeatCreateOrder(String orderNo) {
        if (orderNo == null || orderNo.isEmpty()) {
            throw new NullParamException("[订单号不能为空]");
        } else {
            UserOrder userOrder = orderService.getUserOrder(orderNo);
            if (userOrder.getUserOrderState().equals(OrderStateEnum.CANCELED.getState())) {
                User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
                userOrder = orderService.repeatCreateOrder(userOrder, user);
                OrderDTO orderDTO = orderService.loadDTO(userOrder);
                return ResultUtil.success(orderDTO);
            } else {
                throw new ParamErrorException("[该订单不可进行此操作]");
            }
        }
    }

    /**
     * 更改引路人信息
     *
     * @param guideDTO
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/updateGuide")
    public Result updateGuide(@Valid GuideDTO guideDTO, String orderNo) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            UserOrder userOrder = orderService.updateGuide(guideDTO, orderNo, user);
            OrderDTO orderDTO = orderService.loadDTO(userOrder);
            return ResultUtil.success(orderDTO);
        }
    }

    /**
     * 种植户取消订单
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/cancelOrder")
    public Result cancelOrder(String orderNo) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            UserOrder userOrder = orderService.cancelOrder(user, orderNo);
            // 为订单通知的推送切面提供原接单人信息，前端调用后需消除原接单人信息再响应给前端！
            userOrder.setReceiveBy(null);
            OrderDTO orderDTO = orderService.loadDTO(userOrder);
            logger.info("种植户[" + user.getMobile() + "]取消订单" + orderNo);
            return ResultUtil.success(orderDTO);
        }
    }

    /**
     * 种植户确认工作进度，并验收合格，进入待支付状态
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/finalAcceptance")
    public Result finalAcceptance(String orderNo) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            UserOrder userOrder = orderService.finalAcceptance(user, orderNo);
            OrderDTO orderDTO = orderService.loadDTO(userOrder);
            logger.info("种植户[" + user.getMobile() + "]确认作业完成" + orderNo);
            return ResultUtil.success(orderDTO);
        }
    }

    /**
     * 种植户删除已被取消的订单
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/deleteOrder")
    public Result deleteOrder(String orderNo) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            UserOrder userOrder = orderService.deleteOrder(user, orderNo);
            OrderDTO orderDTO = orderService.loadDTO(userOrder);
            logger.info("种植户[" + user.getMobile() + "]删除订单" + orderNo);
            return ResultUtil.success(orderDTO);
        }
    }

}
