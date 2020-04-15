package com.plansolve.farm.util;

import com.plansolve.farm.model.client.CropsDiseaseDTO;
import com.plansolve.farm.model.database.agricultural.CropsDisease;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/7/25
 * @Description:
 **/
@Slf4j
public class CropsUtil {

    public static List<CropsDiseaseDTO> loadCropsDiseaseDTO(List<CropsDisease> cropsDiseases){
        List<CropsDiseaseDTO> cropsDiseaseDTOS = new ArrayList<>();
        for (CropsDisease cropDisease: cropsDiseases) {
            CropsDiseaseDTO cropsDiseaseDTO = new CropsDiseaseDTO();
            String[] samplesUrls = cropDisease.getSamples().split(";");
            cropsDiseaseDTO.setSamples(samplesUrls);
            cropsDiseaseDTO.setDiseaseName(cropDisease.getDiseaseName());
            cropsDiseaseDTO.setDiseaseFeature(StringUtil.toString(cropDisease.getDiseaseFeature()).replaceAll("\\s*", ""));
            cropsDiseaseDTO.setRegularity(StringUtil.toString(cropDisease.getRegularity()).replaceAll("\\s*", ""));
            cropsDiseaseDTO.setPathogen(StringUtil.toString(cropDisease.getPathogen()).replaceAll("\\s*", ""));
            cropsDiseaseDTO.setTreatment(StringUtil.toString(cropDisease.getTreatment()).replaceAll("\\s*", ""));
            cropsDiseaseDTOS.add(cropsDiseaseDTO);
        }
        return cropsDiseaseDTOS;
    }

}
