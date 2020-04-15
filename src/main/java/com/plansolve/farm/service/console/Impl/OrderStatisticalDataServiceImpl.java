package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.model.EncacheConstant;
import com.plansolve.farm.model.database.dictionary.DictMachineryType;
import com.plansolve.farm.model.database.order.OrderInfo;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.repository.dictionary.DictMachineryRepository;
import com.plansolve.farm.repository.order.OrderInfoRepository;
import com.plansolve.farm.repository.order.UserOrderRepository;
import com.plansolve.farm.service.console.OrderStatisticalDataService;
import com.plansolve.farm.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @Author: Andrew
 * @Date: 2019/5/24
 * @Description:
 */
@Service
public class OrderStatisticalDataServiceImpl implements OrderStatisticalDataService {

    @Autowired
    private DictMachineryRepository dictMachineryRepository;

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Override
    public Map<String, Integer> getOrderStatisticalDateByMachineType(Date begin, Date end) {
        Map<String, Integer> map = new HashMap<>();
        if (null != begin && null != end) {

        } else if (null == begin && null == end) {
            List<OrderInfo> orderInfos = orderInfoRepository.findDistinctIdMachineryParentTypeAndOrderStateNot(OrderStateEnum.CANCELED.getState());
            if (null != orderInfos) {
                if (orderInfos.size() > 0) {
                    for (OrderInfo orderInfo : orderInfos) {
                        DictMachineryType dictMachineryType = dictMachineryRepository.findByIdMachineryType(orderInfo.getIdMachineryParentType());
                        String cateGeory = dictMachineryType.getValue();
                        if (null != orderInfo.getIdMachineryParentType()) {
                            Integer count = orderInfoRepository.countGroupByIdMachineryParentType(orderInfo.getIdMachineryParentType(), OrderStateEnum.CANCELED.getState());
                            map.put(cateGeory, count);
                        }
                    }
                }
            }
        } else {

        }
        return map;
    }

    @Override
    public Map<String, Integer> queryDistinctCreateTimeOrdersByCrops(String crop) {
        Map<String, Integer> map = new HashMap<>();
        List<Date> dateList = userOrderRepository.queryDistinctOrdersCreateTime();
        Integer totalNum = userOrderRepository.countByCropNameAndUserOrderStateNot(crop, OrderStateEnum.CANCELED.getState());
        map.put("totalNumber", totalNum);
        for (Date date : dateList) {
            Date dayBegin = DateUtils.getDayBegin(date);
            Date dayEnd = DateUtils.getDayEnd(date);
            Integer count = userOrderRepository.countByCropNameAndUserOrderStateNotAndCreateTimeBetween(crop, OrderStateEnum.CANCELED.getState(), dayBegin, dayEnd);
            String[] dayArray = date.toString().substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

    @Override
    public Map<String, Integer> queryTodayOrdersByCrops(String crop) {
        Map<String, Integer> map = new HashMap<>();
        Date dayBegin = DateUtils.getDayBegin(new Date());
        Date dayEnd = DateUtils.getDayEnd(new Date());
        Integer count = userOrderRepository.countByCropNameAndUserOrderStateNotAndCreateTimeBetween(crop, OrderStateEnum.CANCELED.getState(), dayBegin, dayEnd);
        if (count > 0) {
            Integer totalNum = userOrderRepository.countByCropNameAndUserOrderStateNot(crop, OrderStateEnum.CANCELED.getState());
            map.put("totalNumber", totalNum);
            String[] dayArray = DateUtils.formatDate(new Date(), "yyyy-MM-dd").substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

    @Override
    public Map<String, Integer> queryDistinctCreateTimeOrdersByMachinery(Integer idMachineryParentType) {
        Map<String, Integer> map = new HashMap<>();
        List<Date> dateList = orderInfoRepository.queryDistinctCreateTime();
        Integer totalNum = orderInfoRepository.countGroupByIdMachineryParentType(idMachineryParentType, OrderStateEnum.CANCELED.getState());
        map.put("totalNumber", totalNum);
        for (Date date : dateList) {
            Date dayBegin = DateUtils.getDayBegin(date);
            Date dayEnd = DateUtils.getDayEnd(date);
            Integer count = orderInfoRepository.countByIdMachineryParentTypeAndCreateTime(idMachineryParentType, OrderStateEnum.CANCELED.getState(), dayBegin, dayEnd);
            String[] dayArray = date.toString().substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

    @Override
    public Map<String, Integer> queryTodayOrdersByMachinery(Integer idMachineryParentType) {
        Map<String, Integer> map = new HashMap<>();
        Date dayBegin = DateUtils.getDayBegin(new Date());
        Date dayEnd = DateUtils.getDayEnd(new Date());
        Integer count = orderInfoRepository.countByIdMachineryParentTypeAndCreateTime(idMachineryParentType, OrderStateEnum.CANCELED.getState(), dayBegin, dayEnd);
        if (count > 0) {
            Integer totalNum = orderInfoRepository.countGroupByIdMachineryParentType(idMachineryParentType, OrderStateEnum.CANCELED.getState());
            map.put("totalNumber", totalNum);
            String[] dayArray = DateUtils.formatDate(new Date(), "yyyy-MM-dd").substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

    @Override
    public String loadCropsName(String crop) {
        switch (crop) {
            case EncacheConstant.RICE_TIMELINE:
                crop = "水稻";
                break; //可选
            case EncacheConstant.CORN_TIMELINE:
                crop = "玉米";
                break; //可选
            case EncacheConstant.SORGHUM_TIMELINE:
                crop = "高粱";
                break; //可选
            case EncacheConstant.WHEAT_TIMELINE:
                crop = "小麦";
                break; //可选
            case EncacheConstant.SOYBEAN_TIMELINE:
                crop = "黄豆";
                break; //可选
            case EncacheConstant.MUNGBEAN_TIMELINE:
                crop = "绿豆";
                break; //可选
            case EncacheConstant.POTATO_TIMELINE:
                crop = "土豆";
                break; //可选
            default: //可选
                break;
        }
        return crop;
    }

    @Override
    public Integer loadMachineryName(String machinery) {
        Integer idMachineryParentType = 0;
        DictMachineryType dictMachineryType = null;
        switch (machinery) {
            case EncacheConstant.PLOUGH_TIMELINE:
                dictMachineryType = dictMachineryRepository.findByValueAndParentIdIs("耕整地机", 0);
                if (null != dictMachineryType){
                    idMachineryParentType = dictMachineryType.getIdMachineryType();
                }
                break; //可选
            case EncacheConstant.SEED_TIMELINE:
                dictMachineryType = dictMachineryRepository.findByValueAndParentIdIs("播种", 0);
                if (null != dictMachineryType){
                    idMachineryParentType = dictMachineryType.getIdMachineryType();
                }
                break; //可选
            case EncacheConstant.FERTILIZATION_TIMELINE:
                dictMachineryType = dictMachineryRepository.findByValueAndParentIdIs("施肥", 0);
                if (null != dictMachineryType){
                    idMachineryParentType = dictMachineryType.getIdMachineryType();
                }
                break; //可选
            case EncacheConstant.PROTECTION_TIMELINE:
                dictMachineryType = dictMachineryRepository.findByValueAndParentIdIs("植保", 0);
                if (null != dictMachineryType){
                    idMachineryParentType = dictMachineryType.getIdMachineryType();
                }
                break; //可选
            case EncacheConstant.HARVEST_TIMELINE:
                dictMachineryType = dictMachineryRepository.findByValueAndParentIdIs("收获", 0);
                if (null != dictMachineryType){
                    idMachineryParentType = dictMachineryType.getIdMachineryType();
                }
                break; //可选
            default: //可选
                break;
        }
        return idMachineryParentType;
    }

    @Override
    public Map<String, Integer> queryDistinctOrdersByWorkmode(Boolean isCooperative) {
        Map<String, Integer> map = new HashMap<>();
        List<Date> dateList = userOrderRepository.queryDistinctOrdersCreateTime();
        Integer totalNum = userOrderRepository.countByIsCooperativeAndUserOrderStateNot(isCooperative, OrderStateEnum.CANCELED.getState());
        map.put("totalNumber", totalNum);
        for (Date date : dateList) {
            Date dayBegin = DateUtils.getDayBegin(date);
            Date dayEnd = DateUtils.getDayEnd(date);
            Integer count = userOrderRepository.countByIsCooperativeAndUserOrderStateNotAndCreateTimeBetween(isCooperative, OrderStateEnum.CANCELED.getState(), dayBegin, dayEnd);
            String[] dayArray = date.toString().substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

    @Override
    public Map<String, Integer> queryTodayOrdersByWorkmode(Boolean isCooperative) {
        Map<String, Integer> map = new HashMap<>();
        Date dayBegin = DateUtils.getDayBegin(new Date());
        Date dayEnd = DateUtils.getDayEnd(new Date());
        Integer count = userOrderRepository.countByIsCooperativeAndUserOrderStateNotAndCreateTimeBetween(isCooperative, OrderStateEnum.CANCELED.getState(), dayBegin, dayEnd);
        if (count > 0) {
            Integer totalNum = userOrderRepository.countByIsCooperativeAndUserOrderStateNot(isCooperative, OrderStateEnum.CANCELED.getState());
            map.put("totalNumber", totalNum);
            String[] dayArray = DateUtils.formatDate(new Date(), "yyyy-MM-dd").substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

    @Override
    public String loadOrderStateName(String orderstate) {
        switch (orderstate) {
            case EncacheConstant.WAITING_ORDER:
                orderstate = "waiting";
                break;
            case EncacheConstant.CONFIRMING_ORDER:
                orderstate = "confirming";
                break;
            case EncacheConstant.WORKING_ORDER:
                orderstate = "working";
                break;
            case EncacheConstant.PAYED_ORDER:
                orderstate = "prepaid";
                break;
            case EncacheConstant.FINISH_ORDER:
                orderstate = "finished";
                break;
            case EncacheConstant.OVERDUE_ORDER:
                orderstate = "overdue";
                break;
            default:
                break;
        }
        return orderstate;
    }

    @Override
    public Map<String, Integer> queryDistinctOrdersByOrderState(String paramOrderState) {
        Map<String, Integer> map = new HashMap<>();
        List<Date> dateList = userOrderRepository.queryDistinctOrdersCreateTime();
        Integer totalNum = userOrderRepository.countByUserOrderState(paramOrderState);
        map.put("totalNumber", totalNum);
        for (Date date : dateList) {
            Date dayBegin = DateUtils.getDayBegin(date);
            Date dayEnd = DateUtils.getDayEnd(date);
            Integer count = userOrderRepository.countByUserOrderStateAndCreateTimeBetween(paramOrderState, dayBegin, dayEnd);
            String[] dayArray = date.toString().substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

    @Override
    public Map<String, Integer> queryTodayOrdersByOrderState(String paramOrderState) {
        Map<String, Integer> map = new HashMap<>();
        Date dayBegin = DateUtils.getDayBegin(new Date());
        Date dayEnd = DateUtils.getDayEnd(new Date());
        Integer count = userOrderRepository.countByUserOrderStateAndCreateTimeBetween(paramOrderState, dayBegin, dayEnd);
        if (count > 0) {
            Integer totalNum = userOrderRepository.countByUserOrderState(paramOrderState);
            map.put("totalNumber", totalNum);
            String[] dayArray = DateUtils.formatDate(new Date(), "yyyy-MM-dd").substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

}
