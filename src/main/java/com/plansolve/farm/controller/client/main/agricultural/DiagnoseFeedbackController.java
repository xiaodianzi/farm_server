package com.plansolve.farm.controller.client.main.agricultural;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.agricultural.DiagnoseFeedback;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.opencv.OpenService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Andrew
 * @Date: 2019/3/29
 * @Description:
 */
@RestController
@RequestMapping("/farm/agricultural/diagnose")
public class DiagnoseFeedbackController {

    @Autowired
    private OpenService openService;

    /**
     * 病虫害诊断结果反馈接口
     * @param diagnoseFeedback 诊断结果反馈信息
     * @return
     */
    @PostMapping("/feedback")
    public Result feedback(DiagnoseFeedback diagnoseFeedback) {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        if (null != diagnoseFeedback){
            diagnoseFeedback.setIdUser(user.getIdUser());
            DiagnoseFeedback savedFeedback = openService.saveDiagnoseFeedback(diagnoseFeedback);
            if (null != savedFeedback){
                return ResultUtil.success("谢谢反馈");
            }else{
                throw new ParamErrorException("");
            }
        }else{
            throw new ParamErrorException("");
        }
    }

}
