package com.plansolve.farm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.plansolve.farm.model.database.user.User;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端进行消息推送的工具类
 *
 * @Author: Andrew
 * @Date: 2018/8/10
 */
public class Jdpush {

    private static final Logger log = LoggerFactory.getLogger(Jdpush.class);

    private static final String APP_KEY = "0dce269af60c3d4c90df9a74";

    private static final String MASTER_SECRET = "8811a85083f78b352e9bb507";

    private static final String TITLE = "托管之家消息服务";

    /**
     * 推送消息到Android客戶端
     *
     * @param parm 封装好的消息参数
     */
    public static PushResult jpushAndroid(Map<String, String> parm, List<String> pushList) {
        // 设置好账号的app_key和masterSecret
        // 创建JPushClient(推送的实例)
        JPushClient jpushClient = new JPushClient(Jdpush.MASTER_SECRET, Jdpush.APP_KEY);
        // 推送的关键,构造一个payload
        PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.android())// 指定android平台的用户
                .setAudience(Audience.registrationId(pushList))// 通过客户端用户的注册id进行推送
//				.setAudience(Audience.all())// 给所有用户进行推送
                .setNotification(Notification.android(parm.get("msg"), Jdpush.TITLE, parm))
                //.setOptions(Options.newBuilder().setApnsProduction(false).build())
                // 这里是指定开发环境,不用设置也没关系
                .setMessage(Message.content(parm.get("msg")))// 自定义信息
                .build();
        try {
            return jpushClient.sendPush(payload);
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("Msg ID: " + e.getMsgId());
        }
        return null;
    }

    /**
     * 将消息内容转换成符合极光规范的消息格式
     *
     * @param msg
     * @return
     */
    public static Map<String, String> messageFactory(String msg) {
        Map<String, String> msgMap = new HashMap<>();
        if (StringUtils.isNotBlank(msg)) {
            msgMap.put("msg", msg);
        } else {
            log.error("推送的消息不能为空！");
        }
        return msgMap;
    }

    //消息推送
    public static PushResult pushMessageUtil(String msg, List<User> users) {
        if (null != users && users.size() > 0 && StringUtils.isNotBlank(msg)) {
            // 待发送消息的集合
            List<String> messages = new ArrayList<>();
            messages.add(msg);
            List<String> pushList = new ArrayList<>();
            //没有条件约束的话，只要用户有极光注册的id就进行消息推送
            if (null != users) {
                if (users.size() > 0) {
                    for (User user : users) {
                        if (null != user) {
                            if (StringUtils.isNotBlank(user.getRegistId())) {
                                pushList.add(user.getRegistId());
                            } else {
                                CacheManager cacheManager = CacheManager.getInstance();
                                Cache cache = cacheManager.getCache("pushCache");
                                Element ele = new Element(user.getMobile(), user);
                                cache.put(ele);
                                Element msgMap = cache.get(user.getIdUser());
                                //更新该用户缓存中的信息列表
                                if (null != msgMap) {
                                    List<String> exsitMsg = (List<String>) msgMap.getObjectValue();
                                    exsitMsg.add(msg);
                                    Element element = new Element(user.getIdUser(), exsitMsg);
                                    cache.put(element);
                                } else {
                                    Element initElement = new Element(user.getIdUser(), messages);
                                    cache.put(initElement);
                                }
                                log.info("已经把用户" + user.getMobile() + "放入消息推送的缓存队列。");
                            }
                        }
                    }
                }
            }
            Map<String, String> parm = messageFactory(msg);
            if (null != parm && pushList.size() > 0) {
                PushResult pushResult = jpushAndroid(parm, pushList);
                return pushResult;
            }
        }
        return null;
    }

}

