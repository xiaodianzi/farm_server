package com.plansolve.farm.model.client;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/2/18
 * @Description:
 */
@Data
public class CropsDataDTO implements Serializable {

    private String intro;//简介

    private List<CropsDiseaseDTO> illnessCrops;//病害作物

    private List<CropsDiseaseDTO> insectCrops;//虫害作物

    private String plantSkill;//种植技术

}
