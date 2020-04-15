package com.plansolve.farm.Interceptor;

import com.plansolve.farm.exception.LoginException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author: 高一平
 * @Date: 2018/6/26
 * @Description: 登录校验的拦截器
 * 执行顺序：
 * preHandler -> Controller -> postHandler -> model渲染-> afterCompletion
 **/
@Slf4j
public class APPLoginInterceptor implements HandlerInterceptor {

    /**
     * 该方法将在请求处理之前进行调用，只有该方法返回true，才会继续执行后续的Interceptor和Controller
     * 当返回值为true 时就会继续调用下一个Interceptor的preHandle 方法，如果已经是最后一个Interceptor的时候就会是调用当前请求的Controller方法
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Token");
        if (token == null || token.length() <= 0) {
            // 该用户未登录
            log.error("【请使用手机验证码登录】：request={}，Token={}", request.getRequestURI(), token);
            throw new LoginException("[请使用手机验证码登录]");
        } else {
            String[] cookie = token.split("=");

            HttpSession session = AppHttpUtil.getSession();
            User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
            if (user == null) {
                log.error("【请重新登录】：request={}，Token={}", request.getRequestURI(), token);
                throw new LoginException("[请重新登录]");
            } else if (EncryptUtil.encrypt(user.getMobile().trim() + user.getAndroidMAC().trim()).equals(cookie[1]) == false) {
                // 该用户身份识别失败
                String encrypt = EncryptUtil.encrypt(user.getMobile().trim() + user.getAndroidMAC().trim());
                log.error("【请使用手机验证码登录】：request={}，Token={}，encrypt={}", request.getRequestURI(), token, encrypt);
                throw new LoginException("[请使用手机验证码登录]");
            } else {
                return true;
            }
        }
    }

    /**
     * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行
     * 该方法将在整个请求结束之后，也就是在DispatcherServlet渲染了对应的视图之后执行。
     * 用于进行资源清理
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 该方法将在请求处理之后，DispatcherServlet进行视图返回渲染之前进行调用，可以在这个方法中对Controller 处理之后的ModelAndView对象进行操作
     *
     * @param request
     * @param response
     * @param handler
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {
    }

}
