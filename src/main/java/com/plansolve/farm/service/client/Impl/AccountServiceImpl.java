package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.model.client.account.AccountDTO;
import com.plansolve.farm.model.client.account.AccountLogDTO;
import com.plansolve.farm.model.database.account.Account;
import com.plansolve.farm.model.database.account.BankCard;
import com.plansolve.farm.model.database.account.WxAccount;
import com.plansolve.farm.model.database.log.AccountLog;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.code.AdminAccountEnum;
import com.plansolve.farm.model.enums.type.AccountLogTypeEnum;
import com.plansolve.farm.model.enums.state.AccountStateEnum;
import com.plansolve.farm.model.enums.type.AccountTypeEnum;
import com.plansolve.farm.repository.account.AccountRepository;
import com.plansolve.farm.repository.account.WxAccountRepository;
import com.plansolve.farm.repository.log.AccountLogRepository;
import com.plansolve.farm.service.client.AccountService;
import com.plansolve.farm.service.client.BankCardService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EncryptUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/20
 * @Description:
 **/
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WxAccountRepository wxAccountRepository;
    @Autowired
    private AccountLogRepository logRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BankCardService bankCardService;

    /**
     * 封装传输对象
     *
     * @param account
     * @return
     */
    @Override
    public AccountDTO loadDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        BeanUtils.copyProperties(account, dto);
        User user = userService.findUser(account.getIdUser());
        dto.setUserMobile(user.getMobile());
        return dto;
    }

    /**
     * 创建账户
     *
     * @param user 账户持有人
     * @return
     */
    @Override
    @Transactional
    public Account createAccount(User user) {
        if (user != null) {
            Account account = new Account();
            account.setIdUser(user.getIdUser());
            account.setAccountBalance(new BigDecimal(0));
            account.setAccountState(AccountStateEnum.NORMAL.getState());
            account.setPasswordSetting(false);
            Date date = new Date();
            account.setAccountOpeningTime(date);
            account.setAccountUpdateTime(date);
            account.setAccountBalanceChangeTime(date);
            account = accountRepository.save(account);

            // 生成日志
            AccountLog log = new AccountLog();
            log.setIdUser(user.getIdUser());
            log.setChangeTime(date);
            log.setChangeType(AccountLogTypeEnum.CREATE.getType());
            log.setChangeNum(new BigDecimal(0));
            log.setBeforeAccountBalance(new BigDecimal(0));
            log.setAfterAccountBalance(new BigDecimal(0));
            logRepository.save(log);
            return account;
        } else {
            throw new NullParamException("[无法获取当前用户]");
        }
    }

    /**
     * @param wxAccount
     * @return
     */
    @Override
    public WxAccount saveWxAccount(WxAccount wxAccount) {
        return wxAccountRepository.save(wxAccount);
    }

    /**
     * 冻结账户
     *
     * @param user
     * @return
     */
    @Override
    @Transactional
    public Account FrozenAccount(User user) {
        if (user != null) {
            Account account = findAccount(user.getIdUser());
            account.setAccountState(AccountStateEnum.FROZEN.getState());
            Date date = new Date();
            account.setAccountUpdateTime(date);
            account = accountRepository.save(account);

            // 生成日志
            AccountLog log = new AccountLog();
            log.setIdUser(user.getIdUser());
            log.setChangeTime(date);
            log.setChangeType(AccountLogTypeEnum.FROZEN.getType());
            log.setChangeNum(new BigDecimal(0));
            log.setBeforeAccountBalance(account.getAccountBalance());
            log.setAfterAccountBalance(account.getAccountBalance());
            logRepository.save(log);
            return account;
        } else {
            throw new NullParamException("[无法获取当前用户]");
        }
    }

    /**
     * 微信收款
     *
     * @param money
     * @param idUser
     * @return
     */
    @Override
    public Account wxIncome(BigDecimal money, Long idUser, String openId, Long idTransactionApplication, Long idWxPayOrderNotify) {
        return null;
    }

    /**
     * 微信付款
     *
     * @param money
     * @param idUser
     * @return
     */
    @Override
    public Account wxExpense(BigDecimal money, Long idUser, String openId, Long idTransactionApplication, Long idWxPayOrderNotify) {
        // 1、用户微信账户增加付款记录
        WxAccount wxAccount = findWxAccount(idUser, openId);
        Account account = findAccount(idUser);

        // 更新日志
        AccountLog log = new AccountLog();
        log.setIdUser(idUser);
        log.setAccountType(AccountTypeEnum.WECHAT.getType());
        log.setChangeTime(new Date());
        log.setChangeType(AccountLogTypeEnum.WX_EXPENSE.getType());
        log.setChangeNum(money.multiply(new BigDecimal(-1)));
        log.setIdTransactionApplication(idTransactionApplication);
        log.setOpenId(wxAccount.getOpenId());
        log.setIdWxAccount(wxAccount.getIdWxAccount());
        log.setIdWxPayOrderNotify(idWxPayOrderNotify);
        logRepository.save(log);

        // 2、平台账户增加收款记录
        Account platform = getPlatform(AdminAccountEnum.PLATFORM_USER_ACCOUNT.getCode());
        BigDecimal beforePlatform = platform.getAccountBalance();
        platform = income(platform, money);

        // 更新日志
        AccountLog platformLog = new AccountLog();
        platformLog.setIdUser(AdminAccountEnum.PLATFORM_USER_ACCOUNT.getCode().longValue());
        platformLog.setAccountType(AccountTypeEnum.WALLET.getType());
        platformLog.setChangeTime(new Date());
        platformLog.setChangeType(AccountLogTypeEnum.INCOME.getType());
        platformLog.setChangeNum(money);
        platformLog.setBeforeAccountBalance(beforePlatform);
        platformLog.setAfterAccountBalance(platform.getAccountBalance());
        platformLog.setIdTransactionApplication(idTransactionApplication);
        logRepository.save(platformLog);
        return account;
    }

    /**
     * 收款
     *
     * @param money  入账金额
     * @param idUser 账户持有人
     * @param type
     * @return
     */
    @Override
    @Transactional
    public Account income(BigDecimal money, Long idUser, String type, Long idTransactionApplication) {
        // 1、平台钱包账户增加付款记录
        Account platform = getPlatform(AdminAccountEnum.PLATFORM_USER_ACCOUNT.getCode());
        BigDecimal beforePlatform = platform.getAccountBalance();
        platform = expense(platform, money);

        // 更新日志
        AccountLog platformLog = new AccountLog();
        platformLog.setIdUser(AdminAccountEnum.PLATFORM_USER_ACCOUNT.getCode().longValue());
        platformLog.setAccountType(AccountTypeEnum.WALLET.getType());
        platformLog.setChangeTime(new Date());
        platformLog.setChangeType(AccountLogTypeEnum.EXPENSE.getType());
        platformLog.setChangeNum(money.multiply(new BigDecimal(-1)));
        platformLog.setBeforeAccountBalance(beforePlatform);
        platformLog.setAfterAccountBalance(platform.getAccountBalance());
        platformLog.setIdTransactionApplication(idTransactionApplication);
        logRepository.save(platformLog);

        // 2、用户钱包账户增加收款记录
        Account account = findAccount(idUser);
        BigDecimal before = account.getAccountBalance();
        account = income(account, money);

        // 更新日志
        AccountLog log = new AccountLog();
        log.setIdUser(idUser);
        log.setAccountType(AccountTypeEnum.WALLET.getType());
        log.setChangeTime(new Date());
        log.setChangeType(type);
        log.setChangeNum(money);
        log.setBeforeAccountBalance(before);
        log.setAfterAccountBalance(account.getAccountBalance());
        log.setIdTransactionApplication(idTransactionApplication);
        logRepository.save(log);
        return account;
    }

    /**
     * 付款
     *
     * @param idUser 账户持有人
     * @param money  出账金额
     * @param type
     * @return
     */
    @Override
    @Transactional
    public Account expense(BigDecimal money, Long idUser, String type, Long idTransactionApplication) {
        // 1、用户钱包账户增加付款记录
        Account account = findAccount(idUser);
        BigDecimal before = account.getAccountBalance();
        account = expense(account, money);

        // 更新日志
        AccountLog log = new AccountLog();
        log.setIdUser(idUser);
        log.setAccountType(AccountTypeEnum.WALLET.getType());
        log.setChangeTime(new Date());
        log.setChangeType(type);
        log.setChangeNum(money.multiply(new BigDecimal(-1)));
        log.setBeforeAccountBalance(before);
        log.setAfterAccountBalance(account.getAccountBalance());
        log.setIdTransactionApplication(idTransactionApplication);
        logRepository.save(log);


        // 2、平台钱包账户增加收款记录
        Account platform = getPlatform(AdminAccountEnum.PLATFORM_USER_ACCOUNT.getCode());
        BigDecimal beforePlatform = platform.getAccountBalance();
        platform = income(platform, money);

        // 更新日志
        AccountLog platformLog = new AccountLog();
        platformLog.setIdUser(AdminAccountEnum.PLATFORM_USER_ACCOUNT.getCode().longValue());
        platformLog.setAccountType(AccountTypeEnum.WALLET.getType());
        platformLog.setChangeTime(new Date());
        platformLog.setChangeType(AccountLogTypeEnum.INCOME.getType());
        platformLog.setChangeNum(money);
        platformLog.setBeforeAccountBalance(beforePlatform);
        platformLog.setAfterAccountBalance(platform.getAccountBalance());
        platformLog.setIdTransactionApplication(idTransactionApplication);
        logRepository.save(platformLog);
        return account;
    }

    /**
     * 银行卡收款
     *
     * @param money
     * @param idUser
     * @return
     */
    @Override
    public Account bankcardIncome(BigDecimal money, Long idUser, Long idBankCard, Long idTransactionApplication) {
        // 1、平台账户增加付款记录
        Account platform = getPlatform(AdminAccountEnum.PLATFORM_USER_ACCOUNT.getCode());
        BigDecimal beforePlatform = platform.getAccountBalance();
        platform = expense(platform, money);

        // 更新日志
        AccountLog platformLog = new AccountLog();
        platformLog.setIdUser(AdminAccountEnum.PLATFORM_USER_ACCOUNT.getCode().longValue());
        platformLog.setAccountType(AccountTypeEnum.WALLET.getType());
        platformLog.setChangeTime(new Date());
        platformLog.setChangeType(AccountLogTypeEnum.EXPENSE.getType());
        platformLog.setChangeNum(money.multiply(new BigDecimal(-1)));
        platformLog.setBeforeAccountBalance(beforePlatform);
        platformLog.setAfterAccountBalance(platform.getAccountBalance());
        platformLog.setIdTransactionApplication(idTransactionApplication);
        logRepository.save(platformLog);

        // 2、用户银行卡账户增加收款记录
        BankCard bankCard = bankCardService.findBankCard(idBankCard);
        Account account = findAccount(idUser);

        // 更新日志
        AccountLog log = new AccountLog();
        log.setIdUser(idUser);
        log.setAccountType(AccountTypeEnum.BANKCARD.getType());
        log.setChangeTime(new Date());
        log.setChangeType(AccountLogTypeEnum.BANKCARD_INCOME.getType());
        log.setChangeNum(money);
        log.setIdTransactionApplication(idTransactionApplication);
        log.setBankCardNo(bankCard.getBankCardNo());
        log.setIdBankCard(idBankCard);
        logRepository.save(log);
        return account;
    }

    /**
     * 银行卡付款
     *
     * @param money
     * @param idUser
     * @return
     */
    @Override
    public Account bankcardExpense(BigDecimal money, Long idUser, Long idBankCard, Long idTransactionApplication) {
        return null;
    }

    /**
     * 查询当前用户账户
     *
     * @param idUser
     * @return
     */
    @Override
    public Account findAccount(Long idUser) {
        Account account = accountRepository.findByIdUser(idUser);
        if (account == null) {
            User user = userService.findUser(idUser);
            account = createAccount(user);
        }
        return account;
    }

    /**
     * 查询用户微信账户
     *
     * @param idUser
     * @param openId
     * @return
     */
    @Override
    public WxAccount findWxAccount(Long idUser, String openId) {
        WxAccount wxAccount = wxAccountRepository.findByIdUserAndAndOpenId(idUser, openId);
        if (wxAccount == null) {
            wxAccount = new WxAccount();
            wxAccount.setIdUser(idUser);
            wxAccount.setOpenId(openId);
            wxAccount = saveWxAccount(wxAccount);
        }
        return wxAccount;
    }

    /**
     * 设置用户提现密码
     *
     * @param idUser
     * @param withdrawPassword
     * @return
     */
    @Override
    @Transactional
    public Account setWithdrawPassword(Long idUser, String withdrawPassword) {
        Account account = findAccount(idUser);
        account.setWithdrawPassword(EncryptUtil.encrypt(withdrawPassword.trim()));
        if (!account.getPasswordSetting()) {
            account.setPasswordSetting(true);
        }
        account.setAccountUpdateTime(new Date());
        accountRepository.save(account);
        return account;
    }

    /**
     * 查询当前用户流水
     *
     * @param user     当前用户
     * @param pageNo   页码
     * @param pageSize 页面大小
     * @return
     */
    @Override
    public List<AccountLog> getAccountLogs(User user, Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "idAccountLog");
        Page<AccountLog> logs = logRepository.findAll(new Specification<AccountLog>() {
            @Override
            public Predicate toPredicate(Root<AccountLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("idUser").as(Integer.class), user.getIdUser());
                Predicate predicate2 = criteriaBuilder.notEqual(root.get("changeType").as(String.class), AccountLogTypeEnum.CREATE.getType());
                Predicate predicate3 = criteriaBuilder.notEqual(root.get("changeType").as(String.class), AccountLogTypeEnum.FROZEN.getType());
                query.where(criteriaBuilder.and(predicate1, predicate2, predicate3));
                return query.getRestriction();
            }
        }, pageable);
        return logs.getContent();
    }

    /**
     * 查找指定用户流水
     *
     * @param idUser
     * @return
     */
    @Override
    public List<AccountLog> getAccountLogs(Long idUser) {
        Sort sort = new Sort(Sort.Direction.DESC, "idAccountLog");
        return logRepository.findByIdUser(idUser, sort);
    }

    /**
     * 查询当前用户不同类型的流水
     *
     * @param user
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<AccountLog> getAccountLogsByType(User user, String type, Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "idAccountLog");
        Page<AccountLog> logs = logRepository.findAll(new Specification<AccountLog>() {
            @Override
            public Predicate toPredicate(Root<AccountLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("idUser").as(Integer.class), user.getIdUser());
                Predicate predicate2 = criteriaBuilder.equal(root.get("changeType").as(String.class), type);
                query.where(criteriaBuilder.and(predicate1, predicate2));
                return query.getRestriction();
            }
        }, pageable);
        return logs.getContent();
    }

    /**
     * 封装流水
     *
     * @param logs
     * @return
     */
    @Override
    public List<AccountLogDTO> loadDTOS(List<AccountLog> logs) {
        List<AccountLogDTO> dtos = new ArrayList<>();
        if (logs != null && logs.size() > 0) {
            for (AccountLog log : logs) {
                AccountLogDTO dto = new AccountLogDTO();
                dto.setIdAccountLog(log.getIdAccountLog());
                dto.setTime(DateUtils.formatDateTime(log.getChangeTime()));
                dto.setChangeType(log.getChangeType());
                dto.setAmount(log.getChangeNum());
                if (log.getChangeType().equals(AccountLogTypeEnum.RECHARGE.getType())
                        || log.getChangeType().equals(AccountLogTypeEnum.INCOME.getType())) {
                    dto.setIo(1);
                } else {
                    dto.setIo(-1);
                }
                dtos.add(dto);
            }
        }
        return dtos;
    }

    /**
     * 获取平台账户
     *
     * @param id
     * @return
     */
    private Account getPlatform(Integer id) {
        Account account = accountRepository.findByIdUser(id.longValue());
        if (account == null) {
            User user = new User();
            user.setIdUser(id.longValue());
            account = createAccount(user);
        }
        return account;
    }

    /**
     * 钱包账户收款
     *
     * @param account
     * @param money
     * @return
     */
    private Account income(Account account, BigDecimal money) {
        BigDecimal before = account.getAccountBalance();

        Date date = new Date();
        account.setAccountBalance(before.add(money));
        account.setAccountUpdateTime(date);
        account.setAccountBalanceChangeTime(date);
        accountRepository.save(account);
        return account;
    }

    /**
     * 钱包账户收款
     *
     * @param account
     * @param money
     * @return
     */
    private Account expense(Account account, BigDecimal money) {
        BigDecimal before = account.getAccountBalance();

        Date date = new Date();
        account.setAccountBalance(before.subtract(money));
        account.setAccountUpdateTime(date);
        account.setAccountBalanceChangeTime(date);
        accountRepository.save(account);
        return account;
    }

}
