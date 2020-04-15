package com.plansolve.farm.service.base.common.impl;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.school.CerealsMassage;
import com.plansolve.farm.model.database.school.CerealsMassageType;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.repository.school.CerealsMassageRepository;
import com.plansolve.farm.repository.school.CerealsMassageTypeRepository;
import com.plansolve.farm.service.base.common.CerealsMassageService;
import com.plansolve.farm.service.common.FileService;
import com.plansolve.farm.util.HtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/5/15
 * @Description:
 **/

@Slf4j
@Service
public class CerealsMassageServiceImpl implements CerealsMassageService {

    @Autowired
    private CerealsMassageTypeRepository typeRepository;
    @Autowired
    private CerealsMassageRepository massageRepository;

    @Autowired
    private FileService fileService;


    /**
     * 添加作物类型
     *
     * @param cerealsMassageType 作物类型
     * @param picture            相关折线图
     * @return
     */
    @Override
    public CerealsMassageType insertType(CerealsMassageType cerealsMassageType, MultipartFile picture) throws IOException {
        if (picture != null) {
            String pictureName = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.CEREALS_REAL_TIME_PRICES_PICTURE, picture);
            cerealsMassageType.setPicture(pictureName);
        }
        cerealsMassageType = typeRepository.save(cerealsMassageType);
        return cerealsMassageType;
    }

    /**
     * 添加信息
     *
     * @param cerealsMassage
     * @return
     */
    @Override
    public CerealsMassage insert(CerealsMassage cerealsMassage, MultipartFile[] files) {
        if (cerealsMassage.getIsValid() == null) {
            cerealsMassage.setIsValid(false);
        }
        if (cerealsMassage.getIdCerealsMassage() != null && cerealsMassage.getIdCerealsMassage() > 0) {
            // 更改
            CerealsMassage oleMassage = getMassage(cerealsMassage.getIdCerealsMassage());
            cerealsMassage.setCreateTime(oleMassage.getCreateTime());

            if (cerealsMassage.getReleaseTime() == null) {
                cerealsMassage.setReleaseTime(oleMassage.getReleaseTime());
            }
        } else {
            // 新增
            cerealsMassage.setCreateTime(new Date());
            if (cerealsMassage.getReleaseTime() == null) {
                cerealsMassage.setReleaseTime(new Date());
            }
        }
        /*******************************************更改图片 保存现图 原图不删*********************************************/
        // 消息详情页内置图
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                try {
                    String originalFilename = file.getOriginalFilename();
                    String filename = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.CEREALS_REAL_TIME_PRICES_DETAIL_PICTURE, file);
                    String detail = cerealsMassage.getDetail();
                    detail = detail.replace(originalFilename, FileProperties.fileUrlPath + SysConstant.CEREALS_REAL_TIME_PRICES_DETAIL_PICTURE + filename);
                    cerealsMassage.setDetail(detail);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /*******************************************更改图片 保存现图 原图不删*********************************************/

        String detail = HtmlUtil.changeDetailToHtml(cerealsMassage.getDetail());
        cerealsMassage.setDetail(detail);
        cerealsMassage = massageRepository.save(cerealsMassage);
        return cerealsMassage;
    }

    /**
     * 获取类型信息
     *
     * @param idCerealsMassageType
     * @return
     */
    @Override
    public CerealsMassageType getType(Integer idCerealsMassageType) {
        CerealsMassageType cerealsMassageType = typeRepository.findByIdCerealsMassageType(idCerealsMassageType);
        return cerealsMassageType;
    }

    /**
     * 获取信息
     *
     * @param idCerealsMassage
     * @return
     */
    @Override
    public CerealsMassage getMassage(Integer idCerealsMassage) {
        return massageRepository.findByIdCerealsMassage(idCerealsMassage);
    }

    /**
     * 查询所有作物类型
     *
     * @return
     */
    @Override
    public List<CerealsMassageType> listTypes() {
        List<CerealsMassageType> types = typeRepository.findAll();
        return types;
    }

    /**
     * 查询对应类型的信息
     *
     * @param idCerealsMassageType
     * @return
     */
    @Override
    public List<CerealsMassage> listMassages(Integer idCerealsMassageType) {
        Sort sort = new Sort(Sort.Direction.DESC, "releaseTime");
        List<CerealsMassage> massages = massageRepository.findByIdCerealsMassageType(idCerealsMassageType, sort);
        return massages;
    }
}
