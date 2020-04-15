package com.plansolve.farm.Interceptor;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.WeChatUser;
import com.plansolve.farm.repository.user.UserInfoRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.repository.user.WeChatUserRepository;
import com.plansolve.farm.service.wechat.WeChatService;
import com.plansolve.farm.util.AppHttpUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 高一平
 * @Date: 2018/6/26
 * @Description: 登录校验的拦截器
 * 执行顺序：
 * preHandler -> Controller -> postHandler -> model渲染-> afterCompletion
 **/
@Slf4j
public class WeChatLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WeChatUserRepository weChatUserRepository;
    @Autowired
    private WeChatService weChatService;

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
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        if (user == null) {
            WxMpOAuth2AccessToken token = (WxMpOAuth2AccessToken) AppHttpUtil.getSessionAttribute(SysConstant.WX_MP_OAUTH2_ACCESS_TOKEN);
            if (token == null) {
                String method = request.getMethod();
                if (method.equals("GET") || method.equals("get")) {
                    String url = "/wechat/home";
                    request.getRequestDispatcher("/authorize?returnUrl=" + url).forward(request, response);
                }
                return false;
            } else {
                // 解决拦截器无法自动注入的问题
                if (userRepository == null || weChatUserRepository == null || weChatService == null) {
                    BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
                    if (userRepository == null) {
                        userRepository = (UserRepository) factory.getBean("userRepository");
                    }
                    if (weChatUserRepository == null) {
                        weChatUserRepository = (WeChatUserRepository) factory.getBean("weChatUserRepository");
                    }
                    if (weChatService == null) {
                        weChatService = (WeChatService) factory.getBean("weChatService");
                    }
                }

                WeChatUser weChatUser = weChatUserRepository.findByOpenId(token.getOpenId());
                if (weChatUser == null) {
                    request.getRequestDispatcher("/wechat/toLogin").forward(request, response);
                    return false;
                } else {
                    user = userRepository.findByIdUser(weChatUser.getIdUser());
                    AppHttpUtil.setSessionAttribute(SysConstant.CURRENT_USER, user);
                    return true;
                }
            }
        } else {
            return true;
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
