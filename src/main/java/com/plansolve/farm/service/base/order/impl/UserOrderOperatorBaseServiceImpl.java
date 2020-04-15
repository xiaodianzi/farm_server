package com.plansolve.farm.service.base.order.impl;

import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.enums.state.OperatorStateEnum;
import com.plansolve.farm.repository.order.OrderOperatorRepository;
import com.plansolve.farm.service.base.order.UserOrderOperatorBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/6/24
 * @Description:
 **/
@Service
public class UserOrderOperatorBaseServiceImpl implements UserOrderOperatorBaseService {

    @Autowired
    private OrderOperatorRepository repository;

    /**
     * 获取工作人员信息
     *
     * @param idUserOrder
     * @return
     */
    @Override
    public List<OrderOperator> listOrderOperator(Long idUserOrder) {
        Sort sort = new Sort(Sort.Direction.DESC, "operatorState");
        List<OrderOperator> operators = repository.findByIdUserOrderAndOperatorStateIn(idUserOrder, Arrays.asList(OperatorStateEnum.INVITED.getState(), OperatorStateEnum.ACCEPTED.getState(), OperatorStateEnum.FINISHED.getState()), sort);
        return operators;
    }
}
