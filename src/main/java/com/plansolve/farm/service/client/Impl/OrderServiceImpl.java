package com.plansolve.farm.service.client.Impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plansolve.farm.exception.*;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.client.PageDTO;
import com.plansolve.farm.model.client.order.*;
import com.plansolve.farm.model.client.user.FarmlandDTO;
import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.cooperation.Cooperation;
import com.plansolve.farm.model.database.cooperation.InviteRelationShip;
import com.plansolve.farm.model.database.dictionary.DictMachineryType;
import com.plansolve.farm.model.database.log.OrderChangeLog;
import com.plansolve.farm.model.database.order.CompletionReport;
import com.plansolve.farm.model.database.order.OrderInfo;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.OperatorStateEnum;
import com.plansolve.farm.model.enums.type.OrderLogTypeEnum;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.repository.dictionary.DictMachineryRepository;
import com.plansolve.farm.repository.log.OrderChangeLogRepository;
import com.plansolve.farm.repository.order.OrderInfoRepository;
import com.plansolve.farm.repository.order.OrderOperatorRepository;
import com.plansolve.farm.repository.order.UserOrderRepository;
import com.plansolve.farm.service.client.*;
import com.plansolve.farm.util.AddressUtil;
import com.plansolve.farm.util.BigDecimalUtil;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
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
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: 高一平
 * @Date: 2018/6/20
 * @Description: 客户端订单相关接口实现类
 **/
@Service
public class OrderServiceImpl implements OrderService {

    private final static Logger logger = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private OrderOperatorRepository orderOperatorRepository;
    @Autowired
    private OrderInfoRepository infoRepository;
    @Autowired
    private OrderChangeLogRepository logRepository;
    @Autowired
    private FarmlandService farmlandService;
    @Autowired
    private UserService userService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private OrderOperatorService operatorService;
    @Autowired
    private OperatorReportService reportService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private DictService dictService;
    @Autowired
    private DictMachineryRepository dictMachineryRepository;

    /**
     * 分页查询各类型订单
     *
     * @param isFarmer
     * @param orderState
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Page<UserOrder> getUserOrders(User user, Boolean isFarmer, String orderState, Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "updateTime");
        Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1;
                if (isFarmer) {
                    predicate1 = criteriaBuilder.equal(root.get("createBy").as(Long.class), user.getIdUser());
                } else {
                    predicate1 = criteriaBuilder.equal(root.get("receiveBy").as(Long.class), user.getIdUser());
                }
                if (orderState == null || orderState.trim().length() == 0) {
                    query.where(criteriaBuilder.and(predicate1));
                } else {
                    Predicate predicate2 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), orderState);
                    query.where(criteriaBuilder.and(predicate1, predicate2));
                }
                return query.getRestriction();
            }
        }, pageable);
        return orderPage;
    }

    /**
     * 获取种植户订单列表
     *
     * @param user
     * @return
     */
    @Override
    public List<UserOrder> getOrderWithFarmer(User user) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<UserOrder> orders = userOrderRepository.findByCreateByAndUserOrderStateNot(user.getIdUser(), OrderStateEnum.DELETED.getState(), sort);
        return orders;
    }

    /**
     * 获取已完成的种植户订单列表
     *
     * @param user
     * @return
     */
    @Override
    public List<UserOrder> getOrderWithFarmerFinished(User user) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<UserOrder> orders = userOrderRepository.findByCreateByAndUserOrderState(user.getIdUser(), OrderStateEnum.FINISHED.getState(), sort);
        return orders;
    }

    /**
     * 获取农机手订单列表
     *
     * @param user
     * @return
     */
    @Override
    public List<UserOrder> getOrderWithOperator(User user) {
        List<OrderOperator> operatorOrders = orderOperatorRepository.findByIdUserAndOperatorStateNotIn(user.getIdUser(), Arrays.asList(OperatorStateEnum.REFUSED.getState(), OperatorStateEnum.CANCELED.getState()));
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<UserOrder> orders = userOrderRepository.findByReceiveByAndUserOrderStateNotIn(user.getIdUser(), Arrays.asList(OrderStateEnum.WAITING.getState(), OrderStateEnum.CANCELED.getState(), OrderStateEnum.DELETED.getState()), sort);
        if (orders == null) orders = new ArrayList<>();

        if (operatorOrders != null && operatorOrders.size() > 0) {
            for (OrderOperator operatorOrder : operatorOrders) {
                UserOrder order = userOrderRepository.findByIdUserOrderAndUserOrderStateNot(operatorOrder.getIdUserOrder(), OrderStateEnum.DELETED.getState());
                if (order != null) {
                    if (orders.size() == 0) {
                        orders.add(order);
                    } else {
                        for (int i = 0; i < orders.size(); i++) {
                            if (orders.get(i).getCreateTime().before(order.getCreateTime())) {
                                orders.add(i, order);
                                break;
                            } else if (orders.get(i).getCreateTime().equals(order.getCreateTime())) {
                                break;
                            } else if (i == orders.size() - 1) {
                                orders.add(order);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return orders;
    }

    /**
     * 获取范围内可接订单
     *
     * @param user      当前用户
     * @param latitude  所处方位
     * @param longitude 所处方位
     * @param pageNo    页码
     * @param pageSize  页面大小
     * @return
     */
    @Override
    public List<UserOrder> getWaitingOrderList(User user, Double latitude, Double longitude, Integer pageNo, Integer pageSize) {
        List<UserOrder> orders = dispatch(user, latitude, longitude, pageNo, pageSize);
        return orders;
    }

    /**
     * @param user      当前用户
     * @param param     检索条件
     * @param latitude  所处方位
     * @param longitude 所处方位
     * @param pageNo    页码
     * @param pageSize  页面大小
     * @return
     */
    @Override
    public List<UserOrder> getWaitingOrderList(User user, String param, Double latitude, Double longitude, Integer pageNo, Integer pageSize) {
        return dispatch(user, param, latitude, longitude, pageNo, pageSize);
    }

/********************************************************************************订单操作********************************************************************************/
    /**
     * 创建订单
     *
     * @param orderDTO   订单基本信息
     * @param farmlandNo 订单相关地块
     * @param user       下单人
     * @param mobile     下单对象
     * @return
     */
    @Override
    @Transactional
    public UserOrder createOrder(OrderDTO orderDTO, String farmlandNo, User user, String mobile) {
        UserOrder userOrder = new UserOrder();
        BeanUtils.copyProperties(orderDTO, userOrder);
        BeanUtils.copyProperties(orderDTO.getGuideDTO(), userOrder);

        // 生成12位订单编号
        String orderNo = getOrderNo();
        userOrder.setUserOrderNo(orderNo);
        userOrder.setCreateBy(user.getIdUser());
        userOrder.setCreateTime(new Date());
        userOrder.setUpdateTime(new Date());

        // 地块信息
        Farmland farmland = farmlandService.getFarmland(farmlandNo);
        userOrder.setIdFarmland(farmland.getIdFarmland());
        userOrder.setArce(farmland.getFarmlandAcreage());
        userOrder = save(userOrder, user, mobile);

        // 下单时，添加订单-关联人冗余信息
        OrderInfo orderInfo = infoRepository.findByIdUserOrderAndIdUser(userOrder.getIdUserOrder(), user.getIdUser());
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            Address address = addressService.getOne(farmland.getIdAddress());
            BeanUtils.copyProperties(address, orderInfo);
            orderInfo.setIdUserOrder(userOrder.getIdUserOrder());
            orderInfo.setIdMachineryType(orderDTO.getIdMachineryType());
            DictMachineryType machinery = dictService.getFarmMachinery(orderInfo.getIdMachineryType());
            orderInfo.setIdMachineryParentType(machinery.getParentId());
            orderInfo.setIdUser(user.getIdUser());
            orderInfo.setMobile(user.getMobile());
            orderInfo.setIdentity(0);
            orderInfo.setCreateTime(userOrder.getCreateTime());
        }
        orderInfo.setOrderState(userOrder.getUserOrderState());
        infoRepository.save(orderInfo);
        return userOrder;
    }

    /**
     * 创建订单
     *
     * @param userOrder
     * @param user
     * @return
     */
    @Override
    public UserOrder repeatCreateOrder(UserOrder userOrder, User user) {
        if (userOrder.getCreateBy().equals(user.getIdUser()) || (userOrder.getReceiveBy() != null && userOrder.getReceiveBy().equals(user.getIdUser()))) {
            userOrder.setTarget("平台");
            userOrder.setUserOrderState(OrderStateEnum.WAITING.getState());
            userOrder.setUpdateTime(new Date());
            userOrder.setReceiveBy(null);
            userOrder.setCooperative(null);
            userOrder.setReportedBy(null);
            userOrder = save(userOrder, user, null);
            return userOrder;
        } else {
            throw new PermissionException("[该用户不是下单人，不能进行此操作]");
        }
    }

    /**
     * 更改引路人信息
     *
     * @param guideDTO 引路人信息
     * @param orderNo  订单号
     * @param user     订单修改人
     * @return
     */
    @Override
    @Transactional
    public UserOrder updateGuide(GuideDTO guideDTO, String orderNo, User user) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getUserOrderState().equals(OrderStateEnum.WAITING.getState())
                || order.getUserOrderState().equals(OrderStateEnum.CONFIRMING.getState())) {
            BeanUtils.copyProperties(guideDTO, order);
            order = userOrderRepository.save(order);

            // 生成日志
            OrderChangeLog log = new OrderChangeLog();
            log.setIdUserOrder(order.getIdUserOrder());
            log.setChangeBy(user.getIdUser());
            log.setChangeTime(new Date());
            log.setChangeType(OrderLogTypeEnum.UPDATE.getType());
            log.setDetail("用户[" + user.getMobile() + "]修改订单[" + order.getUserOrderNo() + "]引路人相关信息");
            logRepository.save(log);

            return order;
        } else {
            throw new PermissionException("[该订单已无法修改]");
        }
    }

    /**
     * 农机手抢单
     *
     * @param user    农机手
     * @param orderNo 订单号
     * @return
     */
    @Override
    @Transactional
    public UserOrder receiveOrder(User user, String orderNo) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getCreateBy().equals(user.getIdUser())) {
            throw new OrderErrorException("[该用户为订单下单人，不可接单]");
        } else {
            if (order.getUserOrderState().equals(OrderStateEnum.WAITING.getState())) {
                // 若下单至“我的合作社”，仅社员可接
                if (order.getTarget().equals("我的合作社")) {
                    User createMan = userService.findUser(order.getCreateBy());
                    if (createMan.getIdCooperation().equals(user.getIdCooperation())) {
                        order.setReceiveBy(user.getIdUser());
                        order = update(order, user, OrderStateEnum.CONFIRMING.getState());
                        logger.info("用户[" + user.getMobile() + "]抢到订单" + orderNo);
                    } else {
                        throw new PermissionException("[该订单为合作社订单，用户不是该合作社成员]");
                    }
                } else if (order.getTarget().equals("用户")) {
                    if (order.getReceiveBy().equals(user.getIdUser())) {
                        order = update(order, user, OrderStateEnum.CONFIRMING.getState());
                        logger.info("用户[" + user.getMobile() + "]接受订单" + orderNo);
                    } else {
                        throw new PermissionException("[该订单已指定接单人，其他农机手无权接单]");
                    }
                } else {
                    order.setReceiveBy(user.getIdUser());
                    order = update(order, user, OrderStateEnum.CONFIRMING.getState());
                    logger.info("用户[" + user.getMobile() + "]抢到订单" + orderNo);
                }
                return order;
            } else if (order.getUserOrderState().equals(OrderStateEnum.CANCELED.getState())) {
                logger.error("该订单已被取消");
                throw new OrderCancelErrorException("[" + orderNo + "]");
            } else {
                logger.error("抢单失败，该订单已被接单");
                throw new GetOrderFailException("[" + orderNo + "]");
            }
        }
    }

    /**
     * 农机手确认订单
     *
     * @param user    农机手
     * @param orderNo 订单号
     * @return
     */
    @Override
    @Transactional
    public UserOrder makeSureOrder(User user, String orderNo) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getReceiveBy().equals(user.getIdUser())) {
            if (order.getUserOrderState().equals(OrderStateEnum.CONFIRMING.getState())) {
                order = update(order, user, OrderStateEnum.WORKING.getState());
                return order;
            } else {
                logger.error("该订单不可确认，订单状态为：" + order.getUserOrderState());
                throw new OrderErrorException("[该订单不可确认]");
            }
        } else {
            // 该用户不是订单接单人，无权操作该订单
            logger.error("该用户不是订单接单人，无权操作订单" + order.getUserOrderNo());
            throw new PermissionException("[当前用户不是该订单接单人，无权操作该订单]");
        }
    }

    /**
     * 取消订单
     *
     * @param user    取消人
     * @param orderNo 订单号
     * @return
     */
    @Override
    @Transactional
    public UserOrder cancelOrder(User user, String orderNo) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getUserOrderState().equals(OrderStateEnum.WAITING.getState())
                || order.getUserOrderState().equals(OrderStateEnum.CONFIRMING.getState())) {
            if ((order.getReceiveBy() != null && order.getReceiveBy().equals(user.getIdUser())) || order.getCreateBy().equals(user.getIdUser())) {
                // 取消相关工作人员
                List<OrderOperator> orderOperators = operatorService.getOrderOperators(order.getIdUserOrder());
                if (orderOperators != null && orderOperators.size() > 0) {
                    for (OrderOperator orderOperator : orderOperators) {
                        operatorService.cancelOperator(order.getUserOrderNo(), orderOperator, user);
                    }
                }
                Long receiveBy = order.getReceiveBy();
                order.setReceiveBy(null);
                order = update(order, user, OrderStateEnum.CANCELED.getState());

                // 添加订单冗余表逻辑
                OrderInfo orderInfo = infoRepository.findByIdUserOrderAndIdUser(order.getIdUserOrder(), order.getCreateBy());
                if (orderInfo != null) {
                    orderInfo.setOrderState(order.getUserOrderState());
                    infoRepository.save(orderInfo);
                }

                if (receiveBy != null
                        && receiveBy.equals(user.getIdUser())
                        && order.getTarget().equals("平台")) {
                    order.setReceiveBy(receiveBy);
                    order = repeatCreateOrder(order, user);
                }
                // 为订单通知的推送切面提供原接单人信息，前端调用后需消除原接单人信息再响应给前端！
                order.setReceiveBy(receiveBy);
                return order;
            } else {
                // 该用户不是订单接单人，无权操作该订单
                logger.error("该用户不是订单下单人或接单人，无权操作订单" + order.getUserOrderNo());
                throw new PermissionException("[当前用户不是该订单下单人或接单人，无权操作该订单]");
            }
        } else {
            logger.error("该订单已不可取消，订单状态为：" + order.getUserOrderState());
            throw new OrderErrorException("[该订单不可取消]");
        }
    }

    /**
     * 批量取消下单至合作社的未接订单
     *
     * @param users
     * @return
     */
    @Override
    public List<UserOrder> cancleCooperationOrder(List<User> users) {
        List<UserOrder> orders = new ArrayList<>();
        if (users != null && users.size() > 0) {
            for (User user : users) {
                List<UserOrder> userOrders = cancleCooperationOrder(user);
                orders.addAll(userOrders);
            }
        }
        return orders;
    }

    /**
     * 取消该用户下单至合作社的未接订单
     *
     * @param user
     * @return
     */
    @Override
    public List<UserOrder> cancleCooperationOrder(User user) {
        List<UserOrder> userOrders = userOrderRepository.findByCreateByAndUserOrderStateAndTarget(user.getIdUser(), OrderStateEnum.WAITING.getState(), "我的合作社");
        if (userOrders != null && userOrders.size() > 0) {
            for (UserOrder userOrder : userOrders) {
                // 更新订单
                userOrder.setUpdateTime(new Date());
                userOrder.setUserOrderState(OrderStateEnum.CANCELED.getState());
                // 生成日志
                OrderChangeLog log = new OrderChangeLog();
                log.setIdUserOrder(userOrder.getIdUserOrder());
                log.setChangeBy(user.getIdUser());
                log.setChangeTime(new Date());
                log.setChangeType(OrderLogTypeEnum.CANCEL.getType());
                log.setDetail("合作社解散，取消订单[" + userOrder.getUserOrderNo() + "]");
                userOrderRepository.save(userOrder);
                logRepository.save(log);
            }
        } else {
            userOrders = new ArrayList<>();
        }
        return userOrders;
    }

    /**
     * 种植户确认工作进度，并验收合格，进入待支付状态
     *
     * @param user
     * @param orderNo
     * @return
     */
    @Override
    @Transactional
    public UserOrder finalAcceptance(User user, String orderNo) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getCreateBy().equals(user.getIdUser())) {
            if (order.getUserOrderState().equals(OrderStateEnum.CHECKING.getState())) {
                if (order.getDemandAmount() != null
                        && BigDecimalUtil.moreThan(order.getDemandAmount(), new BigDecimal(0))) {
//                    order.setAmountPayable(order.getDemandAmount()); // 验收合格，即代表下单人承认此金额，此金额为最后支付金额（逻辑修改，弃用）
                    order.setUserOrderState(OrderStateEnum.PAYMENT.getState());
                    order.setUpdateTime(new Date());
                    userOrderRepository.save(order);

                    // 生成日志
                    OrderChangeLog log = new OrderChangeLog();
                    log.setIdUserOrder(order.getIdUserOrder());
                    log.setChangeBy(user.getIdUser());
                    log.setChangeTime(new Date());
                    log.setChangeType(OrderLogTypeEnum.WORK_FINAL_ACCEPTANCE.getType());
                    log.setDetail("订单[" + order.getUserOrderNo() + "]作业完成，由用户[" + user.getMobile() + "]确认验收合格");
                    logRepository.save(log);

                    return order;
                } else {
                    throw new PermissionException("[该订单索要报酬为空，不可验收通过]");
                }
            } else {
                throw new PermissionException("[该订单作业未结束，不能进行此项操作]");
            }
        } else {
            throw new PermissionException("[该用户不是下单人，无权做此操作]");
        }
    }

    /**
     * 用户提交索要金额
     *
     * @param orderNo
     * @param demandPrice
     * @return
     */
    @Override
    public UserOrder demandPrice(User user, String orderNo, BigDecimal demandPrice) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getReceiveBy().equals(user.getIdUser())) {
            if (order.getUserOrderState().equals(OrderStateEnum.WORKING.getState())
                    || order.getUserOrderState().equals(OrderStateEnum.CHECKING.getState())
                    || order.getUserOrderState().equals(OrderStateEnum.PAYMENT.getState())) {
                order.setDemandAmount(demandPrice);
                order.setUpdateTime(new Date());
                userOrderRepository.save(order);

                // 生成日志
                OrderChangeLog log = new OrderChangeLog();
                log.setIdUserOrder(order.getIdUserOrder());
                log.setChangeBy(user.getIdUser());
                log.setChangeTime(new Date());
                log.setChangeType(OrderLogTypeEnum.DEMAND_PRICE.getType());
                log.setDetail("用户[" + user.getMobile() + "]提交订单[" + order.getUserOrderNo() + "]索要金额为：" + demandPrice);
                logRepository.save(log);
                return order;
            } else {
                throw new PermissionException("[该订单现不可提交报酬金额]");
            }
        } else {
            throw new PermissionException("[该用户不是接单人，无权做此操作]");
        }
    }

    /**
     * 种植户付款
     *
     * @param orderNo 订单号
     * @return
     */
    @Override
    @Transactional
    public UserOrder paidOrder(String orderNo, Boolean onlinePayment) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getUserOrderState().equals(OrderStateEnum.PAYMENT.getState())) {
            order.setIsOnlinePayment(onlinePayment);
            order.setUserOrderState(OrderStateEnum.PREPAID.getState());
            order.setAmountPayable(order.getDemandAmount()); // 记录 下单人 实际支付金额
            order.setPaymentTime(new Date());
            order.setUpdateTime(new Date());
            userOrderRepository.save(order);

            // 生成日志
            OrderChangeLog log = new OrderChangeLog();
            log.setIdUserOrder(order.getIdUserOrder());
            log.setChangeBy(order.getCreateBy());
            log.setChangeTime(new Date());
            log.setChangeType(OrderLogTypeEnum.ORDER_PAY.getType());
            if (onlinePayment) {
                log.setDetail("订单[" + order.getUserOrderNo() + "]作业完成，并完成支付");
            } else {
                log.setDetail("订单[" + order.getUserOrderNo() + "]作业完成，已线下支付");
            }
            logRepository.save(log);
            return order;
        } else {
            throw new PermissionException("该订单已支付");
        }
    }

    /**
     * 农机手确认收款
     * 待确认：收款给谁--协同作业--如何处理款项问题
     * 现：统一付款给收款人（协同作业收款人为社长）
     *
     * @param user
     * @param orderNo
     * @return
     */
    @Override
    @Transactional
    public UserOrder confirmReceipt(User user, String orderNo) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getReceiveBy().equals(user.getIdUser())) {
            if (order.getUserOrderState().equals(OrderStateEnum.PREPAID.getState())) {
                order.setUserOrderState(OrderStateEnum.RECEIPT.getState());
                order.setUpdateTime(new Date());
                userOrderRepository.save(order);

                // 生成日志
                OrderChangeLog log = new OrderChangeLog();
                log.setIdUserOrder(order.getIdUserOrder());
                log.setChangeBy(user.getIdUser());
                log.setChangeTime(new Date());
                log.setChangeType(OrderLogTypeEnum.CONFIRM_RECEIPT.getType());
                if (order.getIsOnlinePayment() == null) {
                    log.setDetail("订单[" + order.getUserOrderNo() + "]确认收款，由用户[" + user.getMobile() + "]确认");
                } else if (order.getIsOnlinePayment()) {
                    log.setDetail("订单[" + order.getUserOrderNo() + "]确认收款，由系统确认");
                } else {
                    log.setDetail("订单[" + order.getUserOrderNo() + "]确认收款，由用户[" + user.getMobile() + "]确认");
                }
                logRepository.save(log);

                return order;
            } else {
                throw new PermissionException("[该订单未确认作业结束，不能进行此项操作]");
            }
        } else {
            throw new PermissionException("[该用户既不是接单人，无权做此操作]");
        }
    }

    /**
     * 农机手确认订单完结
     *
     * @param user
     * @param orderNo
     * @return
     */
    @Override
    @Transactional
    public UserOrder finishOrder(User user, String orderNo) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getReceiveBy().equals(user.getIdUser())) {
            if (order.getUserOrderState().equals(OrderStateEnum.RECEIPT.getState())) {
                order.setUpdateTime(new Date());
                order.setUserOrderState(OrderStateEnum.FINISHED.getState());
                userOrderRepository.save(order);

                // 生成日志
                OrderChangeLog log = new OrderChangeLog();
                log.setIdUserOrder(order.getIdUserOrder());
                log.setChangeBy(user.getIdUser());
                log.setChangeTime(new Date());
                log.setChangeType(OrderLogTypeEnum.FINISHED.getType());
                log.setDetail("订单[" + order.getUserOrderNo() + "]订单完成，由用户[" + user.getMobile() + "]确认订单结束");
                logRepository.save(log);

                // 添加订单冗余表逻辑
                List<OrderInfo> orderInfos = infoRepository.findByIdUserOrder(order.getIdUserOrder());
                if (orderInfos != null && orderInfos.size() > 0) {
                    for (OrderInfo orderInfo : orderInfos) {
                        orderInfo.setOrderState(order.getUserOrderState());
                        infoRepository.save(orderInfo);
                    }
                }

                return order;
            } else {
                throw new PermissionException("[该订单未确认收款，不能进行此项操作]");
            }
        } else {
            throw new PermissionException("[该用户不是接单人，无权做此操作]");
        }
    }

    /**
     * 种植户删除已被取消的订单
     *
     * @param user
     * @param orderNo
     * @return
     */
    @Override
    @Transactional
    public UserOrder deleteOrder(User user, String orderNo) {
        UserOrder order = getUserOrder(orderNo);
        if (order.getCreateBy().equals(user.getIdUser())) {
            if (order.getUserOrderState().equals(OrderStateEnum.CANCELED.getState())) {
                order.setUpdateTime(new Date());
                order.setUserOrderState(OrderStateEnum.DELETED.getState());
                userOrderRepository.save(order);

                // 生成日志
                OrderChangeLog log = new OrderChangeLog();
                log.setIdUserOrder(order.getIdUserOrder());
                log.setChangeBy(user.getIdUser());
                log.setChangeTime(new Date());
                log.setChangeType(OrderLogTypeEnum.DELETE.getType());
                log.setDetail("订单[" + order.getUserOrderNo() + "]由用户[" + user.getMobile() + "]删除");
                logRepository.save(log);

                return order;
            } else {
                throw new PermissionException("[该订单不可删除]");
            }
        } else {
            throw new PermissionException("[该用户既不是下单人，无权做此操作]");
        }
    }

    /**
     * 给过期订单过期
     */
    @Override
    public void overdueOrder() {
        List<UserOrder> orders = userOrderRepository.findByUserOrderState(OrderStateEnum.WAITING.getState());
        if (orders != null && orders.size() > 0) {
            Date today = new Date();
            for (UserOrder order : orders) {
                Date overdueDay = DateUtils.getDate_PastOrFuture_Day(order.getStartTime(), order.getPeriod());
                if (overdueDay.before(today)) {
                    order.setUserOrderState(OrderStateEnum.OVERDUE.getState());
                    order.setUpdateTime(new Date());
                    userOrderRepository.save(order);

                    // 生成日志
                    OrderChangeLog log = new OrderChangeLog();
                    log.setIdUserOrder(order.getIdUserOrder());
                    log.setChangeBy(0l);
                    log.setChangeTime(new Date());
                    log.setChangeType(OrderLogTypeEnum.OVERDUE.getType());
                    log.setDetail("订单[" + order.getUserOrderNo() + "]过期");
                    logRepository.save(log);

                    // 添加订单冗余表逻辑
                    OrderInfo orderInfo = infoRepository.findByIdUserOrderAndIdUser(order.getIdUserOrder(), order.getCreateBy());
                    if (orderInfo != null) {
                        orderInfo.setOrderState(order.getUserOrderState());
                        infoRepository.save(orderInfo);
                    }
                }
            }
        }
    }
/********************************************************************************订单操作********************************************************************************/
/********************************************************************************订单封装********************************************************************************/
    /**
     * 获取订单传输对象
     *
     * @param user
     * @param userOrder
     * @return
     */
    @Override
    public OrderDTO loadDTO(User user, UserOrder userOrder) {
        OrderDTO orderDTO = loadDTO(userOrder);
        OrderOperator operator = operatorService.getOperator(user.getIdUser(), userOrder.getIdUserOrder());
        if (operator != null) {
            orderDTO.setOperatorState(operator.getOperatorState());
        }
        return orderDTO;
    }

    /**
     * 获取订单传输对象
     *
     * @param userOrder
     * @return
     */
    @Override
    public OrderDTO loadDTO(UserOrder userOrder) {
        OrderDTO orderDTO = new OrderDTO();
        GuideDTO guideDTO = new GuideDTO();

        BeanUtils.copyProperties(userOrder, guideDTO);
        orderDTO.setGuideDTO(guideDTO);

        BeanUtils.copyProperties(userOrder, orderDTO);

        Farmland farmland = farmlandService.getFarmland(userOrder.getIdFarmland());
        FarmlandDTO farmlandDTO = farmlandService.loadDTO(farmland);
        orderDTO.setFarmlandDTO(farmlandDTO);
        orderDTO.setCreateBy(userService.findUser(userOrder.getCreateBy(), false));
        if (userOrder.getReportedBy() != null && userOrder.getReportedBy() > 0) {
            orderDTO.setReportedBy(userService.findUser(userOrder.getReportedBy(), false));
        }
        if (userOrder.getReceiveBy() != null && userOrder.getReceiveBy() > 0) {
            orderDTO.setReceiveBy(userService.findUser(userOrder.getReceiveBy(), false));
        }
        return orderDTO;
    }

    /**
     * 批量封装订单传输对象
     *
     * @param orders
     * @return
     */
    @Override
    public List<OrderDTO> loadDTOs(List<UserOrder> orders) {
        List<OrderDTO> orderDTOS = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (UserOrder order : orders) {
                OrderDTO orderDTO = loadDTO(order);
                orderDTOS.add(orderDTO);
            }
        }
        return orderDTOS;
    }

    /**
     * 简单封装订单信息
     *
     * @param userOrder
     * @return
     */
    @Override
    public OrderSimpleDTO loadSimpleDTO(UserOrder userOrder) {
        OrderSimpleDTO orderSimpleDTO = new OrderSimpleDTO();
        BeanUtils.copyProperties(userOrder, orderSimpleDTO);
        orderSimpleDTO.setEndTime(DateUtils.getDate_PastOrFuture_Day(userOrder.getStartTime(), userOrder.getPeriod()));
        User createMan = userService.findUser(userOrder.getCreateBy());
        orderSimpleDTO.setCreateManNickname(createMan.getNickname());
        orderSimpleDTO.setCreateManMobile(createMan.getMobile());
        orderSimpleDTO.setCreateManAvatar(createMan.getAvatar());

        Farmland farmland = farmlandService.getFarmland(userOrder.getIdFarmland());
        FarmlandDTO farmlandDTO = farmlandService.loadDTO(farmland);
        orderSimpleDTO.setFarmlandAddress(farmlandDTO.getAddress());
        return orderSimpleDTO;
    }

    /**
     * 批量封装订单简单信息
     *
     * @param orders
     * @return
     */
    @Override
    public List<OrderSimpleDTO> loadSimpleDTOs(List<UserOrder> orders) {
        List<OrderSimpleDTO> orderSimpleDTOS = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (UserOrder order : orders) {
                OrderSimpleDTO orderSimpleDTO = loadSimpleDTO(order);
                orderSimpleDTOS.add(orderSimpleDTO);
            }
        }
        return orderSimpleDTOS;
    }
/********************************************************************************订单封装********************************************************************************/
    /**
     * 获取用户在某一时间段的“忙”“闲”状态
     *
     * @param user      相关用户
     * @param startTime 开始时间
     * @param days      持续天数
     * @return
     */
    @Override
    public Boolean isBusy(User user, Date startTime, Integer days) {
        if (startTime == null || days == null || days <= 0) {
            throw new NullParamException("[请输入时间段，用以判断用户状态]");
        } else {
            // 用户在指定时间后有无订单任务
            List<String> states = new ArrayList<>();
            states.add(OperatorStateEnum.REFUSED.getState());
            states.add(OperatorStateEnum.CANCELED.getState());

            List<OrderOperator> orders = orderOperatorRepository.findByIdUserAndEndTimeAfterAndOperatorStateNotIn(user.getIdUser(), startTime, states);
            if (orders == null || orders.size() == 0) {
                return false;
            } else {
                Date endTime = DateUtils.getDate_PastOrFuture_Day(startTime, days);
                for (OrderOperator order : orders) {
                    if ((startTime.after(order.getStartTime()) && startTime.before(order.getEndTime()))
                            || (endTime.after(order.getStartTime()) && endTime.before(order.getEndTime()))
                            || (startTime.before(order.getStartTime()) && endTime.after(order.getEndTime()))) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return
     */
    @Override
    public UserOrder getUserOrder(String orderNo) {
        UserOrder order = userOrderRepository.findByUserOrderNoAndUserOrderStateNot(orderNo, OrderStateEnum.DELETED.getState());
        if (order != null) {
            return order;
        } else {
            throw new NullParamException("[查询不到该订单]");
        }
    }

    /**
     * 根据订单主键查询订单
     *
     * @param idUserOrder
     * @return
     */
    @Override
    public UserOrder getUserOrder(Long idUserOrder) {
        UserOrder order = userOrderRepository.findByIdUserOrderAndUserOrderStateNotIn(idUserOrder, Arrays.asList(OrderStateEnum.DELETED.getState(), OrderStateEnum.CANCELED.getState()));
        return order;
    }

    /**
     * 获取未完成的协同作业订单
     *
     * @param user
     * @return
     */
    @Override
    public List<UserOrder> getUnfinishedCooperativeOrder(User user) {
        // 查询社长
        User proprieter = cooperationService.findProprieterByUser(user);
        if (proprieter != null) {
            List<UserOrder> userOrders = userOrderRepository.findByReceiveByAndUserOrderStateInAndIsCooperativeIsTrue(proprieter.getIdUser(), Arrays.asList(OrderStateEnum.CONFIRMING.getState(), OrderStateEnum.WORKING.getState(), OrderStateEnum.CHECKING.getState()));
            if (proprieter.getIdUser().equals(user.getIdUser())) {
                // 社长直接返回
                return userOrders;
            } else {
                return getOperatorCooperativeOrder(userOrders, user);
            }
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取该用户未完成的协同作业订单任务
     *
     * @param user
     * @return
     */
    @Override
    public List<UserOrder> getUserUnfinishedCooperativeOrder(User user) {
        List<UserOrder> unfinishedCooperativeOrder = getUnfinishedCooperativeOrder(user);
        List<UserOrder> userUnfinishedCooperativeOrder = new ArrayList<>();
        if (unfinishedCooperativeOrder != null && unfinishedCooperativeOrder.size() > 0) {
            for (UserOrder userOrder : unfinishedCooperativeOrder) {
                OrderOperator operator = operatorService.getOperator(user.getIdUser(), userOrder.getIdUserOrder());
                if (operator != null && operator.getOperatorState().equals(OperatorStateEnum.ACCEPTED.getState())) {
                    userUnfinishedCooperativeOrder.add(userOrder);
                }
            }
            return userUnfinishedCooperativeOrder;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 根据状态获取该用户的协同作业订单任务
     *
     * @param user
     * @param state
     * @return
     */
    @Override
    public PageDTO<UserOrder> getUserCooperativeOrder(User user, String state, Integer pageNo, Integer pageSize) {
        Page<OrderOperator> operatorPage = operatorService.findByUser(user, state, pageNo, pageSize);
        PageDTO<UserOrder> pageDTO = new PageDTO<>();
        if (operatorPage != null) {
            pageDTO.setTotal(operatorPage.getTotalElements());

            List<UserOrder> orders = new ArrayList<>();
            List<OrderOperator> operators = operatorPage.getContent();
            if (operators != null && operators.size() > 0) {
                for (OrderOperator operator : operators) {
                    UserOrder userOrder = getUserOrder(operator.getIdUserOrder());
                    orders.add(userOrder);
                }
            }
            pageDTO.setRows(orders);
        }
        return pageDTO;
    }

    /**
     * 获取已完成的协同作业订单
     *
     * @param user
     * @return
     */
    @Override
    public List<UserOrder> getFinishedCooperativeOrder(User user) {
        // 查询社长
        User proprieter = cooperationService.findProprieterByUser(user);
        if (proprieter != null) {
            List<UserOrder> userOrders = userOrderRepository.findByReceiveByAndUserOrderStateInAndIsCooperativeIsTrue(proprieter.getIdUser(), Arrays.asList(OrderStateEnum.FINISHED.getState()));
            if (proprieter.getIdUser().equals(user.getIdUser())) {
                // 社长直接返回
                return userOrders;
            } else {
                return getOperatorCooperativeOrder(userOrders, user);
            }
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取该用户加入合作社后所有完成的订单
     *
     * @param user   用户
     * @param getAll 是否获取所有
     * @param date   若不获取所有，则获取的月份
     * @return
     */
    @Override
    public List<UserOrder> getCooperationOrder(User user, Boolean getAll, Date date) {
        if (date != null) {
            Date monthStart = DateUtils.getMonthStart(date);
            Date monthEnd = DateUtils.getMonthEnd(date);
            return getCooperationOrder(user, getAll, monthStart, monthEnd);
        } else {
            if (getAll == false) {
                throw new NullParamException("[查询日期不可为空]");
            }
            return getCooperationOrder(user, getAll, null, null);
        }
    }

    /**
     * 获取该用户加入合作社后所有完成的订单
     *
     * @param user
     * @param getAll
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<UserOrder> getCooperationOrder(User user, Boolean getAll, Date startTime, Date endTime) {
        List<UserOrder> orders = new ArrayList<>();
        List<OrderOperator> orderOperators = null;
        if (getAll) {
            Cooperation cooperation = cooperationService.getById(user.getIdCooperation());
            if (cooperation.getIdUser().equals(user.getIdUser())) {
                orderOperators = operatorService.findByUserAndDate(user, cooperation.getRegistTime(), null);
            } else {
                InviteRelationShip inviterState = cooperationService.findInviterState(user);
                orderOperators = operatorService.findByUserAndDate(user, inviterState.getPermitTime(), null);
            }
        } else {
            if (startTime == null || endTime == null) {
                throw new NullParamException("[查询日期不可为空]");
            }
            Date monthStart = DateUtils.getMonthStart(startTime);
            Date monthEnd = DateUtils.getMonthEnd(endTime);
            orderOperators = operatorService.findByUserAndDate(user, monthStart, monthEnd);
        }
        if (orderOperators != null && orderOperators.size() > 0) {
            for (OrderOperator orderOperator : orderOperators) {
                UserOrder order = userOrderRepository.findByIdUserOrderAndUserOrderStateNotIn(orderOperator.getIdUserOrder(),
                        Arrays.asList(OrderStateEnum.CANCELED.getState(), OrderStateEnum.DELETED.getState(), OrderStateEnum.CONFIRMING.getState(), OrderStateEnum.WAITING.getState()));
                if (order != null) {
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    /**
     * 获取下给合作社的订单
     *
     * @param user
     * @return
     */
    @Override
    public List<UserOrder> getToCooperationOrder(User user) {
        if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
            // 查询社长
            User proprieter = cooperationService.findProprieterByUser(user);
            if (proprieter != null) {
                Date date = null;
                if (proprieter.getIdUser().equals(user.getIdUser())) {
                    // 该用户是社长
                    Cooperation cooperation = cooperationService.getById(user.getIdCooperation());
                    if (cooperation != null) {
                        date = cooperation.getRegistTime();
                    } else {
                        throw new ParamErrorException("[无法查询到合作社]");
                    }
                } else {
                    // 该用户是社员
                    // 查询该社员入社时间
                    InviteRelationShip state = cooperationService.findInviterState(user);
                    if (state == null) {
                        throw new NotExistException("[用户不是该合作社社员]");
                    }
                    date = state.getPermitTime();
                }
                if (date == null) {
                    throw new PermissionException("[用户未被批准加入合作社]");
                }
//                List<UserOrder> orders = userOrderRepository.findByCreateByAndReceiveByAndTargetNotAndCreateTimeAfterAndUserOrderStateNot(user.getIdUser(), proprieter.getIdUser(), "平台", date, OrderStateEnum.DELETED.getState());
                List<UserOrder> orders = userOrderRepository.findByCreateByAndReceiveByAndTargetNotAndCreateTimeAfterAndUserOrderState(user.getIdUser(), proprieter.getIdUser(), "平台", date, OrderStateEnum.FINISHED.getState());
                return orders;
            } else {
                throw new PermissionException("[该用户未参加合作社]");
            }
        } else {
            throw new PermissionException("[该用户未参加合作社]");
        }
    }

    /**
     * 获取用户指定订单变更历史
     *
     * @param user    当前用户
     * @param orderNo 指定订单编号
     * @return 变更历史
     */
    @Override
    public List<OrderChangeLogDTO> getUserOrderChangeLog(User user, String orderNo) {
        UserOrder userOrder = getUserOrder(orderNo);
        List<OrderChangeLog> orderChangeLogs = logRepository.findByIdUserOrderAndChangeBy(userOrder.getIdUserOrder(), user.getIdUser());
        /*if (userOrder.getCreateBy().equals(user.getIdUser())
                || userOrder.getReceiveBy().equals(user.getIdUser())) {
            orderChangeLogs = logRepository.findByIdUserOrder(userOrder.getIdUserOrder());
        } else {
            orderChangeLogs = logRepository.findByIdUserOrderAndChangeBy(userOrder.getIdUserOrder(), user.getIdUser());
        }*/

        if (orderChangeLogs != null && orderChangeLogs.size() > 0) {
            List<OrderChangeLogDTO> changeLogDTOS = new ArrayList<>();
            for (OrderChangeLog orderChangeLog : orderChangeLogs) {
                OrderChangeLogDTO changeLogDTO = new OrderChangeLogDTO();
                BeanUtils.copyProperties(orderChangeLog, changeLogDTO);
                changeLogDTO.setOrderNo(orderNo);
                if (user.getIdUser().equals(orderChangeLog.getChangeBy())) {
                    changeLogDTO.setChangeBy(user.getMobile());
                } else {
                    User changeBy = userService.findUser(orderChangeLog.getChangeBy());
                    if (changeBy != null) {
                        changeLogDTO.setChangeBy(changeBy.getMobile());
                    }
                }
                changeLogDTOS.add(changeLogDTO);
            }
            return changeLogDTOS;
        } else {
            return null;
        }
    }

/********************************************************************************订单统计********************************************************************************/
    /**
     * 作业统计--我的统计
     * 按月份统计当前用户合作社工作量
     *
     * @param user
     * @param date
     * @return
     */
    @Override
    public Map<Integer, OrderStatisticsDTO> userStatistics(User user, String date) {
        if (date != null && date.isEmpty() == false) {
            // 查询社长
            User proprieter = cooperationService.findProprieterByUser(user);
            if (proprieter != null) {
                // 根据年初年末筛选协同作业订单开始时间
                Date startTime = DateUtils.getYearStart(date);
                Date endTime = DateUtils.getYearEnd(date);
                Sort sort = new Sort(Sort.Direction.DESC, "createTime");
                List<UserOrder> userOrders = userOrderRepository.findByReceiveByAndUserOrderStateAndIsCooperativeAndUpdateTimeBetween(proprieter.getIdUser(), OrderStateEnum.FINISHED.getState(), true, startTime, endTime, sort);

                Map<Integer, OrderStatisticsDTO> map = new HashMap<>();
                for (UserOrder order : userOrders) {
                    // 是否参与工作
                    OrderOperator operator = operatorService.getOperator(user.getIdUser(), order.getIdUserOrder());
                    if (operator != null) {
                        Date orderStartTime = order.getStartTime();
                        int month = Integer.parseInt(DateUtils.getMonth(orderStartTime));
                        OrderStatisticsDTO orderStatisticsDTO = map.get(month);
                        if (orderStatisticsDTO == null) {
                            orderStatisticsDTO = new OrderStatisticsDTO();
                            orderStatisticsDTO.setUserName(user.getNickname());
                            orderStatisticsDTO.setArea(0f);
                            orderStatisticsDTO.setOrderNum(0);
                            orderStatisticsDTO.setIncome(new BigDecimal(0));
                        }
                        // 是否提交完工报告
                        CompletionReport report = reportService.getOperatorReport(order.getIdUserOrder(), user.getIdUser());
                        if (report != null) {
                            orderStatisticsDTO.setArea(orderStatisticsDTO.getArea() + report.getAcre());
                            orderStatisticsDTO.setOrderNum(orderStatisticsDTO.getOrderNum() + 1);
                            orderStatisticsDTO.setIncome(orderStatisticsDTO.getIncome().add(order.getPrice().multiply(new BigDecimal(report.getAcre()))));
                        }
                        map.put(month, orderStatisticsDTO);
                    }
                }
                return map;
            } else {
                return new HashMap<>();
            }
        } else {
            throw new NullParamException("[统计年份不可为空]");
        }
    }

    /**
     * 查询单个用户合作社完结订单
     *
     * @return
     */
    @Override
    public List<UserOrder> getUserStatisticalOrder(User user, Date startDate, Date endDate) {
        if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
            Map<Long, List<UserOrder>> statisticalOrder = getStatisticalOrder(Arrays.asList(user), user.getIdCooperation(), startDate, endDate, user);
            return statisticalOrder.get(user.getIdUser());
        } else {
            return null;
        }
    }

    /**
     * 返回用户统计订单
     *
     * @param users         统计人员列表
     * @param idCooperation 合作社
     * @param date          统计时间段
     * @param currentUser   当前用户
     * @return
     */
    @Override
    public Map<Long, List<UserOrder>> getStatisticalOrder(List<User> users, Integer idCooperation, Date date, User currentUser) {
        if (date != null) {
            Date dateStart = DateUtils.getMonthStart(date);
            Date dateEnd = DateUtils.getMonthEnd(date);
            return getStatisticalOrder(users, idCooperation, dateStart, dateEnd, currentUser);
        } else {
            throw new NullParamException("查询日期不可为空");
        }
    }

    /**
     * 返回用户统计订单
     *
     * @param users         统计人员列表
     * @param idCooperation 合作社
     * @param startDate     统计时间段
     * @param endDate       统计时间段
     * @param currentUser   当前用户
     * @return
     */
    @Override
    public Map<Long, List<UserOrder>> getStatisticalOrder(List<User> users, Integer idCooperation, Date startDate, Date endDate, User currentUser) {
        return getMembersOrderMap(users, idCooperation, startDate, endDate);
    }

    /**
     * 返回用户统计信息
     *
     * @param users         统计人员列表
     * @param idCooperation 合作社
     * @param date          统计时间段
     * @param currentUser   当前用户
     * @return
     */
    @Override
    public List<OrderStatisticsDTO> getStatisticalMsg(List<User> users, Integer idCooperation, Date date, User currentUser) {
        if (date != null) {
            Date dateStart = DateUtils.getMonthStart(date);
            Date dateEnd = DateUtils.getMonthEnd(date);
            return getStatisticalMsg(users, idCooperation, dateStart, dateEnd, currentUser);
        } else {
            throw new NullParamException("查询日期不可为空");
        }
    }

    /**
     * 返回用户统计信息
     *
     * @param users         统计人员列表
     * @param idCooperation 合作社
     * @param startDate     统计时间段
     * @param endDate       统计时间段
     * @param currentUser   当前用户
     * @return
     */
    @Override
    public List<OrderStatisticsDTO> getStatisticalMsg(List<User> users, Integer idCooperation, Date startDate, Date endDate, User currentUser) {
        if (startDate != null && endDate != null) {
            List<OrderStatisticsDTO> statisticsDTOS = new ArrayList<>();
            if (users != null && users.size() > 0) {
                Map<Long, List<UserOrder>> map = getMembersOrderMap(users, idCooperation, startDate, endDate);
                for (User user : users) {
                    if (user.getIdCooperation().equals(idCooperation)) {
                        List<UserOrder> orders = map.get(user.getIdUser());
                        OrderStatisticsDTO statisticsDTO = new OrderStatisticsDTO();
                        statisticsDTO.setUserName(user.getNickname());
                        statisticsDTO.setAvatar(user.getAvatar());
                        statisticsDTO.setMobile(user.getMobile());
                        statisticsDTO.setOrderNum(orders.size());
                        Float totalArce = 0f;
                        BigDecimal income = new BigDecimal(0);

                        if (orders.size() > 0) {
                            for (UserOrder order : orders) {
                                Float arce = order.getArce();
                                totalArce = totalArce + arce;
                                income = income.add(order.getPrice().multiply(new BigDecimal(arce)));
                            }
                        }
                        statisticsDTO.setArea(totalArce);
                        statisticsDTO.setIncome(income);
                        if (user.getIdUser().equals(currentUser.getIdUser())) {
                            statisticsDTOS.add(0, statisticsDTO);
                        } else {
                            statisticsDTOS.add(statisticsDTO);
                        }
                    }
                }
            }
            return statisticsDTOS;
        } else {
            throw new NullParamException("查询日期不可为空");
        }
    }

    /**
     * 返回单个用户的统计信息
     *
     * @param user
     * @return
     */
    @Override
    public List<UserOrder> getStatisticalMsg(User user) {
        List<OrderOperator> operators = operatorService.findByUser(user);
        return getOperatorFinishedOrders(operators);
    }

    /**
     * 返回单个用户的统计信息
     *
     * @param user
     * @param date 月份时间
     * @return
     */
    @Override
    public List<UserOrder> getStatisticalMsg(User user, Date date) {
        if (date != null) {
            Date dateStart = DateUtils.getMonthStart(date);
            Date dateEnd = DateUtils.getMonthEnd(date);
            List<OrderOperator> operators = operatorService.findByUserAndDate(user, dateStart, dateEnd);
            return getOperatorFinishedOrders(operators);
        } else {
            throw new NullParamException("查询日期不可为空");
        }
    }

    /**
     * 根据年份返回该用户各个月统计信息
     *
     * @param user
     * @param date
     * @return
     */
    @Override
    public List<OrderStatisticsDTO> getUserOrdersStatisticalMsg(User user, String date) {
        if (date != null && date.isEmpty() == false) {
            Date startTime = DateUtils.getYearStart(date);
            Date endTime = DateUtils.getYearEnd(date);
            return getUserOrdersStatisticalMsg(user, startTime, endTime);
        } else {
            throw new NullParamException("[日期不可为空]");
        }
    }

    /**
     * 查询一段时间内不同农作物的统计数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Map<String, Integer> getOrderStatisticalDateByCrop(Date begin, Date end) {
        Map<String, Integer> map = new HashMap<>();
        String[] crops = dictService.getValue(SysConstant.CROP).split(SysConstant.FARM_STRING_SEPARATOR);
        if (null != begin && null != end){
            if (crops != null && crops.length > 0) {
                for (String crop : crops) {
                    Integer orderNum = userOrderRepository.countByUpdateTimeAfterAndUpdateTimeBeforeAndUserOrderStateAndCropName(begin, end, OrderStateEnum.FINISHED.getState(), crop);
                    map.put(crop, orderNum);
                }
            }
        }else if(null == begin && null == end){
            if (crops != null && crops.length > 0) {
                for (String crop : crops) {
                    Integer orderNum = userOrderRepository.countByCropNameAndUserOrderStateIsNot(crop, OrderStateEnum.CANCELED.getState());
                    map.put(crop, orderNum);
                }
            }
        }else{

        }
        return map;
    }

    /**
     * 查询一段时间内不同农机的统计数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Map<String, Integer> getOrderStatisticalDateByMachineType(Date begin, Date end) {
        Map<String, Integer> map = new HashMap<>();
        String[] types = dictService.getValue(SysConstant.MACHINERY_TYPE).split(SysConstant.MACHINERY_STRING_SEPARATOR);
        if (null != begin && null != end){
            if (types != null && types.length > 0) {
                for (String type : types) {
                    Integer orderNum = userOrderRepository.countByUpdateTimeAfterAndUpdateTimeBeforeAndUserOrderStateAndMachineryType(begin, end, OrderStateEnum.FINISHED.getState(), type);
                    map.put(type, orderNum);
                }
            }
        }else if(null == begin && null == end){
            if (types != null && types.length > 0) {
                for (String type : types) {
                    Integer orderNum = userOrderRepository.countByMachineryTypeAndUserOrderStateIsNot(type, OrderStateEnum.CANCELED.getState());
                    map.put(type, orderNum);
                }
            }
        }else{

        }
        return map;
    }

    /*public Map<String, Integer> getOrderStatisticalDateByMachineCategory(Date begin, Date end) {
        Map<String, Integer> map = new HashMap<>();
        List<DictMachineryType> machineryCategorys = dictMachineryRepository.findByParentIdAndHasChildIsTrue(0);
        if (null != machineryCategorys){
            List<Integer> parentIds = new ArrayList<>();
            if (machineryCategorys.size()>0){
                for (DictMachineryType dictMachineryType: machineryCategorys) {
                    parentIds.add(dictMachineryType.getIdMachineryType());
                }
                if (null != begin && null != end){
                    if (parentIds.size() > 0) {
                        for (Integer parentId : parentIds) {
                            Integer orderNum = userOrderRepository.countByUpdateTimeAfterAndUpdateTimeBeforeAndUserOrderStateAndMachineryType(begin, end, OrderStateEnum.FINISHED.getState(), type);
                            map.put(type, orderNum);
                        }
                    }
                }else if(null == begin && null == end){
                    if (parentIds.size() > 0) {
                        for (Integer parentId : parentIds) {
                            Integer orderNum = userOrderRepository.countByMachineryTypeAndUserOrderStateIsNot(type, OrderStateEnum.CANCELED.getState());
                            map.put(type, orderNum);
                        }
                    }
                }else{

                }
            }
        }
        return map;
    }*/

    /**
     * 查询一段时间内不同地区的统计数据
     *
     * @param begin        开始时间
     * @param end          终止时间
     * @param addressType  地址类型 取值：province-省、city-市、county-区县
     * @param addressRange 地址范围（可为空） 当地址类型为city或county时，指明地址查询范围，若为空，则查询全部
     * @return
     */
    @Override
    public JsonArray getOrderStatisticalDataByAddress(Date begin, Date end, String addressType, String addressRange) {
        Integer totalNumber = 0;
        JsonArray jsonArray = new JsonArray();
        Map<String, Integer> map = new HashMap<>();
        CopyOnWriteArrayList<OrderInfo> orderList = new CopyOnWriteArrayList<>();
        if (null == begin && null == end && StringUtils.isBlank(addressType) && StringUtils.isBlank(addressRange)) {
            List<OrderInfo> orders = infoRepository.findAllBySql();
            totalNumber = orders.size();
            orderList.addAll(orders);
        } else if (null != begin && null == end && StringUtils.isBlank(addressType) && StringUtils.isBlank(addressRange)) {

        } else if (null != begin && null != end && StringUtils.isBlank(addressType) && StringUtils.isBlank(addressRange)) {

        } else {
            // todo
        }
        if (orderList.size() > 0) {
            List<String> provinces = new ArrayList<>();
            for (OrderInfo order : orderList) {
                if (StringUtils.isNotBlank(order.getProvince())) {
                    String province = order.getProvince();

                    if (StringUtils.isNotBlank(province)) {
                        provinces.add(province);
                    }
                }
                if (orderList.size() == 0) {
                    break;
                }
            }
            if (provinces.size() > 0) {
                for (int i = 0; i < provinces.size(); i++) {
                    JsonObject jsonObject = new JsonObject();
                    String province = provinces.get(i);
                    Integer count = infoRepository.countByDistinctProvince(province);
                    if (province.contains("省")) {
                        province = province.replaceAll("省", "");
                    } else if (province.contains("市")) {
                        province = province.replaceAll("市", "");
                    } else if (province.contains("内蒙古")) {
                        province = "内蒙古";
                    } else if (province.contains("新疆")) {
                        province = "新疆";
                    } else if (province.contains("宁夏")) {
                        province = "宁夏";
                    } else if (province.contains("西藏")) {
                        province = "西藏";
                    } else if (province.contains("广西")) {
                        province = "广西";
                    } else {
                    }
                    jsonObject.addProperty("name", province);
                    jsonObject.addProperty("value", count);
                    jsonArray.add(jsonObject);
                }
                List<OrderInfo> orderInfos = infoRepository.findDistinctIdUserOrderAndOrderStateNot(OrderStateEnum.CANCELED.getState());
                if (null != orderInfos && orderInfos.size()>0){
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("totalNumber", orderInfos.size());
                    jsonArray.add(jsonObject);
                }
            }
        }
        return jsonArray;
    }

    /**
     * 该用户月统计信息
     *
     * @param user
     * @param startDate 统计开始时间
     * @param endDate   统计结束时间
     * @return
     */
    public List<OrderStatisticsDTO> getUserOrdersStatisticalMsg(User user, Date startDate, Date endDate) {
        List<OrderOperator> operators = operatorService.findByUserAndDate(user, startDate, endDate);
        Map<Integer, List<OrderOperator>> map = new HashMap<>();
        if (operators != null && operators.size() > 0) {
            for (OrderOperator operator : operators) {
                Date orderEndTime = operator.getEndTime();
                int month = Integer.parseInt(DateUtils.getMonth(orderEndTime));
                List<OrderOperator> orderOperators = map.get(month);
                if (orderOperators == null) {
                    orderOperators = new ArrayList<>();
                }
                orderOperators.add(operator);
                map.put(month, orderOperators);
            }
        }

        List<OrderStatisticsDTO> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            List<OrderOperator> orderOperators = map.get(i);
            if (orderOperators != null && orderOperators.size() > 0) {
                OrderStatisticsDTO dto = new OrderStatisticsDTO();
                dto.setUserName(user.getNickname());
                dto.setAvatar(user.getAvatar());
                dto.setMobile(user.getMobile());
                dto.setMonth(String.valueOf(i) + "月");
                dto.setOrderNum(0);
                dto.setArea(0f);
                dto.setIncome(new BigDecimal(0));
                for (OrderOperator operator : orderOperators) {
                    if (operator.getIdCompletionReport() != null && operator.getIdCompletionReport() > 0) {
                        CompletionReport report = reportService.getOperatorReport(operator.getIdCompletionReport());
                        UserOrder order = getUserOrder(operator.getIdUserOrder());
                        dto.setOrderNum(dto.getOrderNum() + 1);
                        dto.setArea(dto.getArea() + report.getAcre());
                        dto.setIncome(dto.getIncome().add(order.getPrice().multiply(new BigDecimal(report.getAcre()))));
                    }
                }
                list.add(dto);
            }
        }
        return list;
    }
/********************************************************************************订单统计********************************************************************************/
/********************************************************************************私有方法********************************************************************************/
    /**
     * 订单号生成
     *
     * @return
     */
    private String getOrderNo() {
        String orderNo = String.valueOf(Math.round(Math.random() * 1000000));
        orderNo = StringUtil.prefixStr(orderNo, 6, "0");
        orderNo = DateUtils.getDate("yyyy/MM/dd").replace("/", "").substring(2) + orderNo;
        UserOrder order = userOrderRepository.findByUserOrderNoAndUserOrderStateNot(orderNo, OrderStateEnum.DELETED.getState());
        if (order == null) {
            return orderNo;
        } else {
            return getOrderNo();
        }
    }

    /**
     * 向数据库存储订单信息并生成相关日志
     *
     * @param userOrder
     * @param user
     * @param mobile
     * @return
     */
    private UserOrder save(UserOrder userOrder, User user, String mobile) {
        OrderChangeLog log = new OrderChangeLog();
        String detail = "用户[" + user.getMobile() + "]创建订单至" + userOrder.getTarget();
        if (userOrder.getTarget().equals("平台")) {
            logger.info("=======================================下单给平台=======================================");
            userOrder.setUserOrderState(OrderStateEnum.WAITING.getState());
            userOrder.setReceiveBy(null);
        } else if (userOrder.getTarget().equals("我的合作社")) {
            logger.info("=======================================下单给我的合作社=======================================");
            if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
                // 查询社长
                Cooperation cooperation = cooperationService.getById(user.getIdCooperation());
                if (cooperation != null) {
                    userOrder.setUserOrderState(OrderStateEnum.WAITING.getState());
                    userOrder.setReceiveBy(null);
                    /*User proprieter = userService.findUser(cooperation.getIdUser());

                    // 指定社长
                    if (proprieter != null) {
                        userOrder.setReceiveBy(proprieter.getIdUser());
                        detail = detail + "-指定接单人为[" + proprieter.getMobile() + "]";
                        userOrder.setUserOrderState(OrderStateEnum.CONFIRMING.getState());
                    } else {
                        throw new ParamErrorException("[未查询到该社社长]");
                    }*/
                } else {
                    throw new ParamErrorException("[请指定正确的下单区域]");
                }
            } else {
                throw new ParamErrorException("[该用户未加入合作社]");
            }
        } else if (userOrder.getTarget().equals("用户")) {
            logger.info("=======================================下单给用户[手机号码搜索]=======================================");
            userOrder.setUserOrderState(OrderStateEnum.WAITING.getState());
            User receiveMan = userService.findByMobile(mobile);
            if (receiveMan != null) {
                if (receiveMan.getIdUser().equals(userOrder.getCreateBy())) {
                    throw new ParamErrorException("[用户不能给自己下单]");
                } else {
                    detail = detail + "-指定接单人为[" + receiveMan.getMobile() + "]";
                    userOrder.setReceiveBy(receiveMan.getIdUser());
                }
            } else {
                throw new ParamErrorException("[未查询到该用户]");
            }
        } else {
            throw new ParamErrorException("[请指定正确的下单区域]");
        }

        userOrder = userOrderRepository.save(userOrder);

        log.setIdUserOrder(userOrder.getIdUserOrder());
        log.setChangeType(OrderLogTypeEnum.CREATE.getType());
        log.setChangeTime(new Date());
        log.setChangeBy(userOrder.getCreateBy());
        log.setDetail(detail);
        logRepository.save(log);
        return userOrder;
    }

    /**
     * 更新订单状态
     *
     * @param userOrder
     * @param user
     * @param stateType
     * @return
     */
    private UserOrder update(UserOrder userOrder, User user, String stateType) {
        // 更新订单
        userOrder.setUpdateTime(new Date());
        userOrder.setUserOrderState(stateType);

        // 生成日志
        OrderChangeLog log = new OrderChangeLog();
        log.setIdUserOrder(userOrder.getIdUserOrder());
        log.setChangeBy(user.getIdUser());
        log.setChangeTime(new Date());
        if (stateType.equals(OrderStateEnum.CONFIRMING.getState())) {
            log.setChangeType(OrderLogTypeEnum.GET_IT.getType());
            log.setDetail("用户[" + user.getMobile() + "]抢到订单[" + userOrder.getUserOrderNo() + "]");
            userOrderRepository.orderAlone(user.getIdUser(), userOrder.getIdUserOrder());
        } else if (stateType.equals(OrderStateEnum.WORKING.getState())) {
            log.setChangeType(OrderLogTypeEnum.CONFIRMED.getType());
            log.setDetail("用户[" + user.getMobile() + "]确认订单[" + userOrder.getUserOrderNo() + "]，该订单内容已被锁定，不可取消或更改");
            userOrder = userOrderRepository.save(userOrder);
        } else if (stateType.equals(OrderStateEnum.CANCELED.getState())) {
            log.setChangeType(OrderLogTypeEnum.CANCEL.getType());
            log.setDetail("用户[" + user.getMobile() + "]取消订单[" + userOrder.getUserOrderNo() + "]");
            userOrder = userOrderRepository.save(userOrder);
        }

        logRepository.save(log);
        return userOrder;
    }

    /**
     * 调度：（用户身份、农机数量与类型、所处方位）
     *
     * @param user      当前用户
     * @param latitude  所处方位
     * @param longitude 所处方位
     * @param pageNo    页码
     * @param pageSize  页面大小
     * @return 有合作社：
     * SELECT *
     * FROM farm_test.user_order
     * WHERE user_order.user_order_state IN ("waiting" , "overdue" , "confirming", "checking", "finished")
     * AND (user_order.target = "平台" OR (user_order.target = "我的合作社" AND user_order.create_by IN (1 , 3 , 51 , 84 , 85)))
     * ORDER BY user_order.user_order_state DESC, user_order.target DESC, user_order.create_time DESC;
     * <p>
     * 无合作社：
     * SELECT *
     * FROM farm_test.user_order
     * WHERE user_order.user_order_state IN ("waiting" , "overdue" , "confirming", "checking", "finished")
     * AND user_order.target = "平台"
     * ORDER BY user_order.user_order_state DESC, user_order.target DESC, user_order.create_time DESC;
     */
    private List<UserOrder> dispatch(User user, Double latitude, Double longitude, Integer pageNo, Integer pageSize) {
//        addressDeal(user, latitude, longitude);
        Sort.Order so1 = new Sort.Order(Sort.Direction.DESC, "userOrderState");
        Sort.Order so2 = new Sort.Order(Sort.Direction.DESC, "target");
        Sort.Order so3 = new Sort.Order(Sort.Direction.DESC, "createTime");
        Sort sort = new Sort(Arrays.asList(so1, so2, so3));
        Pageable pageable = new PageRequest(pageNo, pageSize, sort);
//        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "createTime");
        Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
            @Override
            public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                /****************************查询所有待抢订单（封装时需判断：该订单当前用户是否可抢）***************************/
                 /*Predicate predicate1 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), OrderStateEnum.WAITING.getState());
                 Predicate predicate2 = criteriaBuilder.greaterThanOrEqualTo(root.get("startTime").as(Date.class), new Date());
                 query.where(criteriaBuilder.and(predicate1, predicate2));*/


                /****************************查询所有下单至平台的可接订单，与下单至当前用户所在合作社的可接订单订单（非对应合作社成员，不可抢合作社订单）****************************/
/*//                Predicate predicate1 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), OrderStateEnum.WAITING.getState());
//                Predicate predicate1 = criteriaBuilder.notEqual(root.get("userOrderState").as(String.class), OrderStateEnum.DELETED.getState());
//                Predicate predicate2 = criteriaBuilder.greaterThanOrEqualTo(root.get("startTime").as(Date.class), DateUtils.getDayBegin(new Date()));
//                Predicate predicate2 = criteriaBuilder.notEqual(root.get("userOrderState").as(String.class), OrderStateEnum.CANCELED.getState());
//                Predicate predicate2 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), OrderStateEnum.CONFIRMING.getState());
                CriteriaBuilder.In<Object> state = criteriaBuilder.in(root.get("userOrderState"));
                state.value(OrderStateEnum.WAITING.getState());
                state.value(OrderStateEnum.OVERDUE.getState());
                state.value(OrderStateEnum.CONFIRMING.getState());
                state.value(OrderStateEnum.CHECKING.getState());
                state.value(OrderStateEnum.FINISHED.getState());

                Predicate predicate3 = criteriaBuilder.equal(root.get("target").as(String.class), "平台");

                // 当前用户所在合作社
                if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
                    List<User> members = cooperationService.members(user.getIdCooperation());
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("createBy"));
                    for (User member : members) {
                        in.value(member.getIdUser());
                    }
                    // SELECT *
                    //FROM farm_test.user_order
                    //WHERE (user_order.target="平台" OR user_order.create_by IN (1 , 3 , 51 , 84 , 85))
                    //		AND (userorder0_.user_order_state IN ("waiting" , "overdue" , "confirming"))
                    //ORDER BY  user_order.user_order_state desc, user_order.create_time DESC limit ?
                    query.where(criteriaBuilder.or(predicate3, in), criteriaBuilder.and(state));
//                    query.where(criteriaBuilder.or(predicate3, in), criteriaBuilder.and(predicate1, predicate2));
                } else {
                    query.where(criteriaBuilder.and(state, predicate3));
//                    query.where(criteriaBuilder.and(predicate1, predicate2, predicate3));
                }*/

                CriteriaBuilder.In<Object> state = criteriaBuilder.in(root.get("userOrderState"));
                state.value(OrderStateEnum.WAITING.getState());
//                state.value(OrderStateEnum.OVERDUE.getState());
                state.value(OrderStateEnum.CONFIRMING.getState());
                state.value(OrderStateEnum.CHECKING.getState());
                state.value(OrderStateEnum.PAYMENT.getState());
                state.value(OrderStateEnum.PREPAID.getState());
                state.value(OrderStateEnum.RECEIPT.getState());
                state.value(OrderStateEnum.FINISHED.getState());

                Predicate predicate1 = criteriaBuilder.equal(root.get("target").as(String.class), "平台");
                if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
                    /**
                     * 有合作社
                     * SELECT *
                     * FROM farm_test.user_order
                     * WHERE user_order.user_order_state IN ("waiting" , "overdue" , "confirming", "checking", "finished")
                     *       AND (user_order.target = "平台" OR (user_order.target = "我的合作社" AND user_order.create_by IN (1 , 3 , 51 , 84 , 85)))
                     * ORDER BY user_order.user_order_state DESC, user_order.target DESC, user_order.create_time DESC;
                     */
                    Predicate predicate2 = criteriaBuilder.equal(root.get("target").as(String.class), "我的合作社");
                    List<User> members = cooperationService.members(user.getIdCooperation());
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("createBy"));
                    for (User member : members) {
                        in.value(member.getIdUser());
                    }
                    query.where(criteriaBuilder.and(state), criteriaBuilder.or(predicate1, criteriaBuilder.and(predicate2, in)));
                } else {
                    /**
                     * 无合作社
                     * SELECT *
                     * FROM farm_test.user_order
                     * WHERE user_order.user_order_state IN ("waiting" , "overdue" , "confirming", "checking", "finished")
                     *       AND user_order.target = "平台"
                     * ORDER BY user_order.user_order_state DESC, user_order.target DESC, user_order.create_time DESC;
                     */
                    query.where(criteriaBuilder.and(state, predicate1));
                }
                return query.getRestriction();
            }
        }, pageable);
        List<UserOrder> userOrders = orderPage.getContent();
        return userOrders;
    }

    /**
     * 调度：（用户身份、农机数量与类型、所处方位）
     * 根据检索条件获取符合条件的订单
     * param检索方向==》手机号，农机类型，作物类型，地块地址
     *
     * @param user      当前用户
     * @param param     检索条件
     * @param latitude  所处方位
     * @param longitude 所处方位
     * @param pageNo    页码
     * @param pageSize  页面大小
     * @return
     */
    private List<UserOrder> dispatch(User user, String param, Double latitude, Double longitude, Integer pageNo, Integer pageSize) {
//        addressDeal(user, latitude, longitude);
        boolean isMumeric = StringUtil.isNumeric(param.trim());
        // 是否是数字，是数字匹配手机号码,高于11位的数字，为无效搜索条件
        if (isMumeric && param.trim().length() > 11) {
            return new ArrayList<>();
        } else {
            if (isMumeric && param.trim().length() == 11) {
                // 电话号码数值不应超过20000000000
                Long num = Long.getLong(param);
                if (num > 20000000000l) {
                    return new ArrayList<>();
                }
            }
            Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "startTime");
            Page<UserOrder> orderPage = userOrderRepository.findAll(new Specification<UserOrder>() {
                @Override
                public Predicate toPredicate(Root<UserOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    /****************************查询所有待抢订单（封装时需判断：该订单当前用户是否可抢）***************************/
                 /*Predicate predicate1 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), OrderStateEnum.WAITING.getState());
                 Predicate predicate2 = criteriaBuilder.greaterThanOrEqualTo(root.get("startTime").as(Date.class), new Date());
                 query.where(criteriaBuilder.and(predicate1, predicate2));*/


                    /****************************查询所有下单至平台的可接订单，与下单至当前用户所在合作社的可接订单订单（非对应合作社成员，不可抢合作社订单）****************************/
                    Predicate predicate1 = criteriaBuilder.equal(root.get("userOrderState").as(String.class), OrderStateEnum.WAITING.getState());
                    Predicate predicate2 = criteriaBuilder.equal(root.get("target").as(String.class), "平台");

                    // 判断param语义
                    Predicate predicate3 = null;
                    if (isMumeric && param.trim().length() <= 11) {
                        // 1、是否是数字，是数字匹配手机号码,高于11位的数字，为无效搜索条件
                        predicate3 = criteriaBuilder.like(root.get("guideMobile").as(String.class), "%" + param + "%");
                    } else if (!isMumeric) {
                        String[] crops = dictService.getValue(SysConstant.CROP).split(SysConstant.FARM_STRING_SEPARATOR);
                        String[] types = dictService.getValue(SysConstant.MACHINERY_TYPE).split(SysConstant.MACHINERY_STRING_SEPARATOR);
                        if (param.trim().length() == 2) {
                            // 2、是否是农作物
                            for (String crop : crops) {
                                if (crop.equals(param)) {
                                    predicate3 = criteriaBuilder.equal(root.get("cropName").as(String.class), param);
                                    break;
                                }
                            }
                        } else {
                            // 3、因模糊查询，单字查询对数据库检索速度拖慢，农作物与农机类型只匹配其中一个，农机类型优先
                            boolean isContains = false;
                            for (String type : types) {
                                if (type.equals(param)) {
                                    predicate3 = criteriaBuilder.equal(root.get("machineryType").as(String.class), param);
                                    isContains = true;
                                    break;
                                }
                            }
                            if (!isContains) {
                                for (String type : types) {
                                    if (type.contains(param)) {
                                        predicate3 = criteriaBuilder.like(root.get("machineryType").as(String.class), "%" + param + "%");
                                        isContains = true;
                                        break;
                                    }
                                }
                            }
                            if (!isContains) {
                                for (String crop : crops) {
                                    if (crop.contains(param)) {
                                        predicate3 = criteriaBuilder.like(root.get("cropName").as(String.class), "%" + param + "%");
                                        isContains = true;
                                        break;
                                    }
                                }
                            }
                            if (!isContains) {
                                predicate3 = criteriaBuilder.like(root.get("assemblyAddress").as(String.class), "%" + param + "%");
                            }
                        }
                    }

                    // 当前用户所在合作社
                    if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
                        List<User> members = cooperationService.members(user.getIdCooperation());
                        if (isMumeric && param.trim().length() <= 11) {
                            // 添加下单人手机号码模糊查询结果
                            List<User> userList = userService.findByMoBileLike(param);
                            members.addAll(userList);
                        }
                        CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("createBy"));
                        for (User member : members) {
                            in.value(member.getIdUser());
                        }
                        if (predicate3 != null) {
                            query.where(criteriaBuilder.or(predicate2, in), criteriaBuilder.and(predicate1, predicate3));
                        } else {
                            // 即没有结果（待测）TODO
                            return null;
                        }
                    } else {
                        if (predicate3 != null) {
                            query.where(criteriaBuilder.and(predicate1, predicate2, predicate3));
                        } else {
                            // 即没有结果（待测）TODO
                            return null;
                        }
                    }
                    return query.getRestriction();
                }
            }, pageable);
            List<UserOrder> userOrders = orderPage.getContent();
            return userOrders;
        }
    }

    /**
     * 处理地址信息
     * 暂时不进行划区域调度
     * 仅存储用户地址信息
     *
     * @param user
     * @param latitude
     * @param longitude
     */
    private void addressDeal(User user, Double latitude, Double longitude) {
        if (latitude != null && latitude > 3 && latitude < 54 && longitude != null && longitude > 73 && longitude < 136) {
            try {
                AddressDTO addressDTO = AddressUtil.convert(latitude, longitude);
                if (AddressUtil.canSave(addressDTO)) {
                    if (user.getIdAddress() != null && user.getIdAddress() > 0) {
                        Address address = addressService.getOne(user.getIdAddress());
                        if (!address.getCity().equals(addressDTO.getCity())) {
                            address.setLatitude(latitude);
                            address.setLongitude(longitude);
                            address.setProvince(addressDTO.getProvince());
                            address.setCity(addressDTO.getCity());
                            address.setCounty(addressDTO.getCounty());
                            address.setTown(addressDTO.getTown());
                            address.setDetail(addressDTO.getDetail());
                            addressService.save(address);
                        }
                    } else {
                        Long idAddress = addressService.insert(addressDTO);
                        user.setIdAddress(idAddress);
                        userService.saveUser(user);
                    }
                }
            } catch (Exception e) {
                logger.error("latitude=" + latitude + "--longitude=" + longitude + "--用户经纬度解析错误...");
            }
        }
    }

    /**
     * 合并两个订单集合，并按创建时间倒叙排列
     *
     * @param orders1
     * @param orders2
     * @return
     */
    private List<UserOrder> resetUserOrderList(List<UserOrder> orders1, List<UserOrder> orders2) {
        if ((orders1 == null || orders1.size() == 0) && (orders2 == null || orders2.size() == 0)) {
            return new ArrayList<>();
        } else if ((orders1 == null || orders1.size() == 0) && (orders2 != null && orders2.size() > 0)) {
            return orders2;
        } else if ((orders1 != null && orders1.size() > 0) && (orders2 == null || orders2.size() == 0)) {
            return orders1;
        } else {
            for (UserOrder order : orders2) {
                for (int i = 0; i < orders1.size(); i++) {
                    if (orders1.get(i).getCreateTime().before(order.getStartTime())) {
                        orders1.add(i, order);
                        break;
                    } else if (orders1.get(i).getCreateTime().equals(order.getStartTime())) {
                        if (orders1.get(i).getIdUserOrder().equals(order.getIdUserOrder())) {
                            break;
                        } else {
                            orders1.add(order);
                        }
                    } else if (i == orders1.size() - 1) {
                        orders1.add(order);
                        break;
                    }
                }
            }
            return orders1;
        }
    }

    /**
     * 根据工作人员信息获取已完成订单列表
     *
     * @param operators
     * @return
     */
    private List<UserOrder> getOperatorFinishedOrders(List<OrderOperator> operators) {
        if (operators != null && operators.size() > 0) {
            List<UserOrder> orders = new ArrayList<>();
            for (OrderOperator operator : operators) {
                if (operator.getOperatorState().equals(OperatorStateEnum.FINISHED.getState())) {
                    /*UserOrder userOrder = userOrderRepository.findByIdUserOrderAndUserOrderState(operator.getIdUserOrder(), OrderStateEnum.FINISHED.getState());*/
                    UserOrder userOrder = getUserOrder(operator.getIdUserOrder());
                    if (userOrder != null) {
                        CompletionReport report = reportService.getOperatorReport(userOrder.getIdUserOrder(), operator.getIdUser());
                        if (report != null) {
                            userOrder.setArce(report.getAcre());
                        } else {
                            userOrder.setArce(0f);
                        }
                        orders.add(userOrder);
                    }
                }
            }
            return orders;
        } else {
            return null;
        }
    }

    /**
     * 查询该社员协同作业订单
     *
     * @param userOrders
     * @param user
     * @return
     */
    private List<UserOrder> getOperatorCooperativeOrder(List<UserOrder> userOrders, User user) {
        if (userOrders != null && userOrders.size() > 0) {
            List<UserOrder> orders = new ArrayList<>();
            for (UserOrder userOrder : userOrders) {
                OrderOperator operator = operatorService.getOperator(user.getIdUser(), userOrder.getIdUserOrder());
                if (operator != null) {
                    orders.add(userOrder);
                }
            }
            return orders;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 查询合作社社员合作社订单
     *
     * @param users         合作社社员
     * @param idCooperation 合作社
     * @param startDate     统计日期
     * @param endDate       统计日期
     * @return
     */
    private Map<Long, List<UserOrder>> getMembersOrderMap(List<User> users, Integer idCooperation, Date startDate, Date endDate) {
        Map<Long, List<UserOrder>> map = new HashMap<>();
        for (User user : users) {
            map.put(user.getIdUser(), new ArrayList<>());
        }

        // 查询协同作业订单并放入集合
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Cooperation cooperation = cooperationService.getById(idCooperation);
        List<UserOrder> cooperationOrders = userOrderRepository.findByReceiveByAndUserOrderStateAndIsCooperativeAndUpdateTimeBetween(cooperation.getIdUser().longValue(), OrderStateEnum.FINISHED.getState(), true, startDate, endDate, sort);
        if (cooperationOrders != null && cooperationOrders.size() > 0) {
            for (UserOrder order : cooperationOrders) {
                List<OrderOperator> orderOperators = operatorService.getOrderOperators(order.getIdUserOrder());
                if (orderOperators != null && orderOperators.size() > 0) {
                    for (OrderOperator orderOperator : orderOperators) {
                        UserOrder cooperationOrder = new UserOrder();
                        BeanUtils.copyProperties(order, cooperationOrder);
                        List<UserOrder> orders = map.get(orderOperator.getIdUser());
                        if (map.containsKey(orderOperator.getIdUser())) {
                            if (orderOperator.getIdCompletionReport() != null && orderOperator.getIdCompletionReport() > 0) {
                                CompletionReport report = reportService.getOperatorReport(orderOperator.getIdCompletionReport());
                                if (report != null) {
                                    cooperationOrder.setArce(report.getAcre());
                                } else {
                                    cooperationOrder.setArce(0f);
                                }
                            } else {
                                cooperationOrder.setArce(0f);
                            }
                            orders.add(cooperationOrder);
                            map.put(orderOperator.getIdUser(), orders);
                        }
                    }
                }
            }
        }

        map = addMembersMapGetOrderByCooperator(users, cooperation, startDate, endDate, sort, map);
        return map;
    }

    /**
     * 将合作社成员接自合作社内部的订单加入map中
     *
     * @param users       合作社成员
     * @param cooperation 合作社
     * @param map         集合
     * @return
     */
    private Map<Long, List<UserOrder>> addMembersMapGetOrderByCooperator(List<User> users, Cooperation cooperation, Date startDate, Date endDate, Sort sort, Map<Long, List<UserOrder>> map) {
        if (users != null && users.size() > 0) {
            for (User user : users) {
                if (user.getIdCooperation().equals(cooperation.getIdCooperation())) {
                    if (startDate.before(cooperation.getRegistTime())) {
                        startDate = cooperation.getRegistTime();
                    }
                    if (cooperationService.proprieter(user) == false) {
                        InviteRelationShip inviterState = cooperationService.findInviterState(user);
                        if (inviterState != null) {
                            if (startDate.before(inviterState.getPermitTime())) {
                                startDate = inviterState.getPermitTime();
                            }
                        } else {
                            break;
                        }
                    }
                    /*List<UserOrder> userOrders = userOrderRepository.findByReceiveByAndUserOrderStateAndIsCooperativeAndUpdateTimeBetween(user.getIdUser(), OrderStateEnum.FINISHED.getState(), false, dateStart, dateEnd, sort);*/
                    List<UserOrder> userOrders = userOrderRepository.findByReceiveByAndUserOrderStateAndIsCooperativeAndTargetAndUpdateTimeBetween(user.getIdUser(), OrderStateEnum.FINISHED.getState(), false, "我的合作社", startDate, endDate, sort);
                    List<UserOrder> userCooperationOrder = map.get(user.getIdUser());
                    List<UserOrder> orders = resetUserOrderList(userOrders, userCooperationOrder);
                    map.put(user.getIdUser(), orders);
                }
            }
        }
        return map;
    }
/********************************************************************************私有方法********************************************************************************/

}
