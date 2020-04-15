package com.plansolve.farm.service.console;

import com.plansolve.farm.model.client.score.*;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.score.ExchangePermitUser;
import com.plansolve.farm.model.database.score.ScoreLog;
import com.plansolve.farm.model.database.score.ScoreRule;
import com.plansolve.farm.model.database.score.ScoreUser;
import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/20
 * @Description:
 */
public interface ScoreManageService {

    /**
     * 获取所有积分规则数据
     * @return
     */
    public Page<ScoreRule> getManageScoreRulesInfo(Integer pageNo, Integer pageSize);

    /**
     * 获取所有有效的积分规则数据
     * @return
     */
    public Page<ScoreRule> getScoreRulesInfo(Integer pageNo, Integer pageSize);

    /**
     * 批量转换传输对象
     * @param scoreRules
     * @return
     */
    public List<ScoreIndexDTO> loadUserScoreDTOs(List<ScoreRule> scoreRules);

    /**
     * 新增或修改积分规则
     * @param scoreRule
     * @return
     */
    public ScoreRule addOrUpdateScoreRule(ScoreRule scoreRule);

    public ScoreTaskDTO signPointTask(User user);

    public ScoreTaskDTO authPointTask(User user, String authType);

    public ScoreTaskDTO sharePointTask(User user);

    public ScoreTaskDTO createOrderPointTask(User user, UserOrder userOrder);

    public ScoreTaskDTO acceptOrderPointTask(User user);

    public ScoreTaskDTO diagnosePointTask(User user);

    /**
     * 用户积分页面数据组装
     * @param user 当前用户
     * @param scoreRules 积分规则
     * @return 返回装配好的model数据模型
     */
    public List<ScoreIndexDTO> loadScoreIndexData(User user, List<ScoreRule> scoreRules);

    public UserScoreDataDTO loadUserScoreData(User user, List<ScoreRule> scoreRules);

    public ScoreTaskDTO getDefaultScoreTask(boolean result, String resultInfo);

    public UserScoreDTO getUserScoreData(User user);

    public User getUserByExchangePermitUser(Long idExchangePermitUser);

    public List<UserScoreDetailDTO> getUserScoreDetails(User user, Integer pageNumber, Integer pageSize);

    /**
     * 判断用户认证信息的积分是否领取
     * @param user
     * @return
     */
    public boolean getAuthedScore(User user);

    /**
     * 向指定用户兑换积分
     * @param user 当前用户
     * @param permitUser 指定的积分兑换人员
     * @param score 积分
     * @return 成功返回当前用户积分兑换log
     */
    public ScoreLog exchangeScore(User user, User permitUser, Integer score);

    /**
     * 积分IO操作
     * @param currentScoreUser 花积分人的积分账户
     * @param permitScoreUser 赚积分人的积分账户
     * @param score 积分
     * @return 是否成功
     */
    public boolean exchangeScoreIO(ScoreUser currentScoreUser, ScoreUser permitScoreUser, Integer score);

    /**
     * 查询所有提供积分兑换服务的用户
     * @return
     */
    public List<ExchangePermitUser> queryUsersByPermitUser();

    /**
     * 转换成ExchangePermitUserDTO列表
     * @return
     */
    public List<ExchangePermitUserDTO> loadExchangePermitUserDTO(List<ExchangePermitUser> exchangePermitUsers);

}
