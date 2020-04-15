package com.plansolve.farm.repository.user;

import com.plansolve.farm.model.database.user.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2018/8/30
 * @Description:
 **/
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
