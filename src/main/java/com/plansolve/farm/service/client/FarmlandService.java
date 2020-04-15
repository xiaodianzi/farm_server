package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.score.ScoreTaskDTO;
import com.plansolve.farm.model.client.user.FarmlandDTO;
import com.plansolve.farm.model.database.Farmland;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/8
 * @Description: 土地相关操作接口
 **/
public interface FarmlandService {

    /**
     * 添加土地信息
     *
     * @param farmlandDTO
     * @param pictures
     * @param idUser
     * @return
     * @throws IOException
     */
    public FarmlandDTO insert(FarmlandDTO farmlandDTO, List<MultipartFile> pictures, Long idUser) throws IOException;

    /**
     * 获取当前用户所有土地信息
     *
     * @param idUser 当前用户
     * @return
     */
    public List<FarmlandDTO> list(Long idUser);

    /**
     * 获取对应编号的土地信息
     *
     * @param farmlandNo
     * @return
     */
    public Farmland getFarmland(String farmlandNo);

    /**
     * 获取土地信息
     *
     * @param idFarmland
     * @return
     */
    public Farmland getFarmland(Long idFarmland);

    /**
     * 获取对应的土地传输对象
     *
     * @param farmland
     * @return
     */
    public FarmlandDTO loadDTO(Farmland farmland);

    /**
     * 用户修改农田信息
     *
     * @param farmlandDTO
     * @param pictures
     * @return
     * @throws IOException
     */
    public FarmlandDTO edit(FarmlandDTO farmlandDTO, List<MultipartFile> pictures) throws IOException;

    /**
     * 删除农田信息
     *
     * @param farmlandNo
     */
    public void delete(String farmlandNo);

    /**
     * 农田主键加工为农田编号
     *
     * @param idFarmland
     * @return
     */
    public String encryption(Long idFarmland);

    /**
     * 农田编号加工为农田主键
     *
     * @param farmlandNo
     * @return
     */
    public Long decryption(String farmlandNo);

}
