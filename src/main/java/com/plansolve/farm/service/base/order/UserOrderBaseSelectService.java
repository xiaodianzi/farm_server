package com.plansolve.farm.service.base.order;

import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.database.log.OrderChangeLog;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description: 用户订单查询接口
 **/
public interface UserOrderBaseSelectService {

    public UserOrder getByUserOrder(Long idUserOrder);

    public UserOrder getByUserOrder(String userOrderNo);

    public Page<UserOrder> pageByOrderState(String orderState, Page<UserOrder> page, Sort sort);

    public Page<UserOrder> pageByOrderStateNot(List<String> orderStates, Page<UserOrder> page, Sort sort);

    /********************************************种植户查询********************************************/

    public Page<UserOrder> pageFarmerOrder(Page<UserOrder> page, User user);

    public Page<UserOrder> pageFarmerOrder(Page<UserOrder> page, String state, User user);

    public Page<UserOrder> pageFarmerOrder(Page<UserOrder> page, List<String> states, User user);

    public List<UserOrder> listFarmerOrderByState(String state, User user);

    public List<UserOrder> listFarmerOrderByStates(List<String> states, User user);

    // 查询用户线上支付订单
    public List<UserOrder> listFarmerOnlinePaymentOrders(Date start, Date end, Long idUser);

    /********************************************种植户查询********************************************/

    /********************************************农机手查询********************************************/

    public Page<UserOrder> pageOperatorOrderByOperator(Page<UserOrder> page, User user);

    public Page<UserOrder> pageOperatorOrderByOperatorState(Page<UserOrder> page, String state, User user);

    public Page<UserOrder> pageOperatorOrderByOperatorStates(Page<UserOrder> page, List<String> states, User user);

    public List<UserOrder> listOperatorOrderByOperatorState(String state, User user);

    public List<UserOrder> listOperatorOrderByOperatorStates(List<String> states, User user);

    public Page<UserOrder> pageOperatorOrder(Page<UserOrder> page, User user);

    public Page<UserOrder> pageOperatorOrder(Page<UserOrder> page, String state, User user);

    public Page<UserOrder> pageOperatorOrder(Page<UserOrder> page, List<String> states, User user);

    public List<UserOrder> listOperatorOrderByState(String state, User user);

    public List<UserOrder> listOperatorOrderByStates(List<String> states, User user);

    public List<UserOrder> listOperatorCooperationOrderByState(String state, User user);

    // 查询用户线上支付订单
    public List<UserOrder> listOperatorOnlinePaymentOrders(Date start, Date end, Long idUser);

    /********************************************农机手任务查询********************************************/

    public List<OrderOperator> findOrderOperator(Long idUserOrder, Long idUser);

    /********************************************农机手任务查询********************************************/
    /********************************************农机手查询********************************************/

    /********************************************查询订单日志********************************************/

    public List<OrderChangeLog> listOrderChangeLog(Long idUserOrder);

    public List<OrderChangeLog> listOrderChangeLog(Long idUserOrder, Long idUser);

    /********************************************查询订单日志********************************************/

}
