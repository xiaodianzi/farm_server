package com.plansolve.farm.aspect.client;

import cn.jpush.api.push.PushResult;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.order.CompletionReport;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.Jdpush;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/22
 * @Description: 推送流程：//成功后发送推送消息给关联用户
 * List<String> pushList =  = Jdpush.findUsersByCoByCondition(u);
 * String msg = "社长给您指派了新的角色, 请赶快去看看吧。";
 * Map<String, String> parm =  = Jdpush.messageFactorctory(msg);
 * PushResult pushResult =  = Jdpush.jpushAndroid(oid(parm, pushList);
 * if(pushResult != null && && pushResult.isResultOK())K()){
 * logger.info("社长("社长给社员指派新角色的消息推送成功。");
 * }else{
 * logger.error("社长("社长给社员指派新角色的消息推送失败！");
 * }
 **/
@Aspect
@Component
public class JdpushAspect {

    private final Logger logger = LoggerFactory.getLogger(JdpushAspect.class);
    @Autowired
    private UserService userService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private OrderService orderService;

    /**
     * 用户通知切面
     */
    @Pointcut("execution(public * com.plansolve.farm.service.client.UserService.change*(..))")
    public void userNoticePointcut() {
    }

    /**
     * 订单状态通知切面
     */
    @Pointcut("execution(public * com.plansolve.farm.service.client.OrderService.*(..))")
    public void orderNoticePointcut() {
    }

    @Pointcut("execution(public * com.plansolve.farm.service.client.OrderOperatorService.*(..))")
    public void operatorNoticePointcut() {
    }

    @Pointcut("execution(public * com.plansolve.farm.service.client.OperatorReportService.*(..))")
    public void reportNoticePointcut() {
    }

    /*******************************************************用户通知*******************************************************/

    @AfterReturning(returning = "user", pointcut = "userNoticePointcut()")
    public void userNoticeAfterReturning(JoinPoint joinPoint, User user) {
        String msg = "用户通知：";

        String method = joinPoint.getSignature().getName();
        if (method.equals("changeUserAndroidMAC")) {
            msg = msg + "您变更了账号绑定的硬件设备";
        } else if (method.equals("changeToBeFarmer")) {
            Object[] args = joinPoint.getArgs();
            if (args[1] != null && args[1].equals(true)) {
                msg = msg + "您获得了种植户身份";
            } else if (args[1] != null && args[1].equals(false)) {
                msg = msg + "您失去了种植户身份";
            }
        } else if (method.equals("changeToBeOperator")) {
            Object[] args = joinPoint.getArgs();
            if (args[1] != null && args[1].equals(true)) {
                msg = msg + "您获得了农机手身份";
            } else if (args[1] != null && args[1].equals(false)) {
                msg = msg + "您失去了农机手身份";
            }
        }

        if (msg.equals("用户通知：") == false) {
            PushResult pushResult = Jdpush.pushMessageUtil(msg, Arrays.asList(user));
            if (pushResult != null && pushResult.isResultOK()) {
                logger.info("用户[" + user.getMobile() + "]推送'" + msg + "'成功");
            } else {
                logger.error("用户[" + user.getMobile() + "]推送'" + msg + "'失败");
            }
        }
    }

    /*******************************************************用户通知*******************************************************/

    /*******************************************************订单通知*******************************************************/

    @AfterReturning(returning = "order", pointcut = "orderNoticePointcut()")
    public void orderNoticeAfterReturning(JoinPoint joinPoint, UserOrder order) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        String method = joinPoint.getSignature().getName();
        String msg = "订单通知：";
        if (method.equals("createOrder")) {
            /*if (order.getTarget().equals("平台")) {
                List<User> users = userService.findAllUserLocal(0d, 0d, user);
                msg = msg + "平台上出现新订单了，快去看看";
                sendOrderNotice(users, msg, order.getUserOrderNo());
            } else if (order.getTarget().equals("我的合作社")) {
                User createUser = userService.findUser(order.getCreateBy());
                List<User> users = cooperationService.members(createUser.getIdCooperation());
                User proprieter = cooperationService.findProprieterByUser(createUser);
                users.add(proprieter);
                msg = msg + "合作社出现新订单了，快去看看";
                sendOrderNotice(users, msg, order.getUserOrderNo());
            } else if (order.getTarget().equals("用户")) {
                msg = msg + "您有新订单了，快去看看";
                sendOrderNotice(Arrays.asList(userService.findUser(order.getReceiveBy())), msg, order.getUserOrderNo());
            }*/
        } else if (method.equals("repeatCreateOrder")) {
            /*List<User> users = userService.findAllUserLocal(0d, 0d, user);
            msg = msg + "平台上出现新订单了，快去看看";
            sendOrderNotice(users, msg, order.getUserOrderNo());*/
        } else if (method.equals("updateGuide")) {
            if (order.getReceiveBy() != null && order.getReceiveBy() > 0) {
                msg = msg + "订单" + order.getUserOrderNo() + "更改了引路人信息，请及时查看";
                sendOrderNotice(Arrays.asList(userService.findUser(order.getReceiveBy())), msg, order.getUserOrderNo());
            }
        } else if (method.equals("receiveOrder")) {
            if (user.getIdUser().equals(order.getReceiveBy())) {
                msg = msg + "订单" + order.getUserOrderNo() + "已被用户" + user.getNickname() + "接单";
                sendOrderNotice(Arrays.asList(userService.findUser(order.getCreateBy())), msg, order.getUserOrderNo());
            }
        } else if (method.equals("makeSureOrder")) {
            if (user.getIdUser().equals(order.getReceiveBy())) {
                msg = msg + "订单" + order.getUserOrderNo() + "农机手已确认，订单已不可取消";
                sendOrderNotice(Arrays.asList(userService.findUser(order.getCreateBy())), msg, order.getUserOrderNo());
            }
        } else if (method.equals("cancelOrder")) {
            if (user.getIdUser().equals(order.getCreateBy())) {
                if (order.getReceiveBy() != null && order.getReceiveBy() > 0) {
                    msg = msg + "订单" + order.getUserOrderNo() + "已被下单人取消";
                    sendOrderNotice(Arrays.asList(userService.findUser(order.getReceiveBy())), msg, order.getUserOrderNo());
                }
            } else if (user.getIdUser().equals(order.getReceiveBy())) {
                msg = msg + "订单" + order.getUserOrderNo() + "已被接单人取消";
                sendOrderNotice(Arrays.asList(userService.findUser(order.getCreateBy())), msg, order.getUserOrderNo());
            }
        } else if (method.equals("finalAcceptance")) {
            if (user.getIdUser().equals(order.getCreateBy())) {
                msg = msg + "订单" + order.getUserOrderNo() + "种植户已验收完成";
                sendOrderNotice(Arrays.asList(userService.findUser(order.getReceiveBy())), msg, order.getUserOrderNo());
            }
        }

    }

    @AfterReturning(returning = "object", pointcut = "operatorNoticePointcut()")
    public void operatorNoticeAfterReturning(JoinPoint joinPoint, Object object) {
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String msg = "订单通知：[协同作业]";
        String orderNo = "";
        List<User> users = new ArrayList<>();

        if (method.equals("setOrderWorkers")) {
            if (args[1].equals(true)) {
                List<String> workers = (List<String>) args[2];
                for (String worker : workers) {
                    User user = userService.findByMobile(worker);
                    if (user != null) {
                        users.add(user);
                    }
                }
                orderNo = (String) args[3];
                msg = msg + "您被邀请为订单" + orderNo + "工作人员，请及时处理";
            }
        } else if (method.equals("managerWorkers")) {
            List<String> workers = (List<String>) args[1];
            for (String worker : workers) {
                User user = userService.findByMobile(worker);
                if (user != null) {
                    users.add(user);
                }
            }
            orderNo = (String) args[0];
            if (args[2].equals(true)) {
                msg = msg + "您被邀请为订单" + orderNo + "工作人员，请及时处理";
            } else if (args[2].equals(false)) {
                msg = msg + "您已不再是订单" + orderNo + "的工作人员";
            }
        } else if (method.equals("acceptOrder")) {
            orderNo = (String) args[1];
            UserOrder order = orderService.getUserOrder(orderNo);

            User user = (User) args[0];
            users.add(userService.findUser(order.getReceiveBy()));

            if (args[2].equals(true)) {
                msg = msg + "用户[" + user.getNickname() + "]同意成为订单" + orderNo + "的工作人员";
            } else if (args[2].equals(false)) {
                msg = msg + "用户[" + user.getNickname() + "]拒绝成为订单" + orderNo + "的工作人员";
            }
        } else if (method.equals("cancelOperator")) {
            orderNo = (String) args[0];
            OrderOperator orderOperator = (OrderOperator) args[1];
            User user = (User) args[2];
            users.add(userService.findUser(orderOperator.getIdUser()));
            msg = msg + "您已被 " + user.getNickname() + "取消订单" + orderNo + "工作人员资格";
        }

        sendOrderNotice(users, msg, orderNo);
    }

    @AfterReturning(returning = "report", pointcut = "reportNoticePointcut()")
    public void reportNoticeAfterReturning(JoinPoint joinPoint, CompletionReport report) {
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String msg = "订单通知：";
        String orderNo = "";
        List<User> users = new ArrayList<>();

        if (method.equals("sentReport")) {
            User user = (User) args[0];
            UserOrder order = orderService.getUserOrder(report.getIdUserOrder());
            if (order.getReceiveBy().equals(user.getIdUser()) == false) {
                orderNo = order.getUserOrderNo();
                if (order.getCooperative()) {
                    users.add(userService.findUser(order.getReceiveBy()));
                    msg = msg + "用户" + user.getNickname() + "提交了完工汇报，请查看";
                }
            }
        } else if (method.equals("endReport")) {
            orderNo = (String) args[1];
            UserOrder order = orderService.getUserOrder(orderNo);
            users.add(userService.findUser(order.getCreateBy()));
            msg = msg + "您的订单" + orderNo + "已提交完工，请查验";
        }

        sendOrderNotice(users, msg, orderNo);
    }

    /**
     * 订单通知发送
     *
     * @param users
     * @param msg
     * @param orderNo
     */
    private void sendOrderNotice(List<User> users, String msg, String orderNo) {
        if (msg.equals("订单通知：") == false && users != null && users.size() > 0) {
            PushResult pushResult = Jdpush.pushMessageUtil(msg, users);
            if (pushResult != null && pushResult.isResultOK()) {
                logger.info("订单[" + orderNo + "]推送'" + msg + "'成功");
            } else {
                logger.error("订单[" + orderNo + "]推送'" + msg + "'失败");
            }
        }
    }

    /*******************************************************订单通知*******************************************************/

}
