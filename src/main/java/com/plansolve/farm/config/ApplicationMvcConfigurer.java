package com.plansolve.farm.config;

import com.plansolve.farm.Interceptor.APPLoginInterceptor;
import com.plansolve.farm.Interceptor.ConsoleLoginInterceptor;
import com.plansolve.farm.Interceptor.WeChatLoginInterceptor;
import com.plansolve.farm.Interceptor.WxAppletLoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: 高一平
 * @Date: 2018/6/26
 * @Description: 登录校验拦截器的路径配置
 **/

@Configuration
@Slf4j
public class ApplicationMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration appRegistration = registry.addInterceptor(new APPLoginInterceptor());
        appRegistration.addPathPatterns("/farm/**");
        appRegistration.excludePathPatterns(
                "/farm/saveErrorLog",
                "/farm/getRegistCaptcha",
                "/farm/regist",
                "/farm/login/**",
                "/farm/changePassword",
                "/farm/captcha",
                "/farm/checkCaptcha",
                "/farm/order/pay/notify");
        log.info("=======================================APP登录拦截器生效=======================================");

        InterceptorRegistration weChatRegistration = registry.addInterceptor(new WeChatLoginInterceptor());
        weChatRegistration.addPathPatterns("/wechat/**");
        weChatRegistration.excludePathPatterns(
                "/wechat/css/**",
                "/wechat/js/**",
                "/wechat/images/**",
                "/wechat/toLogin",
                "/wechat/sendCaptcha",
                "/wechat/login");
        log.info("=======================================微信公众号登录拦截器生效=======================================");

        InterceptorRegistration consoleRegistration = registry.addInterceptor(new ConsoleLoginInterceptor());
        consoleRegistration.addPathPatterns("/manger/**");
        consoleRegistration.excludePathPatterns(
                "/manger/css/**",
                "/manger/file/**",
                "/manger/fonts/**",
                "/manger/icon/**",
                "/manger/images/**",
                "/manger/js/**");
        log.info("=======================================后台登录拦截器生效=======================================");

        /*InterceptorRegistration wxAppletRegistration = registry.addInterceptor(new WxAppletLoginInterceptor());
        wxAppletRegistration.addPathPatterns("/wx/applet/**");
        wxAppletRegistration.excludePathPatterns(
                "/wx/applet/toLogin",
                "/wx/applet/sendCaptcha",
                "/wx/applet/login");
        log.info("=======================================微信小程序登录拦截器生效=======================================");*/
    }
}
