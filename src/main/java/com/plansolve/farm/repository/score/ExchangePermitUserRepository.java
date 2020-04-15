package com.plansolve.farm.repository.score;

import com.plansolve.farm.model.database.score.ExchangePermitUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/5/10
 * @Description:
 */
public interface ExchangePermitUserRepository extends JpaRepository<ExchangePermitUser, Long>, JpaSpecificationExecutor<ExchangePermitUser> {

    public ExchangePermitUser findByIdExchangePermitUser(Long idExchangePermitUser);

    public ExchangePermitUser findByIdUser(Long idUser);

    public List<ExchangePermitUser> findAllByValidIsTrue();

}
