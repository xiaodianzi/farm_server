package com.plansolve.farm.repository.score;

import com.plansolve.farm.model.database.score.ScoreLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Date;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/20
 * @Description:
 */
public interface ScoreLogRepository extends JpaRepository<ScoreLog, Long>, JpaSpecificationExecutor<ScoreLog> {

    public ScoreLog findOneByIdScoreLog(Long idScoreLog);

    public List<ScoreLog> findByIdUser(Pageable pageable, Long idUser);

    public boolean existsByChangeTimeAfterAndChangeTypeAndIdUser(Date today, String changeType, Long idUser);

    public Integer countByChangeTimeAfterAndChangeTypeAndIdUser(Date today, String changeType, Long idUser);

    public boolean existsByIdUserAndChangeType(Long idUser, String changeType);

    public Integer countByIdUserAndChangeType(Long idUser, String changeType);

    public Long countByIdUser(Long idUser);

}
