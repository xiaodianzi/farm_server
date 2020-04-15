package com.plansolve.farm.service.client;

import com.plansolve.farm.model.database.user.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2018/11/14
 * @Description: 微信APP支付接口
 * <p>
 * 微信APP支付流程：
 * 1、客户端选择订单向后台发起支付请求
 * 2、后台接收请求，查询相关订单，向微信支付系统发起统一支付，生成预付订单返回给后台
 * 3、将预付信息返回给客户端
 * 4、用户确认支付，客户端凭借预付信息调取微信支付，用户支付成功返回信息
 **/
public interface WeChatAppPayService {

    /**
     * 微信支付-创建预付订单
     *
     * @param user    付款人
     * @param orderNo 订单号
     * @param ip      用户ip地址
     * @param money   支付金额
     */
    public Object create(User user, String orderNo, String ip, BigDecimal money);

    /**
     * 支付成功，获取通知
     *
     * @param request
     * @throws IOException
     */
    public void notify(HttpServletRequest request) throws IOException;

}
