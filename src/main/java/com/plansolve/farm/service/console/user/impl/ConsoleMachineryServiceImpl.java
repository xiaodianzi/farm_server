package com.plansolve.farm.service.console.user.impl;

import com.plansolve.farm.model.database.Machinery;
import com.plansolve.farm.model.enums.state.MachineryStateEnum;
import com.plansolve.farm.repository.MachineryRepository;
import com.plansolve.farm.service.console.user.ConsoleMachineryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/6/4
 * @Description:
 **/
@Service
public class ConsoleMachineryServiceImpl implements ConsoleMachineryService {

    @Autowired
    private MachineryRepository repository;

    @Override
    public List<Machinery> listByUser(Long idUser) {
        List<Machinery> machinerys = repository.findByIdUserAndMachineryStateNot(idUser, MachineryStateEnum.DELETED.getState());
        return machinerys;
    }
}
