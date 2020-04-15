package com.plansolve.farm.repository.order;

import com.plansolve.farm.model.database.order.BidOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/6
 * @Description:
 **/
public interface BidOrderRepository extends JpaRepository<BidOrder, Integer> {

    public BidOrder findByBidOrderNo(String bidOrderNo);

    public List<BidOrder> findByStartTimeAfterAndBidOrderState(Date date, String bidOrderState);

}
