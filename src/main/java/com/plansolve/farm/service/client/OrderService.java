package com.plansolve.farm.service.client;

import com.google.gson.JsonArray;
import com.plansolve.farm.model.client.PageDTO;
import com.plansolve.farm.model.client.order.*;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: 高一平
 * @Date: 2018/6/20
 * @Description: 订单相关接口
 **/
public interface OrderService {

    /**
     * 分页查询各类型订单
     *
     * @param isFarmer
     * @param orderState
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<UserOrder> getUserOrders(User user, Boolean isFarmer, String orderState, Integer pageNo, Integer pageSize);

    /**
     * 获取种植户订单列表
     *
     * @param user
     * @return
     */
    public List<UserOrder> getOrderWithFarmer(User user);

    /**
     * 获取已完成的种植户订单列表
     *
     * @param user
     * @return
     */
    public List<UserOrder> getOrderWithFarmerFinished(User user);

    /**
     * 获取农机手订单列表
     *
     * @param user
     * @return
     */
    public List<UserOrder> getOrderWithOperator(User user);

    /**
     * 获取范围内可接订单
     *
     * @param user      当前用户
     * @param latitude  所处方位
     * @param longitude 所处方位
     * @param pageNo    页码
     * @param pageSize  页面大小
     * @return
     */
    public List<UserOrder> getWaitingOrderList(User user, Double latitude, Double longitude, Integer pageNo, Integer pageSize);

    /**
     * @param user      当前用户
     * @param param     检索条件
     * @param latitude  所处方位
     * @param longitude 所处方位
     * @param pageNo    页码
     * @param pageSize  页面大小
     * @return
     */
    public List<UserOrder> getWaitingOrderList(User user, String param, Double latitude, Double longitude, Integer pageNo, Integer pageSize);

    /********************************************************************************订单操作********************************************************************************/
    /**
     * 创建订单
     *
     * @param orderDTO   订单基本信息
     * @param farmlandNo 订单相关地块
     * @param user       下单人
     * @param mobile     下单对象
     * @return
     */
    public UserOrder createOrder(OrderDTO orderDTO, String farmlandNo, User user, String mobile);

    /**
     * 创建订单
     *
     * @param userOrder
     * @param user
     * @return
     */
    public UserOrder repeatCreateOrder(UserOrder userOrder, User user);

    /**
     * 更改引路人信息
     *
     * @param guideDTO 引路人信息
     * @param orderNo  订单号
     * @param user     订单修改人
     * @return
     */
    public UserOrder updateGuide(GuideDTO guideDTO, String orderNo, User user);

    /**
     * 农机手抢单
     *
     * @param user    农机手
     * @param orderNo 订单号
     * @return
     */
    public UserOrder receiveOrder(User user, String orderNo);

    /**
     * 农机手确认订单
     *
     * @param user    农机手
     * @param orderNo 订单号
     * @return
     */
    public UserOrder makeSureOrder(User user, String orderNo);

    /**
     * 取消订单
     *
     * @param user    取消人
     * @param orderNo 订单号
     * @return
     */
    public UserOrder cancelOrder(User user, String orderNo);

    /**
     * 批量取消下单至合作社的未接订单
     *
     * @param users
     * @return
     */
    public List<UserOrder> cancleCooperationOrder(List<User> users);

    /**
     * 取消该用户下单至合作社的未接订单
     *
     * @param user
     * @return
     */
    public List<UserOrder> cancleCooperationOrder(User user);

    /**
     * 种植户确认工作进度，并验收合格，进入待支付状态
     *
     * @param user
     * @param orderNo
     * @return
     */
    public UserOrder finalAcceptance(User user, String orderNo);

    /**
     * 用户提交索要金额
     *
     * @param orderNo
     * @param demandPrice
     * @return
     */
    public UserOrder demandPrice(User user, String orderNo, BigDecimal demandPrice);

    /**
     * 种植户付款
     *
     * @param orderNo 订单号
     * @return
     */
    public UserOrder paidOrder(String orderNo, Boolean onlinePayment);

    /**
     * 农机手确认收款
     * 待确认：收款给谁--协同作业--如何处理款项问题
     * 现：统一付款给收款人（协同作业收款人为社长）
     *
     * @param user
     * @param orderNo
     * @return
     */
    public UserOrder confirmReceipt(User user, String orderNo);

    /**
     * 农机手确认订单完结
     *
     * @param user
     * @param orderNo
     * @return
     */
    public UserOrder finishOrder(User user, String orderNo);

    /**
     * 种植户删除已被取消的订单
     *
     * @param user
     * @param orderNo
     * @return
     */
    public UserOrder deleteOrder(User user, String orderNo);

    /**
     * 给过期订单过期
     */
    public void overdueOrder();
    /********************************************************************************订单操作********************************************************************************/
    /********************************************************************************订单封装********************************************************************************/
    /**
     * 获取订单传输对象
     *
     * @param user
     * @param userOrder
     * @return
     */
    public OrderDTO loadDTO(User user, UserOrder userOrder);

    /**
     * 获取订单传输对象
     *
     * @param userOrder
     * @return
     */
    public OrderDTO loadDTO(UserOrder userOrder);

    /**
     * 批量封装订单传输对象
     *
     * @param orders
     * @return
     */
    public List<OrderDTO> loadDTOs(List<UserOrder> orders);

    /**
     * 简单封装订单信息
     *
     * @param userOrder
     * @return
     */
    public OrderSimpleDTO loadSimpleDTO(UserOrder userOrder);

    /**
     * 批量封装订单简单信息
     *
     * @param orders
     * @return
     */
    public List<OrderSimpleDTO> loadSimpleDTOs(List<UserOrder> orders);
    /********************************************************************************订单封装********************************************************************************/

    /**
     * 获取用户在某一时间段的“忙”“闲”状态
     *
     * @param user      相关用户
     * @param startTime 开始时间
     * @param days      持续天数
     * @return
     */
    public Boolean isBusy(User user, Date startTime, Integer days);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return
     */
    public UserOrder getUserOrder(String orderNo);

    /**
     * 根据订单主键查询订单
     *
     * @param idUserOrder
     * @return
     */
    public UserOrder getUserOrder(Long idUserOrder);

    /**
     * 获取未完成的协同作业订单
     *
     * @param user
     * @return
     */
    public List<UserOrder> getUnfinishedCooperativeOrder(User user);

    /**
     * 获取该用户未完成的协同作业订单任务
     *
     * @param user
     * @return
     */
    public List<UserOrder> getUserUnfinishedCooperativeOrder(User user);

    /**
     * 根据状态获取该用户的协同作业订单任务
     *
     * @param user
     * @param state
     * @return
     */
    public PageDTO<UserOrder> getUserCooperativeOrder(User user, String state, Integer pageNo, Integer pageSize);

    /**
     * 获取已完成的协同作业订单
     *
     * @param user
     * @return
     */
    public List<UserOrder> getFinishedCooperativeOrder(User user);

    /**
     * 获取该用户加入合作社后所有完成的订单
     *
     * @param user   用户
     * @param getAll 是否获取所有
     * @param date   若不获取所有，则获取的月份
     * @return
     */
    public List<UserOrder> getCooperationOrder(User user, Boolean getAll, Date date);

    /**
     * 获取该用户加入合作社后所有完成的订单
     *
     * @param user
     * @param getAll
     * @param startTime
     * @param endTime
     * @return
     */
    public List<UserOrder> getCooperationOrder(User user, Boolean getAll, Date startTime, Date endTime);

    /**
     * 获取下给合作社的订单
     *
     * @param user
     * @return
     */
    public List<UserOrder> getToCooperationOrder(User user);

    /**
     * 获取用户指定订单变更历史
     *
     * @param user    当前用户
     * @param orderNo 指定订单编号
     * @return 变更历史
     */
    public List<OrderChangeLogDTO> getUserOrderChangeLog(User user, String orderNo);

    /********************************************************************************订单统计********************************************************************************/
    /**
     * 作业统计--我的统计
     * 按月份统计当前用户合作社工作量
     *
     * @param user
     * @param date
     * @return
     */
    public Map<Integer, OrderStatisticsDTO> userStatistics(User user, String date);

    /**
     * 查询单个用户合作社完结订单
     *
     * @return
     */
    public List<UserOrder> getUserStatisticalOrder(User user, Date startDate, Date endDate);

    /**
     * 返回用户统计订单
     *
     * @param users         统计人员列表
     * @param idCooperation 合作社
     * @param date          统计时间段
     * @param currentUser   当前用户
     * @return
     */
    public Map<Long, List<UserOrder>> getStatisticalOrder(List<User> users, Integer idCooperation, Date date, User currentUser);

    /**
     * 返回用户统计订单
     *
     * @param users         统计人员列表
     * @param idCooperation 合作社
     * @param startDate     统计时间段
     * @param endDate       统计时间段
     * @param currentUser   当前用户
     * @return
     */
    public Map<Long, List<UserOrder>> getStatisticalOrder(List<User> users, Integer idCooperation, Date startDate, Date endDate, User currentUser);

    /**
     * 返回用户统计信息
     *
     * @param users         统计人员列表
     * @param idCooperation 合作社
     * @param date          统计时间段
     * @param currentUser   当前用户
     * @return
     */
    public List<OrderStatisticsDTO> getStatisticalMsg(List<User> users, Integer idCooperation, Date date, User currentUser);

    /**
     * 返回用户统计信息
     *
     * @param users         统计人员列表
     * @param idCooperation 合作社
     * @param startDate     统计时间段
     * @param endDate       统计时间段
     * @param currentUser   当前用户
     * @return
     */
    public List<OrderStatisticsDTO> getStatisticalMsg(List<User> users, Integer idCooperation, Date startDate, Date endDate, User currentUser);

    /**
     * 返回单个用户的统计信息
     *
     * @param user
     * @return
     */
    public List<UserOrder> getStatisticalMsg(User user);

    /**
     * 返回单个用户的统计信息
     *
     * @param user
     * @param date
     * @return
     */
    public List<UserOrder> getStatisticalMsg(User user, Date date);

    /**
     * 根据年份返回该用户各个月统计信息
     *
     * @param user
     * @param date
     * @return
     */
    public List<OrderStatisticsDTO> getUserOrdersStatisticalMsg(User user, String date);

    /**
     * 查询一段时间内不同农作物的统计数据
     *
     * @param begin
     * @param end
     * @return
     */
    public Map<String, Integer> getOrderStatisticalDateByCrop(Date begin, Date end);

    /**
     * 查询一段时间内不同农机的统计数据
     *
     * @param begin
     * @param end
     * @return
     */
    public Map<String, Integer> getOrderStatisticalDateByMachineType(Date begin, Date end);

    /**
     * 查询一段时间内不同地区的统计数据
     *
     * @param begin        开始时间
     * @param end          终止时间
     * @param addressType  地址类型 取值：province-省、city-市、county-区县
     * @param addressRange 地址范围（可为空） 当地址类型为city或county时，指明地址查询范围，若为空，则查询全部
     * @return
     */
    public JsonArray getOrderStatisticalDataByAddress(Date begin, Date end, String addressType, String addressRange);

    /********************************************************************************订单统计********************************************************************************/


}
