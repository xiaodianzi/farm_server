package com.plansolve.farm.service.base.common.impl;

import com.plansolve.farm.model.database.Dictionary;
import com.plansolve.farm.repository.DictionaryRepository;
import com.plansolve.farm.service.base.common.DictionaryBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 高一平
 * @Date: 2019/6/24
 * @Description:
 **/
@Service
public class DictionaryBaseServiceImpl implements DictionaryBaseService {

    @Autowired
    private DictionaryRepository repository;

    @Override
    public Dictionary save(Dictionary dictionary) {
        return repository.save(dictionary);
    }

    @Override
    public Dictionary getValue(String key) {
        return repository.getOne(key);
    }
}
