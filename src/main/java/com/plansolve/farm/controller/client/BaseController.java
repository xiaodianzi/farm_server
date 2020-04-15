package com.plansolve.farm.controller.client;

import com.plansolve.farm.controller.client.common.UserController;
import com.plansolve.farm.exception.LoginException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.Cookie;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/
@Slf4j
public class BaseController {

    /**
     * 校验用户免登录状态
     */
    @ModelAttribute
    public void checkUserLoginStatus() {
    }

}
