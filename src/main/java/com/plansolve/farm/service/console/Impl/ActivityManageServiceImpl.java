package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.exception.AddRepeatException;
import com.plansolve.farm.exception.NotExistException;
import com.plansolve.farm.model.PromotionActivityConstant;
import com.plansolve.farm.model.client.activity.PromotionActivityAppDTO;
import com.plansolve.farm.model.client.activity.PromotionActivityDTO;
import com.plansolve.farm.model.client.activity.PromotionWinnersDTO;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.console.activity.PromotionActivityWebDTO;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.promotion.PromotionActivity;
import com.plansolve.farm.model.database.promotion.PromotionPlayer;
import com.plansolve.farm.model.database.promotion.PromotionWinners;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.repository.FarmlandRepository;
import com.plansolve.farm.repository.activity.PromotionActivityRepository;
import com.plansolve.farm.repository.activity.PromotionPlayerRepository;
import com.plansolve.farm.repository.activity.PromotionWinnersRepository;
import com.plansolve.farm.service.client.FarmlandService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.service.console.ActivityManageService;
import com.plansolve.farm.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: Andrew
 * @Date: 2019/5/27
 * @Description: 优惠活动实现类
 */
@Service
public class ActivityManageServiceImpl implements ActivityManageService {

    @Autowired
    private PromotionActivityRepository promotionActivityRepository;

    @Autowired
    private PromotionPlayerRepository promotionPlayerRepository;

    @Autowired
    private FarmlandRepository farmlandRepository;

    @Autowired
    private FarmlandService farmlandService;

    @Autowired
    private UserService userService;

    @Autowired
    private PromotionWinnersRepository promotionWinnersRepository;

    @Override
    public Page<PromotionActivity> queryActivityInfo(Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.ASC, "idPromotionActivity");
        Page<PromotionActivity> promotionActivities = promotionActivityRepository.findAll(pageable);
        return promotionActivities;
    }

    @Override
    public Page<PromotionActivity> queryValidActivityInfo(Integer pageNo, Integer pageSize) {
        return null;
    }

    @Override
    public List<PromotionActivityDTO> loadPromotionActivityDTOS(List<PromotionActivity> promotionActivities) {
        List<PromotionActivityDTO> promotionActivityDTOS = new ArrayList<>();
        if (null != promotionActivities) {
            if (promotionActivities.size() > 0) {
                for (PromotionActivity promotionActivity : promotionActivities) {
                    String startTime = DateUtils.formatDate(promotionActivity.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                    String endTime = DateUtils.formatDate(promotionActivity.getEndTime(), "yyyy-MM-dd HH:mm:ss");
                    PromotionActivityDTO promotionActivityDTO = new PromotionActivityDTO(promotionActivity.getIdPromotionActivity(),
                            promotionActivity.getPictureUrl(), promotionActivity.getActivityName(), promotionActivity.getDescription(),
                            promotionActivity.getActivityType(), promotionActivity.getMaxPlayers(), promotionActivity.getMaxAcreage(),
                            startTime, endTime, promotionActivity.getContacts(), promotionActivity.getAddressDetail(), promotionActivity.getIsValid());
                    // BeanUtils.copyProperties(scoreRule, userScoreDTO);
                    promotionActivityDTOS.add(promotionActivityDTO);
                }
            }
        }
        return promotionActivityDTOS;
    }

    @Override
    @Transactional
    public PromotionActivity addOrUpdatePromotionActivity(PromotionActivityDTO promotionActivityDTO) {
        PromotionActivity promotionActivity = new PromotionActivity();
        if (null != promotionActivityDTO) {
            //修改
            if (null != promotionActivityDTO.getIdPromotionActivity()) {
                PromotionActivity oneByIdPromotionActivity = promotionActivityRepository.findOneByIdPromotionActivity(promotionActivityDTO.getIdPromotionActivity());
                if (null == oneByIdPromotionActivity) {
                    throw new NotExistException(":目标对象不存在!");
                }
            } else {
                //新增
                PromotionActivity activity = promotionActivityRepository.findByActivityName(promotionActivityDTO.getActivityName());
                if (null != activity) {
                    throw new AddRepeatException("");
                }
            }
            BeanUtils.copyProperties(promotionActivityDTO, promotionActivity);
            if (StringUtils.isNotBlank(promotionActivityDTO.getStartTime())) {
                promotionActivity.setStartTime(DateUtils.parseDate(promotionActivityDTO.getStartTime()));
            }
            if (StringUtils.isNotBlank(promotionActivityDTO.getEndTime())) {
                promotionActivity.setEndTime(DateUtils.parseDate(promotionActivityDTO.getEndTime()));
            }
            PromotionActivity savedPromotionActivity = promotionActivityRepository.save(promotionActivity);
            return savedPromotionActivity;
        }
        return null;
    }

    @Override
    public PromotionActivityAppDTO queryActivityDetails(Long idPromotionActivity, User user) {
        PromotionActivityAppDTO promotionActivity = new PromotionActivityAppDTO();
        Float totalAcreage = 0f;
        boolean exist = promotionPlayerRepository.existsByIdPromotionActivityAndIdUserAndValidIsTrue(idPromotionActivity, user.getIdUser());
        promotionActivity.setJoined(exist);
        PromotionActivity promotion = promotionActivityRepository.findOneByIdPromotionActivity(idPromotionActivity);
        if (null != promotion) {
            promotionActivity.setIdPromotionActivity(promotion.getIdPromotionActivity());
            promotionActivity.setActivityName(promotion.getActivityName());
            promotionActivity.setDescription(promotion.getDescription());
            if (null != promotion.getStartTime()) {
                promotionActivity.setStartTime(DateUtils.formatDate(promotion.getStartTime(), "yyyy-MM-dd"));
            }
            if (null != promotion.getEndTime()) {
                promotionActivity.setEndTime(DateUtils.formatDate(promotion.getEndTime(), "yyyy-MM-dd"));
            }
            promotionActivity.setContacts(promotion.getContacts());
            if (StringUtils.isNotBlank(promotion.getAddressDetail())) {
                promotionActivity.setAddressDetail(promotion.getAddressDetail());
            }
            List<PromotionPlayer> promotionPlayers = promotionPlayerRepository.findByIdPromotionActivity(idPromotionActivity);
            if (null != promotionPlayers) {
                List<UserDTO> players = new ArrayList<>();
                if (promotionPlayers.size() > 0) {
                    for (PromotionPlayer pw : promotionPlayers) {
                        if (null != pw.getLandAcreage()) {
                            totalAcreage += pw.getLandAcreage();
                        }
                        User player = userService.findUser(pw.getIdUser());
                        UserDTO userDTO = new UserDTO();
                        BeanUtils.copyProperties(player, userDTO);
                        players.add(userDTO);
                    }
                }
                if (PromotionActivityConstant.LOTTERY_ACTIVITY_NAME.equals(promotion.getActivityName().trim())) {
                    promotionActivity.setPlayers(players);
                    promotionActivity.setDeadline(DateUtils.parseDate(PromotionActivityConstant.LOTTERY_END_TIME).getTime());
                    if (null != promotionPlayers) {
                        promotionActivity.setPlayerAmount(promotionPlayers.size());
                    } else {
                        promotionActivity.setPlayerAmount(PromotionActivityConstant.INITIAL_PLAYER_AMOUNT);
                    }
                } else {
                    promotionActivity.setTotalAcreage(totalAcreage);
                }
            }
        }
        return promotionActivity;
    }

    @Override
    public List<PromotionActivity> loadBannersInfo() {
        List<PromotionActivity> promotionActivities = promotionActivityRepository.findByIsValidTrue();
        return promotionActivities;
    }

    @Override
    public Boolean checkQualification(Long idPromotionActivity) {
        Float totalAcreage = 0f;
        List<PromotionPlayer> promotionPlayers = promotionPlayerRepository.findByIdPromotionActivity(idPromotionActivity);
        if (null != promotionPlayers) {
            if (promotionPlayers.size() > 0) {
                for (PromotionPlayer player : promotionPlayers) {
                    if (null != player.getLandAcreage()) {
                        totalAcreage += player.getLandAcreage();
                    }
                }
            }
        }
        synchronized (this) {
            return Float.parseFloat(PromotionActivityConstant.FARMLAND_ACREAGE_LIMIT) > totalAcreage;
        }
    }

    @Override
    @Transactional
    public PromotionPlayer signUpActivity(Long idPromotionActivity, User user, String idFarmLands) {
        boolean exist = promotionPlayerRepository.existsByIdPromotionActivityAndIdUserAndValidIsTrue(idPromotionActivity, user.getIdUser());
        if (exist) {
            throw new AddRepeatException(":优惠活动不能重复参加!");
        }
        PromotionPlayer promotionPlayer = new PromotionPlayer();
        promotionPlayer.setIdPromotionActivity(idPromotionActivity);
        promotionPlayer.setIdUser(user.getIdUser());
        promotionPlayer.setIdFarmLands(idFarmLands);
        Float landAcreage = 0f;
        if (idFarmLands.indexOf("/") > 0) {
            //获取参与活动的土地编号集合
            String[] ids = idFarmLands.split("/");
            for (int i = 0; i < ids.length; i++) {
                Long idFarmland = farmlandService.decryption(ids[i]);
                Farmland farmland = farmlandRepository.findByIdFarmland(idFarmland);
                if (null != farmland) {
                    landAcreage += farmland.getFarmlandAcreage();
                }
            }
        } else {
            Long idFarmland = Long.parseLong(idFarmLands);
            Farmland farmland = farmlandRepository.findByIdFarmland(idFarmland);
            if (null != farmland) {
                landAcreage += farmland.getFarmlandAcreage();
            }
        }
        promotionPlayer.setLandAcreage(landAcreage);
        promotionPlayer.setRemark(PromotionActivityConstant.DRONE_ACTIVITY_REMARK);
        promotionPlayer.setCreateTime(new Date());
        promotionPlayer.setValid(true);
        synchronized (this) {
            PromotionPlayer player = promotionPlayerRepository.save(promotionPlayer);
            return player;
        }
    }

    @Override
    @Transactional
    public PromotionPlayer signUpLottery(Long idPromotionActivity, User user) {
        boolean exist = promotionPlayerRepository.existsByIdPromotionActivityAndIdUserAndValidIsTrue(idPromotionActivity, user.getIdUser());
        if (exist) {
            throw new AddRepeatException(":抽奖活动不能重复参加!");
        }
        PromotionPlayer promotionPlayer = new PromotionPlayer();
        promotionPlayer.setIdPromotionActivity(idPromotionActivity);
        promotionPlayer.setIdUser(user.getIdUser());
        promotionPlayer.setRemark(PromotionActivityConstant.LOTTERY_ACTIVITY_REMARK);
        promotionPlayer.setCreateTime(new Date());
        promotionPlayer.setValid(true);
        synchronized (this) {
            PromotionPlayer player = promotionPlayerRepository.save(promotionPlayer);
            return player;
        }
    }

    @Override
    public List<PromotionWinners> lotteryWinners(Long idPromotionActivity) {
        List<PromotionWinners> winners = promotionWinnersRepository.findByIdPromotionActivityAndValidIsTrue(idPromotionActivity);
        return winners;
    }

    @Override
    public List<PromotionWinnersDTO> lotteryWinnersDTO(List<PromotionWinners> promotionWinners) {
        List<PromotionWinnersDTO> promotionWinnersDTOS = new ArrayList<>();
        if (promotionWinners.size() > 0) {
            for (PromotionWinners promotionWinner : promotionWinners) {
                PromotionWinnersDTO promotionWinnersDTO = new PromotionWinnersDTO();
                promotionWinnersDTO.setIdPromotionWinners(promotionWinner.getIdPromotionWinners());
                PromotionActivity activity = promotionActivityRepository.findOneByIdPromotionActivity(promotionWinner.getIdPromotionActivity());
                if (null != activity) {
                    promotionWinnersDTO.setActivityName(activity.getActivityName());
                }
                User winner = userService.findUser(promotionWinner.getIdUser());
                promotionWinnersDTO.setWinnerName(winner.getNickname());
                promotionWinnersDTO.setPrize(promotionWinner.getPrize());
                promotionWinnersDTO.setCreateTime(DateUtils.formatDate(promotionWinner.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                promotionWinnersDTOS.add(promotionWinnersDTO);
            }
        }
        return promotionWinnersDTOS;
    }

    @Override
    @Transactional
    public List<PromotionWinners> randomLuckydraw(Long idPromotionActivity, Integer winnerNumber, String prize) {
        List<PromotionWinners> promotionWinners = new ArrayList<>();
        if (null != idPromotionActivity) {
            CopyOnWriteArrayList<PromotionPlayer> players = promotionPlayerRepository.findByIdPromotionActivityAndValidIsTrue(idPromotionActivity);
            if (null != players) {
                if (players.size() > 0) {
                    Random random = new Random();
                    if (players.size() < winnerNumber) {
                        winnerNumber = players.size();
                    }
                    synchronized (this) {
                        for (int i = 0; i < winnerNumber; i++) {
                            int index = random.nextInt(players.size());
                            PromotionPlayer player = players.get(index);
                            if (null != player) {
                                PromotionWinners winner = new PromotionWinners();
                                winner.setIdPromotionActivity(idPromotionActivity);
                                winner.setIdUser(player.getIdUser());
                                winner.setPrize(prize);
                                winner.setRemark(PromotionActivityConstant.LOTTERY_WINNER_REMARK);
                                winner.setValid(true);
                                winner.setCreateTime(new Date());
                                PromotionWinners save = promotionWinnersRepository.save(winner);
                                PromotionPlayer promotionPlayer = promotionPlayerRepository.findByIdPromotionActivityAndIdUser(idPromotionActivity, player.getIdUser());
                                if (null != promotionPlayer) {
                                    promotionPlayer.setValid(false);
                                    promotionPlayer.setRemark("已中奖");
                                    promotionPlayerRepository.save(promotionPlayer);
                                }
                                promotionWinners.add(save);
                                players.remove(player);
                            }
                            if (players.size() <= 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return promotionWinners;
    }

    @Override
    public Page<PromotionPlayer> queryActivityPlayers(Integer pageNumber, Integer pageSize, PromotionActivityWebDTO promotionActivityWebDTO) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, Sort.Direction.ASC, "idPromotionPlayer");
        Page<PromotionPlayer> promotionPlayers;
        if (null != promotionActivityWebDTO) {
            if (StringUtils.isNotBlank(promotionActivityWebDTO.getActivityName())
                    || StringUtils.isNotBlank(promotionActivityWebDTO.getMobile())) {
                promotionPlayers = promotionPlayerRepository.findAll(new Specification<PromotionPlayer>() {
                    @Override
                    public Predicate toPredicate(Root<PromotionPlayer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        Predicate predicate = null;
                        if (StringUtils.isNotBlank(promotionActivityWebDTO.getActivityName())) {
                            PromotionActivity activity = promotionActivityRepository.findByActivityName(promotionActivityWebDTO.getActivityName());
                            if (null != activity) {
                                Predicate activityNamePredicate = criteriaBuilder.equal(root.get("idPromotionActivity").as(Long.class), activity.getIdPromotionActivity());
                                if (predicate != null) {
                                    predicate = criteriaBuilder.and(predicate, activityNamePredicate);
                                } else {
                                    predicate = criteriaBuilder.and(activityNamePredicate);
                                }
                            }
                        }
                        if (StringUtils.isNotBlank(promotionActivityWebDTO.getMobile())) {
                            User user = userService.findByMobile(promotionActivityWebDTO.getMobile());
                            Predicate userPredicate = null;
                            if (null != user) {
                                userPredicate = criteriaBuilder.equal(root.get("idUser").as(Long.class), user.getIdUser());
                            }else{
                                userPredicate = criteriaBuilder.equal(root.get("idUser").as(Long.class), 123456789);
                            }
                            if (predicate != null) {
                                predicate = criteriaBuilder.and(predicate, userPredicate);
                            } else {
                                predicate = criteriaBuilder.and(userPredicate);
                            }
                        }
                        if (null != predicate) {
                            predicates.add(predicate);
                        }
                        if (predicates.size() > 0) {
                            query.where(criteriaBuilder.and(predicates.get(0)));
                        }
                        return query.getRestriction();
                    }
                }, pageable);
            } else {
                promotionPlayers = promotionPlayerRepository.findAll(pageable);
            }
        } else {
            promotionPlayers = promotionPlayerRepository.findAll(pageable);
        }
        return promotionPlayers;
    }

    @Override
    public Page<PromotionWinners> queryActivityWinners(Integer pageNumber, Integer pageSize, PromotionActivityWebDTO promotionActivityWebDTO) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, Sort.Direction.ASC, "idPromotionWinners");
        Page<PromotionWinners> promotionWinners;
        if (null != promotionActivityWebDTO) {
            if (StringUtils.isNotBlank(promotionActivityWebDTO.getMobile())) {
                User user = userService.findByMobile(promotionActivityWebDTO.getMobile());
                if (null != user) {
                    promotionWinners = promotionWinnersRepository.findAll(new Specification<PromotionWinners>() {
                        @Override
                        public Predicate toPredicate(Root<PromotionWinners> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                            List<Predicate> predicates = new ArrayList<>();
                            Predicate userPredicate = criteriaBuilder.equal(root.get("idUser").as(Long.class), user.getIdUser());
                            Predicate predicate = criteriaBuilder.and(userPredicate);
                            if (null != predicate) {
                                predicates.add(predicate);
                            }
                            if (predicates.size() > 0) {
                                query.where(criteriaBuilder.and(predicates.get(0)));
                            }
                            return query.getRestriction();
                        }
                    }, pageable);
                }else{
                    promotionWinners = null;
                }
            } else {
                promotionWinners = promotionWinnersRepository.findByValidIsTrue(pageable);
            }
        } else {
            promotionWinners = promotionWinnersRepository.findByValidIsTrue(pageable);
        }
        return promotionWinners;
    }

    @Override
    public List<PromotionActivityWebDTO> loadPromotionActivityWebDTOS(List<PromotionPlayer> promotionPlayers) {
        List<PromotionActivityWebDTO> promotionActivityWebDTOS = new ArrayList<>();
        if (null != promotionPlayers && promotionPlayers.size() > 0) {
            for (PromotionPlayer player : promotionPlayers) {
                PromotionActivityWebDTO dto = new PromotionActivityWebDTO();
                if (null != player.getIdPromotionPlayer()) {
                    dto.setIdPromotionPlayer(player.getIdPromotionPlayer());
                }
                if (null != player.getIdPromotionActivity()) {
                    PromotionActivity activity = promotionActivityRepository.findOneByIdPromotionActivity(player.getIdPromotionActivity());
                    dto.setActivityName(activity.getActivityName());
                }
                if (null != player.getIdUser()) {
                    User user = userService.findUser(player.getIdUser());
                    dto.setPlayerName(user.getNickname());
                    dto.setMobile(user.getMobile());
                }
                dto.setTotalAcreage(player.getLandAcreage());
                dto.setCreateTime(DateUtils.formatDate(player.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                promotionActivityWebDTOS.add(dto);
            }
        }
        return promotionActivityWebDTOS;
    }

    @Override
    public List<PromotionActivityWebDTO> loadActivityWinnersDTOS(List<PromotionWinners> promotionWinners) {
        List<PromotionActivityWebDTO> promotionActivityWebDTOS = new ArrayList<>();
        if (null != promotionWinners && promotionWinners.size() > 0) {
            for (PromotionWinners winners : promotionWinners) {
                PromotionActivityWebDTO dto = new PromotionActivityWebDTO();
                if (null != winners.getIdUser()) {
                    dto.setIdPromotionWinners(winners.getIdPromotionWinners());
                    User user = userService.findUser(winners.getIdUser());
                    dto.setPlayerName(user.getNickname());
                    dto.setMobile(user.getMobile());
                    PromotionWinners winner = promotionWinnersRepository.findByIdUserAndValidIsTrue(winners.getIdUser());
                    if (null != winner) {

                        dto.setPrize(winner.getPrize());
                    }
                }
                if (null != winners.getIdPromotionActivity()) {
                    PromotionActivity activity = promotionActivityRepository.findOneByIdPromotionActivity(winners.getIdPromotionActivity());
                    dto.setActivityName(activity.getActivityName());
                }
                dto.setCreateTime(DateUtils.formatDate(winners.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                promotionActivityWebDTOS.add(dto);
            }
        }
        return promotionActivityWebDTOS;
    }

}
