package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.CropsDataDTO;
import com.plansolve.farm.model.client.GrainMarketDTO;
import com.plansolve.farm.model.database.agricultural.GrainMarket;
import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/29
 * @Description:
 */
public interface GrainMarketService {

    /**
     * 发布粮食购销信息
     * @param user 当前用户
     * @param grainMarket 消息内容
     * @return
     */
    public GrainMarket saveGrainMarket(User user, GrainMarketDTO grainMarket);

    /**
     * 获取指定类型的消息
     * @param user
     * @param infoType
     * @return 消息列表
     */
    public List<GrainMarketDTO> getInformationByUser(Integer pageNumber, Integer pageSize, User user, String infoType);

    public List<GrainMarketDTO> getInformationByType(Integer pageNumber, Integer pageSize, String infoType);

    /**
     * 获取指定类型的农作物数据
     * @param pageNumber
     * @param pageSize
     * @param dataType
     * @return
     */
    public CropsDataDTO getPlantDataByType(Integer pageNumber, Integer pageSize, String dataType);

    /**
     * 根据指定条件查询粮食信息
     * @param pageNumber
     * @param pageSize
     * @param idUser
     * @param marketType 消息类型
     * @param grainType 农作物类型
     * @return
     */
    public Page<GrainMarket> getInformationByCondition(Integer pageNumber, Integer pageSize, Long idUser, String marketType, String grainType, String validTime);

    public List<GrainMarketDTO> loadGrainMarketWebDTO(List<GrainMarket> grainMarkets);

}
