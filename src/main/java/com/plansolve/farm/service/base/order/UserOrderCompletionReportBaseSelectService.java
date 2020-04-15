package com.plansolve.farm.service.base.order;

import com.plansolve.farm.model.database.order.CompletionReport;

/**
 * @Author: 高一平
 * @Date: 2019/5/9
 * @Description:
 **/
public interface UserOrderCompletionReportBaseSelectService {

    public CompletionReport listCompletionReports(Long idUserOrder, Long idUser);

}
