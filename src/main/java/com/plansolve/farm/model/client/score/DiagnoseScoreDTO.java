package com.plansolve.farm.model.client.score;

import com.plansolve.farm.model.client.CropsDiseaseDTO;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/28
 * @Description:
 */
@Data
public class DiagnoseScoreDTO implements Serializable {

    private String diagnosePictureUrl;//已保存的诊断图片地址

    private List<CropsDiseaseDTO> cropsDiseases;

//    private ScoreTaskDTO diagnoseScoreData;

}
