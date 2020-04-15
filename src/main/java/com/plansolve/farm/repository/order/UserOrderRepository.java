package com.plansolve.farm.repository.order;

import com.plansolve.farm.model.database.order.UserOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/
public interface UserOrderRepository extends JpaRepository<UserOrder, Integer>, JpaSpecificationExecutor<UserOrder> {

    @Modifying
    @Query("update UserOrder o " +
            "set o.receiveBy = :idUser, o.userOrderState = 'confirming' " +
            "where o.idUserOrder = :idUserOrder and o.userOrderState = 'waiting' and (o.receiveBy is null or o.receiveBy <= 0)")
    public void orderAlone(@Param(value = "idUser") Long idUser, @Param(value = "idUserOrder") Long idUserOrder);

    public UserOrder findByIdUserOrder(Long idUserOrder);

    public UserOrder findByUserOrderNo(String userOrderNo);

    public List<UserOrder> findByIsOnlinePaymentAndCreateByAndPaymentTimeAfterAndPaymentTimeBefore(Boolean isOnlinePayment, Long idUser, Date start, Date end);

    public List<UserOrder> findByIsOnlinePaymentAndReceiveByAndPaymentTimeAfterAndPaymentTimeBefore(Boolean isOnlinePayment, Long idUser, Date start, Date end);

    public List<UserOrder> findByTargetAndCreateByInAndUserOrderState(String target, List<Long> idUsers, String userOrderState);

    public List<UserOrder> findByUserOrderState(String userOrderState);

    public UserOrder findByIdUserOrderAndUserOrderStateNot(Long idUserOrder, String userOrderState);

    public UserOrder findByIdUserOrderAndUserOrderStateNotIn(Long idUserOrder, List<String> userOrderState);

    public UserOrder findByIdUserOrderAndUserOrderState(Long idUserOrder, String userOrderState);

    public UserOrder findByUserOrderNoAndUserOrderStateNot(String userOrderNo, String userOrderState);

    public List<UserOrder> findByReceiveByAndUserOrderStateAndIsCooperativeAndUpdateTimeBetween(Long receiveBy, String userOrderState, Boolean isCooperative, Date dateStart, Date dateEnd, Sort sort);

    public List<UserOrder> findByReceiveByAndUserOrderStateAndIsCooperativeAndTargetAndUpdateTimeBetween(Long receiveBy, String userOrderState, Boolean isCooperative, String target, Date dateStart, Date dateEnd, Sort sort);

    public List<UserOrder> findByCreateByAndUserOrderStateNot(Long createBy, String state, Sort sort);

    public List<UserOrder> findByCreateByAndUserOrderStateIn(Long createBy, List<String> states, Sort sort);

    public List<UserOrder> findByCreateByAndUserOrderStateNot(Long createBy, String state);

    public List<UserOrder> findByCreateByAndUserOrderStateAndTarget(Long createBy, String state, String target);

    public List<UserOrder> findByCreateByAndUserOrderState(Long createBy, String state, Sort sort);

    public List<UserOrder> findByReceiveByAndUserOrderState(Long receiveBy, String state, Sort sort);

    public List<UserOrder> findByReceiveByAndUserOrderStateNotIn(Long receiveBy, List<String> states, Sort sort);

    public List<UserOrder> findByReceiveByAndUserOrderStateIn(Long receiveBy, List<String> states, Sort sort);

    public List<UserOrder> findByReceiveByAndUserOrderStateInAndIsCooperativeIsTrue(Long receiveBy, List<String> states);

    public List<UserOrder> findByCreateByAndReceiveByAndTargetNotAndCreateTimeAfterAndUserOrderState(Long createBy, Long receiveBy, String target, Date createTime, String userOrderState);

    public List<UserOrder> findByCreateTimeAfter(Date begin);

    public List<UserOrder> findByCreateTimeBetween(Date startTime, Date endTime);

    /**************************************用户数量统计**************************************/
    public Integer countByCreateTimeBefore(Date date);

    public Integer countByCreateTimeBetween(Date dateBegin, Date dateEnd);

    public Integer countByCreateTimeBeforeAndAndAssemblyAddressLike(Date date, String address);

    public Integer countByCreateTimeBetweenAndAndAssemblyAddressLike(Date dateBegin, Date dateEnd, String address);

    public Integer countByUpdateTimeAfterAndUpdateTimeBeforeAndUserOrderStateAndCropName(Date begin, Date end, String orderState, String crop);

    public Integer countByUpdateTimeAfterAndUpdateTimeBeforeAndUserOrderStateAndMachineryType(Date begin, Date end, String orderState, String machineryType);

    public Integer countByCropNameAndUserOrderStateIsNot(String crop, String orderState);

    public Integer countByMachineryTypeAndUserOrderStateIsNot(String machineryType, String orderState);

    @Query(nativeQuery = true, value = "SELECT uo.create_time from user_order uo GROUP BY SUBSTRING(uo.create_time, 1, 10)")
    public List<Date> queryDistinctOrdersCreateTime();

    public Integer countByCropNameAndUserOrderStateNot(String cropName, String userOrderState);

    public Integer countByCropNameAndUserOrderStateNotAndCreateTimeBetween(String cropName, String userOrderState, Date dateBegin, Date dateEnd);

    public Integer countByIsCooperativeAndUserOrderStateNot(Boolean isCooperative, String userOrderState);

    public Integer countByIsCooperativeAndUserOrderStateNotAndCreateTimeBetween(Boolean isCooperative, String state, Date dayBegin, Date dayEnd);

    public Integer countByUserOrderState(String userOrderState);

    public Integer countByUserOrderStateAndCreateTimeBetween(String userOrderState, Date dayBegin, Date dayEnd);

}
