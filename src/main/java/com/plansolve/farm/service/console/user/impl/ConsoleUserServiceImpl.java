package com.plansolve.farm.service.console.user.impl;

import com.plansolve.farm.model.console.user.AppUserDTO;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.console.user.ConsoleUserService;
import com.plansolve.farm.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * @Author: 高一平
 * @Date: 2018/7/27
 * @Description:
 **/
@Service
public class ConsoleUserServiceImpl implements ConsoleUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CooperationService cooperationService;

    /**
     * 查询用户
     *
     * @param idUser
     * @return
     */
    @Override
    public User findUser(Long idUser) {
        return userRepository.findByIdUser(idUser);
    }

    /**
     * 获取用户列表
     *
     * @param mobile    用户手机号码
     * @param userState 用户状态
     * @param pageNo    当前页
     * @param pageSize  每页大小
     * @return
     */
    @Override
    public Page<User> findAllUsers(String mobile, String userState, Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "idUser");
        Page<User> userPage;
        if ((mobile == null || mobile.equals("")) && userState == null) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findAll(new Specification<User>() {
                @Override
                public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    Predicate predicate1;
                    if (userState != null) {
                        predicate1 = criteriaBuilder.equal(root.get("userState").as(String.class), userState);
                    } else {
                        predicate1 = criteriaBuilder.notEqual(root.get("userState").as(String.class), UserStateEnum.DELETED.getState());
                    }
                    if (mobile != null && !mobile.equals("")) {
                        Predicate predicate2 = criteriaBuilder.like(root.get("mobile").as(String.class), "%" + mobile + "%");
                        query.where(criteriaBuilder.and(predicate1, predicate2));
                    } else {
                        query.where(criteriaBuilder.and(predicate1));
                    }
                    return query.getRestriction();
                }
            }, pageable);
        }
        return userPage;
    }

    @Override
    public AppUserDTO loadDTO(User user) {
        AppUserDTO userDTO = new AppUserDTO();
        BeanUtils.copyProperties(user, userDTO);

        if (user.getUserState().equals(UserStateEnum.PENDING.getState())) {
            userDTO.setUserState(UserStateEnum.PENDING.getMessage());
        } else if (user.getUserState().equals(UserStateEnum.AUDIT.getState())) {
            userDTO.setUserState(UserStateEnum.AUDIT.getMessage());
        } else if (user.getUserState().equals(UserStateEnum.NORMOL.getState())) {
            userDTO.setUserState(UserStateEnum.NORMOL.getMessage());
        } else if (user.getUserState().equals(UserStateEnum.FROZEN.getState())) {
            userDTO.setUserState(UserStateEnum.FROZEN.getMessage());
        } else if (user.getUserState().equals(UserStateEnum.DELETED.getState())) {
            userDTO.setUserState(UserStateEnum.DELETED.getMessage());
        }

        if (user.getFarmer() && user.getOperator()) {
            userDTO.setIdentity("种植户/农机手");
        } else if (user.getFarmer()) {
            userDTO.setIdentity("种植户");
        } else if (user.getOperator()) {
            userDTO.setIdentity("农机手");
        } else {
            userDTO.setIdentity("");
        }

        if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
            boolean proprieter = cooperationService.proprieter(user);
            boolean captain = cooperationService.captain(user);
            if (proprieter && captain) {
                userDTO.setPosition("社员/队长");
            } else if (proprieter) {
                userDTO.setPosition("社员");
            } else if (captain) {
                userDTO.setPosition("队长");
            } else {
                userDTO.setPosition("社员");
            }
        } else {
            userDTO.setPosition("普通用户");
        }

        userDTO.setRegistTime(DateUtils.formatDateTime(user.getRegistTime()));
        userDTO.setUpdateTime(DateUtils.formatDateTime(user.getUpdateTime()));
        userDTO.setButton("");
        return userDTO;
    }

    /**
     * 批量转换传输对象
     *
     * @param users
     * @return
     */
    @Override
    public List<AppUserDTO> loadDTOs(List<User> users) {
        List<AppUserDTO> userDTOS = new ArrayList<>();
        if (users != null && users.size() > 0) {
            for (User user : users) {
                AppUserDTO userDTO = loadDTO(user);
                userDTOS.add(userDTO);
            }
        }
        return userDTOS;
    }

    @Override
    public Map<String, Integer> getStatisticalUserData(Date beginTime, Date endTime) {
        Map<String, Integer> map = new HashMap<>();
        List<Date> dateList = userRepository.queryUserGroupByRegistTime();
        Long totalNum = userRepository.count();
        String total = totalNum + "";
        map.put("totalNumber", Integer.parseInt(total));
        for (Date date : dateList) {
            Date dayBegin = DateUtils.getDayBegin(date);
            Date dayEnd = DateUtils.getDayEnd(date);
            Integer count = userRepository.countByRegistTimeBetween(dayBegin, dayEnd);
            String[] dayArray = date.toString().substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

    @Override
    public Map<String, Integer> getTodayUserData() {
        Map<String, Integer> map = new HashMap<>();
        Date dayBegin = DateUtils.getDayBegin(new Date());
        Date dayEnd = DateUtils.getDayEnd(new Date());
        Integer count = userRepository.countByRegistTimeBetween(dayBegin, dayEnd);
        if (count > 0){
            Long totalNum = userRepository.count();
            String total = totalNum + "";
            map.put("totalNumber", Integer.parseInt(total));
            String[] dayArray = DateUtils.formatDate(new Date(), "yyyy-MM-dd").substring(0, 10).split("-");
            String month = dayArray[1];
            String day = dayArray[2];
            if (month.charAt(0) == '0') {
                month = month.substring(1);
            }
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
            String key = dayArray[0] + "-" + month + "-" + day;
            map.put(key, count);
        }
        return map;
    }

}
