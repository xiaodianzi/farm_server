package com.plansolve.farm.service.client.Impl;

import com.google.gson.JsonArray;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.user.MachineryDTO;
import com.plansolve.farm.model.database.Machinery;
import com.plansolve.farm.model.database.dictionary.DictMachineryType;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.MachineryStateEnum;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.repository.MachineryRepository;
import com.plansolve.farm.service.client.DictService;
import com.plansolve.farm.service.client.MachineryService;
import com.plansolve.farm.service.common.FileService;
import com.plansolve.farm.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/11
 * @Description:
 **/
@Service
public class MachineryServiceImpl implements MachineryService {

    @Autowired
    private MachineryRepository machineryRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private DictService dictService;

    /**
     * 根据农机编号删除农机
     *
     * @param machineryNo
     * @return
     */
    @Override
    public Machinery findByMachineryNo(String machineryNo) {
        Long idMachinery = decryption(machineryNo);
        return machineryRepository.findByIdMachinery(idMachinery);
    }

    /**
     * 用户添加农机
     *
     * @param machineryDTO 农机基本信息
     * @param pictures     农机相关图片
     * @param idUser       用户
     * @return
     */
    @Override
    @Transactional
    public MachineryDTO insert(MachineryDTO machineryDTO, List<MultipartFile> pictures, Long idUser) throws IOException {
        // 保存农机图片
        JsonArray array = new JsonArray();
        if (pictures != null && pictures.size() > 0) {
            for (MultipartFile picture : pictures) {
                String pictureName = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.MACHINERY_PICTURE, picture);
                array.add(pictureName);
            }
        }

        // 保存农机信息
        Machinery machinery = new Machinery();
        BeanUtils.copyProperties(machineryDTO, machinery);
        machinery.setIdUser(idUser);
        machinery.setPictures(array.toString());
        machinery.setMachineryState(MachineryStateEnum.NORMOL.getState());
        machinery.setCreateDate(new Date());
        machinery.setUpdateDate(new Date());

        try {
            if (machineryDTO.getIdMachineryType() != null && machineryDTO.getIdMachineryType() > 0) {
                DictMachineryType farmMachinery = dictService.getFarmMachinery(machineryDTO.getIdMachineryType());
                if (farmMachinery != null) {
                    if (farmMachinery.getParentId() != 0) {
                        DictMachineryType type = dictService.getFarmMachinery(farmMachinery.getParentId());
                        machinery.setParentMachineryType(type.getValue());
                    } else {
                        machinery.setParentMachineryType(farmMachinery.getValue());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        machinery = machineryRepository.save(machinery);

        // 回显信息
        BeanUtils.copyProperties(machinery, machineryDTO);
        machineryDTO.setMachineryNo(encryption(machinery.getIdMachinery()));
        return machineryDTO;
    }

    /**
     * 查询用户农机信息
     *
     * @param idUser
     * @return
     */
    @Override
    public List<Machinery> list(Long idUser) {
        List<Machinery> machineries = machineryRepository.findByIdUserAndMachineryStateNot(idUser, MachineryStateEnum.DELETED.getState());
        return machineries;
    }

    /**
     * 修改农机信息
     *
     * @param machineryDTO
     * @param pictures
     * @return
     * @throws IOException
     */
    @Override
    @Transactional
    public MachineryDTO edit(MachineryDTO machineryDTO, List<MultipartFile> pictures) throws IOException {
        if (machineryDTO.getMachineryNo() == null || machineryDTO.getMachineryNo().isEmpty()) {
            throw new NullParamException("[农机编码不能为空]");
        } else {
            // 获取原对象
            Long idMachinery = decryption(machineryDTO.getMachineryNo());
            Machinery machinery = machineryRepository.findByIdMachinery(idMachinery);

            // 更新农机相关图片
            JsonArray array = new JsonArray();
            if (pictures != null && pictures.size() > 0) {
                for (MultipartFile picture : pictures) {
                    String pictureName = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.MACHINERY_PICTURE, picture);
                    array.add(pictureName);
                }
            }

            // 更新农机信息
            if (array.size() > 0) {
                String machineryPictures = machineryDTO.getPictures();
                if (machineryPictures != null && machineryPictures.isEmpty() == false) {
                    if (machineryPictures.length() <= 2) {
                        machinery.setPictures(array.toString());
                    } else {
                        machineryPictures = machineryPictures.substring(1, machineryPictures.length() - 1);
                        machineryPictures = array.toString().replace("[", "[" + machineryPictures + ",");
                        machinery.setPictures(machineryPictures);
                    }
                } else {
                    machinery.setPictures(array.toString());
                }
            }
            machinery.setMachineryAbility(machineryDTO.getMachineryAbility());
            machinery.setCount(machineryDTO.getCount());
            machinery.setDraggingDevice(machineryDTO.getDraggingDevice());
            machinery.setRatedPower(machineryDTO.getRatedPower());
            machinery.setLicenseNum(machineryDTO.getLicenseNum());
            machinery.setDetail(machineryDTO.getDetail());
            machinery.setUpdateDate(new Date());
            machineryRepository.save(machinery);

            machineryDTO.setPictures(array.toString());
            return machineryDTO;
        }
    }

    /**
     * 用户删除农机信息
     *
     * @param machineryNo
     */
    @Override
    @Transactional
    public void delete(String machineryNo) {
        if (machineryNo.isEmpty()) {
            throw new NullParamException("[农机编码不能为空]");
        } else {
            Long idMachinery = decryption(machineryNo);
            Machinery machinery = machineryRepository.findByIdMachinery(idMachinery);
            machinery.setMachineryState(MachineryStateEnum.DELETED.getState());
            machinery.setUpdateDate(new Date());
            machineryRepository.save(machinery);
        }
    }

    /**
     * 将数据库对象装载为传输对象
     *
     * @param machinery
     * @return
     */
    @Override
    public MachineryDTO loadDTO(Machinery machinery) {
        MachineryDTO machineryDTO = new MachineryDTO();
        machineryDTO.setMachineryNo(encryption(machinery.getIdMachinery()));
        BeanUtils.copyProperties(machinery, machineryDTO);
        if (machinery.getParentMachineryType() != null && machinery.getParentMachineryType().trim().length() > 0) {
            machineryDTO.setMachineryType(machinery.getParentMachineryType() + "-" + machinery.getMachineryType());
        }
        return machineryDTO;
    }

    /**
     * 查询单个个人的是否拥有该类型农机
     *
     * @param user
     * @param machineryType
     * @return
     */
    @Override
    public Boolean checkSingleUserMachineryType(User user, String machineryType) {
        List<Machinery> list = list(user.getIdUser());
        if (list != null && list.size() > 0) {
            for (Machinery machinery : list) {
                if (machinery.getMachineryType().equals(machineryType)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 农田主键加工为农田编号
     *
     * @param idMachinery
     * @return
     */
    private String encryption(Long idMachinery) {
        return StringUtil.prefixStr(idMachinery.toString(), 8, "0");
    }

    /**
     * 农田编号加工为农田主键
     *
     * @param machineryNo
     * @return
     */
    private Long decryption(String machineryNo) {
        return Long.valueOf(machineryNo);
    }

}
