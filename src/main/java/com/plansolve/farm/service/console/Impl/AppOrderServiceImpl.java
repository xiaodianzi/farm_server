package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.model.console.AppOrderDTO;
import com.plansolve.farm.model.database.log.OrderChangeLog;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.repository.log.OrderChangeLogRepository;
import com.plansolve.farm.repository.order.UserOrderRepository;
import com.plansolve.farm.service.base.order.UserOrderOperatorBaseService;
import com.plansolve.farm.service.console.AppOrderService;
import com.plansolve.farm.service.console.user.ConsoleUserService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EnumUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/2
 * @Description:
 **/
@Service
public class AppOrderServiceImpl implements AppOrderService {

    @Autowired
    private UserOrderRepository orderRepository;
    @Autowired
    private OrderChangeLogRepository logRepository;
    @Autowired
    private ConsoleUserService userService;
    @Autowired
    private UserOrderOperatorBaseService orderOperatorBaseService;

    /**
     * 封装订单传输对象
     *
     * @param orders
     * @return
     */
    @Override
    public List<AppOrderDTO> loadDTOs(List<UserOrder> orders) {
        List<AppOrderDTO> list = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (UserOrder order : orders) {
                AppOrderDTO dto = new AppOrderDTO();
                BeanUtils.copyProperties(order, dto);
                dto.setUserOrderState(EnumUtil.getByState(order.getUserOrderState(), OrderStateEnum.class).getMessage());
                User user1 = userService.findUser(order.getCreateBy());
                dto.setCreateByDetail(user1.getNickname() + ":" + user1.getMobile());
                dto.setCreateTime(DateUtils.formatDateTime(order.getCreateTime()));
                dto.setUpdateTime(DateUtils.formatDateTime(order.getUpdateTime()));
                dto.setStartTime(DateUtils.formatDate(order.getStartTime(), "yyyy-MM-dd"));
                if (order.getReceiveBy() != null && order.getReceiveBy() > 0) {
                    User user2 = userService.findUser(order.getReceiveBy());
                    dto.setReceiveByDetail(user2.getNickname() + ":" + user2.getMobile());
                }
                if (order.getReportedBy() != null && order.getReportedBy() > 0) {
                    if (order.getReportedBy().equals(order.getReceiveBy())) {
                        dto.setReportedByDetail(dto.getReceiveByDetail());
                    } else {
                        User user2 = userService.findUser(order.getReceiveBy());
                        dto.setReportedByDetail(user2.getNickname() + ":" + user2.getMobile());
                    }
                }
                list.add(dto);
            }
        }
        return list;
    }

    /**
     * 查询订单
     *
     * @param idUserOrder
     * @return
     */
    @Override
    public UserOrder findOrder(Long idUserOrder) {
        return orderRepository.findByIdUserOrder(idUserOrder);
    }

    /**
     * 分页查询订单
     *
     * @param userOrderNo
     * @param userOrderState
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Page<UserOrder> findAllOrders(String userOrderNo, String userOrderState, Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "idUserOrder");
        Page<UserOrder> userOrderPage;
        if ((userOrderNo == null || userOrderNo.isEmpty()) && (userOrderState == null || userOrderState.isEmpty())) {
            userOrderPage = orderRepository.findAll(pageable);
        } else {
            userOrderPage = orderRepository.findAll(new Specification<UserOrder>() {
                @Override
                public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    if (userOrderNo != null && !userOrderNo.isEmpty() && userOrderState != null && !userOrderState.isEmpty()) {
                        Predicate predicate1 = criteriaBuilder.like(root.get("userOrderNo").as(String.class), "%" + userOrderNo + "%");
                        Predicate predicate2 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), userOrderState);
                        query.where(criteriaBuilder.and(predicate1, predicate2));
                    } else {
                        Predicate predicate;
                        if (userOrderNo != null && !userOrderNo.isEmpty()) {
                            predicate = criteriaBuilder.like(root.get("userOrderNo").as(String.class), "%" + userOrderNo + "%");
                        } else {
                            predicate = criteriaBuilder.equal(root.get("userOrderState").as(String.class), userOrderState);
                        }
                        query.where(criteriaBuilder.and(predicate));
                    }
                    return query.getRestriction();
                }
            }, pageable);
        }
        return userOrderPage;
    }

    /**
     * 获取订单工作人员
     *
     * @param idUserOrder
     * @return
     */
    @Override
    public List<User> listOrderWorker(Long idUserOrder) {
        List<OrderOperator> operators = orderOperatorBaseService.listOrderOperator(idUserOrder);
        List<User> workers = new ArrayList<>();
        if (operators != null && operators.size() > 0) {
            for (OrderOperator operator : operators) {
                User user = userService.findUser(operator.getIdUser());
                workers.add(user);
            }
        }
        return workers;
    }

    /**
     * 获取订单日志
     *
     * @param idUserOrder
     * @return
     */
    @Override
    public List<OrderChangeLog> getOrderLogs(Long idUserOrder) {
        List<OrderChangeLog> logs = logRepository.findByIdUserOrder(idUserOrder);
        return logs;
    }
}
