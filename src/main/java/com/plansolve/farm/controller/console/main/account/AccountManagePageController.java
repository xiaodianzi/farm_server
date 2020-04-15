package com.plansolve.farm.controller.console.main.account;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.model.console.account.AccountLogConsoleDTO;
import com.plansolve.farm.model.database.log.AccountLog;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.code.AdminAccountEnum;
import com.plansolve.farm.model.enums.type.AccountLogTypeEnum;
import com.plansolve.farm.model.enums.type.AccountTypeEnum;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.service.console.AccountManageService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EnumUtil;
import com.plansolve.farm.util.PagerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/3/20
 * @Description:
 **/
@Controller
@RequestMapping("/manager/app/account")
public class AccountManagePageController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountManageService accountManageService;


    /**
     * 跳转到支付记录信息页面
     *
     * @return 支付记录列表页
     */
    @RequestMapping("/cash/listPage")
    public String cashListPage() {
        return "account/cashRecordList";
    }

    /**
     * 查询资金明细列表
     *
     * @param offset   页码
     * @param limit 每页显示多少条
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cash/list")
    public PagerUtil cashList(@RequestParam(defaultValue = "0") Integer offset, @RequestParam(defaultValue = "10") Integer limit) {
        PagerUtil pager = null;
        Integer pageNumber = getPage(limit, offset);
        Integer count = accountManageService.countAccountLogs();
        List<AccountLog> accountLogs = accountManageService.getAccountLogInfo(pageNumber, limit);
        if (accountLogs.size() > 0) {
            List<AccountLogConsoleDTO> accountLogList = new ArrayList<>();
            for (AccountLog accountLog : accountLogs) {
                AccountLogConsoleDTO accountLogConsoleDTO = new AccountLogConsoleDTO();
                accountLogConsoleDTO.setIdAccountLog(accountLog.getIdAccountLog());
                if (null != accountLog.getIdUser()) {
                    if (accountLog.getIdUser()>0){
                        User user = userService.findUser(accountLog.getIdUser());
                        accountLogConsoleDTO.setMobile(user.getMobile());
                    }else if(accountLog.getIdUser()==0){
                        accountLogConsoleDTO.setMobile(AdminAccountEnum.PLATFORM_USER_ACCOUNT.getMessage());
                    }else{
                        accountLogConsoleDTO.setMobile(AdminAccountEnum.PLATFORM_Fill.getMessage());
                    }
                    //用户id同时也是账户id
                    accountLogConsoleDTO.setIdUser(accountLog.getIdUser());
                }
                if (StringUtils.isNotBlank(accountLog.getAccountType())){
                    String accountType = EnumUtil.getByType(accountLog.getAccountType(), AccountTypeEnum.class).getMessage();
                    accountLogConsoleDTO.setAccountType(accountType);
                }
                if (StringUtils.isNotBlank(accountLog.getChangeType())){
                    String changeType = EnumUtil.getByType(accountLog.getChangeType(), AccountLogTypeEnum.class).getMessage();
                    accountLogConsoleDTO.setChangeType(changeType);
                }
                accountLogConsoleDTO.setAmount(accountLog.getChangeNum());
                accountLogConsoleDTO.setChangeTime(DateUtils.formatDateTime(accountLog.getChangeTime()));
                accountLogList.add(accountLogConsoleDTO);
            }
            pager = new PagerUtil(pageNumber, limit, count, accountLogList);
        } else {

            pager = new PagerUtil(pageNumber, limit, count, null);
        }
        return pager;
    }

    /**
     * 按条件查询资金明细信息
     * @param limit 每页条数
     * @param offset 页码
     * @param mobile 手机号
     * @param accountLogType 资金明细类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cash/conditions/list")
    public PageDTO<AccountLogConsoleDTO> cashList(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, String mobile, String accountLogType) {
        PageDTO<AccountLogConsoleDTO> pageDTO = new PageDTO<>();
        Integer pageNumber = getPage(limit, offset);
        User user = null;
        if (StringUtils.isNotBlank(mobile)){
            user = userService.findByMobile(mobile);
        }
        Page<AccountLog> accountLogPage = accountManageService.findAllBySpecification(pageNumber, limit, user, accountLogType);
        List<AccountLog> accountLogs = accountLogPage.getContent();
        List<AccountLogConsoleDTO> accountLogList = accountManageService.loadAccountLogDTO(accountLogs);
        pageDTO.setTotal(accountLogPage.getTotalElements());
        pageDTO.setRows(accountLogList);
        return pageDTO;
    }

}
