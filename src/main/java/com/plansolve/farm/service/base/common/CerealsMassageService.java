package com.plansolve.farm.service.base.common;

import com.plansolve.farm.model.database.school.CerealsMassage;
import com.plansolve.farm.model.database.school.CerealsMassageType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/5/15
 * @Description:
 **/
public interface CerealsMassageService {

    /**
     * 添加作物类型
     *
     * @param cerealsMassageType 作物类型
     * @param picture            相关折线图
     * @return
     */
    public CerealsMassageType insertType(CerealsMassageType cerealsMassageType, MultipartFile picture) throws IOException;

    /**
     * 添加信息
     *
     * @param cerealsMassage
     * @return
     */
    public CerealsMassage insert(CerealsMassage cerealsMassage, MultipartFile[] files);

    /**
     * 获取类型信息
     *
     * @param idCerealsMassageType
     * @return
     */
    public CerealsMassageType getType(Integer idCerealsMassageType);

    /**
     * 获取信息
     *
     * @param idCerealsMassage
     * @return
     */
    public CerealsMassage getMassage(Integer idCerealsMassage);

    /**
     * 查询所有作物类型
     *
     * @return
     */
    public List<CerealsMassageType> listTypes();

    /**
     * 查询对应类型的信息
     *
     * @param idCerealsMassageType
     * @return
     */
    public List<CerealsMassage> listMassages(Integer idCerealsMassageType);

}
