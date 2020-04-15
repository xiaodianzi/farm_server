package com.plansolve.farm.controller.console.main.order;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.service.client.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: 高一平
 * @Date: 2018/7/27
 * @Description:
 **/
@Controller
@RequestMapping(value = "/manger/app/order/bid")
public class AppBidOrderPageController extends BaseController {

    @Autowired
    private DictService dictService;

    @GetMapping(value = "/bidOrderPage")
    public String bidOrderPage(){
        return "order/bidOrderList";
    }

    /**
     * 跳转预约订单创建页面
     *
     * @return
     */
    @GetMapping(value = "/appBidOrderFormPage")
    public String appBidOrderFormPage(ModelMap modelMap) {
        String[] crops = dictService.getValue(SysConstant.CROP).split(SysConstant.FARM_STRING_SEPARATOR);
        String[] types = dictService.getValue(SysConstant.MACHINERY_TYPE).split(SysConstant.MACHINERY_STRING_SEPARATOR);
        modelMap.addAttribute("crops", crops);
        modelMap.addAttribute("types", types);
        return "order/bidOrderForm";
    }

}
