package com.plansolve.farm.service.base.user.impl;

import com.google.gson.JsonArray;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.bo.user.FarmlandBO;
import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.enums.state.FarmlandStateEnum;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.repository.AddressRepository;
import com.plansolve.farm.repository.FarmlandRepository;
import com.plansolve.farm.service.base.user.FarmlandBaseService;
import com.plansolve.farm.service.common.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description: 用户田地接口
 **/
@Slf4j
@Service
public class FarmlandBaseServiceImpl implements FarmlandBaseService {

    @Autowired
    private FarmlandRepository farmlandRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private FileService fileService;

    /******************************************增删改Start******************************************/

    /**
     * 新增田地
     *
     * @param farmland
     * @param pictures
     * @return
     * @throws IOException
     */
    @Override
    public FarmlandBO insert(FarmlandBO farmland, List<MultipartFile> pictures) throws IOException {
        Date date = new Date();
        // 1、保存地址
        Address addressDO = new Address();
        BeanUtils.copyProperties(farmland, addressDO);
        addressDO.setDetail(farmland.getAddressDetail());
        addressDO.setCreateDate(date);
        addressDO.setUpdateDate(date);
        addressDO = addressRepository.save(addressDO);

        // 2、保存图片
        JsonArray array = new JsonArray();
        if (pictures != null && pictures.size() > 0) {
            for (MultipartFile picture : pictures) {
                String pictureName = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.FARMLAND_PICTURE, picture);
                array.add(pictureName);
            }
        }

        // 3、保存田地
        Farmland farmlandDO = new Farmland();
        BeanUtils.copyProperties(farmland, farmlandDO);
        farmlandDO.setDetail(farmland.getFarmlandDetail());
        farmlandDO.setIdAddress(addressDO.getIdAddress());
        farmlandDO.setFarmlandState(FarmlandStateEnum.NORMOL.getState());
        farmlandDO.setPictures(array.toString());
        farmlandDO.setCreateDate(date);
        farmlandDO.setUpdateDate(date);
        farmlandDO = farmlandRepository.save(farmlandDO);

        farmland.setIdFarmland(farmlandDO.getIdFarmland());
        return farmland;
    }

    /******************************************增删改End******************************************/
    /******************************************查Start******************************************/
    /**
     * 查询田地业务对象
     *
     * @param idFarmland
     * @return
     */
    @Override
    public FarmlandBO getFarmlandBO(Long idFarmland) {
        Farmland farmlandDO = getFarmlandDO(idFarmland);
        if (farmlandDO != null) {
            FarmlandBO farmlandBO = new FarmlandBO();
            BeanUtils.copyProperties(farmlandDO, farmlandBO);
            farmlandBO.setFarmlandDetail(farmlandDO.getDetail());

            Address address = addressRepository.findByIdAddress(farmlandDO.getIdAddress());
            BeanUtils.copyProperties(address, farmlandBO);
            farmlandBO.setAddressDetail(address.getDetail());
            return farmlandBO;
        } else {
            return null;
        }
    }

    /**
     * 查询田地数据库对象
     *
     * @param idFarmland
     * @return
     */
    private Farmland getFarmlandDO(Long idFarmland) {
        Farmland farmland = farmlandRepository.findByIdFarmland(idFarmland);
        if (!farmland.getFarmlandState().equals(FarmlandStateEnum.DELETED.getState())) {
            return farmland;
        } else {
            return null;
        }
    }
    /******************************************查End******************************************/
}
