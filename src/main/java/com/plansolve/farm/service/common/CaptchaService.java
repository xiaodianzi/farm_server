package com.plansolve.farm.service.common;

import com.plansolve.farm.exception.MessageSendErrorException;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.Message;
import com.plansolve.farm.service.console.MessageService;
import com.plansolve.farm.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/6
 * @Description:
 **/
@Service
public class CaptchaService {

    private final static Logger logger = LoggerFactory.getLogger(CaptchaService.class);

    @Value("${development}")
    private Boolean development;
    @Autowired
    private MessageService messageService;

    /**
     * 生成验证码
     *
     * @param mobile
     * @return
     */
    private String getCaptcha(String mobile) {
        String captcha = String.valueOf(Math.round(Math.random() * 10000));
        captcha = StringUtil.prefixStr(captcha, 4, "0");
        /*logger.info(mobile + "验证码为：" + captcha);*/
        return captcha;
    }

    /**
     * 发送验证码
     *
     * @param mobile 接受验证码手机号码
     * @return 发送结果
     * @throws Exception
     */
    public Result sendCaptcha(String mobile) throws Exception {
        // 获取验证码
        String captcha = getCaptcha(mobile);
        // 短信内容
        String content = "您好，您的验证码是" + captcha + "【农小满】";

        Message message = new Message();
        message.setMobile(mobile);
        message.setDetail(content);

        String backMessage = HttpUtil.sendCaptcha(mobile, captcha);
        logger.info(backMessage);

        if (backMessage.startsWith("000")) {
            message.setIsSuccess(true);
            messageService.save(message);

            // 发送成功
            HttpSession session = AppHttpUtil.getSession();
            session.setAttribute(SysConstant.CAPTCHA, captcha);
            session.setAttribute(SysConstant.MOBILE, mobile);
            session.setAttribute(SysConstant.EXPIRE_TIME, DateUtils.getDate_PastOrFuture_Minute(new Date(), 3));
            return ResultUtil.success(null);
        } else if (backMessage.startsWith("-111")) {
            message.setIsSuccess(false);
            messageService.save(message);

            // 服务器IP地址不是发送短信服务的授权IP
            if (development) {
                /**************************************************测试***************************************************/
                HttpSession session = AppHttpUtil.getSession();
                session.setAttribute(SysConstant.CAPTCHA, captcha);
                session.setAttribute(SysConstant.MOBILE, mobile);
                session.setAttribute(SysConstant.EXPIRE_TIME, DateUtils.getDate_PastOrFuture_Minute(new Date(), 3));
                logger.info("验证码为：" + captcha);
                return ResultUtil.success(captcha);
                /**************************************************测试***************************************************/
            } else {
                throw new MessageSendErrorException("[服务器IP地址未经短信服务授权]");
            }
        } else {
            // 其他原因导致短信发送失败
            throw new MessageSendErrorException("[未知原因]");
        }
    }

    /**
     * 校验手机验证码
     *
     * @param mobile  验证手机
     * @param captcha 验证码
     * @return
     */
    public Boolean checkCaptcha(String mobile, String captcha) {
        // 获取系统内存储手机验证码
        HttpSession session = AppHttpUtil.getSession();
        String sysCaptcha = (String) session.getAttribute(SysConstant.CAPTCHA);
        String sysMobile = (String) session.getAttribute(SysConstant.MOBILE);
        Date expireTime = (Date) session.getAttribute(SysConstant.EXPIRE_TIME);
        if (sysCaptcha == null || sysCaptcha.isEmpty()
                || sysMobile == null || sysCaptcha.isEmpty()
                || expireTime == null) {
            // 系统未获取到验证码
            throw new NullParamException("[系统未获取到验证码]");
        } else {
            /*// 验证码仅可验证一次，验证错误，需重新获取
            session.setAttribute(SysConstant.CAPTCHA, null);
            session.setAttribute(SysConstant.MOBILE, null);
            session.setAttribute(SysConstant.EXPIRE_TIME, null);*/
            if (sysCaptcha.equals(captcha) && sysMobile.equals(mobile)) {
                if (expireTime.before(new Date())) {
                    // 手机验证码已过期
                    throw new ParamErrorException("[手机验证码已过期]");
                } else {
                    return true;
                }
            } else {
                // 手机验证码不匹配
                throw new ParamErrorException("[手机验证码与系统不匹配]");
            }
        }
    }

    /**
     * 给指定手机用户发送短信
     * @param mobile
     * @return
     */
    private Result sendNotice(String mobile, String notice) throws Exception {
        String backMessage = HttpUtil.sendNotice(mobile, notice);
        logger.info(backMessage);
        if (backMessage.startsWith("000")) {
            // 发送成功
            return ResultUtil.success(null);
        } else if (backMessage.startsWith("-111")) {
            // 服务器IP地址不是发送短信服务的授权IP
            /**************************************************测试***************************************************/
//            return ResultUtil.success(backMessage);
            /**************************************************测试***************************************************/
            throw new MessageSendErrorException("[服务器IP地址未经短信服务授权]");
        } else {
            // 其他原因导致短信发送失败
            throw new MessageSendErrorException("[未知原因]");
        }
    }


}
