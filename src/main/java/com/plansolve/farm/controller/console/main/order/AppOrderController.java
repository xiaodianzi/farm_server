package com.plansolve.farm.controller.console.main.order;

import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.console.AppOrderDTO;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.model.database.cooperation.Cooperation;
import com.plansolve.farm.model.database.log.OrderChangeLog;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.model.enums.type.OrderLogTypeEnum;
import com.plansolve.farm.service.base.common.AddressBaseService;
import com.plansolve.farm.service.base.user.UserBaseService;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.client.order.UserOrderClientSelectService;
import com.plansolve.farm.service.console.AppOrderService;
import com.plansolve.farm.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/7/27
 * @Description:
 **/
@Slf4j
@Controller
@RequestMapping(value = "/manger/app/user")
public class AppOrderController extends BaseController {

    @Autowired
    private AppOrderService orderService;
    @Autowired
    private UserOrderClientSelectService clientSelectService;
    @Autowired
    private UserBaseService userBaseService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private AddressBaseService addressBaseService;

    /**
     * 客户端用户分页查询
     *
     * @return
     */
    @GetMapping(value = "/appOrderPage")
    public String appUserPage() {
        return "order/allOrderList";
    }

    /**
     * 客户端用户分页查询
     *
     * @return
     */
    @GetMapping(value = "/appOrderList")
    @ResponseBody
    public PageDTO<AppOrderDTO> appOrderList(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset,
                                             String userOrderNo, String userOrderState) {
        Integer page = getPage(limit, offset);
        if (userOrderState.equals("all")) {
            userOrderState = null;
        }
        Page<UserOrder> userOrderPage = orderService.findAllOrders(userOrderNo, userOrderState, page, limit);
        PageDTO<AppOrderDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(userOrderPage.getTotalElements());
        List<AppOrderDTO> dtos = orderService.loadDTOs(userOrderPage.getContent());
        pageDTO.setRows(dtos);
        return pageDTO;
    }

    /**
     * 用户个人订单
     *
     * @param page
     * @param identity 查询角色 种植户为0、农机手为1
     * @param idUser   查询人
     * @return
     */
    @RequestMapping(value = "/appUserOrderList")
    @ResponseBody
    public PageDTO<AppOrderDTO> appUserOrderList(com.plansolve.farm.model.Page page, Integer identity, Long idUser) {
        User user = userBaseService.getUser(idUser);
        com.plansolve.farm.model.Page<UserOrder> orderPage = clientSelectService.pageUserOrders(page, identity, 0, user);
        PageDTO<AppOrderDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(orderPage.getTotal());
        List<AppOrderDTO> dtos = orderService.loadDTOs(orderPage.getRows());
        pageDTO.setRows(dtos);
        return pageDTO;
    }

    /**
     * 获取用户订单详情
     *
     * @param idUserOrder
     * @param model
     * @return
     */
    @GetMapping(value = "/appOrderDetail")
    public String appOrderDetail(Long idUserOrder, Model model) {
        UserOrder order = orderService.findOrder(idUserOrder);
        order.setUserOrderState(EnumUtil.getByState(order.getUserOrderState(), OrderStateEnum.class).getMessage());
        model.addAttribute("order", order);

        // 订单日志
        List<OrderChangeLog> logs = orderService.getOrderLogs(order.getIdUserOrder());
        for (OrderChangeLog log : logs) {
            String message = EnumUtil.getByType(log.getChangeType(), OrderLogTypeEnum.class).getMessage();
            log.setChangeType(message);
        }
        model.addAttribute("logs", logs);

        // 下单人
        User createUser = userBaseService.getUser(order.getCreateBy());
        model.addAttribute("createUser", createUser);
        if (order.getReceiveBy() != null) {
            // 接单人
            User receiveUser = userBaseService.getUser(order.getReceiveBy());
            model.addAttribute("receiveUser", receiveUser);
            if (receiveUser != null) {
                if (receiveUser.getIdCooperation() != null && receiveUser.getIdCooperation() > 0) {
                    // 接单人合作社信息
                    Cooperation cooperation = cooperationService.getById(receiveUser.getIdCooperation());
                    model.addAttribute("cooperation", cooperation);
                    if (cooperation != null) {
                        User proprieter = userBaseService.getUser(cooperation.getIdUser());
                        model.addAttribute("proprieter", proprieter);
                        Address address = addressBaseService.getAddress(cooperation.getIdAddress());
                        String addresstStr = addressBaseService.getAddress(address);
                        model.addAttribute("address", addresstStr);
                        List<User> members = cooperationService.members(receiveUser.getIdCooperation());
                        model.addAttribute("members", members);
                    }
                }
                // 订单参与者
                List<User> users = orderService.listOrderWorker(order.getIdUserOrder());
                model.addAttribute("workers", users);
            }
        }
        // 微信支付详情
        if (order.getUserOrderState().equals(OrderStateEnum.PAYMENT.getMessage())
                || order.getUserOrderState().equals(OrderStateEnum.PREPAID.getMessage())
                || order.getUserOrderState().equals(OrderStateEnum.RECEIPT.getMessage())
                || order.getUserOrderState().equals(OrderStateEnum.FINISHED.getMessage())) {
            try {
                WxPayOrderQueryResult wxPayresult = wxPayService.queryOrder(null, order.getUserOrderNo());
                model.addAttribute("wxPay", wxPayresult);
            } catch (WxPayException e) {
                log.error("微信支付情况获取失败");
                e.printStackTrace();
            }
        }
        return "console/order/detail";
    }


}
