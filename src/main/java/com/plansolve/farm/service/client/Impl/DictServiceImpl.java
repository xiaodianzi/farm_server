package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.model.client.FarmMachineryDict;
import com.plansolve.farm.model.client.MachineryDictDTO;
import com.plansolve.farm.model.database.dictionary.DictMachineryType;
import com.plansolve.farm.repository.DictionaryRepository;
import com.plansolve.farm.repository.dictionary.DictMachineryRepository;
import com.plansolve.farm.service.client.DictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/30
 * @Description:
 **/
@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictMachineryRepository dictMachineryRepository;

    @Override
    public DictMachineryType setDictData(DictMachineryType dictData) {
        DictMachineryType dictMachineryType = dictMachineryRepository.save(dictData);
        return dictMachineryType;
    }

    @Override
    public String getValue(String key) {
        return dictionaryRepository.getOne(key).getDictValue();
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public List<MachineryDictDTO> getMachineryDictData() {
        List<MachineryDictDTO> machineryDictDTOS = new ArrayList<>();
        List<DictMachineryType> machineryCategorys = dictMachineryRepository.findByParentIdAndDeletedIsFalse(0);
        if (null != machineryCategorys){
            if (machineryCategorys.size()>0){
                for (DictMachineryType dictMachineryType:machineryCategorys) {
                    MachineryDictDTO machineryDictDTO = new MachineryDictDTO();
                    String categoryValue = dictMachineryType.getValue();
                    if (StringUtils.isNotBlank(categoryValue)){
                        machineryDictDTO.setMachineryCategory(categoryValue);
                        List<DictMachineryType> subMachinerys = dictMachineryRepository.findByParentIdAndDeletedIsFalse(dictMachineryType.getIdMachineryType());
                        if (null != subMachinerys){
                            if (subMachinerys.size()>0){
                                List<FarmMachineryDict> subMachineryList = new ArrayList<>();
                                for (DictMachineryType subMachineryType:subMachinerys) {
                                    FarmMachineryDict farmMachinery = new FarmMachineryDict();
                                    farmMachinery.setIdMachineryType(subMachineryType.getIdMachineryType());
                                    farmMachinery.setValue(subMachineryType.getValue());
                                    subMachineryList.add(farmMachinery);
                                }
                                machineryDictDTO.setMachineryTypeList(subMachineryList);
                            }
                        }
                    }
                    machineryDictDTOS.add(machineryDictDTO);
                }
            }
        }
        return machineryDictDTOS;
    }

    @Override
    public DictMachineryType getFarmMachinery(Integer idMachineryType) {
        DictMachineryType Machinery = dictMachineryRepository.findByIdMachineryType(idMachineryType);
        return Machinery;
    }

    @Override
    public boolean deleteMachineryDictData(Integer idMachineryType) {
        DictMachineryType machineryType = dictMachineryRepository.findByIdMachineryTypeAndParentIdNot(idMachineryType, 0);
        if (null != machineryType){
            machineryType.setDeleted(true);
            DictMachineryType deletedMachineryType = dictMachineryRepository.save(machineryType);
            if (null != deletedMachineryType){
                if (deletedMachineryType.isDeleted()){
                    return true;
                }
            }
        }
        return false;
    }

}
