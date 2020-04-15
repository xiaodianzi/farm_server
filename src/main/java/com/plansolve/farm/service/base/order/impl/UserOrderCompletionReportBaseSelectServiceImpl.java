package com.plansolve.farm.service.base.order.impl;

import com.plansolve.farm.model.database.order.CompletionReport;
import com.plansolve.farm.repository.order.CompletionReportRepository;
import com.plansolve.farm.service.base.order.UserOrderCompletionReportBaseSelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/5/9
 * @Description:
 **/
@Service
public class UserOrderCompletionReportBaseSelectServiceImpl implements UserOrderCompletionReportBaseSelectService {

    @Autowired
    private CompletionReportRepository completionReportRepository;

    @Override
    public CompletionReport listCompletionReports(Long idUserOrder, Long idUser) {
        return completionReportRepository.findByIdUserOrderAndIdUser(idUserOrder, idUser);
    }
}
