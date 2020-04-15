package com.plansolve.farm.service.client.order;

import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.bo.order.OrderChangeLogDTO;
import com.plansolve.farm.model.client.userOrder.OrderSimpleDTO;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;

import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description: 客户端接口 订单查询
 **/
public interface UserOrderClientSelectService {

    /**
     * 根据角色及订单状态分类查询用户订单
     *
     * @param page
     * @param identity
     * @param state
     * @param user
     * @return
     */
    public Page<UserOrder> pageUserOrders(Page page, Integer identity, Integer state, User user);

    /**
     * 查询线上支付订单
     *
     * @param start
     * @param end
     * @param idUser
     * @return
     */
    public List<UserOrder> listOnlinePaymentOrders(Date start, Date end, Long idUser);

    /**
     * 获取用户指定订单日志
     *
     * @param userOrderNo
     * @param user
     * @return
     */
    public List<OrderChangeLogDTO> getUserOrderLog(String userOrderNo, User user);

    public OrderSimpleDTO loadSimpleDTO(UserOrder userOrder, User user);

    public List<OrderSimpleDTO> loadSimpleDTO(List<UserOrder> userOrders, User user);

}
