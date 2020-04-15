package com.plansolve.farm.service.console;

import com.plansolve.farm.model.console.account.TransactionApplicationDTO;
import com.plansolve.farm.model.console.account.WithdrawApplicationDetailDTO;
import com.plansolve.farm.model.database.account.ApplicationDetail;
import com.plansolve.farm.model.database.account.TransactionApplication;
import com.plansolve.farm.model.database.console.AdminUser;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/12
 * @Description:
 **/
public interface ConsoleTransactionApplicationService {

    /**
     * 转换传输对象
     *
     * @param transactionApplication
     * @return
     */
    public TransactionApplicationDTO loadDTO(TransactionApplication transactionApplication);

    /**
     * 转换提现申请传输对象
     * @param applicationDetails
     * @return
     */
    public List<WithdrawApplicationDetailDTO> loadWithdrawDTO(List<ApplicationDetail> applicationDetails);

    /**
     * 批量转换传输对象
     * @param transactionApplications
     * @return
     */
    public List<TransactionApplicationDTO> loadDTOS(List<TransactionApplication> transactionApplications);

    /**
     * 分页查询申请
     *
     * @param applicationType          申请类型--提现、付款
     * @param applicationState         申请进度--创建、初审、复核
     * @param transactionApplicationNo 申请编号
     * @param pageNo                   页码
     * @param pageSize                 每页条数
     * @return
     */
    public Page<TransactionApplication> findAllApplication(String applicationType, String applicationState, String transactionApplicationNo, Integer pageNo, Integer pageSize);

    /**
     * 查询指定用户申请
     *
     * @param idUser           查询用户
     * @param applicationType  申请类型--提现、付款
     * @param applicationState 申请进度--创建、初审、复核
     * @return
     */
    public List<TransactionApplication> findApplicationByUser(Long idUser, String applicationType, String applicationState);

    /**
     * 查询指定提现申请
     *
     * @param idTransactionApplication 提现申请id
     * @return
     */
    public TransactionApplication findApplicationById(Long idTransactionApplication);

    /**
     * 提现复审
     *
     * @param idTransactionApplication
     * @param adminUser                审核人
     * @param result                   审核结果
     * @param detail                   备注（可为空）
     * @return
     */
    public ApplicationDetail withdrawRecheck(Long idTransactionApplication, AdminUser adminUser, Boolean result, String detail);

}
