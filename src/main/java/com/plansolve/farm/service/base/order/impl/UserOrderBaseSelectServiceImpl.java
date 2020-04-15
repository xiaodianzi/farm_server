package com.plansolve.farm.service.base.order.impl;

import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.database.log.OrderChangeLog;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.OperatorStateEnum;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.repository.log.OrderChangeLogRepository;
import com.plansolve.farm.repository.order.OrderOperatorRepository;
import com.plansolve.farm.repository.order.UserOrderRepository;
import com.plansolve.farm.service.base.order.UserOrderBaseSelectService;
import com.plansolve.farm.service.client.CooperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description:
 **/
@Slf4j
@Service
public class UserOrderBaseSelectServiceImpl implements UserOrderBaseSelectService {

    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private OrderOperatorRepository operatorRepository;
    @Autowired
    private OrderChangeLogRepository logRepository;
    @Autowired
    private CooperationService cooperationService;

    @Override
    public UserOrder getByUserOrder(Long idUserOrder) {
        return userOrderRepository.findByIdUserOrder(idUserOrder);
    }

    @Override
    public UserOrder getByUserOrder(String userOrderNo) {
        return userOrderRepository.findByUserOrderNo(userOrderNo);
    }

    @Override
    public Page<UserOrder> pageByOrderState(String orderState, Page<UserOrder> page, Sort sort) {
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), orderState);
                query.where(criteriaBuilder.and(predicate1));
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(orderPage.getTotalElements());
        page.setRows(orderPage.getContent());
        return page;
    }

    @Override
    public Page<UserOrder> pageByOrderStateNot(List<String> orderStates, Page<UserOrder> page, Sort sort) {
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (orderStates != null && orderStates.size() > 0) {
                    for (String orderState : orderStates) {
                        Predicate predicate = criteriaBuilder.notEqual(root.get("userOrderState").as(String.class), orderState);
                        predicates.add(predicate);
                    }
                } else {
                    Predicate predicate = criteriaBuilder.notEqual(root.get("userOrderState").as(String.class), OrderStateEnum.DELETED.getState());
                    predicates.add(predicate);
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(orderPage.getTotalElements());
        page.setRows(orderPage.getContent());
        return page;
    }

    /********************************************种植户查询********************************************/
    @Override
    public Page<UserOrder> pageFarmerOrder(Page<UserOrder> page, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("createBy").as(Long.class), user.getIdUser());
                query.where(criteriaBuilder.and(predicate1));
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(orderPage.getTotalElements());
        page.setRows(orderPage.getContent());
        return page;
    }

    @Override
    public Page<UserOrder> pageFarmerOrder(Page<UserOrder> page, String state, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("createBy").as(Long.class), user.getIdUser());
                Predicate predicate2 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), state);
                query.where(criteriaBuilder.and(predicate1, predicate2));
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(orderPage.getTotalElements());
        page.setRows(orderPage.getContent());
        return page;
    }

    @Override
    public Page<UserOrder> pageFarmerOrder(Page<UserOrder> page, List<String> states, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("createBy").as(Long.class), user.getIdUser());
                if (states != null && states.size() > 0) {
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("userOrderState"));
                    for (String state : states) {
                        in.value(state);
                    }
                    query.where(criteriaBuilder.and(predicate1, in));
                } else {
                    query.where(criteriaBuilder.and(predicate1));
                }
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(orderPage.getTotalElements());
        page.setRows(orderPage.getContent());
        return page;
    }

    @Override
    public List<UserOrder> listFarmerOrderByState(String state, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        List<UserOrder> orders = userOrderRepository.findByCreateByAndUserOrderState(user.getIdUser(), state, sort);
        return orders;
    }

    @Override
    public List<UserOrder> listFarmerOrderByStates(List<String> states, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        List<UserOrder> orders = userOrderRepository.findByCreateByAndUserOrderStateIn(user.getIdUser(), states, sort);
        return orders;
    }

    @Override
    public List<UserOrder> listFarmerOnlinePaymentOrders(Date start, Date end, Long idUser) {
        List<UserOrder> orders = userOrderRepository.findByIsOnlinePaymentAndCreateByAndPaymentTimeAfterAndPaymentTimeBefore(true, idUser, start, end);
        return orders;
    }
    /********************************************种植户查询********************************************/
    /********************************************农机手查询********************************************/
    @Override
    public Page<UserOrder> pageOperatorOrderByOperator(Page<UserOrder> page, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<OrderOperator> operatorPage = operatorRepository.findAll(new Specification<OrderOperator>() {
            @Override
            public Predicate toPredicate(Root<OrderOperator> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("idUser").as(Long.class), user.getIdUser());
                Predicate predicate2 = criteriaBuilder.notEqual(root.get("operatorState").as(String.class), OperatorStateEnum.CANCELED.getState());
                Predicate predicate3 = criteriaBuilder.notEqual(root.get("operatorState").as(String.class), OperatorStateEnum.REFUSED.getState());
                query.where(criteriaBuilder.and(predicate1, predicate2, predicate3));
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(operatorPage.getTotalElements());

        List<OrderOperator> operators = operatorPage.getContent();
        List<UserOrder> orders = new ArrayList<>();
        if (operators != null && operators.size() > 0) {
            for (OrderOperator operator : operators) {
                UserOrder order = userOrderRepository.findByIdUserOrder(operator.getIdUserOrder());
                orders.add(order);
            }
        }
        page.setRows(orders);
        return page;
    }

    @Override
    public Page<UserOrder> pageOperatorOrderByOperatorState(Page<UserOrder> page, String state, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<OrderOperator> operatorPage = operatorRepository.findAll(new Specification<OrderOperator>() {
            @Override
            public Predicate toPredicate(Root<OrderOperator> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("idUser").as(Long.class), user.getIdUser());
                Predicate predicate2 = criteriaBuilder.equal(root.get("operatorState").as(String.class), state);
                query.where(criteriaBuilder.and(predicate1, predicate2));
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(operatorPage.getTotalElements());

        List<OrderOperator> operators = operatorPage.getContent();
        List<UserOrder> orders = new ArrayList<>();
        if (operators != null && operators.size() > 0) {
            for (OrderOperator operator : operators) {
                UserOrder order = userOrderRepository.findByIdUserOrder(operator.getIdUserOrder());
                orders.add(order);
            }
        }
        page.setRows(orders);
        return page;
    }

    @Override
    public Page<UserOrder> pageOperatorOrderByOperatorStates(Page<UserOrder> page, List<String> states, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<OrderOperator> operatorPage = operatorRepository.findAll(new Specification<OrderOperator>() {
            @Override
            public Predicate toPredicate(Root<OrderOperator> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("idUser").as(Long.class), user.getIdUser());
                if (states != null && states.size() > 0) {
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("operatorState"));
                    for (String state : states) {
                        in.value(state);
                    }
                    query.where(criteriaBuilder.and(predicate1, in));
                } else {
                    query.where(criteriaBuilder.and(predicate1));
                }
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(operatorPage.getTotalElements());

        List<OrderOperator> operators = operatorPage.getContent();
        List<UserOrder> orders = new ArrayList<>();
        if (operators != null && operators.size() > 0) {
            for (OrderOperator operator : operators) {
                UserOrder order = userOrderRepository.findByIdUserOrder(operator.getIdUserOrder());
                orders.add(order);
            }
        }
        page.setRows(orders);
        return page;
    }

    @Override
    public List<UserOrder> listOperatorOrderByOperatorState(String state, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        List<OrderOperator> operators = operatorRepository.findByIdUserAndOperatorState(user.getIdUser(), state, sort);
        ArrayList<UserOrder> orders = new ArrayList<>();
        if (operators != null && operators.size() > 0) {
            for (OrderOperator operator : operators) {
                UserOrder order = userOrderRepository.findByIdUserOrder(operator.getIdUserOrder());
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public List<UserOrder> listOperatorOrderByOperatorStates(List<String> states, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        List<OrderOperator> operators = operatorRepository.findByIdUserAndOperatorStateIn(user.getIdUser(), states, sort);
        ArrayList<UserOrder> orders = new ArrayList<>();
        if (operators != null && operators.size() > 0) {
            for (OrderOperator operator : operators) {
                UserOrder order = userOrderRepository.findByIdUserOrder(operator.getIdUserOrder());
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public Page<UserOrder> pageOperatorOrder(Page<UserOrder> page, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("receiveBy").as(Long.class), user.getIdUser());
                query.where(criteriaBuilder.and(predicate1));
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(orderPage.getTotalElements());
        page.setRows(orderPage.getContent());
        return page;
    }

    @Override
    public Page<UserOrder> pageOperatorOrder(Page<UserOrder> page, String state, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("receiveBy").as(Long.class), user.getIdUser());
                Predicate predicate2 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), state);
                query.where(criteriaBuilder.and(predicate1, predicate2));
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(orderPage.getTotalElements());
        page.setRows(orderPage.getContent());
        return page;
    }

    @Override
    public Page<UserOrder> pageOperatorOrder(Page<UserOrder> page, List<String> states, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("receiveBy").as(Long.class), user.getIdUser());
                if (states != null && states.size() > 0) {
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("userOrderState"));
                    for (String state : states) {
                        in.value(state);
                    }
                    query.where(criteriaBuilder.and(predicate1, in));
                } else {
                    query.where(criteriaBuilder.and(predicate1));
                }
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(orderPage.getTotalElements());
        page.setRows(orderPage.getContent());
        return page;
    }

    @Override
    public List<UserOrder> listOperatorOrderByState(String state, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        List<UserOrder> orders = userOrderRepository.findByReceiveByAndUserOrderState(user.getIdUser(), state, sort);
        return orders;
    }

    @Override
    public List<UserOrder> listOperatorOrderByStates(List<String> states, User user) {
        // 根据主键倒序
        Sort sort = new Sort(Sort.Direction.DESC, "idUserOrder");
        List<UserOrder> orders = userOrderRepository.findByReceiveByAndUserOrderStateIn(user.getIdUser(), states, sort);
        return orders;
    }

    @Override
    public List<UserOrder> listOperatorCooperationOrderByState(String state, User user) {
        List<UserOrder> orders = new ArrayList<>();
        if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
            List<User> members = cooperationService.members(user.getIdCooperation());
            if (members != null && members.size() > 0) {
                List<Long> ids = new ArrayList<>();
                for (User member : members) {
                    ids.add(member.getIdUser());
                }
                orders = userOrderRepository.findByTargetAndCreateByInAndUserOrderState("我的合作社", ids, state);
            }
        }
        return orders;
    }

    @Override
    public List<UserOrder> listOperatorOnlinePaymentOrders(Date start, Date end, Long idUser) {
        List<UserOrder> orders = userOrderRepository.findByIsOnlinePaymentAndReceiveByAndPaymentTimeAfterAndPaymentTimeBefore(true, idUser, start, end);
        return orders;
    }
    /********************************************农机手查询********************************************/
    /********************************************农机手任务查询********************************************/

    @Override
    public List<OrderOperator> findOrderOperator(Long idUserOrder, Long idUser) {
        List<OrderOperator> operators = operatorRepository.findByIdUserAndIdUserOrderAndOperatorStateNotIn(idUser, idUserOrder, Arrays.asList(OperatorStateEnum.REFUSED.getState(), OperatorStateEnum.CANCELED.getState()));
        return operators;
    }

    /********************************************农机手任务查询********************************************/
    /********************************************查询订单日志********************************************/

    @Override
    public List<OrderChangeLog> listOrderChangeLog(Long idUserOrder) {
        return logRepository.findByIdUserOrder(idUserOrder);
    }

    @Override
    public List<OrderChangeLog> listOrderChangeLog(Long idUserOrder, Long idUser) {
        return logRepository.findByIdUserOrderAndChangeBy(idUserOrder, idUser);
    }

    /********************************************查询订单日志********************************************/
}
