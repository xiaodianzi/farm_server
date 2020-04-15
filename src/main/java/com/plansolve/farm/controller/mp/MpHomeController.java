package com.plansolve.farm.controller.mp;

import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.bo.user.FarmlandBO;
import com.plansolve.farm.model.client.userOrder.OrderItemDTO;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.service.base.order.UserOrderBaseSelectService;
import com.plansolve.farm.service.base.user.FarmlandBaseService;
import com.plansolve.farm.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/6/6
 * @Description:
 **/
@Controller
@RequestMapping("/wx/mp")
public class MpHomeController {

    @Autowired
    private UserOrderBaseSelectService baseSelectService;
    @Autowired
    private FarmlandBaseService farmlandBaseService;

    /**
     * 公众号首页订单列表
     *
     * @param page
     * @param model
     * @return
     */
    @RequestMapping("/home")
    public String homeOrders(Page page, Model model) {
        Page<OrderItemDTO> dtoPage = new Page<>();
        dtoPage.setPageNo(page.getPageNo());
        dtoPage.setPageSize(page.getPageSize());

        List<UserOrder> orders = new ArrayList<>();
        List<OrderItemDTO> dtos = new ArrayList<>();
        Boolean isContinue = true;

        Page<UserOrder> orderPage = baseSelectService.pageByOrderState(OrderStateEnum.WAITING.getState(), page, new Sort(Sort.Direction.DESC, "idUserOrder"));

        if (orderPage != null) {
            List<UserOrder> rows = orderPage.getRows();
            orders.addAll(rows);
            if (orderPage.getRows().size() > page.getPageSize()) {
                isContinue = false;
            }
        }

        if (isContinue) {
            Page continueOrder = baseSelectService.pageByOrderStateNot(Arrays.asList(OrderStateEnum.DELETED.getState(), OrderStateEnum.CANCELED.getState()), page, new Sort(Sort.Direction.DESC, "idUserOrder"));
            if (continueOrder != null) {
                List<UserOrder> rows = continueOrder.getRows();
                orders.addAll(rows);
            }
        }

        if (orders != null && orders.size() > 0) {
            for (UserOrder order : orders) {
                OrderItemDTO dto = new OrderItemDTO();
                dto.setUserOrderNo(order.getUserOrderNo());
                dto.setCrop(order.getCropName());
                dto.setMachineryType(order.getMachineryType());
                dto.setPrice(order.getPrice());
                dto.setCreateTime(DateUtils.formatDate(order.getCreateTime(), "yyyy-MM-dd"));
                dto.setStartTime(DateUtils.formatDate(order.getStartTime(), "yyyy-MM-dd"));
                dto.setAcreage(order.getArce());
                if (order.getUserOrderState().equals(OrderStateEnum.WAITING.getState())) {
                    dto.setState(0);
                } else if (order.getUserOrderState().equals(OrderStateEnum.OVERDUE.getState())) {
                    dto.setState(-1);
                } else {
                    dto.setState(1);
                }

                FarmlandBO farmlandBO = farmlandBaseService.getFarmlandBO(order.getIdFarmland());
                if (farmlandBO != null) {
                    dto.setAddress(farmlandBO.getProvince() + farmlandBO.getCounty() + farmlandBO.getCounty() + farmlandBO.getAddressDetail());
                } else {
                    dto.setAddress("黑龙江省大庆市红岗区创业街道元丰路2号");
                }
                dtos.add(dto);
            }
        }

        dtoPage.setTotal((long) orders.size());
        dtoPage.setRows(dtos);

        model.addAttribute("page", dtoPage);
        return "mp/index";
    }

}
