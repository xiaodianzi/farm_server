package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.model.database.log.BidOrderChangeLog;
import com.plansolve.farm.model.database.order.BidOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.BidOrderStateEnum;
import com.plansolve.farm.model.enums.type.OrderLogTypeEnum;
import com.plansolve.farm.repository.log.BidOrderChangeLogRepository;
import com.plansolve.farm.repository.order.BidOrderRepository;
import com.plansolve.farm.service.console.AppBidOrderService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/8/13
 * @Description:
 **/
@Service
public class AppBidOrderServiceImpl implements AppBidOrderService {

    @Autowired
    private BidOrderRepository bidOrderRepository;
    @Autowired
    private BidOrderChangeLogRepository logRepository;

    /**
     * 创建竞价订单
     *
     * @param bidOrder
     * @param user
     * @return
     */
    @Override
    @Transactional
    public BidOrder createOrder(BidOrder bidOrder, User user) {
        String orderNo = getOrderNo();
        bidOrder.setBidOrderNo(orderNo);
        bidOrder.setBidOrderState(BidOrderStateEnum.WAITING.getState());
        bidOrder.setCreateBy(user.getIdUser());
        bidOrder.setCreateTime(new Date());

        bidOrder =  save(bidOrder, user);
        return bidOrder;
    }

    /**
     * 保存竞价订单
     *
     * @param bidOrder
     * @param user
     * @return
     */
    private BidOrder save(BidOrder bidOrder, User user) {
        bidOrder = bidOrderRepository.save(bidOrder);

        BidOrderChangeLog log = new BidOrderChangeLog();
        log.setIdBidOrder(bidOrder.getIdBidOrder());
        log.setChangeType(OrderLogTypeEnum.CREATE.getType());
        log.setChangeTime(new Date());
        log.setChangeBy(user.getIdUser());
        log.setDetail("用户[" + user.getMobile() + "]创建竞价订单[" + bidOrder.getBidOrderNo() + "]");
        logRepository.save(log);
        return bidOrder;
    }

    /**
     * 订单号生成
     *
     * @return
     */
    private String getOrderNo() {
        String orderNo = String.valueOf(Math.round(Math.random() * 1000000));
        orderNo = StringUtil.prefixStr(orderNo, 6, "0");
        orderNo = DateUtils.getDate("yyyy/MM/dd").replace("/", "").substring(2) + orderNo;
        BidOrder order = bidOrderRepository.findByBidOrderNo(orderNo);
        if (order == null) {
            return orderNo;
        } else {
            return getOrderNo();
        }
    }

}
