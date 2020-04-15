package com.plansolve.farm.controller.console.main.statistical;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.EncacheConstant;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.service.console.user.ConsoleUserService;
import com.plansolve.farm.service.console.OrderStatisticalDataService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EncacheUtil;
import com.plansolve.farm.util.JsonUtil;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Date;
import java.util.Map;

/**
 * @Author: Andrew
 * @Date: 2018/7/27
 * @Description:
 **/
@Controller
@RequestMapping(value = "/manger/web/statistical")
public class DataStatisticalController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ConsoleUserService appUserService;

    @Autowired
    private OrderStatisticalDataService orderStatisticalDataService;

    /**
     * 跳转到数据统计页面
     * @return
     */
    @GetMapping("/mapPage")
    public String mapPage(){
        return "statistical/statisticalData";
    }

    /**
     * 按照行政区域统计订单
     * @param begin
     * @param end
     * @param addressType
     * @param addressRange
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/query/region")
    public String statisticalByRegion(String begin, String end, String addressType, String addressRange){
        Date beginTime = null;
        Date endTime = null;
        if (StringUtils.isNotBlank(begin)){
            beginTime = DateUtils.parseDate(begin);
        }
        if (StringUtils.isNotBlank(end)){
            endTime = DateUtils.parseDate(end);
        }
        JsonArray jsonArray = orderService.getOrderStatisticalDataByAddress(beginTime, endTime, addressType, addressRange);
        String jsonArrayStr = JsonUtil.toJson(jsonArray);
        return jsonArrayStr;
    }

    /**
     * 按照农作物类型统计订单
     * @param daterangepicker_start
     * @param daterangepicker_end
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/query/crops")
    public String statisticalOrderByCrops(String daterangepicker_start, String daterangepicker_end){
        JsonArray jsonArray = new JsonArray();
        Date beginTime = null;
        Date endTime = null;
        if (StringUtils.isNotBlank(daterangepicker_start)){
            beginTime = DateUtils.parseDate(daterangepicker_start);
        }
        if (StringUtils.isNotBlank(daterangepicker_end)){
            endTime = DateUtils.parseDate(daterangepicker_end);
        }
        Map<String, Integer> cropsOrders = orderService.getOrderStatisticalDateByCrop(beginTime, endTime);
        if(cropsOrders.size() > 0){
            Integer totalNumber = 0;
            for (Map.Entry entry : cropsOrders.entrySet()) {
                JsonObject jsonObject = new JsonObject();
                String crops = entry.getKey().toString();
                Integer count = (Integer) entry.getValue();
                totalNumber += count;
                jsonObject.addProperty("name", crops);
                jsonObject.addProperty("value", count);
                jsonArray.add(jsonObject);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("totalNumber", totalNumber);
            jsonArray.add(jsonObject);
        }
        String jsonArrayStr = JsonUtil.toJson(jsonArray);
        return jsonArrayStr;
    }

    /**
     * 按照农机类型类型统计订单
     * @param daterangepicker_start
     * @param daterangepicker_end
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/query/machinery")
    public String statisticalOrderByMachinery(String daterangepicker_start, String daterangepicker_end){
        JsonArray jsonArray = new JsonArray();
        Date beginTime = null;
        Date endTime = null;
        if (StringUtils.isNotBlank(daterangepicker_start)){
            beginTime = DateUtils.parseDate(daterangepicker_start);
        }
        if (StringUtils.isNotBlank(daterangepicker_end)){
            endTime = DateUtils.parseDate(daterangepicker_end);
        }
        Map<String, Integer> machineryOrders = orderStatisticalDataService.getOrderStatisticalDateByMachineType(beginTime, endTime);
        if(machineryOrders.size() > 0){
            Integer totalNumber = 0;
            for (Map.Entry entry : machineryOrders.entrySet()) {
                JsonObject jsonObject = new JsonObject();
                String machinery = entry.getKey().toString();
                Integer count = (Integer) entry.getValue();
                totalNumber += count;
                jsonObject.addProperty("name", machinery);
                jsonObject.addProperty("value", count);
                jsonArray.add(jsonObject);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("totalNumber", totalNumber);
            jsonArray.add(jsonObject);
        }
        String jsonArrayStr = JsonUtil.toJson(jsonArray);
        return jsonArrayStr;
    }

    /**
     * 用户量每日统计
     * @param daterangepicker_start
     * @param daterangepicker_end
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/query/users")
    public String statisticalUserByDate(String daterangepicker_start, String daterangepicker_end){
        Date beginTime = null;
        Date endTime = null;
        if (StringUtils.isNotBlank(daterangepicker_start)){
            beginTime = DateUtils.parseDate(daterangepicker_start);
        }
        if (StringUtils.isNotBlank(daterangepicker_end)){
            endTime = DateUtils.parseDate(daterangepicker_end);
        }
        Map<String, Integer> userData = null;
        boolean exsited = EncacheUtil.checkElementCached(EncacheConstant.REGISTER_TIMELINE);
        if (exsited) {
            Element cacheObj = EncacheUtil.getCacheObj(EncacheConstant.REGISTER_TIMELINE);
            userData = (Map<String, Integer>) cacheObj.getObjectValue();
        } else {
            userData = appUserService.getStatisticalUserData(beginTime, endTime);
            EncacheUtil.addNewCacheObj(EncacheConstant.REGISTER_TIMELINE, userData);
        }
        Map<String, Integer> todayUserData = appUserService.getTodayUserData();
        userData.putAll(todayUserData);
        return JsonUtil.toJson(userData);
    }

    /**
     * 按照农作物类型统计订单数据接口
     * @param crop 农作物
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/query/crop/timeline")
    public String queryOrdersByCropEveryday(String crop){
        String paramCrop = "";
        if (StringUtils.isBlank(crop)){
            throw new ParamErrorException("");
        }else{
            paramCrop = orderStatisticalDataService.loadCropsName(crop);
        }
        Map<String, Integer> userOrderData = null;
        boolean exsited = EncacheUtil.checkElementCached(crop);
        if (exsited) {
            Element cacheObj = EncacheUtil.getCacheObj(crop);
            userOrderData = (Map<String, Integer>) cacheObj.getObjectValue();
        } else {
            userOrderData = orderStatisticalDataService.queryDistinctCreateTimeOrdersByCrops(paramCrop);
            EncacheUtil.addNewCacheObj(crop, userOrderData);
        }
        Map<String, Integer> todayOrdersData = orderStatisticalDataService.queryTodayOrdersByCrops(paramCrop);
        userOrderData.putAll(todayOrdersData);
        return JsonUtil.toJson(userOrderData);
    }

    /**
     * 按照农机类型统计订单数据接口
     * @param machinery 农机
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/query/machinery/timeline")
    public String queryOrdersByMachineryEveryday(String machinery){
        Integer idMachineryParentType = 0;
        if (StringUtils.isBlank(machinery)){
            throw new ParamErrorException("");
        }else{
            idMachineryParentType = orderStatisticalDataService.loadMachineryName(machinery);
        }
        Map<String, Integer> userOrderData = null;
        boolean exsited = EncacheUtil.checkElementCached(machinery);
        if (exsited) {
            Element cacheObj = EncacheUtil.getCacheObj(machinery);
            userOrderData = (Map<String, Integer>) cacheObj.getObjectValue();
        } else {
            userOrderData = orderStatisticalDataService.queryDistinctCreateTimeOrdersByMachinery(idMachineryParentType);
            EncacheUtil.addNewCacheObj(machinery, userOrderData);
        }
        Map<String, Integer> todayOrdersData = orderStatisticalDataService.queryTodayOrdersByMachinery(idMachineryParentType);
        if (null != todayOrdersData){
            userOrderData.putAll(todayOrdersData);
        }
        return JsonUtil.toJson(userOrderData);
    }

    /**
     * 按照作业方式统计订单数据接口
     * @param workmode 作业方式
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/query/workmode/timeline")
    public String queryOrdersByWorkmodeEveryday(String workmode){
        Boolean isCooperative = false;
        if (StringUtils.isBlank(workmode)){
            throw new ParamErrorException("");
        }else{
            if (workmode.equals(EncacheConstant.COLLABORATIVE_WORK)){
                isCooperative = true;
            }
        }
        Map<String, Integer> userOrderData = null;
        boolean exsited = EncacheUtil.checkElementCached(workmode);
        if (exsited) {
            Element cacheObj = EncacheUtil.getCacheObj(workmode);
            userOrderData = (Map<String, Integer>) cacheObj.getObjectValue();
        } else {
            userOrderData = orderStatisticalDataService.queryDistinctOrdersByWorkmode(isCooperative);
            EncacheUtil.addNewCacheObj(workmode, userOrderData);
        }
        Map<String, Integer> todayOrdersData = orderStatisticalDataService.queryTodayOrdersByWorkmode(isCooperative);
        if (null != todayOrdersData){
            userOrderData.putAll(todayOrdersData);
        }
        return JsonUtil.toJson(userOrderData);
    }

    /**
     * 按照作业方式统计订单数据接口
     * @param orderstate 订单状态
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/query/orderstate/timeline")
    public String queryOrdersByOrderstateEveryday(String orderstate){
        String paramOrderState = "";
        if (StringUtils.isBlank(orderstate)){
            throw new ParamErrorException("");
        }else{
            paramOrderState = orderStatisticalDataService.loadOrderStateName(orderstate);
        }
        Map<String, Integer> userOrderData = null;
        boolean exsited = EncacheUtil.checkElementCached(orderstate);
        if (exsited) {
            Element cacheObj = EncacheUtil.getCacheObj(orderstate);
            userOrderData = (Map<String, Integer>) cacheObj.getObjectValue();
        } else {
            userOrderData = orderStatisticalDataService.queryDistinctOrdersByOrderState(paramOrderState);
            EncacheUtil.addNewCacheObj(orderstate, userOrderData);
        }
        Map<String, Integer> todayOrdersData = orderStatisticalDataService.queryTodayOrdersByOrderState(paramOrderState);
        if (null != todayOrdersData){
            userOrderData.putAll(todayOrdersData);
        }
        return JsonUtil.toJson(userOrderData);
    }

}


