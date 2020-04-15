package com.plansolve.farm.repository.order;

import com.plansolve.farm.model.database.order.CompletionReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/
public interface CompletionReportRepository extends JpaRepository<CompletionReport, Long> {

    public CompletionReport findByIdCompletionReport(Long idCompletionReport);

    public List<CompletionReport> findByIdUserOrder(Long idUserOrder);

    public CompletionReport findByIdUserOrderAndIdUser(Long idUserOrder, Long idUser);

}
