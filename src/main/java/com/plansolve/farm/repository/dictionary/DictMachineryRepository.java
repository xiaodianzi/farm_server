package com.plansolve.farm.repository.dictionary;

import com.plansolve.farm.model.database.dictionary.DictMachineryType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/
public interface DictMachineryRepository extends JpaRepository<DictMachineryType, Integer> {

    public List<DictMachineryType> findByName(String name);

    public List<DictMachineryType> findByValue(String value);

    public List<DictMachineryType> findByParentId(Integer parentId);

    public List<DictMachineryType> findByParentIdAndHasChildIsTrue(Integer parentId);

    public DictMachineryType findByIdMachineryType(Integer idMachineryType);

    public List<DictMachineryType> findByParentIdAndDeletedIsFalse(Integer parentId);

    public DictMachineryType findByIdMachineryTypeAndParentIdNot(Integer idMachineryType, Integer parentId);

    public DictMachineryType findByValueAndParentIdIs(String value, Integer parentId);

}
