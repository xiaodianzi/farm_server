package com.plansolve.farm.repository.order;

import com.plansolve.farm.model.database.order.AppointOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/2
 * @Description:
 **/
public interface AppointOrderRepository extends JpaRepository<AppointOrder, Integer> {

    public AppointOrder findByAppointOrderNo(String appointOrderNo);

    public List<AppointOrder> findByCreateBy(Long createBy);

}
