package com.plansolve.farm.service.base.order;

import com.plansolve.farm.model.database.order.OrderOperator;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/6/24
 * @Description:
 **/
public interface UserOrderOperatorBaseService {

    /**
     * 获取工作人员信息
     *
     * @param idUserOrder
     * @return
     */
    public List<OrderOperator> listOrderOperator(Long idUserOrder);

}
