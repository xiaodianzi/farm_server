package com.plansolve.farm.controller.client.opencv;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.score.DiagnoseScoreDTO;
import com.plansolve.farm.model.database.agricultural.CropsDisease;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.repository.CropsDiseaseRepository;
import com.plansolve.farm.service.opencv.OpenService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;

/**
 * @Author: Andrew
 * @Date: 2019/1/31
 * @Description:
 */
@Controller
@RequestMapping(value = "/farm/opencv")
public class OpenController {

    @Autowired
    private OpenService openService;

    @Autowired
    private CropsDiseaseRepository cropsDiseaseRepository;

    /**
     * 图片相似度精确匹配
     *
     * @return
     */
    @PostMapping(value = "/matchImage")
    public String imageHandler(String cropType, MultipartFile modelImg, Model model) {
        if (StringUtils.isBlank(cropType) || null == modelImg) {
            throw new ParamErrorException("参数不能为空");
        }
        String picture = openService.smartCompareImage(cropType, modelImg);
        if (null != picture) {
            if (picture.indexOf("models") >= 0) {
                String folderName = picture.substring(picture.indexOf("models") + 7, picture.lastIndexOf("/"));
                CropsDisease cropsDisease = cropsDiseaseRepository.findOneByFolderName(folderName);
                if (null != cropsDisease) {
                    model.addAttribute("diseaseName", cropsDisease.getDiseaseName());
                } else {
                    model.addAttribute("diseaseName", null);
                }
            }
        }
        model.addAttribute("matchImage", picture);
        return "opencv/imageMatch";
    }

    /**
     * 农作物病虫害诊断接口
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/referenceImages")
    public Result referenceImage(String cropType, MultipartFile modelImg) {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        if (StringUtils.isBlank(cropType) || null == modelImg) {
            throw new ParamErrorException("参数不能为空");
        }
        DiagnoseScoreDTO diagnoseScoreDTO = openService.compareImage(user, cropType, modelImg);
        return ResultUtil.success(diagnoseScoreDTO);
    }

}