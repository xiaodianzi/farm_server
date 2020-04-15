package com.plansolve.farm.repository.log;

import com.plansolve.farm.model.database.log.AccountLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/22
 * @Description:
 **/
public interface AccountLogRepository extends JpaRepository<AccountLog, Long>, JpaSpecificationExecutor<AccountLog> {

    public List<AccountLog> findAllByChangeTypeNotAndChangeTypeNot(Pageable pageable, String createState, String frozenState);

    public Integer countByChangeTypeNotAndChangeTypeNot(String createState, String frozenState);

    public List<AccountLog> countByIdUserAndChangeType(Long idUser, String changeType);

    public List<AccountLog> findByIdUser(Long idUser, Sort sort);

}
