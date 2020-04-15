package com.plansolve.farm.model.client;

import lombok.Data;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2019/2/18
 * @Description:
 */
@Data
public class CropsDiseaseDTO implements Serializable {

    private String diseaseName;//病害名称

    private String[] samples;//示例图片

    private String diseaseFeature;//危害症状

    private String regularity;//发病规律

    private String pathogen;//病害病原

    private String treatment;//治疗方法

    private String percentage;//相似度百分比

}
