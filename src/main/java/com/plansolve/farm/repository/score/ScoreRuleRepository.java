package com.plansolve.farm.repository.score;

import com.plansolve.farm.model.database.score.ScoreRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/20
 * @Description:
 */
public interface ScoreRuleRepository extends JpaRepository<ScoreRule, Long>, JpaSpecificationExecutor<ScoreRule> {

    public ScoreRule findOneByIdScoreRule(Long IdScoreRule);

    public ScoreRule findByRuleName(String ruleName);

    public Page<ScoreRule> findByIsValidTrue(Pageable pageable);

    public List<ScoreRule> findByIsValidTrue();

}
