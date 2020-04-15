package com.plansolve.farm.controller.client.main.operator;

import com.plansolve.farm.controller.client.BaseController;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.order.AppointOrderDTO;
import com.plansolve.farm.model.database.order.AppointOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.AppointOrderService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/2
 * @Description:
 **/
@RestController
@RequestMapping(value = "/farm/operator/order/appoint")
public class AppointOrderController extends BaseController {

    @Autowired
    private AppointOrderService appointOrderService;

    /**
     * 创建提前预约订单
     *
     * @param appointOrderDTO
     * @return
     */
    @PostMapping(value = "/createOrder")
    public Result createOrder(@Valid AppointOrderDTO appointOrderDTO) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        AppointOrder appointOrder = appointOrderService.createAppointOrder(appointOrderDTO, user);
        appointOrderDTO = appointOrderService.loadDTO(appointOrder);
        return ResultUtil.success(appointOrderDTO);
    }

    /**
     * 获取提前预约的订单
     *
     * @return
     */
    @PostMapping(value = "/getOrders")
    public Result getOrders() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<AppointOrder> appointOrders = appointOrderService.getUserAppointOrders(user);
        List<AppointOrderDTO> appointOrderDTOS = appointOrderService.loadDTOs(appointOrders);
        return ResultUtil.success(appointOrderDTOS);
    }


}
