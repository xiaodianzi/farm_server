package com.plansolve.farm.service.console;

import java.util.Date;
import java.util.Map;

/**
 * @Author: Andrew
 * @Date: 2018/7/27
 * @Description: 大屏数据统计信息接口
 **/

public interface OrderStatisticalDataService {

    /**
     * 查询一段时间内不同农机的统计数据
     *
     * @param begin 统计开始日期
     * @param end 统计结束日期
     * @return
     */
    public Map<String, Integer> getOrderStatisticalDateByMachineType(Date begin, Date end);

    /**
     * 指定农作物时间轴统计信息接口
     * @param crop 农作物
     * @return 每一天的订单数量
     */
    public Map<String, Integer> queryDistinctCreateTimeOrdersByCrops(String crop);

    /**
     * 查询当天农作物的订单数量
     * @param crop
     * @return
     */
    public Map<String,Integer> queryTodayOrdersByCrops(String crop);

    /**
     * 指定农机时间轴统计信息接口
     * @param idMachineryParentType 农机
     * @return 每一天的订单数量
     */
    public Map<String,Integer> queryDistinctCreateTimeOrdersByMachinery(Integer idMachineryParentType);

    /**
     * 查询当天农机的订单数量
     * @param idMachineryParentType
     * @return
     */
    public Map<String,Integer> queryTodayOrdersByMachinery(Integer idMachineryParentType);

    /**
     * 通过参数转换为农作物名称
     * @param crop
     * @return
     */
    public String loadCropsName(String crop);

    /**
     * 通过参数转换为农机名称
     * @param machinery
     * @return
     */
    public Integer loadMachineryName(String machinery);

    /**
     * 通过作业方式查询订单量信息
     * @param isCooperative
     * @return
     */
    public Map<String,Integer> queryDistinctOrdersByWorkmode(Boolean isCooperative);

    /**
     * 通过作业方式查询当日订单量信息
     * @param isCooperative
     * @return
     */
    public Map<String,Integer> queryTodayOrdersByWorkmode(Boolean isCooperative);

    /**
     * 转换参数为订单状态的值
     * @param orderstate
     * @return
     */
    public String loadOrderStateName(String orderstate);

    public Map<String,Integer> queryDistinctOrdersByOrderState(String paramOrderState);

    public Map<String,Integer> queryTodayOrdersByOrderState(String paramOrderState);

}


