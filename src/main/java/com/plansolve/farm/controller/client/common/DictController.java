package com.plansolve.farm.controller.client.common;

import com.plansolve.farm.controller.client.BaseController;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.client.MachineryDictDTO;
import com.plansolve.farm.model.database.dictionary.DictMachineryType;
import com.plansolve.farm.model.enums.code.ResultEnum;
import com.plansolve.farm.service.client.DictService;
import com.plansolve.farm.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户无需登录所能进行的操作
 **/

@RestController
@RequestMapping(value = "/farm/dictionary")
public class DictController extends BaseController {

    @Autowired
    private DictService dictService;

    private final static Logger logger = LoggerFactory.getLogger(DictController.class);

    /**
     * 添加新农机
     * @param dictData
     * @return
     */
    @RequestMapping("/setMachineryDictData")
    public Result setDictData(DictMachineryType dictData) {
        Result result = new Result();
        if (null != dictData) {
            DictMachineryType dictMachineryType = dictService.setDictData(dictData);
            if (null != dictMachineryType) {
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg("农机添加成功");
                return result;
            }
        }
        result.setCode(ResultEnum.PARAM_ERROR.getCode());
        result.setMsg("农机添加失败");
        return result;
    }

    /**
     * 获取所有有效的农机
     * @return
     */
    @RequestMapping("/getMachineryDictData")
    public Result getMachineryDictData() {
        List<MachineryDictDTO> machineryDictData = dictService.getMachineryDictData();
        return ResultUtil.success(machineryDictData);
    }

    /**
     * 删除指定类型的农机（逻辑删除）
     * @param idMachineryType
     * @return
     */
    @RequestMapping("/deleteMachineryDictData")
    public Result deleteMachineryDictData(Integer idMachineryType) {
        boolean deleted = dictService.deleteMachineryDictData(idMachineryType);
        if (deleted){
            return ResultUtil.success(null);
        }else{
            Result result = new Result();
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg("农机类型删除失败");
            return result;
        }
    }

}
