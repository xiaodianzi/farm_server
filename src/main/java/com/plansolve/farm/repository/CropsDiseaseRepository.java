package com.plansolve.farm.repository;

import com.plansolve.farm.model.database.agricultural.CropsDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/2/18
 * @Description:
 */
public interface CropsDiseaseRepository extends JpaRepository<CropsDisease, Integer>, JpaSpecificationExecutor<CropsDisease> {

    public CropsDisease findOneById(Integer id);

    public CropsDisease findOneByDiseaseName(String diseaseName);

    public CropsDisease findOneByFolderName(String folderName);

    public List<CropsDisease> findByDiseaseType(String diseaseType);

}
