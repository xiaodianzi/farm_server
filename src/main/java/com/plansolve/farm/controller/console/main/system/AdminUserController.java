package com.plansolve.farm.controller.console.main.system;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.console.AdminUserDTO;
import com.plansolve.farm.model.database.console.AdminUser;
import com.plansolve.farm.service.console.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/28
 * @Description:
 **/
@Controller
@RequestMapping(value = "/manger/admin/user")
public class AdminUserController extends BaseController {

    @Autowired
    private AdminUserService adminUserService;

    /**
     * 管理员用户页面
     *
     * @return
     */
    @GetMapping(value = "/adminUserPage")
    public String adminUserListPage() {
        return "admin/adminUserList";
    }

    /**
     * 获取管理员用户列表
     *
     * @return
     */
    @GetMapping(value = "/getAdminUserList")
    @ResponseBody
    public List<AdminUserDTO> getAdminUserList() {
        List<AdminUser> adminUsers = adminUserService.findAll();
        return adminUserService.loadDTOS(adminUsers);
    }


}
