package com.plansolve.farm.repository.agricultural;

import com.plansolve.farm.model.database.agricultural.DiagnoseFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author: Andrew
 * @Date: 2019/5/10
 * @Description:
 */
public interface DiagnoseFeedbackRepository extends JpaRepository<DiagnoseFeedback, Long>, JpaSpecificationExecutor<DiagnoseFeedback> {
}

