package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.model.database.Dictionary;
import com.plansolve.farm.repository.DictionaryRepository;
import com.plansolve.farm.service.console.AppDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/9/26
 * @Description:
 **/
@Service
public class AppDictServiceImpl implements AppDictService {

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Override
    public List<Dictionary> findAll() {
        return dictionaryRepository.findAll();
    }

    @Override
    public Dictionary save(Dictionary dictionary) {
        return dictionaryRepository.save(dictionary);
    }

    @Override
    public Dictionary get(String dictKey) {
        return dictionaryRepository.getOne(dictKey);
    }
}
