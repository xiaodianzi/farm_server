package com.plansolve.farm.repository.score;

import com.plansolve.farm.model.database.score.ScoreUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author: Andrew
 * @Date: 2019/3/20
 * @Description:
 */
public interface ScoreUserRepository extends JpaRepository<ScoreUser, Long>, JpaSpecificationExecutor<ScoreUser> {

    public ScoreUser findOneByIdUser(Long idUser);

    public boolean existsByIdUser(Long idUser);

}
