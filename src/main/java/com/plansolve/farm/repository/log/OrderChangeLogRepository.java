package com.plansolve.farm.repository.log;

import com.plansolve.farm.model.database.log.OrderChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/
public interface OrderChangeLogRepository extends JpaRepository<OrderChangeLog, Long> {

    public List<OrderChangeLog> findByIdUserOrder(Long idUserOrder);

    public List<OrderChangeLog> findByIdUserOrderAndChangeBy(Long idUserOrder, Long changeBy);

}
