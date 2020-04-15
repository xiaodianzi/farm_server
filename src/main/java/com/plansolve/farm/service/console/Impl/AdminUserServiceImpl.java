package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.model.console.AdminUserDTO;
import com.plansolve.farm.model.database.console.AdminUser;
import com.plansolve.farm.model.enums.state.AdminUserStateEnum;
import com.plansolve.farm.repository.console.AdminUserRepository;
import com.plansolve.farm.service.console.AdminUserService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EncryptUtil;
import com.plansolve.farm.util.EnumUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/28
 * @Description:
 **/

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    /**
     * 添加管理员
     *
     * @param adminUser
     * @return
     */
    @Override
    public AdminUser create(AdminUserDTO adminUser) {
        AdminUser user = new AdminUser();
        BeanUtils.copyProperties(adminUser, user);
        if (user.getPassword() == null) {
            user.setPassword(EncryptUtil.encrypt("123456"));
        }
        if (adminUser.getIdentity().equals("超级管理员")) {
            user.setIsSuperAdmin(true);
        } else {
            user.setIsSuperAdmin(false);
        }
        user.setUserState(AdminUserStateEnum.NORMAL.getState());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        return adminUserRepository.save(user);
    }

    /**
     * 修改管理员
     *
     * @param adminUser
     * @return
     */
    @Override
    public AdminUser update(AdminUserDTO adminUser) {
        AdminUser user = findByMobile(adminUser.getMobile());
        user.setUsername(adminUser.getUsername());
        user.setRole(adminUser.getRole());
        if (adminUser.getIdentity().equals("超级管理员")) {
            user.setIsSuperAdmin(true);
        } else {
            user.setIsSuperAdmin(false);
        }
        user.setUpdateTime(new Date());
        return adminUserRepository.save(user);
    }

    /**
     * 查询管理员列表
     *
     * @return
     */
    @Override
    public List<AdminUser> findAll() {
        return adminUserRepository.findByUserStateNot(AdminUserStateEnum.DELETED.getState());
    }

    /**
     * 根据手机号码查找管理员
     *
     * @param mobile
     * @return
     */
    @Override
    public AdminUser findByMobile(String mobile) {
        return adminUserRepository.findByMobileAndUserStateNot(mobile, AdminUserStateEnum.DELETED.getState());
    }

    /**
     * 查询用户
     *
     * @param idAdminUser
     * @return
     */
    @Override
    public AdminUser findUser(Integer idAdminUser) {
        return adminUserRepository.findByIdAdminUser(idAdminUser);
    }

    /**
     * 转换为传输对象
     *
     * @param adminUser
     * @return
     */
    @Override
    public AdminUserDTO loadDTO(AdminUser adminUser) {
        AdminUserDTO adminUserDTO = new AdminUserDTO();
        BeanUtils.copyProperties(adminUser, adminUserDTO);
        AdminUserStateEnum state = EnumUtil.getByState(adminUser.getUserState(), AdminUserStateEnum.class);
        adminUserDTO.setUserState(state.getMessage());
        if (adminUser.getIsSuperAdmin()) {
            adminUserDTO.setIdentity("超级管理员");
        } else {
            adminUserDTO.setIdentity("普通用户");
        }
        adminUserDTO.setCreateTime(DateUtils.formatDateTime(adminUser.getCreateTime()));
        adminUserDTO.setUpdateTime(DateUtils.formatDateTime(adminUser.getUpdateTime()));
        return adminUserDTO;
    }

    /**
     * 转换为传输对象
     *
     * @param adminUsers
     * @return
     */
    @Override
    public List<AdminUserDTO> loadDTOS(List<AdminUser> adminUsers) {
        List<AdminUserDTO> adminUserDTOS = new ArrayList<>();
        if (adminUsers != null && adminUsers.size() > 0) {
            for (AdminUser adminUser : adminUsers) {
                AdminUserDTO adminUserDTO = loadDTO(adminUser);
                adminUserDTOS.add(adminUserDTO);
            }
        }
        return adminUserDTOS;
    }


}
