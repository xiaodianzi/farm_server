package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.account.AccountDTO;
import com.plansolve.farm.model.client.account.AccountLogDTO;
import com.plansolve.farm.model.database.account.Account;
import com.plansolve.farm.model.database.account.WxAccount;
import com.plansolve.farm.model.database.log.AccountLog;
import com.plansolve.farm.model.database.user.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/20
 * @Description: 用户钱包账户
 * 预计：托管之家虚拟账户、微信交易往来、银行卡交易往来
 * 用户支付订单--直接从钱包支付[一条支付记录]、
 * 第三方支付[两条支付记录-1充值记录2付款记录]
 * 记录支付成功回执（暂时第三方支付只有微信支付，支付宝与银联支付暂缓开通）
 * 手动查询支付结果接口、定制第三方账单流水接口
 * <p>
 * 用户订单收款--支付订单后，系统自动生成收款申请，初审（系统：检查金额是否正确，钱款是否到账）、复核（暂无内容，设想：物联网检查作业质量）、付款至收款人托管之家虚拟账户[一条收款记录]
 * <p>
 * 用户充值--第三方支付[一条支付记录]
 * <p>
 * 用户提现--用户提交提现申请，初审、复核、财务付款至第三方账户（暂时只有微信支付与财务手动付款至银行卡）
 * <p>
 * 用户查询--用户查询支付或提现进度，或第三方支付后，订单状态未改变，手动核查，使订单进入下一流程
 * <p>
 * 用户账单--用户可导出指定日期内，账户交易流水
 * <p>
 * 后台账单流水--系统管理员可定制各类账单
 **/
public interface AccountService {

    /**
     * 封装传输对象
     *
     * @param account
     * @return
     */
    public AccountDTO loadDTO(Account account);

    /**
     * 创建账户
     *
     * @param user 账户持有人
     * @return
     */
    public Account createAccount(User user);

    /**
     * @param wxAccount
     * @return
     */
    public WxAccount saveWxAccount(WxAccount wxAccount);

    /**
     * 冻结账户
     *
     * @param user
     * @return
     */
    public Account FrozenAccount(User user);

    /**
     * 微信收款
     *
     * @param money
     * @param idUser
     * @return
     */
    public Account wxIncome(BigDecimal money, Long idUser, String openId, Long idTransactionApplication, Long idWxPayOrderNotify);

    /**
     * 微信付款
     *
     * @param money
     * @param idUser
     * @return
     */
    public Account wxExpense(BigDecimal money, Long idUser, String openId, Long idTransactionApplication, Long idWxPayOrderNotify);

    /**
     * 收款
     *
     * @param money  入账金额
     * @param idUser 账户持有人
     * @param type
     * @return
     */
    public Account income(BigDecimal money, Long idUser, String type, Long idTransactionApplication);

    /**
     * 付款
     *
     * @param idUser 账户持有人
     * @param money  出账金额
     * @param type
     * @return
     */
    public Account expense(BigDecimal money, Long idUser, String type, Long idTransactionApplication);

    /**
     * 银行卡收款
     *
     * @param money
     * @param idUser
     * @return
     */
    public Account bankcardIncome(BigDecimal money, Long idUser, Long idBankCard, Long idTransactionApplication);

    /**
     * 银行卡付款
     *
     * @param money
     * @param idUser
     * @return
     */
    public Account bankcardExpense(BigDecimal money, Long idUser, Long idBankCard, Long idTransactionApplication);

    /**
     * 查询当前用户账户
     *
     * @param idUser
     * @return
     */
    public Account findAccount(Long idUser);

    /**
     * 查询用户微信账户
     *
     * @param idUser
     * @param openId
     * @return
     */
    public WxAccount findWxAccount(Long idUser, String openId);

    /**
     * 设置用户提现密码
     *
     * @param idUser
     * @param withdrawPassword
     * @return
     */
    public Account setWithdrawPassword(Long idUser, String withdrawPassword);

    /**
     * 查询当前用户流水
     *
     * @param user     当前用户
     * @param pageNo   页码
     * @param pageSize 页面大小
     * @return
     */
    public List<AccountLog> getAccountLogs(User user, Integer pageNo, Integer pageSize);

    /**
     * 查找指定用户流水
     *
     * @param idUser
     * @return
     */
    public List<AccountLog> getAccountLogs(Long idUser);

    /**
     * 查询当前用户不同类型的流水
     *
     * @param user
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<AccountLog> getAccountLogsByType(User user, String type, Integer pageNo, Integer pageSize);

    /**
     * 封装流水
     *
     * @param logs
     * @return
     */
    public List<AccountLogDTO> loadDTOS(List<AccountLog> logs);

}
