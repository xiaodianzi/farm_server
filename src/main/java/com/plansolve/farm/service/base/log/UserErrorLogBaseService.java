package com.plansolve.farm.service.base.log;

import com.plansolve.farm.model.database.log.UserErrorLog;

/**
 * @Author: 高一平
 * @Date: 2019/6/21
 * @Description:
 **/
public interface UserErrorLogBaseService {

    public UserErrorLog insert(UserErrorLog log);

}
