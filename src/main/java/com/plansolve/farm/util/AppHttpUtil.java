package com.plansolve.farm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 获取HTTP相关对象
 **/

public class AppHttpUtil {
    private final static Logger logger = LoggerFactory.getLogger(AppHttpUtil.class);

    /**
     * 获取HTTP请求
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request;
    }

    /**
     * 获取HTTP响应
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        return response;
    }

    /**
     * 获取Cookie
     *
     * @param name Cookie名称
     * @return
     */
    public static Cookie getCookie(String name) {
        HttpServletRequest request = getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                logger.info("=======================================COOKIE=====================================");
                logger.info(cookie.getName() + ":" + cookie.getValue() + ":" + cookie.getMaxAge());
                logger.info("=======================================COOKIE=====================================");
            }
        }
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie;
    }

    /**
     * 获取客户端对应的SESSION（服务端对话）
     *
     * @return
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        HttpSession session;

        String sessionId = request.getHeader("Plansolv");
        logger.info("客户端会话ID：" + sessionId);
        if (sessionId != null && sessionId.isEmpty() == false) {
            sessionId = sessionId.replace("SESSIONID=", "");
            sessionId = sessionId.replace("sessionid=", "");
            sessionId = sessionId.trim();

            if (sessionId.isEmpty()) {
                session = request.getSession(true);
            } else {
                session = SessionContextUtil.getInstance().getSession(sessionId);
                if (session == null) {
                    session = request.getSession(true);
                }
            }
        } else {
            session = request.getSession(true);
        }
        logger.info("服务器会话ID：SESSIONID=" + session.getId());
        return session;
    }

    /**
     * 向SESSION中存值
     *
     * @param name
     * @param value
     */
    public static void setSessionAttribute(String name, Object value) {
        HttpSession session = getSession();
        session.setAttribute(name, value);
    }

    /**
     * 向SESSION中取值
     *
     * @param name
     * @return
     */
    public static Object getSessionAttribute(String name) {
        HttpSession session = getSession();
        return session.getAttribute(name);
    }

}
