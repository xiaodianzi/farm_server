package com.plansolve.farm.controller.mp.common;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.school.CerealsRealTimePriceDTO;
import com.plansolve.farm.model.client.school.CerealsRealTimePriceDetailDTO;
import com.plansolve.farm.model.database.school.CerealsMassage;
import com.plansolve.farm.model.database.school.CerealsMassageType;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.service.base.common.CerealsMassageService;
import com.plansolve.farm.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/5/8
 * @Description:
 **/
@Slf4j
@Controller
@RequestMapping("/wx/mp/cereals/realTime")
public class CerealsRealTimePricesController {

    @Autowired
    private CerealsMassageService massageService;

    /**
     * 实时粮价主页
     *
     * @return
     */
    @RequestMapping(value = "/index")
    public String list(Model model) {
        List<CerealsMassageType> types = massageService.listTypes();
        model.addAttribute("types", types);
        return "common/cerealsRealTimePrices/index";
    }

    /**
     * 实时粮价子页
     *
     * @param idCerealsMassageType 农作物类型
     * @param model
     * @return
     */
    @RequestMapping(value = "/item")
    public String item(Integer idCerealsMassageType, Model model) {
        CerealsMassageType type = massageService.getType(idCerealsMassageType);
        type.setPicture(FileProperties.fileUrlPath + SysConstant.CEREALS_REAL_TIME_PRICES_PICTURE + type.getPicture());
        List<CerealsMassage> massages = massageService.listMassages(idCerealsMassageType);
        List<CerealsRealTimePriceDTO> dtos = new ArrayList<>();
        if (massages != null && massages.size() > 0) {
            for (CerealsMassage massage : massages) {
                CerealsRealTimePriceDTO dto = new CerealsRealTimePriceDTO();
                BeanUtils.copyProperties(massage, dto);
                dto.setCreateTime(DateUtils.formatDate(massage.getCreateTime(), "yyyy-MM-dd"));
                if (massage.getUrl() == null || massage.getUrl().trim().length() == 0) {
                    dto.setUrl("detail?idMessage=" + massage.getIdCerealsMassage());
                }
                dtos.add(dto);
            }
        }
        model.addAttribute("type", type);
        model.addAttribute("items", dtos);
        return "common/cerealsRealTimePrices/item";
    }

    /**
     * 粮价消息详情页
     *
     * @param idMessage
     * @param model
     * @return
     */
    @RequestMapping(value = "/detail")
    public String detail(Integer idMessage, Model model) {
        CerealsMassage massage = massageService.getMassage(idMessage);
        CerealsRealTimePriceDetailDTO dto = new CerealsRealTimePriceDetailDTO();
        BeanUtils.copyProperties(massage, dto);
        dto.setReleaseTime(DateUtils.formatDateTime(massage.getReleaseTime()));
        model.addAttribute("massage", dto);
        return "common/cerealsRealTimePrices/detail";
    }

}
