package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.model.database.user.Feedback;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.repository.user.FeedbackRepository;
import com.plansolve.farm.service.client.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/8/30
 * @Description:
 **/
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public Feedback save(String feedback, User user) {
        Feedback userFeedback = new Feedback();
        userFeedback.setIdUser(user.getIdUser());
        userFeedback.setCreateTime(new Date());
        userFeedback.setDetail(feedback);
        return feedbackRepository.save(userFeedback);
    }
}
