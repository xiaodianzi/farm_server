package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: 高一平
 * @Date: 2018/8/3
 * @Description:
 **/
public interface OrderOperatorService {

    /**
     * 确定订单工作人员（初始化：选择是否协同作业）
     *
     * @param user          当前用户
     * @param isCooperative 是否协同作业
     * @param workers       协同作业工作人员
     * @param orderNo       订单号
     * @return
     */
    public List<UserDTO> setOrderWorkers(User user, Boolean isCooperative, List<String> workers, String orderNo);

    /**
     * 管理订单工作人员
     *
     * @param orderNo 订单号
     * @param workers 添加或减少的工作人员
     * @param isAdd   添加或减少
     * @param user    管理人
     * @return
     */
    public List<UserDTO> managerWorkers(String orderNo, List<String> workers, Boolean isAdd, User user);

    /**
     * 用户接受或拒绝协同作业的邀请
     *
     * @param user    当前用户
     * @param orderNo 操作订单号
     * @param accept  是否接受
     * @return
     */
    public Boolean acceptOrder(User user, String orderNo, Boolean accept);

    /**
     * 查询订单工作人员
     *
     * @param orderNo 订单号
     * @param user    查询人
     * @return
     */
    public List<UserDTO> getOrderOperators(String orderNo, User user);

    /**
     * 查询相关工作人员
     *
     * @param idUserOrder
     * @return
     */
    public List<OrderOperator> getOrderOperators(Long idUserOrder);

    /**
     * 批量获取当前用户协同作业订单的用户状态
     *
     * @param orderNoList
     * @param user
     * @return
     */
    public Map<String, String> getOperatorState(String[] orderNoList, User user);

    /**
     * 获取工作人员信息
     *
     * @param idUser
     * @param idUserOrder
     * @return
     */
    public OrderOperator getOperator(Long idUser, Long idUserOrder);

    /**
     * 下单人取消农机手工作人员资格
     *
     * @param orderOperator 工作人员
     * @param user          下单人
     */
    public void cancelOperator(String orderNo, OrderOperator orderOperator, User user);

    /**
     * 查询某时间段内该用户的工作人员信息
     *
     * @param user
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public List<OrderOperator> findByUserAndDate(User user, Date dateStart, Date dateEnd);

    /**
     * 当前用户是否完成当前合作社的合作社订单任务
     *
     * @param user
     * @return
     */
    public Boolean isCooperationOrderFinished(User user);

    /**
     * 查询用户所有参与的订单
     *
     * @param user
     * @return
     */
    public List<OrderOperator> findByUser(User user);

    /**
     * 查询用户所有参与的订单
     *
     * @param user
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<OrderOperator> findByUser(User user, String state, Integer pageNo, Integer pageSize);
}
