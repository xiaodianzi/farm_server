package com.plansolve.farm.service.console.user;

import com.plansolve.farm.model.console.user.AppUserDTO;
import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Page;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: 高一平
 * @Date: 2018/7/27
 * @Description: 用户相关接口
 **/

public interface ConsoleUserService {

    /**
     * 查询用户
     *
     * @param idUser
     * @return
     */
    public User findUser(Long idUser);

    /**
     * 获取用户列表
     *
     * @param mobile    用户手机号码
     * @param userState 用户状态
     * @param pageNo    当前页
     * @param pageSize  每页大小
     * @return
     */
    public Page<User> findAllUsers(String mobile, String userState, Integer pageNo, Integer pageSize);

    /**
     * 转换传输对象
     *
     * @param user
     * @return
     */
    public AppUserDTO loadDTO(User user);

    /**
     * 批量转换传输对象
     *
     * @param users
     * @return
     */
    public List<AppUserDTO> loadDTOs(List<User> users);


    /**
     * 查询指定时间内平台用户的数量
     * @param beginTime
     * @param endTime
     * @return
     */
    public Map<String,Integer> getStatisticalUserData(Date beginTime, Date endTime);

    /**
     * 查询当天新增用户数量
     * @return
     */
    public Map<String, Integer> getTodayUserData();

}
