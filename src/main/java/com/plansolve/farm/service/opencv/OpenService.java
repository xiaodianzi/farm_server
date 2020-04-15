package com.plansolve.farm.service.opencv;

import com.plansolve.farm.model.client.score.DiagnoseScoreDTO;
import com.plansolve.farm.model.database.agricultural.DiagnoseFeedback;
import com.plansolve.farm.model.database.user.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Andrew
 * @Date: 2019/1/31
 * @Description:
 */
public interface OpenService {

    /**
     * 智能比对图片接口
     * @param modelImg 模板图片
     * @return
     */
    public String smartCompareImage(String cropType, MultipartFile modelImg);

    public DiagnoseScoreDTO compareImage(User user, String cropType, MultipartFile modelImg);

    public DiagnoseFeedback saveDiagnoseFeedback(DiagnoseFeedback diagnoseFeedback);

}
