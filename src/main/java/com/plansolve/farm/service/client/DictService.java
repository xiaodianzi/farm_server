package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.MachineryDictDTO;
import com.plansolve.farm.model.database.dictionary.DictMachineryType;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/8
 * @Description: 数据字典相关操作接口
 **/
public interface DictService {

    public DictMachineryType setDictData(DictMachineryType dictData);

    public String getValue(String key);

    public List<MachineryDictDTO> getMachineryDictData();

    public DictMachineryType getFarmMachinery(Integer idMachineryType);

    public boolean deleteMachineryDictData(Integer idMachineryType);
}
