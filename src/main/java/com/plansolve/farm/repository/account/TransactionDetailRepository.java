package com.plansolve.farm.repository.account;

import com.plansolve.farm.model.database.account.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/20
 * @Description:
 **/
public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, Long> {

    public List<TransactionDetail> findByIdTransactionApplication(Long idTransactionApplication);

}
