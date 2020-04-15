package com.plansolve.farm.service.base.user.impl;

import com.plansolve.farm.model.database.log.UserChangeLog;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserInfo;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import com.plansolve.farm.model.enums.type.LogTypeEnum;
import com.plansolve.farm.repository.log.UserChangeLogRepository;
import com.plansolve.farm.repository.user.UserInfoRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.service.base.user.UserBaseService;
import com.plansolve.farm.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/6/12
 * @Description:
 **/
@Slf4j
@Service
public class UserBaseServiceImpl implements UserBaseService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserInfoRepository infoRepository;
    @Autowired
    private UserChangeLogRepository logRepository;

    private String[] tags = {"A", "B", "C", "D", "E", "F"};

    @Override
    public User getUser(Long idUser) {
        return repository.findByIdUser(idUser);
    }

    @Override
    public User getUser(String mobile) {
        return repository.findByMobile(mobile);
    }

    @Override
    public UserInfo getUserInfo(Long idUser) {
        return infoRepository.findByIdUser(idUser);
    }

    @Override
    public User getValidatedUser(String mobile) {
        return repository.findByMobileAndUserState(mobile, UserStateEnum.NORMOL.getState());
    }

    @Override
    @Transactional
    public User insert(User user) {
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(EncryptUtil.encryptForPage("12345678"));
        }
        user.setPassword(EncryptUtil.encrypt(user.getPassword().trim()));
        user.setUserState(UserStateEnum.PENDING.getState());
        user.setIsFarmer(false);
        user.setIsOperator(false);

        user.setRegistTime(new Date());
        user.setUpdateTime(new Date());
        user = repository.save(user);

        generateLog(user, LogTypeEnum.CREATE.getType(), "");
        return user;
    }

    @Override
    @Transactional
    public User update(User user, String detail) {
        user.setUpdateTime(new Date());
        user = repository.save(user);

        generateLog(user, LogTypeEnum.UPDATE.getType(), detail);
        return user;
    }

    @Override
    @Transactional
    public User verify(Long idUser) {
        User user = getUser(idUser);
        user.setUserState(UserStateEnum.NORMOL.getState());
        user.setUpdateTime(new Date());
        user = repository.save(user);

        generateLog(user, LogTypeEnum.VERIFY.getType(), "");
        return user;
    }

    @Override
    @Transactional
    public User delete(Long idUser) {
        User user = getUser(idUser);
        // 为用户标识可用删除符号
        String mobile = user.getMobile();
        for (String tag : tags) {
            if (getUser(tag + mobile) == null) {
                user.setMobile(tag + mobile);
                user.setUserState(UserStateEnum.DELETED.getState());
                user.setUpdateTime(new Date());
            }
        }
        user = repository.save(user);

        generateLog(user, LogTypeEnum.DELETE.getType(), "");
        return user;
    }

    private void generateLog(User user, String changeType, String detail) {
        UserChangeLog log = new UserChangeLog();
        log.setIdUser(user.getIdUser());
        log.setChangeType(changeType);
        log.setChangeTime(new Date());
        log.setDetail(detail);
        logRepository.save(log);
    }
}
