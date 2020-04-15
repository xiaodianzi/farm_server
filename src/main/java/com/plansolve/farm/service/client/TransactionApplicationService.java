package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.account.ApplicationDetailDTO;
import com.plansolve.farm.model.client.account.TransactionApplicationDTO;
import com.plansolve.farm.model.database.account.ApplicationDetail;
import com.plansolve.farm.model.database.account.TransactionApplication;
import com.plansolve.farm.model.database.console.AdminUser;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/25
 * @Description: 交易申请
 **/
public interface TransactionApplicationService {

    /**
     * 根据申请编号查询申请
     *
     * @param transactionApplicationNo
     * @return
     */
    public TransactionApplication findTransactionApplication(String transactionApplicationNo);

    /**
     * 根据申请编号查询申请详细信息
     *
     * @param transactionApplicationNo
     * @return
     */
    public List<ApplicationDetail> findApplicationDetails(String transactionApplicationNo);

    /**
     * 封装详情
     *
     * @param details
     * @return
     */
    public List<ApplicationDetailDTO> loadDetailDTOS(List<ApplicationDetail> details);

    /**
     * 根据ID查询申请详情
     *
     * @param idTransactionApplication
     * @return
     */
    public TransactionApplication findTransactionApplication(Long idTransactionApplication);

    /**
     * 根据用户查找用户所有申请
     *
     * @param user
     * @return
     */
    public List<TransactionApplication> findUserTransactionApplications(User user, String applicationType);

    /**
     * 转化传输对象
     *
     * @param transactionApplication
     * @return
     */
    public TransactionApplicationDTO loadDTO(TransactionApplication transactionApplication);

    /**
     * 批量转化传输对象
     *
     * @param transactionApplications
     * @return
     */
    public List<TransactionApplicationDTO> loadDTOS(List<TransactionApplication> transactionApplications);

    /**
     * 创建申请
     *
     * @param user            申请人
     * @param applicationType 申请类型
     * @param idUserOrder     相关订单号码（可为空）
     * @param idBankCard      相关银行卡号码（可为空）
     * @param money           申请金额（可为空）
     * @return
     */
    public TransactionApplication create(User user, String applicationType, Long idUserOrder, Long idBankCard, BigDecimal money);

    /**
     * 付款初审
     *
     * @param userOrder       订单
     * @param transactionType 申请类型
     * @param transactionId   平台订单号
     * @return
     */
    public ApplicationDetail paymentCheck(UserOrder userOrder, String transactionType, String transactionId);

    /**
     * 付款复核
     *
     * @param userOrder       订单
     * @param transactionType 申请类型
     * @return
     */
    public ApplicationDetail paymentRecheck(UserOrder userOrder, String transactionType);

    /**
     * 提现初审
     *
     * @param idTransactionApplication
     * @return
     */
    public ApplicationDetail withdrawCheck(Long idTransactionApplication);

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
