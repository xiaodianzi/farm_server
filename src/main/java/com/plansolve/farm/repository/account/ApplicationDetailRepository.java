package com.plansolve.farm.repository.account;

import com.plansolve.farm.model.database.account.ApplicationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/20
 * @Description:
 **/
public interface ApplicationDetailRepository extends JpaRepository<ApplicationDetail, Integer> {

    public List<ApplicationDetail> findByIdTransactionApplicationAndTransactionTypeAndApplicationResult(Long idTransactionApplication, String transactionType, Boolean applicationResult);

    public List<ApplicationDetail> findByIdTransactionApplication(Long idTransactionApplication);

}
