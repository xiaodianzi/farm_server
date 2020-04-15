package com.plansolve.farm.repository.order;

import com.plansolve.farm.model.database.order.WxPayOrderNotify;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2019/1/16
 * @Description:
 **/
public interface WxPayOrderNotifyRepository extends JpaRepository<WxPayOrderNotify, Long> {

    public WxPayOrderNotify findByTransactionId(String transactionId);

}
