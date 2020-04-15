package com.plansolve.farm.controller.console.main.agricultural;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.client.GrainMarketDTO;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.model.database.agricultural.GrainMarket;
import com.plansolve.farm.service.client.GrainMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/29
 * @Description:
 */
@Controller
@RequestMapping("/manager/web/grainmarket")
public class GrainMarketPageController extends BaseController {

    @Autowired
    private GrainMarketService grainMarketService;

    /**
     * 粮食购销首页
     *
     * @return
     */
    @GetMapping("/indexpage")
    public String grainIndexPage() {
        return "agricultural/grainMarketIndex";
    }

    /**
     * 条件查询粮食购销信息
     * @param limit    每页条数
     * @param offset   页码
     * @param idUser   发布人id
     * @param marketType 消息类型
     * @param grainType 农作物类型
     * @return 信息列表
     */
    @ResponseBody
    @RequestMapping("/getInformationByType")
    public PageDTO<GrainMarketDTO> getInformationByType(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, Long idUser, String marketType, String grainType, String validTime) {
        PageDTO<GrainMarketDTO> pageDTO = new PageDTO<>();
        Integer pageNumber = getPage(limit, offset);
        Page<GrainMarket> grainMarketInfoList = grainMarketService.getInformationByCondition(pageNumber, limit, idUser, marketType, grainType, validTime);
        List<GrainMarket> grainMarkets = grainMarketInfoList.getContent();
        List<GrainMarketDTO> grainMarketDTOList = grainMarketService.loadGrainMarketWebDTO(grainMarkets);
        pageDTO.setTotal(grainMarketInfoList.getTotalElements());
        pageDTO.setRows(grainMarketDTOList);
        return pageDTO;
    }

}
