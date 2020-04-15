package com.plansolve.farm.controller.client.main.agricultural;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.client.CropsDataDTO;
import com.plansolve.farm.service.client.GrainMarketService;
import com.plansolve.farm.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Andrew
 * @Date: 2019/3/29
 * @Description:
 */
@RestController
@RequestMapping("/farm/plantSkill")
public class PlantSkillController {

    @Autowired
    private GrainMarketService grainMarketService;

    /**
     * 获取所有发布的消息内容
     *
     * @param dataType 作物类型
     * @return 消息列表
     */
    @PostMapping("/getPlantDataByType")
    public Result getInformationByType(@RequestParam(defaultValue = "0") Integer pageNumber,
                                       @RequestParam(defaultValue = "20") Integer pageSize, String dataType) {
        if (StringUtils.isNotBlank(dataType)){
            CropsDataDTO cropsData = grainMarketService.getPlantDataByType(pageNumber, pageSize, dataType);
            return ResultUtil.success(cropsData);
        }else{
            throw new ParamErrorException("");
        }
    }

}
