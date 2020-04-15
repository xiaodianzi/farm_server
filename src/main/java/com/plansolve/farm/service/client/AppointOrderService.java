package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.order.AppointOrderDTO;
import com.plansolve.farm.model.database.order.AppointOrder;
import com.plansolve.farm.model.database.user.User;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/2
 * @Description:
 **/
public interface AppointOrderService {

    /**
     * 生成提前预约订单
     *
     * @param appointOrderDTO 订单详情
     * @param user            下单人
     * @return
     */
    public AppointOrder createAppointOrder(AppointOrderDTO appointOrderDTO, User user);

    /**
     * 获取用户提前预约订单
     *
     * @param user
     * @return
     */
    public List<AppointOrder> getUserAppointOrders(User user);

    /**
     * 封装传输对象
     *
     * @param appointOrder
     * @return
     */
    public AppointOrderDTO loadDTO(AppointOrder appointOrder);

    /**
     * 批量封装传输对象
     *
     * @param appointOrders
     * @return
     */
    public List<AppointOrderDTO> loadDTOs(List<AppointOrder> appointOrders);

}
