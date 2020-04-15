package com.plansolve.farm.controller.client.opencv;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: Andrew
 * @Date: 2019/1/31
 * @Description:
 */
@Controller
@RequestMapping(value = "/openPage")
public class OpenPageController {

    /**
     * 图片精准匹配
     * @return
     */
    @GetMapping(value = "/imagePage")
    public String imagePage(){
        return "opencv/matchImage";
    }

    /**
     * 图片模糊匹配
     * @return
     */
    @GetMapping(value = "/referencePage")
    public String referencePage(){
        return "opencv/referenceImage";
    }

}