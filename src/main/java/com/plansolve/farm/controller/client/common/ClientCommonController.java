package com.plansolve.farm.controller.client.common;

import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.database.log.UserErrorLog;
import com.plansolve.farm.service.base.log.UserErrorLogBaseService;
import com.plansolve.farm.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 高一平
 * @Date: 2019/6/21
 * @Description:
 **/
@RestController
@RequestMapping(value = "/farm")
public class ClientCommonController {
    @Autowired
    private UserErrorLogBaseService userErrorLogBaseService;

    @RequestMapping(value = "/saveErrorLog")
    public Result saveErrorLog(UserErrorLog log){
        UserErrorLog errorLog = userErrorLogBaseService.insert(log);
        return ResultUtil.success(errorLog);
    }

}
