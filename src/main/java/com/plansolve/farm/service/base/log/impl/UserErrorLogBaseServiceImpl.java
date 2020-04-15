package com.plansolve.farm.service.base.log.impl;

import com.plansolve.farm.model.database.log.UserErrorLog;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.repository.log.UserErrorLogRepository;
import com.plansolve.farm.service.base.log.UserErrorLogBaseService;
import com.plansolve.farm.service.base.user.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/6/21
 * @Description:
 **/
@Service
public class UserErrorLogBaseServiceImpl implements UserErrorLogBaseService {

    @Autowired
    private UserErrorLogRepository logRepository;
    @Autowired
    private UserBaseService userBaseService;

    @Override
    public UserErrorLog insert(UserErrorLog log) {
        if (log.getMobile() != null && log.getMobile().trim().length() > 0) {
            User user = userBaseService.getUser(log.getMobile());
            log.setIdUser(user.getIdUser());
        }
        log.setCreateTime(new Date());
        return logRepository.save(log);
    }
}
