package com.plansolve.farm.controller.client.order;

import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.userOrder.OrderSimpleDTO;
import com.plansolve.farm.model.database.order.UserOrder;
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
 * @Date: 2019/4/30
 * @Description:
 **/
@Slf4j
@RestController
@RequestMapping("/farm/order/user")
public class ClientUserOrderController {

    @Autowired
    private UserOrderClientSelectService clientSelectService;

    /**
     * 客户端入口：我的订单 种植户/农机手
     * 查询用户订单
     *
     * @param page     分页相关参数及返回对象   pageNo页码默认为0; pageSize页面大小默认为20;
     * @param identity 查询角色 种植户为0、农机手为1
     * @param state    查询相关状态
     *                 种植户：0 - 全部、1 - 待接单/确认中、2 - 作业中、3 - 待验收、4 - 待支付、5 - 已完成
     *                 农机手：0 - 全部、1 - 待接单、2 - 待确认、3 - 待作业、4 - 验收中、5 - 已完成
     *                 全部：普通订单、协同作业订单
     *                 待接单：发单到个人待确认、协同作业新邀请、合作社新订单
     *                 待确认：普通订单
     *                 待作业：普通订单、协同作业已同意且未完成任务 作业进行中订单
     *                 验收中：普通订单、协同作业已同意且已提交完工报告 等待验收订单
     *                 已完成：普通订单
     * @return
     */
    @RequestMapping("/mine/list")
    public Result userOrders(Page page, Integer identity, Integer state){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        Page<UserOrder> orderPage = clientSelectService.pageUserOrders(page, identity, state, user);
        Page<OrderSimpleDTO> dtoPage = new Page<>();
        if (orderPage != null) {
            dtoPage.setPageNo(orderPage.getPageNo());
            dtoPage.setPageSize(orderPage.getPageSize());
            dtoPage.setTotal(orderPage.getTotal());
            List<OrderSimpleDTO> dtos = clientSelectService.loadSimpleDTO(orderPage.getRows(), user);
            dtoPage.setRows(dtos);
        }
        return ResultUtil.success(dtoPage);
    }


}
