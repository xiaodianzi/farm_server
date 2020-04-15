package com.plansolve.farm.service.client.Impl;

import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.model.client.account.ApplicationDetailDTO;
import com.plansolve.farm.model.client.account.BankCardDTO;
import com.plansolve.farm.model.client.account.TransactionApplicationDTO;
import com.plansolve.farm.model.database.account.*;
import com.plansolve.farm.model.database.console.AdminUser;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.order.WxPayOrderNotify;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.BankCardStateEnum;
import com.plansolve.farm.model.enums.state.TransactionApplicationStateEnum;
import com.plansolve.farm.model.enums.type.AccountLogTypeEnum;
import com.plansolve.farm.model.enums.type.AccountTypeEnum;
import com.plansolve.farm.model.enums.type.ApplicationTypeEnum;
import com.plansolve.farm.model.enums.type.TransactionApplicationTypeEnum;
import com.plansolve.farm.model.enums.state.WxPayOrderStateEnum;
import com.plansolve.farm.repository.account.ApplicationDetailRepository;
import com.plansolve.farm.repository.account.TransactionApplicationRepository;
import com.plansolve.farm.repository.account.TransactionDetailRepository;
import com.plansolve.farm.repository.order.WxPayOrderNotifyRepository;
import com.plansolve.farm.service.client.AccountService;
import com.plansolve.farm.service.client.BankCardService;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.service.client.TransactionApplicationService;
import com.plansolve.farm.service.console.AdminUserService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EnumUtil;
import com.plansolve.farm.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/25
 * @Description:
 **/
@Service
public class TransactionApplicationServiceImpl implements TransactionApplicationService {

    @Autowired
    private TransactionApplicationRepository applicationRepository;
    @Autowired
    private ApplicationDetailRepository applicationDetailRepository;
    @Autowired
    private WxPayOrderNotifyRepository notifyRepository;
    @Autowired
    private TransactionDetailRepository transactionDetailRepository;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private AdminUserService adminUserService;

    /**
     * 根据申请编号查询申请
     *
     * @param transactionApplicationNo
     * @return
     */
    @Override
    public TransactionApplication findTransactionApplication(String transactionApplicationNo) {
        return applicationRepository.findByTransactionApplicationNo(transactionApplicationNo);
    }

    /**
     * 根据申请编号查询申请详细信息
     *
     * @param transactionApplicationNo
     * @return
     */
    @Override
    public List<ApplicationDetail> findApplicationDetails(String transactionApplicationNo) {
        TransactionApplication application = findTransactionApplication(transactionApplicationNo);
        List<ApplicationDetail> details = applicationDetailRepository.findByIdTransactionApplication(application.getIdTransactionApplication());
        return details;
    }

    /**
     * 封装详情
     *
     * @param details
     * @return
     */
    @Override
    public List<ApplicationDetailDTO> loadDetailDTOS(List<ApplicationDetail> details) {
        List<ApplicationDetailDTO> dtos = new ArrayList<>();
        if (details != null && details.size() > 0) {
            for (ApplicationDetail detail : details) {
                ApplicationDetailDTO dto = new ApplicationDetailDTO();
                BeanUtils.copyProperties(detail, dto);
                TransactionApplication application = findTransactionApplication(detail.getIdTransactionApplication());
                dto.setTransactionApplicationNo(application.getTransactionApplicationNo());
                if (detail.getIdAdmin() != null && detail.getIdAdmin() > 0) {
                    AdminUser user = adminUserService.findUser(detail.getIdAdmin());
                    if (user != null) {
                        dto.setAdminName(user.getUsername());
                        dto.setAdminMobile(user.getMobile());
                    }
                }
                dtos.add(dto);
            }
        }
        return dtos;
    }

    /**
     * 查找申请
     *
     * @param idTransactionApplication
     * @return
     */
    public TransactionApplication findTransactionApplication(Long idTransactionApplication) {
        TransactionApplication application = applicationRepository.findByIdTransactionApplication(idTransactionApplication);
        return application;
    }

    /**
     * 根据用户查找用户所有申请
     *
     * @param user
     * @return
     */
    @Override
    public List<TransactionApplication> findUserTransactionApplications(User user, String applicationType) {
        Sort sort = new Sort(Sort.Direction.DESC, "idTransactionApplication");
        return applicationRepository.findByIdUserAndApplicationType(user.getIdUser(), applicationType, sort);
    }

    /**
     * 转化传输对象
     *
     * @param transactionApplication
     * @return
     */
    @Override
    public TransactionApplicationDTO loadDTO(TransactionApplication transactionApplication) {
        TransactionApplicationDTO dto = new TransactionApplicationDTO();
        BeanUtils.copyProperties(transactionApplication, dto);
        dto.setCreateTime(DateUtils.formatDateTime(transactionApplication.getApplicationCreateTime()));
        if (transactionApplication.getIdUserOrder() != null && transactionApplication.getIdUserOrder() > 0) {
            UserOrder order = orderService.getUserOrder(transactionApplication.getIdUserOrder());
            dto.setUserOrderNo(order.getUserOrderNo());
        }
        if (transactionApplication.getIdBankCard() != null && transactionApplication.getIdBankCard() > 0) {
            BankCard bankCard = bankCardService.findBankCard(transactionApplication.getIdBankCard());
            BankCardDTO cardDTO = bankCardService.loadDTO(bankCard);
            dto.setBankCard(cardDTO);
        }
        if (transactionApplication.getApplicationState().equals(TransactionApplicationStateEnum.RECHECK_PASS.getState())
                || transactionApplication.getApplicationState().equals(TransactionApplicationStateEnum.REMITTANCE.getState())) {
            dto.setApplicationState("success");
        } else if (transactionApplication.getApplicationState().equals(TransactionApplicationStateEnum.CHECK_FAIL.getState())
                || transactionApplication.getApplicationState().equals(TransactionApplicationStateEnum.RECHECK_FAIL.getState())
                || transactionApplication.getApplicationState().equals(TransactionApplicationStateEnum.FROZEN.getState())) {
            dto.setApplicationState("fail");
        } else {
            dto.setApplicationState("wait");
        }
        return dto;
    }

    /**
     * 批量转化传输对象
     *
     * @param transactionApplications
     * @return
     */
    @Override
    public List<TransactionApplicationDTO> loadDTOS(List<TransactionApplication> transactionApplications) {
        List<TransactionApplicationDTO> dtos = new ArrayList<>();
        if (transactionApplications != null && transactionApplications.size() > 0) {
            for (TransactionApplication transactionApplication : transactionApplications) {
                TransactionApplicationDTO dto = loadDTO(transactionApplication);
                dtos.add(dto);
            }
        }
        return dtos;
    }

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
    @Override
    @Transactional
    public TransactionApplication create(User user, String applicationType, Long idUserOrder, Long idBankCard, BigDecimal money) {
        // 若订单ID不为空，需先检验是否已有该申请
        TransactionApplication application;
        if (!applicationType.equals(TransactionApplicationTypeEnum.WITHDRAW.getType())) {
            application = applicationRepository.findByIdUserOrder(idUserOrder);
            if (application == null) {
                application = new TransactionApplication();
            }
            application.setIdUserOrder(idUserOrder);
        } else {
            application = new TransactionApplication();
            application.setIdBankCard(idBankCard);
        }
        application.setMoney(money);
        application.setTransactionApplicationNo(getTransactionApplicationNo());
        application.setIdUser(user.getIdUser());
        application.setApplicationType(applicationType);
        application.setApplicationState(TransactionApplicationStateEnum.CREATE.getState());
        Date date = new Date();
        application.setApplicationCreateTime(date);
        application.setApplicationUpdateTime(date);
        application = applicationRepository.save(application);
        return application;
    }

    /**
     * 微信付款初审
     *
     * @param userOrder       订单
     * @param transactionType 申请类型
     * @param transactionId   平台订单号
     * @return
     */
    @Override
    @Transactional
    public ApplicationDetail paymentCheck(UserOrder userOrder, String transactionType, String transactionId) {
        if (userOrder == null) {
            throw new NullParamException("[订单不可为空]");
        } else {
            TransactionApplication application = applicationRepository.findByIdUserOrder(userOrder.getIdUserOrder());
            if (application == null) {
                throw new NullParamException("[无法找到该申请]");
            } else {
                if (transactionType.equals(TransactionApplicationTypeEnum.WX_PAYMENT.getType())) {
                    // 若为微信支付，须有微信交易订单号
                    if (transactionId == null || transactionId.isEmpty()) {
                        // 订单号为空，初审失败
                        ApplicationDetail applicationDetail = createCheckDetail(application, transactionType, new BigDecimal(0), false, null, "微信支付交易号为空");
                        return applicationDetail;
                    } else {
                        // 查询微信支付数额，初审成功
                        try {
                            WxPayOrderQueryResult result = wxPayService.queryOrder(null, userOrder.getUserOrderNo());
                            // 保存返回内容
                            WxPayOrderNotify notify = notifyRepository.findByTransactionId(result.getTransactionId());
                            if (notify == null) {
                                notify = new WxPayOrderNotify();
                                BeanUtils.copyProperties(result, notify);
                            }
                            notify.setTradeType(result.getTradeType());
                            notify.setTradeStateDesc(result.getTradeStateDesc());
                            notifyRepository.save(notify);

                            BigDecimal money = new BigDecimal(result.getTotalFee()).divide(new BigDecimal(100));
                            /*// 校验支付金额
                            if (!MathUtil.equals(result.getOrderAmount(), order.getOrderAmount().doubleValue())) {
                                log.error("【微信支付】异步通知，订单金额不一致，orderId={}，微信通知金额={}，系统金额={}",
                                        payResponse.getOrderId(), payResponse.getOrderAmount(), orderDTO.getOrderAmount());
                                throw new SellException(ResultEnum.WX_PAY_NOTIFY_MONEY_VERIFY_ERROR);
                            }*/
                            ApplicationDetail applicationDetail;
                            if (result.getTradeState().equals(WxPayOrderStateEnum.SUCCESS.getState())) {
                                // 微信支付成功
                                applicationDetail = createCheckDetail(application, transactionType, money, true, transactionId, "微信支付成功-订单金额：" + result.getTotalFee() + "，应结订单金额：" + result.getSettlementTotalFee());
                                /************************************************用户付款************************************************/
                                // 1、用户微信账户增加出账记录
                                accountService.wxExpense(money, userOrder.getCreateBy(), notify.getOpenid(), application.getIdTransactionApplication(), notify.getIdWxPayOrderNotify());
                                // 2、用户平台账户增加充值记录
                                accountService.income(money, userOrder.getCreateBy(), AccountLogTypeEnum.RECHARGE.getType(), application.getIdTransactionApplication());
                                // 3、用户平台账户增加付款记录
                                accountService.expense(money, userOrder.getCreateBy(), AccountLogTypeEnum.EXPENSE.getType(), application.getIdTransactionApplication());

                                // 新增交易流水一条 TODO
                                // 1、用户微信账户充值入用户平台账户
                                // 2、用户平台账户支付金额至平台中转账户
                                TransactionDetail transactionDetail = new TransactionDetail();
                                transactionDetail.setIdUser(application.getIdUser());
                                transactionDetail.setIdTransactionApplication(application.getIdTransactionApplication());
                                transactionDetail.setTransactionType(TransactionApplicationTypeEnum.WX_PAYMENT.getType());
                                transactionDetail.setTransactionMoney(money);
                                transactionDetail.setTransactionTime(new Date());
                                transactionDetail.setIdUserExpense(application.getIdUser());
                                transactionDetail.setExpenseAccountType(AccountTypeEnum.WECHAT.getType());
                                transactionDetail.setIdUserIncome(application.getIdUser());
                                transactionDetail.setIncomeAccountType(AccountTypeEnum.WALLET.getType());
                                transactionDetail.setIdOrder(userOrder.getIdUserOrder());
                                transactionDetail.setWxTransactionId(transactionId);
                                transactionDetail.setIdWxPayOrderNotify(notify.getIdWxPayOrderNotify());
                                transactionDetailRepository.save(transactionDetail);
                                /************************************************用户付款************************************************/
                            } else {
                                WxPayOrderStateEnum stateEnum = EnumUtil.getByState(result.getTradeState(), WxPayOrderStateEnum.class);
                                applicationDetail = createCheckDetail(application, transactionType, money, false, transactionId, "微信支付未完成-[" + stateEnum.getMessage() + "]");
                            }
                            return applicationDetail;
                        } catch (WxPayException e) {
                            // 初审失败
                            ApplicationDetail applicationDetail = createCheckDetail(application, transactionType, new BigDecimal(0), false, null, "无法在微信平台查出支付信息");
                            e.printStackTrace();
                            return applicationDetail;
                        }
                    }
                } else {
                    if (transactionType.equals(TransactionApplicationTypeEnum.WITHDRAW.getType())) {
                        return withdrawCheck(application.getIdTransactionApplication());
                    } else {
                        // 余额支付 暂缓开通
                        return null;
                    }
                }
            }
        }
    }

    /**
     * 微信付款复核
     *
     * @param userOrder       订单
     * @param transactionType 申请类型
     * @return
     */
    @Override
    @Transactional
    public ApplicationDetail paymentRecheck(UserOrder userOrder, String transactionType) {
        if (userOrder == null) {
            throw new NullParamException("[订单不可为空]");
        } else {
            TransactionApplication application = applicationRepository.findByIdUserOrder(userOrder.getIdUserOrder());
            if (application == null) {
                throw new NullParamException("[无法找到该申请]");
            } else {
                BigDecimal money = application.getMoney();
                ApplicationDetail applicationDetail = createRecheckDetail(application, transactionType, money, true, "暂无复核内容");
                // 账户付款-付款给收款人
                accountService.income(userOrder.getAmountPayable(), userOrder.getReceiveBy(), AccountLogTypeEnum.INCOME.getType(), application.getIdTransactionApplication());

                // 新增交易流水一条 TODO
                // 平台中转账户支付金额至用户平台账户
                TransactionDetail transactionDetail = new TransactionDetail();
                transactionDetail.setIdUser(application.getIdUser());
                transactionDetail.setIdTransactionApplication(application.getIdTransactionApplication());
                transactionDetail.setTransactionType(TransactionApplicationTypeEnum.PAYMENT.getType());
                transactionDetail.setTransactionMoney(money);
                transactionDetail.setTransactionTime(new Date());
                transactionDetail.setIdUserExpense(userOrder.getCreateBy());
                transactionDetail.setExpenseAccountType(AccountTypeEnum.WALLET.getType());
                transactionDetail.setIdUserIncome(userOrder.getReceiveBy());
                transactionDetail.setIncomeAccountType(AccountTypeEnum.WALLET.getType());
                transactionDetail.setIdOrder(userOrder.getIdUserOrder());
                transactionDetailRepository.save(transactionDetail);
                return applicationDetail;
            }
        }
    }

    /**
     * 提现初审
     *
     * @param idTransactionApplication
     * @return
     */
    @Override
    @Transactional
    public ApplicationDetail withdrawCheck(Long idTransactionApplication) {
        TransactionApplication application = findTransactionApplication(idTransactionApplication);
        if (application == null) {
            throw new NullParamException("[无法找到该申请]");
        } else {
            if (application.getApplicationType().equals(TransactionApplicationTypeEnum.WITHDRAW.getType())) {
                // 校验用户账户是否具有该数额的金钱
                Account account = accountService.findAccount(application.getIdUser());
                ApplicationDetail applicationDetail;
                if (account.getAccountBalance().compareTo(application.getMoney()) > -1) { // 该用户账户具有此金额
                    // 该用户是否提交正确银行卡
                    if (application.getIdBankCard() != null && application.getIdBankCard() > 0) {
                        BankCard bankCard = bankCardService.findBankCard(application.getIdBankCard());
                        if (bankCard != null && bankCard.getBankCardState().equals(BankCardStateEnum.NORMAL.getState())) {
                            // 提现成功
                            applicationDetail = createCheckDetail(application, TransactionApplicationTypeEnum.WITHDRAW.getType(), application.getMoney(), true, null, "提现初审通过");
                            // 用户平台账户付款
                            accountService.expense(application.getMoney(), application.getIdUser(), AccountLogTypeEnum.WITHDRAW.getType(), application.getIdTransactionApplication());

                            // 新增交易流水一条 TODO
                            // 用户平台账户支付金额至平台中转账户
                        } else {
                            applicationDetail = createCheckDetail(application, TransactionApplicationTypeEnum.WITHDRAW.getType(), application.getMoney(), false, null, "未找到给用户有效银行卡");
                        }
                    } else {
                        applicationDetail = createCheckDetail(application, TransactionApplicationTypeEnum.WITHDRAW.getType(), application.getMoney(), false, null, "该用户银行卡信息为空，请重新申请");
                    }
                } else {
                    // 提现失败
                    applicationDetail = createCheckDetail(application, TransactionApplicationTypeEnum.WITHDRAW.getType(), application.getMoney(), false, null, "该用户余额不足");
                }
                return applicationDetail;
            } else {
                // 该申请不是提现申请
                UserOrder userOrder = orderService.getUserOrder(application.getIdUserOrder());
                return paymentCheck(userOrder, application.getApplicationType(), application.getTransactionApplicationNo());
            }
        }
    }

    /**
     * 提现复审
     *
     * @param idTransactionApplication
     * @param adminUser                审核人
     * @param result                   审核结果
     * @param detail                   备注（可为空）
     * @return
     */
    @Override
    @Transactional
    public ApplicationDetail withdrawRecheck(Long idTransactionApplication, AdminUser adminUser, Boolean result, String detail) {
        TransactionApplication application = findTransactionApplication(idTransactionApplication);
        if (application == null) {
            throw new NullParamException("[无法找到该申请]");
        } else {
            ApplicationDetail applicationDetail = createRecheckDetail(application, TransactionApplicationTypeEnum.WITHDRAW.getType(), application.getMoney(), result, detail);
            if (result) {
                // 用户银行卡账户收款
                accountService.bankcardIncome(application.getMoney(), application.getIdUser(), application.getIdBankCard(), application.getIdTransactionApplication());

                // 新增交易流水一条 TODO
                // 平台中转账户支付金额至用户银行卡账户
                TransactionDetail transactionDetail = new TransactionDetail();
                transactionDetail.setIdUser(application.getIdUser());
                transactionDetail.setIdTransactionApplication(application.getIdTransactionApplication());
                transactionDetail.setTransactionType(TransactionApplicationTypeEnum.WITHDRAW.getType());
                transactionDetail.setTransactionMoney(application.getMoney());
                transactionDetail.setTransactionTime(new Date());
                transactionDetail.setIdUserIncome(application.getIdUser());
                transactionDetail.setIncomeAccountType(AccountTypeEnum.BANKCARD.getType());
                transactionDetail.setBankCardIncomeId(application.getIdBankCard());
                BankCard bankCard = bankCardService.findBankCard(application.getIdBankCard());
                transactionDetail.setBankCardIncomeNo(bankCard.getBankCardNo());
                transactionDetail.setIdUserExpense(application.getIdUser());
                transactionDetail.setExpenseAccountType(AccountTypeEnum.WALLET.getType());
                transactionDetailRepository.save(transactionDetail);
            }
            return applicationDetail;
        }
    }


    /**
     * 创建初审详情
     *
     * @param application
     * @param transactionType
     * @param money
     * @param result
     * @param transactionId
     * @param detail
     * @return
     */
    private ApplicationDetail createCheckDetail(TransactionApplication application, String transactionType, BigDecimal money,
                                                Boolean result, String transactionId, String detail) {
        ApplicationDetail applicationDetail = new ApplicationDetail();
        applicationDetail.setIdTransactionApplication(application.getIdTransactionApplication());
        applicationDetail.setTransactionType(transactionType);
        applicationDetail.setApplicationType(ApplicationTypeEnum.CHECK.getType());
        applicationDetail.setApplicationMoney(money);
        applicationDetail.setApplicationTime(new Date());

        applicationDetail.setApplicationResult(result);
        if (transactionId != null && transactionId.length() > 0) {
            applicationDetail.setTransactionId(transactionId);
        }
        applicationDetail.setApplicationDetail(detail);
        applicationDetailRepository.save(applicationDetail);

        // 申请信息状态更改
        if (result) {
            application.setApplicationState(TransactionApplicationStateEnum.CHECK_PASS.getState());
        } else {
            application.setApplicationState(TransactionApplicationStateEnum.CHECK_FAIL.getState());
        }
        application.setApplicationUpdateTime(new Date());
        applicationRepository.save(application);
        return applicationDetail;
    }

    /**
     * 创建复核详情
     *
     * @param application
     * @param transactionType
     * @param money
     * @param result
     * @param detail
     * @return
     */
    private ApplicationDetail createRecheckDetail(TransactionApplication application, String transactionType, BigDecimal money,
                                                  Boolean result, String detail) {
        ApplicationDetail applicationDetail = new ApplicationDetail();
        applicationDetail.setIdTransactionApplication(application.getIdTransactionApplication());
        applicationDetail.setTransactionType(transactionType);
        applicationDetail.setApplicationType(ApplicationTypeEnum.RECHECK.getType());
        applicationDetail.setApplicationMoney(money);
        applicationDetail.setApplicationTime(new Date());

        applicationDetail.setApplicationResult(result);
        applicationDetail.setApplicationDetail(detail);
        applicationDetailRepository.save(applicationDetail);

        // 申请信息状态更改
        if (result) {
            application.setApplicationState(TransactionApplicationStateEnum.RECHECK_PASS.getState());
        } else {
            application.setApplicationState(TransactionApplicationStateEnum.RECHECK_FAIL.getState());
        }
        application.setApplicationUpdateTime(new Date());
        applicationRepository.save(application);
        return applicationDetail;
    }

    /**
     * 获取申请编号
     *
     * @return
     */
    private String getTransactionApplicationNo() {
        String transactionApplicationNo = String.valueOf(Math.round(Math.random() * 1000000));
        transactionApplicationNo = StringUtil.prefixStr(transactionApplicationNo, 6, "0");
        transactionApplicationNo = DateUtils.getDate("yyyy/MM/dd").replace("/", "").substring(2) + transactionApplicationNo;
        return transactionApplicationNo;
    }
}
