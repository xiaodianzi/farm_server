package com.plansolve.farm.controller.console.common;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.console.AdminUser;
import com.plansolve.farm.service.common.CaptchaService;
import com.plansolve.farm.service.console.AdminUserService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

@Slf4j
@Controller
public class ManagerController extends BaseController {

    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private CaptchaService captchaService;

    /*************************************************************用户登录*************************************************************/

    /**
     * 密码登录页
     *
     * @return
     */
    @GetMapping(value = "/toLoginByPassword")
    public String loginByPasswordPage() {
        log.info("hehe");
        return "console/loginByPassword";
    }

    /**
     * 验证码登录页
     *
     * @return
     */
    @GetMapping(value = "/toLoginByCaptcha")
    public String loginByCaptchaPage() {
        return "console/loginByCaptcha";
    }

    /**
     * 获取手机验证码并发送给用户
     *
     * @param mobile 登录手机号码（唯一）
     * @return
     */
    @PostMapping(value = "/captcha")
    @ResponseBody
    public Integer getLoginCaptcha(String mobile) throws Exception {
        if (mobile.isEmpty()) {
            log.error("手机号码为空");
            return -1;
        } else {
            // 判断数据库是否已存在这个号码
            AdminUser adminUser = adminUserService.findByMobile(mobile);
            if (adminUser != null) {
                Result result = captchaService.sendCaptcha(mobile);
                if (result.getCode().equals(200)) {
                    return 0;
                } else {
                    log.error("短信发送失败");
                    return 1;
                }
            } else {
                // 该手机号码未注册
                log.error("该手机号码不存在");
                return 2;
            }
        }
    }

    /**
     * 用户登录（手机/密码登录）
     *
     * @param mobile
     * @param password
     * @return
     */
    @PostMapping(value = "/admin/login/password")
    public String loginByPassword(String mobile, String password, Model model) {
        if (password.isEmpty()) {
            log.error("密码为空");
            return error("密码为空！", "/plansolve/toLoginByPassword", model);
        } else {
            // 校对手机密码
            AdminUser adminUser = adminUserService.findByMobile(mobile);
            if (adminUser == null) {
                log.error("该用户不存在");
                return error("该用户不存在！", "/plansolve/toLoginByPassword", model);
            } else {
                if (EncryptUtil.comparator(password, adminUser.getPassword())) {
                    // 登录成功，将当前用户存进session中
                    AppHttpUtil.setSessionAttribute(SysConstant.CURRENT_USER, adminUser);
                    return "redirect:/home";
                } else {
                    // 登录失败
                    log.error("用户手机密码不匹配");
                    return error("手机或密码错误！", "/plansolve/toLoginByPassword", model);
                }
            }
        }
    }

    /**
     * 用户登录（手机/验证码登录）
     *
     * @param mobile
     * @param captcha
     * @return
     */
    @PostMapping(value = "/admin/login/captcha")
    public String loginByCaptcha(String mobile, String captcha, Model model) {
        if (captcha.isEmpty()) {
            log.error("验证码为空");
            return error("验证码不能为空！", "/plansolve/toLoginByCaptcha", model);
        } else {
            AdminUser adminUser = adminUserService.findByMobile(mobile);
            if (adminUser != null) {
                if (captchaService.checkCaptcha(mobile, captcha)) {
                    // 登录成功，将手机号码与设备码共同生成唯一标识，并将当前用户存进session中
                    AppHttpUtil.setSessionAttribute(SysConstant.CURRENT_USER, adminUser);
                    return "redirect:/home";
                } else {
                    log.error("用户手机验证码不匹配");
                    return error("用户手机验证码不匹配！", "/plansolve/toLoginByCaptcha", model);
                }
            } else {
                return error("该用户不存在！", "/plansolve/toLoginByCaptcha", model);
            }
        }
    }

    /*************************************************************用户登录*************************************************************/

    /**
     * 首页
     *
     * @return
     */
    @RequestMapping(value = "/home")
    public String home() {
        return "main/home";
    }
}
