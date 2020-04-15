package com.plansolve.farm.controller.wechat.main;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.order.OrderDTO;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.type.PageTypeEnum;
import com.plansolve.farm.service.client.*;
import com.plansolve.farm.util.AppHttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/10/19
 * @Description:
 **/
@Controller
@RequestMapping("/wechat")
public class WeChatHomeController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param model
     * @return
     */
    @GetMapping("/orderDetail")
    public String orderDetail(String orderNo, Model model) {
        UserOrder userOrder = orderService.getUserOrder(orderNo);
        OrderDTO orderDTO = orderService.loadDTO(userOrder);
        model.addAttribute("order", orderDTO);
        return "wechat/home/orderDetail";
    }

    /**
     * 我的订单页
     *
     * @param model
     * @return
     */
    @GetMapping("/indent")
    public String indent(Model model) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<UserOrder> orderWithFarmer = orderService.getOrderWithFarmer(user);
        List<OrderDTO> farmerOrderDTOS = orderService.loadDTOs(orderWithFarmer);
        List<UserOrder> orderWithOperator = orderService.getOrderWithOperator(user);
        List<OrderDTO> operatorOrderDTOS = orderService.loadDTOs(orderWithOperator);
        model.addAttribute("pageType", PageTypeEnum.INDENT.getType());
        model.addAttribute("orderWithFarmer", farmerOrderDTOS);
        model.addAttribute("orderWithOperator", operatorOrderDTOS);
        return "wechat/order/myOrder";
    }

}
