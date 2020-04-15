package com.plansolve.farm.controller.client.main.agricultural;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.GrainMarketDTO;
import com.plansolve.farm.model.client.PageDTO;
import com.plansolve.farm.model.database.agricultural.GrainMarket;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.code.ResultEnum;
import com.plansolve.farm.repository.agricultural.GrainMarketRepository;
import com.plansolve.farm.service.client.GrainMarketService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/29
 * @Description:
 */
@RestController
@RequestMapping("/farm/grainMarket")
public class GrainMarketController {

    @Autowired
    private GrainMarketService grainMarketService;

    @Autowired
    private GrainMarketRepository grainMarketRepository;

    /**
     * 发布粮食购销信息
     * @param grainMarket
     * @return
     */
    @PostMapping("/publishInfomation")
    public Result saleGrain(@Valid GrainMarketDTO grainMarket) {
        Result result = new Result();
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        if (null != grainMarket){
            GrainMarket savedGrainMarket = grainMarketService.saveGrainMarket(user, grainMarket);
            if(null != savedGrainMarket){
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg("消息发布成功");
                return result;
            }else{
                result.setCode(ResultEnum.SERVER_BUSY_ERROR.getCode());
                result.setMsg("服务器忙，请稍后重试");
                return result;
            }
        }else{
            throw new ParamErrorException("");
        }
    }

    /**
     * 获取当前用户的粮食消息
     *
     * @param infoType 买方/卖方/全部
     * @return 消息列表
     */
    @PostMapping("/getInformationByUser")
    public Result getMarketInfoByUser(@RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "20") Integer pageSize, String infoType) {
        PageDTO<GrainMarketDTO> pageDTO = new PageDTO();
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        if (null != user && StringUtils.isNotBlank(infoType)){
            Long count = grainMarketRepository.countAllByIdUserAndMarketType(user.getIdUser(), infoType);
            List<GrainMarketDTO> grainMarketInfoList = grainMarketService.getInformationByUser(pageNumber, pageSize, user, infoType);
            pageDTO.setTotal(count);
            pageDTO.setRows(grainMarketInfoList);
            return ResultUtil.success(pageDTO);
        }else{
            throw new ParamErrorException("");
        }
    }

    /**
     * 获取所有发布的消息内容
     *
     * @param infoType 买方/卖方/全部
     * @return 消息列表
     */
    @PostMapping("/getInformationByType")
    public Result getInformationByType(@RequestParam(defaultValue = "0") Integer pageNumber,
                                       @RequestParam(defaultValue = "20") Integer pageSize, String infoType) {
        PageDTO<GrainMarketDTO> pageDTO = new PageDTO();
        if (StringUtils.isNotBlank(infoType)){
            Long count = grainMarketRepository.countAllByMarketType(infoType);
            List<GrainMarketDTO> grainMarketInfoList = grainMarketService.getInformationByType(pageNumber, pageSize, infoType);
            pageDTO.setTotal(count);
            pageDTO.setRows(grainMarketInfoList);
            return ResultUtil.success(pageDTO);
        }else{
            throw new ParamErrorException("");
        }
    }

}
