package com.plansolve.farm.service.console;

import com.plansolve.farm.model.database.Dictionary;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/9/26
 * @Description:
 **/
public interface AppDictService {

    /**
     * 查询所有数据字典
     *
     * @return
     */
    public List<Dictionary> findAll();

    /**
     * 保存/更新数据字典
     *
     * @param dictionary
     * @return
     */
    public Dictionary save(Dictionary dictionary);

    /**
     * 获取对应字典值
     *
     * @param dictKey
     * @return
     */
    public Dictionary get(String dictKey);

}
