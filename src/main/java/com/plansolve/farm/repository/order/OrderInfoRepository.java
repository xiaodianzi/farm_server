package com.plansolve.farm.repository.order;

import com.plansolve.farm.model.database.order.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/2
 * @Description:
 **/
public interface OrderInfoRepository extends JpaRepository<OrderInfo, Long> {

    public List<OrderInfo> findByIdUserOrder(Long idUserOrder);

    public OrderInfo findByIdUserOrderAndIdUser(Long idUserOrder, Long idUser);

    @Query(nativeQuery = true, value = "SELECT * from (select * from order_info WHERE order_state <> 'canceled' GROUP BY id_user_order) as oi GROUP BY oi.province")
    public List<OrderInfo> findAllBySql();

    @Query(nativeQuery = true, value = "SELECT COUNT(oi.id_order_info) from (\n" +
            "SELECT * FROM order_info WHERE order_state <> 'canceled' GROUP BY id_user_order) as oi WHERE oi.province = :province")
    public Integer countByDistinctProvince(@Param("province") String province);

    @Query(nativeQuery = true, value = "SELECT * FROM order_info WHERE order_state <> :orderState GROUP BY id_user_order")
    public List<OrderInfo> findDistinctIdUserOrderAndOrderStateNot(@Param("orderState") String orderState);

    @Query(nativeQuery = true, value = "SELECT * from (SELECT * FROM order_info WHERE order_state <> :orderState GROUP BY id_user_order) as oi GROUP BY oi.id_machinery_parent_type")
    public List<OrderInfo> findDistinctIdMachineryParentTypeAndOrderStateNot(@Param("orderState") String orderState);

    @Query(nativeQuery = true, value = "SELECT count(distinct o.id_user_order) FROM order_info o WHERE o.id_machinery_parent_type = :idMachineryParentType AND o.order_state <> :orderState")
    public Integer countGroupByIdMachineryParentType(@Param("idMachineryParentType") Integer idMachineryParentType, @Param("orderState") String orderState);

    @Query(nativeQuery = true, value = "SELECT uo.create_time from order_info uo GROUP BY SUBSTRING(uo.create_time, 1, 10)")
    public List<Date> queryDistinctCreateTime();

    @Query(nativeQuery = true, value = "SELECT count(distinct o.id_user_order) FROM order_info o WHERE o.id_machinery_parent_type = :idMachineryParentType AND o.order_state <> :orderState AND o.create_time between :dayBegin and :dayEnd")
    public Integer countByIdMachineryParentTypeAndCreateTime(@Param("idMachineryParentType") Integer idMachineryParentType, @Param("orderState") String orderState, @Param("dayBegin") Date dayBegin, @Param("dayEnd") Date dayEnd);

}
