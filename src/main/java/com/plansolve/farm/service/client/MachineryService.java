package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.user.MachineryDTO;
import com.plansolve.farm.model.database.Machinery;
import com.plansolve.farm.model.database.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/11
 * @Description:
 **/
public interface MachineryService {

    /**
     * 根据农机编号删除农机
     *
     * @param machineryNo
     * @return
     */
    public Machinery findByMachineryNo(String machineryNo);

    /**
     * 用户添加农机
     *
     * @param machineryDTO 农机基本信息
     * @param pictures     农机相关图片
     * @param idUser       用户
     * @return
     * @throws IOException
     */
    public MachineryDTO insert(MachineryDTO machineryDTO, List<MultipartFile> pictures, Long idUser) throws IOException;

    /**
     * 查询用户农机信息
     *
     * @param idUser
     * @return
     */
    public List<Machinery> list(Long idUser);

    /**
     * 修改农机信息
     *
     * @param machineryDTO
     * @param pictures
     * @return
     * @throws IOException
     */
    public MachineryDTO edit(MachineryDTO machineryDTO, List<MultipartFile> pictures) throws IOException;

    /**
     * 用户删除农机信息
     *
     * @param machineryNo
     */
    public void delete(String machineryNo);

    /**
     * 将农机信息封装成传输对象
     *
     * @param machinery
     * @return
     */
    public MachineryDTO loadDTO(Machinery machinery);

    /**
     * 查询单个个人的是否拥有该类型农机
     *
     * @param user
     * @param machineryType
     * @return
     */
    public Boolean checkSingleUserMachineryType(User user, String machineryType);

}
