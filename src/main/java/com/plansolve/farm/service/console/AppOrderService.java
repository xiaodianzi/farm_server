package com.plansolve.farm.service.console;

import com.plansolve.farm.model.console.AppOrderDTO;
import com.plansolve.farm.model.database.log.OrderChangeLog;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/2
 * @Description:
 **/
public interface AppOrderService {

    /**
     * 封装订单传输对象
     *
     * @param orders
     * @return
     */
    public List<AppOrderDTO> loadDTOs(List<UserOrder> orders);

    /**
     * 查询订单
     *
     * @param idUserOrder
     * @return
     */
    public UserOrder findOrder(Long idUserOrder);

    /**
     * 分页查询订单
     *
     * @param userOrderNo
     * @param userOrderState
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<UserOrder> findAllOrders(String userOrderNo, String userOrderState, Integer pageNo, Integer pageSize);

    /**
     * 获取订单工作人员
     *
     * @param idUserOrder
     * @return
     */
    public List<User> listOrderWorker(Long idUserOrder);

    /**
     * 获取订单日志
     *
     * @param idUserOrder
     * @return
     */
    public List<OrderChangeLog> getOrderLogs(Long idUserOrder);

}
