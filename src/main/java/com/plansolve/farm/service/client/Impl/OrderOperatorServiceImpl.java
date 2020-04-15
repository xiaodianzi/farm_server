package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.log.OrderChangeLog;
import com.plansolve.farm.model.database.order.OrderInfo;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.OperatorStateEnum;
import com.plansolve.farm.model.enums.type.OrderLogTypeEnum;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.repository.log.OrderChangeLogRepository;
import com.plansolve.farm.repository.order.OrderInfoRepository;
import com.plansolve.farm.repository.order.OrderOperatorRepository;
import com.plansolve.farm.repository.order.UserOrderRepository;
import com.plansolve.farm.service.client.*;
import com.plansolve.farm.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * @Author: 高一平
 * @Date: 2018/8/3
 * @Description:
 **/
@Service
public class OrderOperatorServiceImpl implements OrderOperatorService {

    private final static Logger logger = LoggerFactory.getLogger(OrderOperatorService.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private FarmlandService farmlandService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private OrderOperatorRepository orderOperatorRepository;
    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private OrderChangeLogRepository logRepository;
    @Autowired
    private OrderInfoRepository infoRepository;

    /**
     * 确定订单工作人员（初始化：选择是否协同作业）
     *
     * @param user          当前用户
     * @param isCooperative 是否协同作业
     * @param workers       协同作业工作人员
     * @param orderNo       订单号
     * @return
     */
    @Override
    @Transactional
    public List<UserDTO> setOrderWorkers(User user, Boolean isCooperative, List<String> workers, String orderNo) {
        UserOrder order = orderService.getUserOrder(orderNo);
        if (order.getReceiveBy().equals(user.getIdUser()) == false) {
            throw new PermissionException("[当前用户不是该订单接单人，无权操作该订单]");
        } else {
            if (order.getUserOrderState().equals(OrderStateEnum.WORKING.getState())) {
                List<UserDTO> operators = new ArrayList<>();
                // 添加接单人为该单工作人员
                OrderOperator orderOperator = getOperator(user.getIdUser(), order.getIdUserOrder());
                if (orderOperator == null) {
                    orderOperator = new OrderOperator();
                    orderOperator.setIdUserOrder(order.getIdUserOrder());
                    orderOperator.setIdUser(user.getIdUser());
                    orderOperator.setOperatorState(OperatorStateEnum.ACCEPTED.getState());
                    orderOperator.setStartTime(order.getStartTime());
                    orderOperator.setEndTime(DateUtils.getDate_PastOrFuture_Day(order.getStartTime(), order.getPeriod()));
                    orderOperator.setIdCooperation(user.getIdCooperation());
                    save(orderOperator, user.getMobile(), user, order);
                }
                operators.add(userService.loadDTO(user, false));

                // 是否协同作业
                boolean proprieter = cooperationService.proprieter(user);
                if (proprieter) {
                    order.setCooperative(isCooperative);
                    userOrderRepository.save(order);
                    if (isCooperative) {
                        // 若有协同作业人员，添加相关信息，并发出邀请
                        if (workers != null && workers.size() > 0) {
                            List<UserDTO> userDTOS = managerWorkers(orderNo, workers, true, user);
                            operators.addAll(userDTOS);
                        }
                    }

                    // 生成日志
                    OrderChangeLog log = new OrderChangeLog();
                    log.setIdUserOrder(order.getIdUserOrder());
                    log.setChangeBy(user.getIdUser());
                    log.setChangeTime(new Date());
                    log.setChangeType(OrderLogTypeEnum.UPDATE.getType());
                    log.setDetail("用户[" + user.getMobile() + "]确认作业方式，是否协同作业：" + isCooperative);
                    logRepository.save(log);
                } else {
                    order.setCooperative(false);
                    userOrderRepository.save(order);
                }
                return operators;
            } else {
                throw new PermissionException("[该订单已不可修改工作人员，该单状态为" + order.getUserOrderState() + "]");
            }
        }
    }

    /**
     * 管理订单工作人员
     *
     * @param orderNo 订单号
     * @param workers 添加或减少的工作人员
     * @param isAdd   添加或减少
     * @param user    管理人
     * @return
     */
    @Override
    @Transactional
    public List<UserDTO> managerWorkers(String orderNo, List<String> workers, Boolean isAdd, User user) {
        UserOrder order = orderService.getUserOrder(orderNo);
        // 判断是否是协同作业订单
        if (order.getCooperative()) {
            // 判断是否是接单人
            if (order.getReceiveBy().equals(user.getIdUser())) {
                boolean proprieter = cooperationService.proprieter(user);
                if (proprieter) {
                    if (isAdd) {
                        if (workers != null && workers.size() > 0) {
                            for (String worker : workers) {
                                managerWorker(order, worker, true, user);
                            }
                        }
                    } else if (isAdd == false) {
                        if (workers != null && workers.size() > 0) {
                            for (String worker : workers) {
                                managerWorker(order, worker, false, user);
                            }
                        }
                    } else {
                        throw new NullParamException("[请指定增加或减少操作]");
                    }
                    List<UserDTO> orderOperators = getOrderOperators(orderNo, user);
                    return orderOperators;
                } else {
                    throw new PermissionException("[该用户不是社长身份，无法进行此操作]");
                }
            } else {
                OrderOperator operator = getOperator(user.getIdUser(), order.getIdUserOrder());
                if (operator != null) {
                    // 不是接单人，也不是社长，是否是队长
                    boolean captain = cooperationService.captain(user);
                    if (captain) {
                        if (workers != null && workers.size() > 0) {
                            for (String worker : workers) {
                                managerWorker(order, worker, false, user);
                            }
                        }
                        List<UserDTO> orderOperators = getOrderOperators(orderNo, user);
                        return orderOperators;
                    } else {
                        throw new PermissionException("[该用户既不是社长身份，也不是队长身份，无法进行此操作]");
                    }
                } else {
                    throw new PermissionException("[该用户不是该单工作人员，无法进行此操作]");
                }
            }
        } else {
            throw new PermissionException("[此订单不是协同作业订单]");
        }
    }

    /**
     * 添加/删除工作人员
     *
     * @param order  订单号
     * @param mobile 工作人员手机号码
     * @param isAdd
     * @param user   修改人
     */
    private void managerWorker(UserOrder order, String mobile, Boolean isAdd, User user) {
        User worker = userService.findByMobile(mobile);
        if (worker != null) {
            if (user.getIdCooperation().equals(worker.getIdCooperation())) {
                if (isAdd) {
                    OrderOperator operator = getOperator(worker.getIdUser(), order.getIdUserOrder());
                    if (operator == null) {
                        operator = new OrderOperator();
                        operator.setIdUser(worker.getIdUser());
                        operator.setIdUserOrder(order.getIdUserOrder());
                        operator.setOperatorState(OperatorStateEnum.INVITED.getState());
                        operator.setStartTime(order.getStartTime());
                        operator.setEndTime(DateUtils.getDate_PastOrFuture_Day(order.getStartTime(), order.getPeriod()));
                        operator.setIdCooperation(worker.getIdCooperation());
                        orderOperatorRepository.save(operator);

                        // 生成日志
                        OrderChangeLog log = new OrderChangeLog();
                        log.setIdUserOrder(order.getIdUserOrder());
                        log.setChangeBy(user.getIdUser());
                        log.setChangeTime(new Date());
                        log.setChangeType(OrderLogTypeEnum.WORKER_ADD.getType());
                        log.setDetail("添加用户[" + mobile + "]为工作人员");
                        logRepository.save(log);
                    } else {
                        logger.error("用户[" + worker.getMobile() + "]已经是该单工作人员");
                    }
                } else {
                    OrderOperator operator = getOperator(worker.getIdUser(), order.getIdUserOrder());
                    if (operator != null) {
                        if (operator.getOperatorState().equals(OperatorStateEnum.INVITED.getState())
                                || operator.getOperatorState().equals(OperatorStateEnum.ACCEPTED.getState())) {
                            operator.setOperatorState(OperatorStateEnum.CANCELED.getState());
                            orderOperatorRepository.save(operator);

                            // 生成日志
                            OrderChangeLog log = new OrderChangeLog();
                            log.setIdUserOrder(order.getIdUserOrder());
                            log.setChangeBy(user.getIdUser());
                            log.setChangeTime(new Date());
                            log.setChangeType(OrderLogTypeEnum.WORKER_CUT.getType());
                            log.setDetail("取消用户[" + mobile + "]工作人员资格");
                            logRepository.save(log);
                        } else if (operator.getOperatorState().equals(OperatorStateEnum.FINISHED.getState())) {
                            throw new PermissionException("[用户" + mobile + "]已提交完工，不可进行该操作");
                        }
                    }
                }
            } else {
                throw new PermissionException("[用户" + mobile + "不是该社社员，无法进行此操作]");
            }
        } else {
            throw new ParamErrorException("[无法查询出用户：" + mobile + "]");
        }
    }

    /**
     * 用户接受或拒绝协同作业的邀请
     *
     * @param user    当前用户
     * @param orderNo 操作订单号
     * @param accept  是否接受
     * @return
     */
    @Override
    @Transactional
    public Boolean acceptOrder(User user, String orderNo, Boolean accept) {
        UserOrder order = orderService.getUserOrder(orderNo);
        OrderOperator operator = getOperator(user.getIdUser(), order.getIdUserOrder());
        if (operator == null) {
            throw new NullParamException("[查询不到用户与该订单相关情况]");
        } else {
            if (operator.getOperatorState().equals(OperatorStateEnum.INVITED.getState())) {
                if (accept) {
                    operator.setOperatorState(OperatorStateEnum.ACCEPTED.getState());
                } else {
                    operator.setOperatorState(OperatorStateEnum.REFUSED.getState());
                }
                save(operator, user.getMobile(), user, order);
                return accept;
            } else {
                throw new PermissionException("[该用户已不可进行此操作]");
            }
        }
    }

    /**
     * 查询订单工作人员
     *
     * @param orderNo 订单号
     * @param user    查询人
     * @return
     */
    @Override
    public List<UserDTO> getOrderOperators(String orderNo, User user) {
        UserOrder order = orderService.getUserOrder(orderNo);
        List<OrderOperator> operators = getOrderOperators(order.getIdUserOrder());
        List<UserDTO> userDTOS = new ArrayList<>();

        if (operators != null && operators.size() > 0) {
            for (OrderOperator operator : operators) {
                // 筛出社长
                if (order.getReceiveBy().equals(operator.getIdUser()) == false) {
                    UserDTO userDTO = userService.findUser(operator.getIdUser(), false);
                    userDTO.setUserState(operator.getOperatorState());
                    userDTOS.add(userDTO);
                }
            }
        }
        return userDTOS;
    }

    /**
     * 查询相关工作人员
     *
     * @param idUserOrder
     * @return
     */
    @Override
    public List<OrderOperator> getOrderOperators(Long idUserOrder) {
        List<OrderOperator> operators = orderOperatorRepository.findByIdUserOrderAndOperatorStateNotIn(idUserOrder, Arrays.asList(OperatorStateEnum.REFUSED.getState(), OperatorStateEnum.CANCELED.getState()));
        return operators;
    }

    /**
     * 批量获取当前用户协同作业订单的用户状态
     *
     * @param orderNoList
     * @param user
     * @return
     */
    @Override
    public Map<String, String> getOperatorState(String[] orderNoList, User user) {
        if (orderNoList != null && orderNoList.length > 0) {
            Map<String, String> map = new HashMap<>();
            for (String orderNo : orderNoList) {
                UserOrder userOrder = orderService.getUserOrder(orderNo);
                if (userOrder.getCooperative()) {
                    OrderOperator operator = getOperator(user.getIdUser(), userOrder.getIdUserOrder());
                    if (operator != null) {
                        map.put(orderNo, operator.getOperatorState());
                    } else {
                        throw new ParamErrorException("[该用户不是订单" + orderNo + "的工作人员]");
                    }
                }
            }
            return map;
        } else {
            throw new NullParamException("[订单号列表为空]");
        }
    }

    /**
     * 根据订单与工作人员获取该工作人员信息
     *
     * @param idUser
     * @param idUserOrder
     * @return
     */
    @Override
    public OrderOperator getOperator(Long idUser, Long idUserOrder) {
        List<OrderOperator> orderOperators = orderOperatorRepository.findByIdUserAndIdUserOrderAndOperatorStateNotIn(idUser, idUserOrder, Arrays.asList(OperatorStateEnum.REFUSED.getState(), OperatorStateEnum.CANCELED.getState()));
        if (orderOperators == null || orderOperators.size() < 1) {
            return null;
        } else if (orderOperators.size() == 1) {
            return orderOperators.get(0);
        } else {
            return dealOperatorTooMore(orderOperators);
        }
    }

    /**
     * 下单人取消农机手工作人员资格
     *
     * @param orderOperator 工作人员
     * @param user          下单人
     */
    @Override
    public void cancelOperator(String orderNo, OrderOperator orderOperator, User user) {
        orderOperator.setOperatorState(OperatorStateEnum.CANCELED.getState());
        User operator = userService.findUser(orderOperator.getIdUser());
        UserOrder order = orderService.getUserOrder(orderNo);
        save(orderOperator, operator.getMobile(), user, order);
    }

    /**
     * 查询某时间段内该用户的工作人员信息
     *
     * @param user
     * @param dateStart
     * @param dateEnd
     * @return
     */
    @Override
    public List<OrderOperator> findByUserAndDate(User user, Date dateStart, Date dateEnd) {
        if (dateEnd != null) {
            List<OrderOperator> orderOperators = orderOperatorRepository.findByIdUserAndEndTimeBetweenAndOperatorStateNotIn(user.getIdUser(), dateStart, dateEnd, Arrays.asList(OperatorStateEnum.REFUSED.getState(), OperatorStateEnum.CANCELED.getState()));
            return orderOperators;
        } else {
            List<OrderOperator> orderOperators = orderOperatorRepository.findByIdUserAndStartTimeAfterAndOperatorStateNotIn(user.getIdUser(), dateStart, Arrays.asList(OperatorStateEnum.REFUSED.getState(), OperatorStateEnum.CANCELED.getState()));
            return orderOperators;
        }
    }

    /**
     * 当前用户是否完成当前合作社的合作社订单任务
     *
     * @param user
     * @return
     */
    @Override
    public Boolean isCooperationOrderFinished(User user) {
        List<UserOrder> userUnfinishedCooperativeOrder = orderService.getUserUnfinishedCooperativeOrder(user);
        if (userUnfinishedCooperativeOrder != null && userUnfinishedCooperativeOrder.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询用户所有参与的订单
     *
     * @param user
     * @return
     */
    @Override
    public List<OrderOperator> findByUser(User user) {
        List<OrderOperator> orderOperators = orderOperatorRepository.findByIdUserAndOperatorStateNotIn(user.getIdUser(), Arrays.asList(OperatorStateEnum.CANCELED.getState(), OperatorStateEnum.REFUSED.getState()));
        return orderOperators;
    }

    /**
     * 查询用户所有参与的订单
     *
     * @param user
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrderOperator> findByUser(User user, String state, Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "idOperator");
        Page<OrderOperator> operatorPage = orderOperatorRepository.findAll(new Specification<OrderOperator>() {
            @Override
            public Predicate toPredicate(Root<OrderOperator> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("idUser").as(Long.class), user.getIdUser());
                if (state != null && state.trim().length() > 0) {
                    Predicate predicate2 = criteriaBuilder.equal(root.get("operatorState").as(String.class), state);
                    query.where(criteriaBuilder.and(predicate1, predicate2));
                } else {
                    query.where(criteriaBuilder.and(predicate1));
                }
                return query.getRestriction();
            }
        }, pageable);
        return operatorPage;
    }

    /**
     * 将多余工作人员信息逻辑删除
     *
     * @param operators
     * @return
     */
    private OrderOperator dealOperatorTooMore(List<OrderOperator> operators) {
        for (int i = 0; i < operators.size(); i++) {
            if (i != 0) {
                OrderOperator orderOperator = operators.get(i);
                orderOperator.setOperatorState(OperatorStateEnum.CANCELED.getState());
                orderOperatorRepository.save(orderOperator);
            }
        }
        return operators.get(0);
    }

    /**
     * 订单工作人员添加与更新
     *
     * @param orderOperator 订单农机手相关情况
     * @param mobile        订单农机手手机号码
     * @param user          当前操作人
     * @param order         当前操作订单
     * @return
     */
    private OrderOperator save(OrderOperator orderOperator, String mobile, User user, UserOrder order) {
        orderOperator = orderOperatorRepository.save(orderOperator);

        // 添加工作人员时，添加订单-关联人冗余信息
        OrderInfo orderInfo = infoRepository.findByIdUserOrderAndIdUser(orderOperator.getIdUserOrder(), orderOperator.getIdUser());
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            Farmland farmland = farmlandService.getFarmland(order.getIdFarmland());
            Address address = addressService.getOne(farmland.getIdAddress());
            BeanUtils.copyProperties(address, orderInfo);
            orderInfo.setIdUserOrder(orderOperator.getIdUserOrder());
            orderInfo.setIdUser(orderOperator.getIdUser());
            orderInfo.setMobile(mobile);
            if (orderOperator.getIdUser().equals(order.getReceiveBy())) {
                orderInfo.setIdentity(1);
            } else {
                orderInfo.setIdentity(2);
            }
            orderInfo.setCreateTime(order.getCreateTime());

            // 查询下单人orderinfo，获取农机ID
            OrderInfo createMan = infoRepository.findByIdUserOrderAndIdUser(order.getIdUserOrder(), order.getCreateBy());
            if (createMan != null) {
                orderInfo.setIdMachineryType(createMan.getIdMachineryType());
                orderInfo.setIdMachineryParentType(createMan.getIdMachineryParentType());
            }
        }
        orderInfo.setOrderState(order.getUserOrderState());
        orderInfo.setCooperativeOrder(order.getCooperative());
        orderInfo.setOperatorState(orderOperator.getOperatorState());
        infoRepository.save(orderInfo);

        // 添加订单变化日志
        OrderChangeLog log = new OrderChangeLog();
        log.setIdUserOrder(orderOperator.getIdUserOrder());
        log.setChangeTime(new Date());
        log.setChangeBy(user.getIdUser());
        String detail = "用户[" + mobile + "]";
        if (orderOperator.getOperatorState().equals(OperatorStateEnum.INVITED.getState())) {
            log.setChangeType(OrderLogTypeEnum.WORKER_ADD.getType());
            detail = detail + "被邀请为订单号[" + order.getUserOrderNo() + "]的工作人员，目前状态：" + OperatorStateEnum.INVITED.getMessage();
        } else if (orderOperator.getOperatorState().equals(OperatorStateEnum.ACCEPTED.getState())) {
            log.setChangeType(OrderLogTypeEnum.WORKER_ADD.getType());
            detail = detail + "确认为订单号[" + order.getUserOrderNo() + "]的工作人员，目前状态：" + OperatorStateEnum.ACCEPTED.getMessage();
        } else if (orderOperator.getOperatorState().equals(OperatorStateEnum.REFUSED.getState())) {
            log.setChangeType(OrderLogTypeEnum.WORKER_CUT.getType());
            detail = detail + "拒绝为订单号[" + order.getUserOrderNo() + "]的工作人员，目前状态：" + OperatorStateEnum.REFUSED.getMessage();
        } else if (orderOperator.getOperatorState().equals(OperatorStateEnum.CANCELED.getState())) {
            log.setChangeType(OrderLogTypeEnum.WORKER_CUT.getType());
            detail = detail + "取消为订单号[" + order.getUserOrderNo() + "]的工作人员，目前状态：" + OperatorStateEnum.CANCELED.getMessage();
        }
        log.setDetail(detail);

        logRepository.save(log);
        return orderOperator;
    }

}
