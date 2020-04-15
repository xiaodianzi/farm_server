package com.plansolve.farm.repository.account;

import com.plansolve.farm.model.database.account.TransactionApplication;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/19
 * @Description:
 **/
public interface TransactionApplicationRepository extends JpaRepository<TransactionApplication, Long>, JpaSpecificationExecutor<TransactionApplication> {

    public TransactionApplication findByIdTransactionApplication(Long idTransactionApplication);

    public TransactionApplication findByIdUserOrder(Long idUserOrder);

    public TransactionApplication findByTransactionApplicationNo(String transactionApplicationNo);

    public List<TransactionApplication> findByIdUser(Long idUser);

    public List<TransactionApplication> findByIdUserAndApplicationType(Long idUser, String applicationType);

    public List<TransactionApplication> findByIdUserAndApplicationType(Long idUser, String applicationType, Sort sort);

    public List<TransactionApplication> findByIdUserAndApplicationState(Long idUser, String applicationState);

    public List<TransactionApplication> findByIdUserAndApplicationTypeAndApplicationState(Long idUser, String applicationType, String applicationState);

}
