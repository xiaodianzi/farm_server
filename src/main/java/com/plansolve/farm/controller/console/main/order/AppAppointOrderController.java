package com.plansolve.farm.controller.console.main.order;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.service.console.user.ConsoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 高一平
 * @Date: 2018/7/27
 * @Description:
 **/
@RestController
@RequestMapping(value = "/manger/app/user")
public class AppAppointOrderController extends BaseController {

    @Autowired
    private ConsoleUserService userService;

}
