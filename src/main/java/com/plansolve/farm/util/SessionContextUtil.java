package com.plansolve.farm.util;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 高一平
 * @Date: 2018/6/26
 * @Description: 适配客户端所做session工具
 **/
public class SessionContextUtil {

    private static final Logger log = Logger.getLogger(SessionContextUtil.class);

    private static SessionContextUtil instance;

    private Map<String, HttpSession> sessionMap;

    private SessionContextUtil() {
        sessionMap = new HashMap<>();
    }

    public static SessionContextUtil getInstance() {
        log.info("============================SESSION对象开始初始化============================");
        if (instance == null) {
            instance = new SessionContextUtil();
        }
        return instance;
    }

    public synchronized void addSession(HttpSession session) {
        if (session != null) {
            sessionMap.put(session.getId(), session);
        } else {
            log.error("============================因为当前SESSION为null，所以新增SESSION无效！============================");
        }
    }

    public synchronized void delSession(HttpSession session) {
        if (session != null) {
            sessionMap.remove(session.getId());
        } else {
            log.error("============================因为当前SESSION为null，所以删除SESSION无效！============================");
        }
    }

    public synchronized HttpSession getSession(String JSESSIONID) {
        log.info("============================根据JSESSIONID来获取指定的SESSION============================");
        if (JSESSIONID == null)
            return null;
        return sessionMap.get(JSESSIONID);
    }

}
