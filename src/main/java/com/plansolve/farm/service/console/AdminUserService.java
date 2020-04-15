package com.plansolve.farm.service.console;

import com.plansolve.farm.model.console.AdminUserDTO;
import com.plansolve.farm.model.database.console.AdminUser;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/28
 * @Description: 后台管理员用户
 **/
public interface AdminUserService {

    /**
     * 添加管理员
     *
     * @param adminUser
     * @return
     */
    public AdminUser create(AdminUserDTO adminUser);

    /**
     * 修改管理员
     *
     * @param adminUser
     * @return
     */
    public AdminUser update(AdminUserDTO adminUser);

    /**
     * 查询管理员列表
     *
     * @return
     */
    public List<AdminUser> findAll();

    /**
     * 根据手机号码查找管理员
     *
     * @param mobile
     * @return
     */
    public AdminUser findByMobile(String mobile);

    /**
     * 查询用户
     *
     * @param idAdminUser
     * @return
     */
    public AdminUser findUser(Integer idAdminUser);

    /**
     * 转换为传输对象
     *
     * @param adminUser
     * @return
     */
    public AdminUserDTO loadDTO(AdminUser adminUser);

    /**
     * 转换为传输对象
     *
     * @param adminUsers
     * @return
     */
    public List<AdminUserDTO> loadDTOS(List<AdminUser> adminUsers);

}
