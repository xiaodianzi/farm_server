package com.plansolve.farm.service.base.user;

import com.plansolve.farm.model.bo.user.FarmlandBO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description:
 **/
public interface FarmlandBaseService {

    /******************************************增删改Start******************************************/

    public FarmlandBO insert(FarmlandBO farmland, List<MultipartFile> pictures) throws IOException;

    /******************************************增删改End******************************************/
    /******************************************查Start******************************************/

    public FarmlandBO getFarmlandBO(Long idFarmland);

    /******************************************查End******************************************/

}
