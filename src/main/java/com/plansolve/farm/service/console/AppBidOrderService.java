package com.plansolve.farm.service.console;

import com.plansolve.farm.model.database.order.BidOrder;
import com.plansolve.farm.model.database.user.User;

/**
 * @Author: 高一平
 * @Date: 2018/8/13
 * @Description:
 **/
public interface AppBidOrderService {

    /**
     * 创建竞价订单
     *
     * @param bidOrder
     * @param user
     * @return
     */
    public BidOrder createOrder(BidOrder bidOrder, User user);

}
