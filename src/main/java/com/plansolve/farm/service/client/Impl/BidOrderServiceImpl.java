package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.client.order.BidOrderDTO;
import com.plansolve.farm.model.client.order.BidOrderOperatorDTO;
import com.plansolve.farm.model.database.log.BidOrderChangeLog;
import com.plansolve.farm.model.database.order.BidOrder;
import com.plansolve.farm.model.database.order.BidOrderOperator;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.BidOperatorStateEnum;
import com.plansolve.farm.model.enums.state.BidOrderStateEnum;
import com.plansolve.farm.model.enums.type.OrderLogTypeEnum;
import com.plansolve.farm.repository.log.BidOrderChangeLogRepository;
import com.plansolve.farm.repository.order.BidOrderOperatorRepository;
import com.plansolve.farm.repository.order.BidOrderRepository;
import com.plansolve.farm.service.client.BidOrderService;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/14
 * @Description:
 **/
@Service
public class BidOrderServiceImpl implements BidOrderService {

    @Autowired
    private BidOrderRepository bidOrderRepository;
    @Autowired
    private BidOrderOperatorRepository bidOrderOperatorRepository;
    @Autowired
    private BidOrderChangeLogRepository logRepository;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private UserService userService;

    /**
     * 获取当前用户的可接竞价订单
     *
     * @param user
     * @return
     */
    @Override
    public List<BidOrder> getAvailableBidOrders(User user) {
        if (user.getOperator() || cooperationService.proprieter(user)) {
            List<BidOrder> bidOrders = bidOrderRepository.findByStartTimeAfterAndBidOrderState(new Date(), BidOrderStateEnum.WAITING.getState());
            return bidOrders;
        } else {
            throw new PermissionException("[该用户不具有接单资格]");
        }
    }

    /**
     * 根据订单号获取竞价订单
     *
     * @param bidOrderNo
     * @return
     */
    @Override
    public BidOrder getBidOrder(String bidOrderNo) {
        BidOrder bidOrder = bidOrderRepository.findByBidOrderNo(bidOrderNo);
        if (bidOrder != null) {
            return bidOrder;
        } else {
            throw new ParamErrorException("[无法获取该订单]");
        }
    }

    /**
     * 竞价订单申请
     *
     * @param bidOrderOperatorDTO
     * @param user
     * @return
     */
    @Override
    public BidOrderOperator applyForBidOrder(BidOrderOperatorDTO bidOrderOperatorDTO, User user) {
        BidOrderOperator bidOrderOperator = new BidOrderOperator();
        BeanUtils.copyProperties(bidOrderOperatorDTO, bidOrderOperator);

        BidOrder bidOrder = getBidOrder(bidOrderOperatorDTO.getBidOrderNo());
        bidOrderOperator.setIdBidOrder(bidOrder.getIdBidOrder());
        bidOrderOperator.setIdUser(user.getIdUser());
        bidOrderOperator.setOperatorState(BidOperatorStateEnum.APPLYING.getState());
        bidOrderOperator.setStartTime(bidOrder.getStartTime());
        bidOrderOperator.setEndTime(DateUtils.getDate_PastOrFuture_Day(bidOrder.getStartTime(), bidOrder.getPeriod()));
        bidOrderOperator.setIdCooperation(user.getIdCooperation());

        bidOrderOperator = save(bidOrderOperator, bidOrder.getBidOrderNo(), user);
        return bidOrderOperator;
    }

    /**
     * 获取该用户所有接过的竞价订单
     *
     * @param user
     * @return
     */
    @Override
    public List<BidOrder> getUserBidOrder(User user) {
        List<BidOrder> bidOrders = new ArrayList<>();
        List<BidOrderOperator> operators = bidOrderOperatorRepository.findByIdUser(user.getIdUser());
        if (operators != null && operators.size() > 0) {

        }
        return bidOrders;
    }

    /**
     * 封装竞价订单对象
     *
     * @param bidOrder
     * @return
     */
    @Override
    public BidOrderDTO loadDTO(BidOrder bidOrder) {
        BidOrderDTO bidOrderDTO = new BidOrderDTO();
        BeanUtils.copyProperties(bidOrder, bidOrderDTO);

        User user = userService.findUser(bidOrder.getCreateBy());
        if (user != null) {
            UserDTO userDTO = userService.loadDTO(user, false);
            bidOrderDTO.setCreateBy(userDTO);
            return bidOrderDTO;
        } else {
            throw new ParamErrorException("[无法查询到下单人]");
        }
    }

    /**
     * 批量封装竞价订单对象
     *
     * @param bidOrders
     * @return
     */
    @Override
    public List<BidOrderDTO> loadDTOs(List<BidOrder> bidOrders) {
        List<BidOrderDTO> bidOrderDTOS = new ArrayList<>();
        if (bidOrders != null && bidOrders.size() > 0) {
            for (BidOrder bidOrder : bidOrders) {
                BidOrderDTO bidOrderDTO = loadDTO(bidOrder);
                bidOrderDTOS.add(bidOrderDTO);
            }
        }
        return bidOrderDTOS;
    }

    /**
     * 保存申请人信息
     *
     * @param bidOrderOperator
     * @param user
     * @return
     */
    private BidOrderOperator save(BidOrderOperator bidOrderOperator, String bidOrderNo, User user) {
        bidOrderOperator = bidOrderOperatorRepository.save(bidOrderOperator);

        BidOrderChangeLog log = new BidOrderChangeLog();
        log.setIdBidOrder(bidOrderOperator.getIdBidOrder());
        log.setChangeType(OrderLogTypeEnum.GET_IT.getType());
        log.setChangeTime(new Date());
        log.setChangeBy(user.getIdUser());
        log.setDetail("用户[" + user.getMobile() + "]申请成为竞价订单[" + bidOrderNo + "]工作人员");
        logRepository.save(log);
        return bidOrderOperator;
    }

}
