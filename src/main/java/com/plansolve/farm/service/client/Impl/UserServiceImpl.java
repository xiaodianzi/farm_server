package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.exception.DeletedStateErrorException;
import com.plansolve.farm.exception.FrozenStateErrorException;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.*;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.client.user.UserDescDTO;
import com.plansolve.farm.model.client.user.UserIdCardDTO;
import com.plansolve.farm.model.database.cooperation.Cooperation;
import com.plansolve.farm.model.database.log.UserChangeLog;
import com.plansolve.farm.model.database.user.UserInfo;
import com.plansolve.farm.model.database.user.WeChatUser;
import com.plansolve.farm.model.enums.type.LogTypeEnum;
import com.plansolve.farm.model.enums.code.UserIdentityEnum;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.type.ScoreTypeEnum;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.repository.log.UserChangeLogRepository;
import com.plansolve.farm.repository.score.ScoreLogRepository;
import com.plansolve.farm.repository.user.UserInfoRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.repository.user.WeChatUserRepository;
import com.plansolve.farm.service.client.AddressService;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.client.TeamService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.service.common.FileService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EncryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户相关接口客户端实现
 **/

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private WeChatUserRepository weChatUserRepository;
    @Autowired
    private UserChangeLogRepository logRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ScoreLogRepository scoreLogRepository;

    private String[] tags = {"A", "B", "C", "D", "E", "F"};

    /**
     * 范围查找用户
     *
     * @param latitude  查找范围
     * @param longitude 查找范围
     * @param user      当前用户（需排除）
     * @return
     */
    @Override
    public List<User> findAllUserLocal(Double latitude, Double longitude, User user) {
        return userRepository.findByUserStateNotAndIsOperatorAndIdUserNot(UserStateEnum.DELETED.getState(), true, user.getIdUser());
    }

    /**
     * 用户注册接口实现
     *
     * @param userDTO
     * @param password
     * @return
     */
    @Override
    @Transactional
    public UserDTO regist(UserDTO userDTO, String password) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        password = EncryptUtil.encrypt(password.trim());
        user.setPassword(password);
        user = add(user);
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 保存用户详情
     *
     * @param userInfo
     * @return
     */
    @Override
    public UserInfo saveUserInfo(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    /**
     * 更新用户设备码
     *
     * @param idUser
     * @param androidMAC
     */
    @Override
    @Transactional
    public User changeUserAndroidMAC(Long idUser, String androidMAC) {
        User user = userRepository.findByIdUser(idUser);
        if (user.getAndroidMAC() == null || user.getAndroidMAC().equals(androidMAC) == false) {
            user.setAndroidMAC(androidMAC);
            user = update(user, LogTypeEnum.UPDATE.getType(), "更新用户设备码");
        }
        return user;
    }

    /**
     * 更改用户手机号码
     *
     * @param idUser
     * @param mobile
     * @return
     */
    @Override
    public User changeUserMobile(Long idUser, String mobile) {
        User user = userRepository.findByIdUser(idUser);
        if (user.getMobile().equals(mobile) == false) {
            user.setMobile(mobile);
            user = update(user, LogTypeEnum.UPDATE.getType(), "更新用户手机号码");
        }
        return user;
    }

    /**
     * 更新用户种植户身份
     *
     * @param idUser
     * @param be
     * @return
     */
    @Override
    @Transactional
    public User changeToBeFarmer(Long idUser, Boolean be) {
        User user = userRepository.findByIdUser(idUser);
        user.setFarmer(be);
        if (be) {
            user = update(user, LogTypeEnum.UPDATE.getType(), "赋予用户种植户身份");
        } else {
            user = update(user, LogTypeEnum.UPDATE.getType(), "取消用户种植户身份");
        }
        return user;
    }

    /**
     * 更新用户农机手身份
     *
     * @param idUser
     * @param be
     * @return
     */
    @Override
    @Transactional
    public User changeToBeOperator(Long idUser, Boolean be) {
        User user = userRepository.findByIdUser(idUser);
        user.setOperator(be);
        if (be) {
            user = update(user, LogTypeEnum.UPDATE.getType(), "赋予用户农机手身份");
        } else {
            user = update(user, LogTypeEnum.UPDATE.getType(), "取消用户农机手身份");
        }
        return user;
    }

    /**
     * 删除用户（逻辑删除）
     * 用户删除后，重新注册即为另一个账号
     * 原账号依然保留，同一手机号码最多注销6次
     *
     * @param idUser
     */
    @Override
    @Transactional
    public void deleteUser(Long idUser) {
        // 查询用户
        User user = userRepository.findByIdUser(idUser);
        // 为用户标识可用删除符号
        String mobile = user.getMobile();
        for (String tag : tags) {
            if (checkMobileExist(tag + mobile) == false) {
                user.setMobile(tag + mobile);
                user.setUserState(UserStateEnum.DELETED.getState());
                update(user, LogTypeEnum.DELETE.getType(), null);
                userRepository.save(user);
                return;
            }
        }
    }

    /**
     * 提交验证信息
     *
     * @param idUser
     * @param userIdCardDTO
     * @return
     */
    @Override
    @Transactional
    public void certificate(Long idUser, UserIdCardDTO userIdCardDTO) {
        User user = userRepository.findByIdUser(idUser);

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userIdCardDTO, userInfo);
        userInfo.setIdUser(user.getIdUser());

        // 现提交即通过
        certificateUser(userInfo, user);
    }

    /**
     * 获取验证信息
     *
     * @param idUser
     * @return
     */
    @Override
    public UserIdCardDTO getCertificate(Long idUser) {
        User user = userRepository.findByIdUser(idUser);
        if (user.getUserState().equals(UserStateEnum.NORMOL.getState()) || user.getUserState().equals(UserStateEnum.AUDIT.getState())) {
            UserInfo userInfo = userInfoRepository.findByIdUser(idUser);
            if (userInfo == null) {
                return new UserIdCardDTO();
            } else {
                UserIdCardDTO userIdCardDTO = new UserIdCardDTO();
                BeanUtils.copyProperties(userInfo, userIdCardDTO);
                return userIdCardDTO;
            }
        } else {
            return new UserIdCardDTO();
        }
    }

    /**
     * 用户修改头像图片
     *
     * @param idUser  变更用户
     * @param picture 头像图片
     * @return
     * @throws IOException
     */
    @Override
    public User changeAvatar(Long idUser, MultipartFile picture) throws IOException {
        User user = userRepository.findByIdUser(idUser);

        if (!(user.getAvatar() == null || user.getAvatar().isEmpty())) {
            fileService.deleteFileByPath(FileProperties.fileRealPath, SysConstant.USER_AVATAR, user.getAvatar());
        }
        String pic = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.USER_AVATAR, picture);

        user.setAvatar(pic);
        user = update(user, LogTypeEnum.UPDATE.getType(), "更新用户头像");
        return user;
    }

    /**
     * 更改用户密码
     *
     * @param idUser
     * @param password
     * @return
     */
    @Override
    public User changePassword(Long idUser, String password) {
        User user = userRepository.findByIdUser(idUser);
        user.setPassword(EncryptUtil.encrypt(password.trim()));
        user = update(user, LogTypeEnum.UPDATE.getType(), "用户更改密码");
        return user;
    }

    /**
     * 更改用户昵称
     *
     * @param idUser
     * @param userDesc
     * @param address
     * @return
     */
    @Override
    public User changeUserDesc(Long idUser, UserDescDTO userDesc, AddressDTO address) {
        User user = userRepository.findByIdUser(idUser);
        UserInfo userInfo = userInfoRepository.findByIdUser(idUser);
        String msg = "用户更改";
        if (address != null) {
            if (user.getIdAddress() != null && user.getIdAddress() > 0) {
                addressService.update(address, user.getIdAddress());
            } else {
                Long idAddress = addressService.insert(address);
                user.setIdAddress(idAddress);
            }
            msg = msg + "所在地址、";
        }

        if (userDesc != null) {
            if (userDesc.getNickname() != null && userDesc.getNickname().isEmpty() == false) {
                if (user.getNickname().equals(userDesc.getNickname()) == false) {
                    user.setNickname(userDesc.getNickname());
                    msg = msg + "昵称、";
                }
            }
            if (userDesc.getBirthday() != null) {
                if (userInfo.getBirthday().equals(userDesc.getBirthday())) {
                    userInfo.setBirthday(userDesc.getBirthday());
                    msg = msg + "出生日期、";
                }
            }
            if (userDesc.getSex() != null && userDesc.getSex().isEmpty() == false) {
                if (userInfo.getSex().equals(userDesc.getSex()) == false) {
                    userInfo.setSex(userDesc.getSex());
                    msg = msg + "性别、";
                }
            }
            if (userDesc.getQq() != null && userDesc.getQq().isEmpty() == false) {
                if (userDesc.getQq().equals(userDesc.getQq()) == false) {
                    userInfo.setQq(userDesc.getQq());
                    msg = msg + "QQ、";
                }
            }
        }
        if (msg.equals("用户更改") == false) {
            msg = msg.substring(0, msg.length() - 1);
            userInfoRepository.save(userInfo);
            user = update(user, LogTypeEnum.UPDATE.getType(), msg);
        }
        return user;
    }

    /**
     * 检验该手机号码是否存在
     *
     * @param mobile
     * @return
     */
    @Override
    public Boolean checkMobileExist(String mobile) {
        User user = userRepository.findByMobile(mobile);
        if (user == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据手机号码查询用户
     *
     * @param mobile
     * @return
     */
    @Override
    public User findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    /**
     * 根据手机号码模糊查询
     *
     * @param mobile
     * @return
     */
    @Override
    public List<User> findByMoBileLike(String mobile) {
        return userRepository.findByMobileLike("%" + mobile + "%");
    }

    /**
     * 根据用户openid查询用户
     *
     * @param openid
     * @return
     */
    @Override
    public User findByOpenId(String openid) {
        if (openid != null && openid.isEmpty() == false) {
            WeChatUser weChatUser = weChatUserRepository.findByOpenId(openid);
            if (weChatUser != null) {
                User user = findUser(weChatUser.getIdUser());
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public User findUser(Long idUser) {
        return userRepository.findByIdUser(idUser);
    }

    /**
     * 根据主键查询用户详情
     *
     * @param idUser
     * @return
     */
    @Override
    public UserInfo findUserInfo(Long idUser) {
        return userInfoRepository.findByIdUser(idUser);
    }

    @Override
    public UserDTO loadDTO(User user, Boolean isDetail) {
        if (user.getUserState().equals(UserStateEnum.DELETED.getState())) {
            throw new DeletedStateErrorException("[该用户已删除]");
        } else if (user.getUserState().equals(UserStateEnum.FROZEN.getState())) {
            throw new FrozenStateErrorException("[该用户已冻结]");
        } else {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            //新增当前用户今日是否签到
            boolean signed = scoreLogRepository.existsByChangeTimeAfterAndChangeTypeAndIdUser(DateUtils.getDayBegin(new Date()), ScoreTypeEnum.SIGN_IN_SCORE_PLUS.getType(), user.getIdUser());
            userDTO.setSignedToday(signed);
            if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
                Cooperation cooperation = cooperationService.getById(user.getIdCooperation());
                if (cooperation != null) {
                    userDTO.setCooperationName(cooperation.getCooperationName());
                    if (cooperationService.proprieter(user)) {
                        userDTO.setIdentity(UserIdentityEnum.PROPRIETER.getCode());
                    } else if (cooperationService.captain(user)) {
                        userDTO.setIdentity(UserIdentityEnum.CAPTAIN.getCode());
                    } else {
                        userDTO.setIdentity(UserIdentityEnum.MEMBER.getCode());
                    }
                } else {
                    userDTO.setIdentity(UserIdentityEnum.NORMOL.getCode());
                }
            } else {
                userDTO.setIdentity(UserIdentityEnum.NORMOL.getCode());
            }

            if (isDetail) {
                if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
                    CooperationInfoDTO cooperationDTO = cooperationService.findById(user.getIdCooperation());
                    userDTO.setCooperationInfoDTO(cooperationDTO);
                }
                if (user.getIdTeam() != null && user.getIdTeam() > 0) {
                    TeamDTO teamDTO = teamService.findById(user.getIdTeam());
                    userDTO.setTeamDTO(teamDTO);
                }
            }
            return userDTO;
        }
    }

    /**
     * 查询用户信息
     *
     * @param idUser   用户主键
     * @param isDetail 是否查询用户详情
     * @return
     */
    @Override
    public UserDTO findUser(Long idUser, Boolean isDetail) {
        User user = findUser(idUser);
        if (user == null) {
            throw new NullParamException("[无法查询到该用户]");
        }
        return loadDTO(user, isDetail);
    }

    /**
     * 用户实名认证
     *
     * @param userInfo
     * @param user
     * @return
     */
    private void certificateUser(UserInfo userInfo, User user) {
        // 用户提交审核信息
        userInfo.setVerifyTime(new Date());
        userInfoRepository.save(userInfo);
        user.setUserState(UserStateEnum.AUDIT.getState());
        update(user, LogTypeEnum.VERIFY.getType(), "上传用户身份信息，实名认证验证中");

        // 用户审核通过
        userInfo.setVerifyTime(new Date());
        userInfoRepository.save(userInfo);

        user.setUserState(UserStateEnum.NORMOL.getState());
        update(user, LogTypeEnum.VERIFY.getType(), "实名验证通过");
    }

    /**
     * 新增用户数据
     *
     * @param user
     * @return
     */
    private User add(User user) {
        user.setRegistTime(new Date());
        user.setUpdateTime(new Date());
        user.setFarmer(false);
        user.setOperator(false);
        user.setUserState(UserStateEnum.PENDING.getState());
        user = userRepository.save(user);

        UserChangeLog log = new UserChangeLog();
        log.setIdUser(user.getIdUser());
        log.setChangeType(LogTypeEnum.CREATE.getType());
        log.setChangeTime(new Date());
        logRepository.save(log);
        return user;
    }

    /**
     * 更新用户
     *
     * @param user       用户最新数据
     * @param changeType 更新类型
     * @param detail     详情
     * @return
     */
    private User update(User user, String changeType, String detail) {
        user.setUpdateTime(new Date());
        userRepository.save(user);

        UserChangeLog log = new UserChangeLog();
        log.setIdUser(user.getIdUser());
        log.setChangeType(changeType);
        log.setChangeTime(new Date());
        log.setDetail(detail);
        logRepository.save(log);
        return user;
    }

    @Override
    public User changeCooperationRelation(User cooperationMember) {
        if (null != cooperationMember) {
            cooperationMember.setUpdateTime(new Date());
            User user = userRepository.save(cooperationMember);
            return user;
        }
        return cooperationMember;
    }

    @Override
    @Transactional
    public User changeUserRegistId(User user, String registId) {
        if (null != user && StringUtils.isNotBlank(registId)) {
            List<User> oldUsers = userRepository.findAllByRegistIdAndIdUserIsNot(registId, user.getIdUser());
            // 将该手机上所有的老用户的注册id全部设为默认状态
            if (null != oldUsers && oldUsers.size() > 0) {
                for (User u : oldUsers) {
                    if (null != u) {
                        u.setRegistId(null);
                        update(u, LogTypeEnum.UPDATE.getType(), "清除手机老用户极光推送的注册id");
                    }
                }
            }
            // 实时更新用户的注册id，确保消息推送到用户最新的手机
            if (StringUtils.isBlank(user.getRegistId()) || !registId.equals(user.getRegistId())) {
                user.setRegistId(registId);
                user = update(user, LogTypeEnum.UPDATE.getType(), "更新当前用户极光推送的注册id");
            }
        }
        return user;
    }

}
