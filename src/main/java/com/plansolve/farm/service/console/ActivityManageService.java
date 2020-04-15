package com.plansolve.farm.service.console;

import com.plansolve.farm.model.client.activity.PromotionActivityAppDTO;
import com.plansolve.farm.model.client.activity.PromotionActivityDTO;
import com.plansolve.farm.model.client.activity.PromotionWinnersDTO;
import com.plansolve.farm.model.console.activity.PromotionActivityWebDTO;
import com.plansolve.farm.model.database.promotion.PromotionActivity;
import com.plansolve.farm.model.database.promotion.PromotionPlayer;
import com.plansolve.farm.model.database.promotion.PromotionWinners;
import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/5/27
 * @Description: 优惠活动处理类
 */
public interface ActivityManageService {

    /**
     * 获取所有优惠活动数据
     * @return
     */
    public Page<PromotionActivity> queryActivityInfo(Integer pageNo, Integer pageSize);

    /**
     * 获取所有有效的积分规则数据
     * @return
     */
    public Page<PromotionActivity> queryValidActivityInfo(Integer pageNo, Integer pageSize);

    /**
     * 批量转换传输对象
     * @param promotionActivities
     * @return
     */
    public List<PromotionActivityDTO> loadPromotionActivityDTOS(List<PromotionActivity> promotionActivities);

    /**
     * 新增或修改积分规则
     * @param promotionActivity
     * @return
     */
    public PromotionActivity addOrUpdatePromotionActivity(PromotionActivityDTO promotionActivity);

    /**
     * 活动优惠季详情页面数据
     * @param idPromotionActivity
     * @param user
     * @return
     */
    public PromotionActivityAppDTO queryActivityDetails(Long idPromotionActivity, User user);

    /**
     * 获取活动banner信息接口
     * @return
     */
    public List<PromotionActivity> loadBannersInfo();

    /**
     * 判断用户是否具有报名资格
     * @param idPromotionActivity 活动id
     * @return
     */
    public Boolean checkQualification(Long idPromotionActivity);

    /**
     * 报名参加优惠季活动
     * @param idPromotionActivity 优惠活动id
     * @param user 当前用户
     * @param idFarmLands 优惠活动关联的土地信息
     * @return
     */
    public PromotionPlayer signUpActivity(Long idPromotionActivity, User user, String idFarmLands);

    /**
     * 参加抽奖活动
     * @param idPromotionActivity 活动id
     * @param user 当前用户
     * @return
     */
    public PromotionPlayer signUpLottery(Long idPromotionActivity, User user);

    /**
     * 获取该优惠活动得中奖者名单
     * @param idPromotionActivity
     * @return
     */
    public List<PromotionWinners> lotteryWinners(Long idPromotionActivity);

    /**
     * 批量转换为DTO对象
     * @param promotionWinners
     * @return
     */
    public List<PromotionWinnersDTO> lotteryWinnersDTO(List<PromotionWinners> promotionWinners);

    /**
     * 随机抽奖
     * @param idPromotionActivity
     * @param winnerNumber 中奖人数
     * @param prize 奖品
     * @return
     */
    public List<PromotionWinners> randomLuckydraw(Long idPromotionActivity, Integer winnerNumber, String prize);

    /**
     * 活动参与人员信息列表
     * @param pageNumber 页码
     * @param pageSize 每页显示条数
     * @param promotionActivityWebDTO 入参封装dto
     * @return
     */
    public Page<PromotionPlayer> queryActivityPlayers(Integer pageNumber, Integer pageSize, PromotionActivityWebDTO promotionActivityWebDTO);

    /**
     * 活动参与人员信息列表
     * @param pageNumber 页码
     * @param pageSize 每页显示条数
     * @param promotionActivityWebDTO 入参封装dto
     * @return
     */
    public Page<PromotionWinners> queryActivityWinners(Integer pageNumber, Integer pageSize, PromotionActivityWebDTO promotionActivityWebDTO);

    /**
     * 封装输出DTO
     * @param promotionPlayers
     * @return
     */
    public List<PromotionActivityWebDTO> loadPromotionActivityWebDTOS(List<PromotionPlayer> promotionPlayers);

    public List<PromotionActivityWebDTO> loadActivityWinnersDTOS(List<PromotionWinners> promotionWinners);

}
