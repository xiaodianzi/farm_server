package com.plansolve.farm.listener;

import com.plansolve.farm.util.SessionContextUtil;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Logger log = Logger.getLogger(SessionListener.class);

    private SessionContextUtil sessionContext = SessionContextUtil.getInstance();

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        log.info("============================监听：SESSION已创建！============================");
        HttpSession session = httpSessionEvent.getSession();
        session.setMaxInactiveInterval(2592000);
        sessionContext.addSession(session);
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        log.info("============================监听：SESSION已销毁！============================");
        sessionContext.delSession(httpSessionEvent.getSession());
    }

}
