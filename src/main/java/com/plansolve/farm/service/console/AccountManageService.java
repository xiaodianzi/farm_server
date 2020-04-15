package com.plansolve.farm.service.console;

import com.plansolve.farm.model.console.account.AccountLogConsoleDTO;
import com.plansolve.farm.model.database.log.AccountLog;
import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/20
 * @Description:
 */
public interface AccountManageService {

    /**
     * 获取所有支付记录数据
     * @return
     */
    public List<AccountLog> getAccountLogInfo(Integer pageNumber, Integer pageSize);

    /**
     * 获取所有支付记录条数
     * @return
     */
    public Integer countAccountLogs();

    /**
     * 条件查询支付记录列表条数
     * @return
     */
    public Page<AccountLog> findAllBySpecification(Integer pageNumber, Integer pageSize, User user, String accountLogType);

    /**
     * DTO对象转换
     * @param accountLogs
     * @return
     */
    public List<AccountLogConsoleDTO> loadAccountLogDTO(List<AccountLog> accountLogs);

}
