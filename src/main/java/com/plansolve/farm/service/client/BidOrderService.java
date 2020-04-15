package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.order.BidOrderDTO;
import com.plansolve.farm.model.client.order.BidOrderOperatorDTO;
import com.plansolve.farm.model.database.order.BidOrder;
import com.plansolve.farm.model.database.order.BidOrderOperator;
import com.plansolve.farm.model.database.user.User;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/13
 * @Description:
 **/
public interface BidOrderService {

    /**
     * 获取当前用户的可接竞价订单
     *
     * @param user
     * @return
     */
    public List<BidOrder> getAvailableBidOrders(User user);

    /**
     * 根据订单号获取竞价订单
     *
     * @param bidOrderNo
     * @return
     */
    public BidOrder getBidOrder(String bidOrderNo);

    /**
     * 竞价订单申请
     *
     * @param bidOrderOperatorDTO
     * @param user
     * @return
     */
    public BidOrderOperator applyForBidOrder(BidOrderOperatorDTO bidOrderOperatorDTO, User user);

    /**
     * 获取该用户所有接过的竞价订单
     *
     * @param user
     * @return
     */
    public List<BidOrder> getUserBidOrder(User user);

    /**
     * 封装竞价订单对象
     *
     * @param bidOrder
     * @return
     */
    public BidOrderDTO loadDTO(BidOrder bidOrder);

    /**
     * 批量封装竞价订单对象
     *
     * @param bidOrders
     * @return
     */
    public List<BidOrderDTO> loadDTOs(List<BidOrder> bidOrders);

}
