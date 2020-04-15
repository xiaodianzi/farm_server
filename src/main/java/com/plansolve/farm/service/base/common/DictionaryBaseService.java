package com.plansolve.farm.service.base.common;

import com.plansolve.farm.model.database.Dictionary;

/**
 * @Author: 高一平
 * @Date: 2019/6/24
 * @Description:
 **/
public interface DictionaryBaseService {

    public Dictionary save(Dictionary dictionary);

    public Dictionary getValue(String key);

}
