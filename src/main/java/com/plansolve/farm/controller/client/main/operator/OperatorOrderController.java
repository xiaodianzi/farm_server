package com.plansolve.farm.controller.client.main.operator;

import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.client.order.OrderDTO;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.client.OrderOperatorService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: 高一平
 * @Date: 2018/6/22
 * @Description: 接单人相关订单操作
 **/
@RestController
@RequestMapping(value = "/farm/operator/order")
public class OperatorOrderController {

    private final static Logger logger = LoggerFactory.getLogger(OperatorOrderController.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderOperatorService operatorService;
    @Autowired
    private CooperationService cooperationService;

    /**
     * 农机手抢单
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/getOrder")
    public Result getOrder(String orderNo) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            UserOrder userOrder = orderService.receiveOrder(user, orderNo);
            OrderDTO orderDTO = orderService.loadDTO(userOrder);
            return ResultUtil.success(orderDTO);
        }
    }

    /**
     * 农机手确认订单
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/makeSure")
    public Result makeSure(String orderNo) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            UserOrder userOrder = orderService.makeSureOrder(user, orderNo);
            OrderDTO orderDTO = orderService.loadDTO(userOrder);

            if (cooperationService.proprieter(user) == false) {
                operatorService.setOrderWorkers(user, false, null, orderNo);
            }
            return ResultUtil.success(orderDTO);
        }
    }

    /**
     * 农机手取消订单
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
            logger.info("农机手[" + user.getMobile() + "]取消订单" + orderNo);
            return ResultUtil.success(orderDTO);
        }
    }

    /**
     * 确定工作人员（协同作业）
     *
     * @param orderNo 订单号码
     * @param workers 协同作业人员手机号码（可为空）
     * @return
     */
    @PostMapping(value = "/orderWorkers")
    public Result orderWorkers(String orderNo, Boolean isCooperative, @RequestParam(required = false) String workers) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            List<UserDTO> userDTOS = operatorService.setOrderWorkers(user, isCooperative, getList(workers), orderNo);
            return ResultUtil.success(userDTOS);
        }
    }

    /**
     * 用户拒绝或接受协同作业订单
     *
     * @param orderNo
     * @param accept
     * @return
     */
    @PostMapping(value = "/acceptOrder")
    public Result acceptOrder(String orderNo, Boolean accept) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            operatorService.acceptOrder(user, orderNo, accept);
            return ResultUtil.success(accept);
        }
    }

    /**
     * 社长添加工作人员
     *
     * @param orderNo
     * @param workers
     * @return
     */
    @PostMapping(value = "/addWorkers")
    public Result addWorkers(String orderNo, String workers) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            List<UserDTO> userDTOS = operatorService.managerWorkers(orderNo, getList(workers), true, user);
            return ResultUtil.success(userDTOS);
        }
    }

    /**
     * 社长或小队长减少工作人员
     *
     * @param orderNo
     * @param workers
     * @return
     */
    @PostMapping(value = "/reduceWorkers")
    public Result reduceWorkers(String orderNo, String workers) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            List<UserDTO> userDTOS = operatorService.managerWorkers(orderNo, getList(workers), false, user);
            return ResultUtil.success(userDTOS);
        }
    }

    /**
     * 用户提交索要金额
     *
     * @param orderNo
     * @param demandPrice
     * @return
     */
    @PostMapping(value = "/demandPrice")
    public Result demandPrice(String orderNo, BigDecimal demandPrice) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        UserOrder order = orderService.demandPrice(user, orderNo, demandPrice);
        OrderDTO dto = orderService.loadDTO(order);
        return ResultUtil.success(dto);
    }

    /**
     * 确认收款
     *
     * @param orderNo
     * @return 如果奖励订单积分则返回当前用户即农机手的接单作业积分数据
     */
    @PostMapping(value = "/confirmReceipt")
    public Result confirmReceipt(String orderNo) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            UserOrder confirmReceiptUserOrder = orderService.confirmReceipt(user, orderNo);
            logger.info("农机手[" + user.getMobile() + "]确认收款" + orderNo);
            Result result = finishOrder(orderNo);
            return result;
        }
    }

    /**
     * 订单完结
     *
     * @param orderNo
     * @return
     */
    @PostMapping(value = "/finishOrder")
    public Result finishOrder(String orderNo) {
        if (orderNo.isEmpty()) {
            throw new NullParamException("[订单号不可为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            UserOrder userOrder = orderService.finishOrder(user, orderNo);
            OrderDTO orderDTO = orderService.loadDTO(userOrder);
            logger.info("农机手[" + user.getMobile() + "]确认订单完成" + orderNo);
            return ResultUtil.success(orderDTO);
        }
    }

    /**
     * 获取订单工作人员
     *
     * @param orderNo 订单编号
     * @return
     */
    @PostMapping(value = "/getOperators")
    public Result getOperators(String orderNo) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<UserDTO> operators = operatorService.getOrderOperators(orderNo, user);
        return ResultUtil.success(operators);
    }

    /**
     * 获取用户协同作业订单状态
     *
     * @param orderNoList
     * @return
     */
    @PostMapping(value = "/getOperatorState")
    public Result getOperatorState(String orderNoList) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        if (orderNoList != null && orderNoList.isEmpty() == false) {
            String[] orders = orderNoList.split(",");
            Map<String, String> operatorState = operatorService.getOperatorState(orders, user);
            return ResultUtil.success(operatorState);
        } else {
            throw new NullParamException("[订单号列表为空]");
        }
    }

    /**
     * 将字符串以“,”分割，并放入集合中
     *
     * @param string
     * @return
     */
    private List<String> getList(String string) {
        List<String> result = new ArrayList<>();
        if (string != null && string.isEmpty() == false) {
            String[] split = string.split(",");
            for (String s : split) {
                result.add(s);
            }
        }
        return result;
    }

}
