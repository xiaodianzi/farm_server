package com.plansolve.farm.controller.console.messages;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.console.cereals.AppCerealsRealTimePricesDTO;
import com.plansolve.farm.model.console.cereals.AppCerealsTypeDTO;
import com.plansolve.farm.model.database.school.CerealsMassage;
import com.plansolve.farm.model.database.school.CerealsMassageType;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.service.base.common.CerealsMassageService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.HtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/5/16
 * @Description:
 **/
@Slf4j
@Controller
@RequestMapping("/manager/cereals/realTimePrice")
public class ConsoleCerealsRealTimePricesController {

    @Autowired
    private CerealsMassageService massageService;

    /**
     * 跳转粮食类别页
     *
     * @return
     */
    @RequestMapping("/typePage")
    public String typePage() {
        return "console/cerealsRealTimePrices/typeList";
    }

    /**
     * 获取粮食类别
     */
    @RequestMapping("/typeList")
    @ResponseBody
    public List<AppCerealsTypeDTO> typeList() {
        List<CerealsMassageType> types = massageService.listTypes();
        List<AppCerealsTypeDTO> dtos = new ArrayList<>();
        if (types != null && types.size() > 0) {
            for (CerealsMassageType type : types) {
                AppCerealsTypeDTO dto = new AppCerealsTypeDTO();
                BeanUtils.copyProperties(type, dto);
                dto.setBanner(FileProperties.fileUrlPath + SysConstant.CEREALS_REAL_TIME_PRICES_PICTURE + type.getPicture());
                dto.setBanner(HtmlUtil.getImgHtml(dto.getBanner(), "300px"));
                dtos.add(dto);
            }
        }
        return dtos;
    }

    /**
     * 跳转粮价列表页
     *
     * @return
     */
    @RequestMapping("/realTimePricePage")
    public String realTimePricePage(Integer idCerealsMassageType, Model model) {
        CerealsMassageType type = massageService.getType(idCerealsMassageType);
        model.addAttribute("type", type);
        return "console/cerealsRealTimePrices/list";
    }

    /**
     * 获取粮价信息
     *
     * @param idCerealsMassageType
     * @return
     */
    @RequestMapping("/realTimePriceList")
    @ResponseBody
    public List<AppCerealsRealTimePricesDTO> realTimePriceList(Integer idCerealsMassageType) {
        List<CerealsMassage> cerealsMassages = massageService.listMassages(idCerealsMassageType);
        CerealsMassageType type = massageService.getType(idCerealsMassageType);
        List<AppCerealsRealTimePricesDTO> dtos = new ArrayList<>();
        if (cerealsMassages != null && cerealsMassages.size() > 0) {
            for (CerealsMassage cerealsMassage : cerealsMassages) {
                AppCerealsRealTimePricesDTO dto = new AppCerealsRealTimePricesDTO();
                BeanUtils.copyProperties(cerealsMassage, dto);
                dto.setCerealsMassageType(type.getCrop());
                if (cerealsMassage.getIsValid() == null || !cerealsMassage.getIsValid()) {
                    dto.setIsValid("无效");
                } else {
                    dto.setIsValid("有效");
                }
                dtos.add(dto);
            }
        }
        return dtos;
    }

    /**
     * 跳转实时粮价消息编辑页
     *
     * @param idCerealsMassage
     * @param model
     * @return
     */
    @RequestMapping("/editRealTimePricePage")
    public String editRealTimePricePage(@RequestParam(required = false) Integer idCerealsMassage,
                                        Integer idCerealsMassageType, Model model) {
        CerealsMassage massage;
        if (idCerealsMassage != null && idCerealsMassage > 0) {
            massage = massageService.getMassage(idCerealsMassage);
            idCerealsMassageType = massage.getIdCerealsMassageType();
            String detail = HtmlUtil.changeHtmlToDetail(massage.getDetail());
            massage.setDetail(detail);
        } else {
            massage = new CerealsMassage();
            massage.setIsValid(false);
        }
        CerealsMassageType type = massageService.getType(idCerealsMassageType);
        model.addAttribute("item", massage);
        model.addAttribute("type", type);
        return "console/cerealsRealTimePrices/edit";
    }

    /**
     * 跳转实时粮价消息编辑
     *
     * @param massage
     * @param time
     * @param files
     * @return
     */
    @RequestMapping("/editRealTimePrice")
    public String editRealTimePrice(CerealsMassage massage,
                                    @RequestParam(required = false) String time,
                                    @RequestParam(value = "files", required = false) MultipartFile[] files) {
        if (time != null && !time.trim().isEmpty()) {
            time = time.replace(".0", "");
            massage.setReleaseTime(DateUtils.parseDate(time));
        }
        massageService.insert(massage, files);
        return "redirect:realTimePricePage?idCerealsMassageType=" + massage.getIdCerealsMassageType();
    }
}
