package com.plansolve.farm.util;

import com.plansolve.farm.model.SysConstant;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 服务端进行消息推送的工具类
 *
 * @Author: Andrew
 * @Date: 2018/8/10
 */
public class EncacheUtil {

    private static final Logger log = LoggerFactory.getLogger(EncacheUtil.class);

    private static CacheManager cacheManager = CacheManager.getInstance();

    /**
     * 动态获取指定Cache，当Cache不存在时自动创建
     *
     * @return Cache
     */
    public static synchronized Cache getOrAddCache(String cacheName) {
        if (StringUtils.isNotBlank(cacheName)) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                log.info("指定的缓存不存在，马上创建缓存" + cacheName);
                cacheManager.addCacheIfAbsent(cacheName);
                cache = cacheManager.getCache(cacheName);
                Integer remainSecondsOneDay = DateUtils.getRemainSecondsOneDay(new Date());
                //指定缓存的配置信息
                cache.getCacheConfiguration().setEternal(false);
                cache.getCacheConfiguration().setTimeToIdleSeconds(0);
                cache.getCacheConfiguration().setTimeToLiveSeconds(remainSecondsOneDay);
                log.info(SysConstant.SCORE_CACHE_NAME + "缓存动态创建成功,时间：" + DateUtils.formatDateTime(new Date()));
            }
            return cache;
        }
        return null;
    }

    /**
     * 判断指定缓存的对象是否存在
     *
     * @return boolean
     */
    public static synchronized void addNewCacheObj(String cacheKey, Object cacheValue) {
        getOrAddCache(SysConstant.SCORE_CACHE_NAME);
        if (StringUtils.isNotBlank(cacheKey) && null != cacheValue) {
            Cache cache = cacheManager.getCache(SysConstant.SCORE_CACHE_NAME);
            Element ele = new Element(cacheKey, cacheValue);
            cache.put(ele);
            log.info("=====================积分任务" + cacheKey + "添加缓存成功=====================");
        }
    }

    /**
     * 获取指定缓存的对象
     *
     * @return boolean
     */
    public static synchronized Element getCacheObj(String cacheKey) {
        if (StringUtils.isNotBlank(cacheKey)) {
            Cache cache = cacheManager.getCache(SysConstant.SCORE_CACHE_NAME);
            if (null != cache){
                Element element = cache.get(cacheKey);
                log.info("=====================积分缓存对象"+cacheKey+"获取结果"+element+"=====================");
                return element;
            }else{
                getOrAddCache(SysConstant.SCORE_CACHE_NAME);
                getCacheObj(cacheKey);
            }
        }
        return null;
    }

    /**
     * 判断指定缓存的对象是否存在
     *
     * @return boolean
     */
    public static synchronized boolean checkElementCached(String cacheKey) {
        if (StringUtils.isNotBlank(cacheKey)) {
            Cache cache = cacheManager.getCache(SysConstant.SCORE_CACHE_NAME);
            if (cache != null) {
                if (cache.isKeyInCache(cacheKey) && cache.getQuiet(cacheKey) != null) {
                    return true;
                }
            }
        }
        return false;
    }

}

