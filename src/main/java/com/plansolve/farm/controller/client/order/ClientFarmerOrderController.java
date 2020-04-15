package com.plansolve.farm.controller.client.order;

import com.plansolve.farm.controller.client.BaseController;
import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.order.GuideDTO;
import com.plansolve.farm.model.client.order.OrderDTO;
import com.plansolve.farm.model.database.dictionary.DictMachineryType;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.DictService;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description:
 **/
@Slf4j
@RestController
@RequestMapping("/farm/order/user/farmer")
public class ClientFarmerOrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private DictService dictService;


    /**
     * 农户创建订单
     * 需优化：
     *
     * @param orderDTO   订单基本情况
     * @param guideDTO   领路人及集合信息
     * @param farmlandNo 订单相关农田
     * @return
     */
    @PostMapping(value = "/create")
    public Result createOrder(@Valid OrderDTO orderDTO, @Valid GuideDTO guideDTO, String farmlandNo,
                              @RequestParam(required = false) String mobile) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        orderDTO.setGuideDTO(guideDTO);

        Integer idMachinery = Integer.valueOf(orderDTO.getMachineryType());
        DictMachineryType farmMachinery = dictService.getFarmMachinery(idMachinery);
        if (farmMachinery != null) {
            orderDTO.setMachineryType(farmMachinery.getValue());
            orderDTO.setIdMachineryType(idMachinery);
        }

        UserOrder order = orderService.createOrder(orderDTO, farmlandNo, user, mobile);
        orderDTO = orderService.loadDTO(order);
        return ResultUtil.success(orderDTO);
    }

}
