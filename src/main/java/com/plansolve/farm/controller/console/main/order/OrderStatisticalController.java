package com.plansolve.farm.controller.console.main.order;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @Author: Andrew
 * @Date: 2018/7/27
 * @Description:
 **/
@Controller
@RequestMapping(value = "/manger/web/order")
public class OrderStatisticalController extends BaseController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/statisticalOrder")
    public String statisticalOrder(Model model, String daterangepicker_start, String daterangepicker_end){
        if(StringUtils.isBlank(daterangepicker_start) || StringUtils.isBlank(daterangepicker_end)){
            throw new NullParamException("参数不能为空");
        }
        Date startDate = DateUtils.parseDate(daterangepicker_start);
        Date lastDate = DateUtils.parseDate(daterangepicker_end);
        if (startDate.after(lastDate)){
            throw new ParamErrorException("开始时间不得晚于结束时间");
        }
        Map<String, Integer> cropsOrders = orderService.getOrderStatisticalDateByCrop(startDate, lastDate);
        Map<String, Integer> machineryOrders = orderService.getOrderStatisticalDateByMachineType(startDate, lastDate);
        if(cropsOrders.size() > 0 || machineryOrders.size() > 0){
            model.addAttribute("showChat", true);
        }else{
            model.addAttribute("showChat", false);
        }
        model.addAttribute("begainDate", daterangepicker_start);
        model.addAttribute("endDate", daterangepicker_end);
        model.addAttribute("cropsOrders", JsonUtil.toJson(cropsOrders));
        model.addAttribute("machineryOrders", JsonUtil.toJson(machineryOrders));
        return "order/statisticalOrder";
    }
}
