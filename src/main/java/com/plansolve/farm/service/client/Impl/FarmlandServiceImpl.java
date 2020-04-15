package com.plansolve.farm.service.client.Impl;

import com.google.gson.JsonArray;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.client.user.FarmlandDTO;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.enums.state.FarmlandStateEnum;
import com.plansolve.farm.repository.FarmlandRepository;
import com.plansolve.farm.service.client.FarmlandService;
import com.plansolve.farm.service.client.AddressService;
import com.plansolve.farm.service.common.FileService;
import com.plansolve.farm.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/8
 * @Description:
 **/
@Service
public class FarmlandServiceImpl implements FarmlandService {

    @Autowired
    private FarmlandRepository farmlandRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private FileService fileService;

    /**
     * 添加土地信息
     *
     * @param farmlandDTO
     * @param idUser
     * @return
     */
    @Override
    @Transactional
    public FarmlandDTO insert(FarmlandDTO farmlandDTO, List<MultipartFile> pictures, Long idUser) throws IOException {
        // 保存农田地址
        Long idAddress = addressService.insert(farmlandDTO.getAddress());

        // 保存地貌图片
        JsonArray array = new JsonArray();
        if (pictures != null && pictures.size() > 0) {
            for (MultipartFile picture : pictures) {
                String pictureName = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.FARMLAND_PICTURE, picture);
                array.add(pictureName);
            }
        }

        // 保存农田信息
        Farmland farmland = new Farmland();
        BeanUtils.copyProperties(farmlandDTO, farmland);
        farmland.setIdUser(idUser);
        farmland.setIdAddress(idAddress);
        farmland.setFarmlandState(FarmlandStateEnum.NORMOL.getState());
        farmland.setPictures(array.toString());
        farmland.setCreateDate(new Date());
        farmland.setUpdateDate(new Date());
        farmland = farmlandRepository.save(farmland);

        // 回显信息
        BeanUtils.copyProperties(farmland, farmlandDTO);
        // 农田编号
        farmlandDTO.setFarmlandNo(encryption(farmland.getIdFarmland()));
        return farmlandDTO;
    }

    /**
     * 获取当前用户所有土地信息
     *
     * @param idUser 当前用户
     * @return
     */
    @Override
    public List<FarmlandDTO> list(Long idUser) {
        List<Farmland> farmlands = farmlandRepository.findByIdUserAndFarmlandStateNot(idUser, FarmlandStateEnum.DELETED.getState());
        if (farmlands != null && farmlands.size() > 0) {
            List<FarmlandDTO> farmlandDTOs = new ArrayList<>();
            for (Farmland farmland : farmlands) {
                FarmlandDTO farmlandDTO = loadDTO(farmland);
                farmlandDTOs.add(farmlandDTO);
            }
            return farmlandDTOs;
        } else {
            return null;
        }
    }

    /**
     * 获取对应编号的土地信息
     *
     * @param farmlandNo
     * @return
     */
    @Override
    public Farmland getFarmland(String farmlandNo) {
        Long idFarmland = decryption(farmlandNo);
        Farmland farmland = getFarmland(idFarmland);
        return farmland;
    }

    /**
     * 获取土地信息
     *
     * @param idFarmland
     * @return
     */
    @Override
    public Farmland getFarmland(Long idFarmland) {
        Farmland farmland = farmlandRepository.findByIdFarmland(idFarmland);
        return farmland;
    }

    /**
     * 获取对应的土地传输对象
     *
     * @param farmland
     * @return
     */
    @Override
    public FarmlandDTO loadDTO(Farmland farmland) {
        FarmlandDTO farmlandDTO = new FarmlandDTO();
        // 农田编号
        farmlandDTO.setFarmlandNo(encryption(farmland.getIdFarmland()));
        // 农田地址
        AddressDTO addressDTO = addressService.getAddress(farmland.getIdAddress());
        farmlandDTO.setAddress(addressDTO);
        BeanUtils.copyProperties(farmland, farmlandDTO);
        return farmlandDTO;
    }

    /**
     * 用户修改农田信息
     *
     * @param farmlandDTO
     * @param pictures
     * @return
     */
    @Override
    @Transactional
    public FarmlandDTO edit(FarmlandDTO farmlandDTO, List<MultipartFile> pictures) throws IOException {
        if (farmlandDTO.getFarmlandNo().isEmpty()) {
            throw new NullParamException("[农田编码不能为空]");
        } else {
            // 获取原对象
            Long idFarmland = decryption(farmlandDTO.getFarmlandNo());
            Farmland farmland = getFarmland(idFarmland);

            // 更新地址
            AddressDTO addressDTO = addressService.update(farmlandDTO.getAddress(), farmland.getIdAddress());
            farmlandDTO.setAddress(addressDTO);

            // 更新农田相关图片
            JsonArray array = new JsonArray();
            if (pictures != null && pictures.size() > 0) {
                for (MultipartFile picture : pictures) {
                    String pictureName = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.FARMLAND_PICTURE, picture);
                    array.add(pictureName);
                }
            }

            // 更新农田信息
            if (array.size() > 0) {
                String farmlandPictures = farmlandDTO.getPictures();
                if (farmlandPictures != null && farmlandPictures.isEmpty() == false) {
                    if (farmlandPictures.length() <= 2) {
                        farmland.setPictures(array.toString());
                    } else {
                        farmlandPictures = farmlandPictures.substring(1, farmlandPictures.length() - 1);
                        farmlandPictures = array.toString().replace("[", "[" + farmlandPictures + ",");
                        farmland.setPictures(farmlandPictures);
                    }
                } else {
                    farmland.setPictures(array.toString());
                }
            }
            farmland.setFarmlandName(farmlandDTO.getFarmlandName());
            farmland.setFarmlandAcreage(farmlandDTO.getFarmlandAcreage());
            farmland.setDetail(farmlandDTO.getDetail());
            farmland.setUpdateDate(new Date());
            farmland.setUpdateDate(new Date());
            farmlandRepository.save(farmland);

            farmlandDTO.setPictures(array.toString());
            return farmlandDTO;
        }
    }

    /**
     * 删除农田信息
     *
     * @param farmlandNo
     */
    @Override
    @Transactional
    public void delete(String farmlandNo) {
        if (farmlandNo.isEmpty()) {
            throw new NullParamException("[农田编码不能为空]");
        } else {
            Long idFarmland = decryption(farmlandNo);
            Farmland farmland = getFarmland(idFarmland);
            farmland.setFarmlandState(FarmlandStateEnum.DELETED.getState());
            farmland.setUpdateDate(new Date());
            farmlandRepository.save(farmland);
        }
    }

    /**
     * 农田主键加工为农田编号
     *
     * @param idFarmland
     * @return
     */
    @Override
    public String encryption(Long idFarmland) {
        return StringUtil.prefixStr(idFarmland.toString(), 8, "0");
    }

    /**
     * 农田编号加工为农田主键
     *
     * @param farmlandNo
     * @return
     */
    @Override
    public Long decryption(String farmlandNo) {
        return Long.valueOf(farmlandNo);
    }

}
