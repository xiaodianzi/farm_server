package com.plansolve.farm.controller.console.main.order;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.service.client.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: 高一平
 * @Date: 2018/7/27
 * @Description:
 **/
@Controller
@RequestMapping(value = "/manger/web/orderPage")
public class OrderStatisticalPageController extends BaseController {

    @Autowired
    private DictService dictService;

    @GetMapping(value = "/statisticalOrderPage")
    public String statisticalOrderPage(Model model){
        String[] crops = dictService.getValue(SysConstant.CROP).split(SysConstant.FARM_STRING_SEPARATOR);
        String[] types = dictService.getValue(SysConstant.MACHINERY_TYPE).split(SysConstant.MACHINERY_STRING_SEPARATOR);
        model.addAttribute("crops", crops);
        model.addAttribute("types", types);
        return "order/statisticalOrder";
    }


}
