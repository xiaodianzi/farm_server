package com.plansolve.farm.controller.console.main.order;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.database.order.BidOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.service.console.AppBidOrderService;
import com.plansolve.farm.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: 高一平
 * @Date: 2018/7/27
 * @Description:
 **/
@RestController
@RequestMapping(value = "/manger/app/order/bid")
public class AppBidOrderController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private AppBidOrderService bidOrderService;

    @PostMapping(value = "/createBigOrder")
    public void createBigOrder (HttpServletResponse response, BidOrder bidOrder, String time) throws IOException {
        bidOrder.setStartTime(DateUtils.parseDate(time));
        User user = userService.findUser(280l);
        BidOrder order = bidOrderService.createOrder(bidOrder, user);
        response.sendRedirect("bidOrderPage");
    }


}
