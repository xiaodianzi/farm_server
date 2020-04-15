package com.plansolve.farm.controller.client.main.farmer;

import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.order.OrderDTO;
import com.plansolve.farm.model.database.Dictionary;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.code.ResultEnum;
import com.plansolve.farm.service.base.common.DictionaryBaseService;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.service.client.WeChatAppPayService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2018/11/13
 * @Description:
 **/
@Slf4j
@RestController
@RequestMapping("/farm/order/pay")
public class FarmerPayController {

    @Autowired
    private WeChatAppPayService weChatAppPayService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private DictionaryBaseService dictionaryBaseService;

    /**
     * 微信支付-预约下单
     *
     * @param orderNo 支付订单
     * @param money   支付金额
     * @return
     */
    @RequestMapping("/create")
    public Result create(String orderNo, BigDecimal money) {
        if (dictionaryBaseService.getValue("WXPAY").getDictValue().equals("1")) {
            if (orderNo == null || orderNo.isEmpty()) {
                throw new NullParamException("[订单编号不能为空]");
            }
            if (money == null) {
                throw new NullParamException("[付款金额能为空]");
            }
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            HttpServletRequest request = AppHttpUtil.getRequest();
            String ip = request.getRemoteAddr();
            Object result = weChatAppPayService.create(user, orderNo, ip, money);
            return ResultUtil.success(result);
        } else {
            return new Result(ResultEnum.NOT_OPEN.getCode(), "暂未开通，请线下支付", null);
        }
    }

    /**
     * 支付后通知
     *
     * @param request
     * @return
     */
    @RequestMapping("/notify")
    public String payNotify(HttpServletRequest request) {
        try {
            weChatAppPayService.notify(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    /**
     * 用户线下支付
     *
     * @param orderNo
     * @return
     */
    @RequestMapping("/offlinePayment")
    public Result offlinePayment(String orderNo) {
        // 订单状态修改
        UserOrder order = orderService.paidOrder(orderNo, false);
        OrderDTO dto = orderService.loadDTO(order);
        return ResultUtil.success(dto);
    }

}
