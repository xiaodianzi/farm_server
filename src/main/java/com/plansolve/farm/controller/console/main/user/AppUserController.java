package com.plansolve.farm.controller.console.main.user;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.console.user.AppFarmlandDTO;
import com.plansolve.farm.model.console.user.AppMachineryDTO;
import com.plansolve.farm.model.console.user.AppUserDTO;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.Machinery;
import com.plansolve.farm.model.database.account.Account;
import com.plansolve.farm.model.database.cooperation.Cooperation;
import com.plansolve.farm.model.database.log.AccountLog;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.AccountStateEnum;
import com.plansolve.farm.model.enums.type.AccountLogTypeEnum;
import com.plansolve.farm.model.enums.type.AccountTypeEnum;
import com.plansolve.farm.service.base.common.AddressBaseService;
import com.plansolve.farm.service.client.AccountService;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.console.user.ConsoleFarmlandService;
import com.plansolve.farm.service.console.user.ConsoleMachineryService;
import com.plansolve.farm.service.console.user.ConsoleUserService;
import com.plansolve.farm.util.EnumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/7/27
 * @Description:
 **/
@Controller
@RequestMapping(value = "/manger/app/user")
public class AppUserController extends BaseController {

    @Autowired
    private ConsoleUserService userService;
    @Autowired
    private ConsoleFarmlandService farmlandService;
    @Autowired
    private ConsoleMachineryService machineryService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private AddressBaseService addressBaseService;
    @Autowired
    private AccountService accountService;

    /**
     * 客户端用户分页查询
     *
     * @return
     */
    @RequestMapping(value = "/appUserPage")
    public String appUserPage() {
        return "console/user/list";
    }

    /**
     * 客户端用户分页查询
     *
     * @return
     */
    @RequestMapping(value = "/appUserList")
    @ResponseBody
    public PageDTO<AppUserDTO> appUserList(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset,
                                           String mobile, String userState) {
        Integer page = getPage(limit, offset);
        Page<User> userPage;
        if (userState.equals("all")) {
            userPage = userService.findAllUsers(mobile, null, page, limit);
        } else {
            userPage = userService.findAllUsers(mobile, userState, page, limit);
        }
        List<AppUserDTO> userDTOS = userService.loadDTOs(userPage.getContent());
        PageDTO<AppUserDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(userPage.getTotalElements());
        pageDTO.setRows(userDTOS);
        return pageDTO;
    }

    /**
     * 客户端用户详情页
     *
     * @param idUser
     * @param model
     * @return
     */
    @RequestMapping(value = "/appUserDetail")
    public String appUserDetail(Long idUser, Model model) {
        User user = userService.findUser(idUser);
        AppUserDTO userDTO = userService.loadDTO(user);
        model.addAttribute("user",userDTO);
        model.addAttribute("avatar",user.getAvatar());

        List<Farmland> farmlands = farmlandService.listByUser(idUser);
        List<AppFarmlandDTO> farmlandDTOS = farmlandService.loadDTO(farmlands);
        model.addAttribute("farmlands",farmlandDTOS);

        List<Machinery> machinerys = machineryService.listByUser(idUser);
        List<AppMachineryDTO> machineryDTOS = new ArrayList<>();
        if (machinerys != null && machinerys.size() > 0) {
            for (Machinery machinery : machinerys) {
                AppMachineryDTO dto = new AppMachineryDTO();
                dto.setIdMachinery(machinery.getIdMachinery());
                dto.setMachineryType(machinery.getMachineryType());
                dto.setMachineryAbility(machinery.getMachineryAbility());
                dto.setCount(machinery.getCount());
                machineryDTOS.add(dto);
            }
        }
        model.addAttribute("machinerys",machineryDTOS);

        if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
            Cooperation cooperation = cooperationService.getById(user.getIdCooperation());
            model.addAttribute("cooperation", cooperation);
            if (cooperation != null) {
                User proprieter = userService.findUser(cooperation.getIdUser());
                model.addAttribute("proprieter", proprieter);
                Address address = addressBaseService.getAddress(cooperation.getIdAddress());
                String addresstStr = addressBaseService.getAddress(address);
                model.addAttribute("address", addresstStr);
                List<User> members = cooperationService.members(user.getIdCooperation());
                model.addAttribute("members", members);
            }
        }

        Account account = accountService.findAccount(user.getIdUser());
        account.setAccountState(EnumUtil.getByState(account.getAccountState(), AccountStateEnum.class).getMessage());
        model.addAttribute("account",account);

        List<AccountLog> accountLogs = accountService.getAccountLogs(user.getIdUser());
        if (accountLogs == null) {
            accountLogs = new ArrayList<>();
        }
        if (accountLogs.size() > 0) {
            for (AccountLog accountLog : accountLogs) {
                if (accountLog.getAccountType() != null) {
                    accountLog.setAccountType(EnumUtil.getByType(accountLog.getAccountType(), AccountTypeEnum.class).getMessage());
                }
                if (accountLog.getChangeType() != null) {
                    accountLog.setChangeType(EnumUtil.getByType(accountLog.getChangeType(), AccountLogTypeEnum.class).getMessage());
                }
            }
        }
        model.addAttribute("accountLogs",accountLogs);
        return "console/user/detail";
    }

}
