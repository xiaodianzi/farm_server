package com.plansolve.farm.controller.client.main.operator;

import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.client.order.BidOrderDTO;
import com.plansolve.farm.model.client.order.BidOrderOperatorDTO;
import com.plansolve.farm.model.database.order.BidOrder;
import com.plansolve.farm.model.database.order.BidOrderOperator;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.BidOrderService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/14
 * @Description:
 **/
@RestController
@RequestMapping(value = "/farm/operator/order/bid")
public class BidOrderController {

    @Autowired
    private BidOrderService bidOrderService;
    @Autowired
    private UserService userService;

    /**
     * 获取可接的竞价订单
     *
     * @return
     */
    @PostMapping(value = "/getAvailableBidOrders")
    public Result getAvailableBidOrders() {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        List<BidOrder> bidOrders = bidOrderService.getAvailableBidOrders(user);
        List<BidOrderDTO> bidOrderDTOS = bidOrderService.loadDTOs(bidOrders);
        return ResultUtil.success(bidOrderDTOS);
    }

    /**
     * 竞价订单申请
     *
     * @param bidOrderOperatorDTO
     * @return
     */
    @PostMapping(value = "/applyForBidOrder")
    public Result applyForBidOrder(@Valid BidOrderOperatorDTO bidOrderOperatorDTO) {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        BidOrderOperator bidOrderOperator = bidOrderService.applyForBidOrder(bidOrderOperatorDTO, user);

        BeanUtils.copyProperties(bidOrderOperator, bidOrderOperatorDTO);
        UserDTO userDTO = userService.loadDTO(user, false);
        bidOrderOperatorDTO.setUser(userDTO);
        return ResultUtil.success(bidOrderOperatorDTO);
    }

    public Result getUserBidOrder(){

        return ResultUtil.success(null);
    }

}
