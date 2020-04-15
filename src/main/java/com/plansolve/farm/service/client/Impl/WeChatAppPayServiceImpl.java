package com.plansolve.farm.service.client.Impl;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.exception.WeChatSellException;
import com.plansolve.farm.model.database.account.ApplicationDetail;
import com.plansolve.farm.model.database.account.WxAccount;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.order.WxPayOrderNotify;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.model.enums.code.ResultEnum;
import com.plansolve.farm.model.enums.type.TransactionApplicationTypeEnum;
import com.plansolve.farm.repository.order.WxPayOrderNotifyRepository;
import com.plansolve.farm.service.client.*;
import com.plansolve.farm.util.BigDecimalUtil;
import com.plansolve.farm.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/11/14
 * @Description:
 **/
@Slf4j
@Service
public class WeChatAppPayServiceImpl implements WeChatAppPayService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private WxPayOrderNotifyRepository wxPayOrderNotifyRepository;
    @Autowired
    private TransactionApplicationService applicationService;
    @Autowired
    private UserService userService;
    @Value("${development}")
    private Boolean development;
    private static Integer ORDER_EXPIRE_TIME = 3;

    /**
     * 微信支付-创建预付订单
     *
     * @param user    付款人
     * @param orderNo 订单号
     * @param ip      用户ip地址
     * @param money   支付金额
     * @return
     */
    @Override
    public Object create(User user, String orderNo, String ip, BigDecimal money) {
        UserOrder order = orderService.getUserOrder(orderNo);

        // 正式环境下，校验支付金额，支付金额需与订单应付金额相等
        if (!development) {
            BigDecimal demandAmount = order.getDemandAmount();
            if (!BigDecimalUtil.equals(demandAmount, money)) {
                throw new WeChatSellException(ResultEnum.WE_CHAT_PAY_ERROR, "[支付金额不正确]");
            }
        }

        // 创建支付申请
        applicationService.create(user, TransactionApplicationTypeEnum.WX_PAYMENT.getType(), order.getIdUserOrder(), null, money);
        if (order != null) {
            if (order.getUserOrderState().equals(OrderStateEnum.PAYMENT.getState())) {
                try {
                    WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
                    orderRequest.setTradeType(WxPayConstants.TradeType.APP);
                    orderRequest.setBody("托管之家-订单支付");
                    orderRequest.setOutTradeNo(order.getUserOrderNo());
                    orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(money.toString()));
                    /*orderRequest.setOpenid(openId); // 用户openid */
                    orderRequest.setSpbillCreateIp(ip);
                    orderRequest.setTimeStart(getTime(new Date()));
                    orderRequest.setTimeExpire(DateUtils.formatDate(DateUtils.getDate_PastOrFuture_Minute(new Date(), ORDER_EXPIRE_TIME), "yyyyMMddHHmmss"));
                    return wxPayService.createOrder(orderRequest);
                } catch (WxPayException e) {
                    log.error("【微信APP支付失败】订单号：{}，原因：{}", orderNo, e.getMessage());
                    e.printStackTrace();
                    throw new WeChatSellException(ResultEnum.WE_CHAT_PAY_ERROR, e.getReturnMsg());
                }
            } else {
                throw new PermissionException("[无法支付-该订单状态为：" + order.getUserOrderState() + "]");
            }
        } else {
            throw new WeChatSellException(ResultEnum.ORDER_NOT_EXIST_ERROR);
        }
    }

    /**
     * 订单支付成功后通知
     *
     * @param request
     * @throws IOException
     */
    @Override
    @Transactional
    public void notify(HttpServletRequest request) throws IOException {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            log.info("微信支付返回结果：" + xmlResult);
            WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(xmlResult);
            String orderNo = result.getOutTradeNo();
            String transactionId = result.getTransactionId(); // 微信交易号
            if (orderNo == null || orderNo.isEmpty()) {
                throw new NullParamException("[订单编号为空]");
            }
            UserOrder order = orderService.getUserOrder(orderNo);
            if (order == null) {
                throw new NullParamException("[查询不到该订单]");
            }

            try {
                // 保存返回结果，不参与任何逻辑，若出错，不抛出异常
                WxPayOrderNotify wxPayOrderNotify = new WxPayOrderNotify();
                BeanUtils.copyProperties(result, wxPayOrderNotify);
                wxPayOrderNotify.setCreateTime(new Date());
                wxPayOrderNotify.setIdUser(order.getCreateBy());
                wxPayOrderNotifyRepository.save(wxPayOrderNotify);

                // 保存用户微信账户，并记录最近一次支付时间
                String openid = result.getOpenid();
                if (openid != null && !openid.isEmpty()) {
                    accountService.findWxAccount(order.getCreateBy(), openid);
                }
            } catch (Exception e) {
                log.error("【微信支付返回结果出错】" + xmlResult);
            }

            // 支付完成，初审
            ApplicationDetail paymentCheck = applicationService.paymentCheck(order, TransactionApplicationTypeEnum.WX_PAYMENT.getType(), transactionId);
            if (paymentCheck.getApplicationResult()) {
                // 订单状态修改
                orderService.paidOrder(orderNo, true);
                // 支付完成，复核
                ApplicationDetail paymentRecheck = applicationService.paymentRecheck(order, TransactionApplicationTypeEnum.WX_PAYMENT.getType());
                if (paymentRecheck.getApplicationResult()) {
                    log.info("用户微信支付至接单人账户");
                    // 线上支付后，系统自动为接单人确认收款
                    User user = userService.findUser(order.getReceiveBy());
                    orderService.confirmReceipt(user, orderNo);
                    orderService.finishOrder(user, orderNo);
                }
            }
        } catch (WxPayException e) {
            log.error("微信回调结果异常,异常原因{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取预付订单所需要的时间戳
     *
     * @return
     */
    private String getTime(Date date) {
        String time = DateUtils.formatDate(date, "yyyy-MM-dd HH:mm:ss");
        time = time.replace("-", "");
        time = time.replace(":", "");
        time = time.replace(" ", "");
        return time;
    }
}
