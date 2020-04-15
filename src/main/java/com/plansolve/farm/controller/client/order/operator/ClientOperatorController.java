package com.plansolve.farm.controller.client.order.operator;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.bo.order.OrderChangeLogDTO;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.UserService;
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
@RequestMapping(value = "/farm/order/operator")
public class ClientOperatorController {

    @Autowired
    private UserOrderClientSelectService clientSelectService;
    @Autowired
    private UserService userService;

    /**
     * 获取指定用户指定订单日志
     *
     * @param orderNo
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/logs")
    public Result logs(String orderNo, String mobile) {
        User user = userService.findByMobile(mobile);
        if (user == null) {
            throw new ParamErrorException("[查询不到该用户]");
        }
        List<OrderChangeLogDTO> logs = clientSelectService.getUserOrderLog(orderNo, user);
        return ResultUtil.success(logs);
    }

}
