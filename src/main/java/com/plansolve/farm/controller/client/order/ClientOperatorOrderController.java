package com.plansolve.farm.controller.client.order;

import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.bo.order.OrderChangeLogDTO;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.order.UserOrderClientSelectService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/5/9
 * @Description:
 **/

@Slf4j
@RestController
@RequestMapping("/farm/order/user/operator")
public class ClientOperatorOrderController {

    @Autowired
    private UserOrderClientSelectService clientSelectService;

    /**
     * 获取当前用户指定订单日志
     *
     * @param userOrderNo
     * @return
     */
    @RequestMapping(value = "/logs")
    public Result logs(String userOrderNo) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<OrderChangeLogDTO> logs = clientSelectService.getUserOrderLog(userOrderNo, user);
        return ResultUtil.success(logs);
    }

}
