package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.client.user.UserDescDTO;
import com.plansolve.farm.model.client.user.UserIdCardDTO;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户相关接口
 **/

public interface UserService {

    /**
     * 范围查找用户
     *
     * @param latitude  查找范围
     * @param longitude 查找范围
     * @param user      当前用户（需排除）
     * @return
     */
    public List<User> findAllUserLocal(Double latitude, Double longitude, User user);

    /**
     * 用户注册接口
     *
     * @param userDTO
     * @param password
     * @return
     */
    public UserDTO regist(UserDTO userDTO, String password);

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    public User saveUser(User user);

    /**
     * 保存用户详情
     *
     * @param userInfo
     * @return
     */
    public UserInfo saveUserInfo(UserInfo userInfo);

    /**
     * 更新用户设备码
     *
     * @param idUser
     * @param androidMAC
     */
    public User changeUserAndroidMAC(Long idUser, String androidMAC);

    /**
     * 更改用户手机号码
     *
     * @param idUser
     * @param mobile
     * @return
     */
    public User changeUserMobile(Long idUser, String mobile);

    /**
     * 更新用户种植户身份
     *
     * @param idUser
     * @param be
     * @return
     */
    public User changeToBeFarmer(Long idUser, Boolean be);

    /**
     * 更新用户农机手身份
     *
     * @param idUser
     * @param be
     * @return
     */
    public User changeToBeOperator(Long idUser, Boolean be);

    /**
     * 删除用户
     *
     * @param idUser
     */
    public void deleteUser(Long idUser);

    /**
     * 提交验证信息
     *
     * @param idUser
     * @param userIdCardDTO
     * @return
     */
    public void certificate(Long idUser, UserIdCardDTO userIdCardDTO);

    /**
     * 获取验证信息
     *
     * @param idUser
     * @return
     */
    public UserIdCardDTO getCertificate(Long idUser);

    /**
     * 更改头像
     *
     * @param idUser  变更用户
     * @param picture 头像图片
     * @return
     */
    public User changeAvatar(Long idUser, MultipartFile picture) throws IOException;

    /**
     * 更改用户密码
     *
     * @param idUser
     * @param password
     * @return
     */
    public User changePassword(Long idUser, String password);

    /**
     * 更改用户昵称
     *
     * @param idUser
     * @param userDesc
     * @param address
     * @return
     */
    public User changeUserDesc(Long idUser, UserDescDTO userDesc, AddressDTO address);

    /**
     * 检验该手机号码是否存在
     *
     * @param mobile
     * @return
     */
    public Boolean checkMobileExist(String mobile);

    /**
     * 根据手机号码查询用户
     *
     * @param mobile
     * @return
     */
    public User findByMobile(String mobile);

    /**
     * 根据手机号码模糊查询
     *
     * @param mobile
     * @return
     */
    public List<User> findByMoBileLike(String mobile);

    /**
     * 根据用户openid查询用户
     *
     * @param openid
     * @return
     */
    public User findByOpenId(String openid);

    /**
     * 根据主键查询用户
     *
     * @param idUser
     * @return
     */
    public User findUser(Long idUser);

    /**
     * 根据主键查询用户详情
     *
     * @param idUser
     * @return
     */
    public UserInfo findUserInfo(Long idUser);

    /**
     * 根据用户返回传输对象
     *
     * @param user
     * @param isDetail
     * @return
     */
    public UserDTO loadDTO(User user, Boolean isDetail);

    /**
     * 查询用户信息
     *
     * @param idUser   用户主键
     * @param isDetail 是否查询用户详情
     * @return
     */
    public UserDTO findUser(Long idUser, Boolean isDetail);

    /**
     * 更新合作社用户信息
     *
     * @param cooperationMember 合作社关联用户
     * @return
     */
    public User changeCooperationRelation(User cooperationMember);

    /**
     * 更新用户的注册id
     *
     * @param user     当前用户
     * @param registId 绑定的注册id
     * @return
     */
    public User changeUserRegistId(User user, String registId);

}
