package com.plansolve.farm.service.client;

import com.plansolve.farm.model.database.user.Feedback;
import com.plansolve.farm.model.database.user.User;

/**
 * @Author: 高一平
 * @Date: 2018/8/30
 * @Description:
 **/
public interface FeedbackService {

    public Feedback save(String feedback, User user);

}
