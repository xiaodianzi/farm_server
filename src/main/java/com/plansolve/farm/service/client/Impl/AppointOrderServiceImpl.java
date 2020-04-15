package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.client.order.AppointOrderDTO;
import com.plansolve.farm.model.database.order.AppointOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.AppointOrderStateEnum;
import com.plansolve.farm.repository.order.AppointOrderRepository;
import com.plansolve.farm.service.client.AppointOrderService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/2
 * @Description:
 **/
@Service
public class AppointOrderServiceImpl implements AppointOrderService {

    @Autowired
    private AppointOrderRepository appointOrderRepository;
    @Autowired
    private UserService userService;

    /**
     * 生成提前预约订单
     *
     * @param appointOrderDTO 订单详情
     * @param user            下单人
     * @return
     */
    @Override
    public AppointOrder createAppointOrder(AppointOrderDTO appointOrderDTO, User user) {
        AppointOrder appointOrder = new AppointOrder();
        BeanUtils.copyProperties(appointOrderDTO, appointOrder);

        String orderNo = getOrderNo();
        appointOrder.setAppointOrderNo(orderNo);
        appointOrder.setAppointOrderState(AppointOrderStateEnum.WAITING.getState());
        appointOrder.setCreateBy(user.getIdUser());
        appointOrder.setCreateTime(new Date());

        appointOrder = appointOrderRepository.save(appointOrder);
        return appointOrder;
    }

    /**
     * 获取用户提前预约订单
     *
     * @param user
     * @return
     */
    @Override
    public List<AppointOrder> getUserAppointOrders(User user) {
        List<AppointOrder> appointOrders = appointOrderRepository.findByCreateBy(user.getIdUser());
        return appointOrders;
    }

    /**
     * 封装传输对象
     *
     * @param appointOrder
     * @return
     */
    @Override
    public AppointOrderDTO loadDTO(AppointOrder appointOrder) {
        AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
        BeanUtils.copyProperties(appointOrder, appointOrderDTO);

        UserDTO user = userService.findUser(appointOrder.getCreateBy(), false);
        appointOrderDTO.setCreateBy(user);
        return appointOrderDTO;
    }

    /**
     * 批量封装传输对象
     *
     * @param appointOrders
     * @return
     */
    @Override
    public List<AppointOrderDTO> loadDTOs(List<AppointOrder> appointOrders) {
        List<AppointOrderDTO> appointOrderDTOS = new ArrayList<>();
        if (appointOrders != null && appointOrders.size() > 0) {
            for (AppointOrder appointOrder : appointOrders) {
                AppointOrderDTO appointOrderDTO = loadDTO(appointOrder);
                appointOrderDTOS.add(appointOrderDTO);
            }
        }
        return appointOrderDTOS;
    }

    /**
     * 订单号生成
     *
     * @return
     */
    private String getOrderNo() {
        String orderNo = String.valueOf(Math.round(Math.random() * 1000000));
        orderNo = StringUtil.prefixStr(orderNo, 6, "0");
        orderNo = DateUtils.getDate("yyyy/MM/dd").replace("/", "").substring(2) + orderNo;
        AppointOrder order = appointOrderRepository.findByAppointOrderNo(orderNo);
        if (order == null) {
            return orderNo;
        } else {
            return getOrderNo();
        }
    }

}
