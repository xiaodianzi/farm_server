package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.model.console.account.TransactionApplicationDTO;
import com.plansolve.farm.model.console.account.WithdrawApplicationDetailDTO;
import com.plansolve.farm.model.console.user.AppUserDTO;
import com.plansolve.farm.model.database.account.ApplicationDetail;
import com.plansolve.farm.model.database.account.BankCard;
import com.plansolve.farm.model.database.account.TransactionApplication;
import com.plansolve.farm.model.database.account.TransactionDetail;
import com.plansolve.farm.model.database.console.AdminUser;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserInfo;
import com.plansolve.farm.model.enums.state.TransactionApplicationStateEnum;
import com.plansolve.farm.model.enums.type.AccountTypeEnum;
import com.plansolve.farm.model.enums.type.ApplicationTypeEnum;
import com.plansolve.farm.model.enums.type.TransactionApplicationTypeEnum;
import com.plansolve.farm.repository.account.ApplicationDetailRepository;
import com.plansolve.farm.repository.account.TransactionApplicationRepository;
import com.plansolve.farm.repository.account.TransactionDetailRepository;
import com.plansolve.farm.service.base.user.UserBaseService;
import com.plansolve.farm.service.console.*;
import com.plansolve.farm.service.console.user.ConsoleUserService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EnumUtil;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/12
 * @Description:
 **/
@Service
public class ConsoleTransactionApplicationServiceImpl implements ConsoleTransactionApplicationService {

    @Autowired
    private TransactionApplicationRepository applicationRepository;
    @Autowired
    private ApplicationDetailRepository applicationDetailRepository;
    @Autowired
    private TransactionDetailRepository transactionDetailRepository;
    @Autowired
    private ConsoleUserService userService;
    @Autowired
    private UserBaseService userBaseService;
    @Autowired
    private AppOrderService orderService;
    @Autowired
    private UserBankCardService cardService;
    @Autowired
    private AdminUserService adminUserService;

    /**
     * 转换传输对象
     *
     * @param transactionApplication
     * @return
     */
    @Override
    public TransactionApplicationDTO loadDTO(TransactionApplication transactionApplication) {
        TransactionApplicationDTO dto = new TransactionApplicationDTO();
        if (transactionApplication != null) {
            BeanUtils.copyProperties(transactionApplication, dto);
            User user = userService.findUser(transactionApplication.getIdUser());
            UserInfo info = userBaseService.getUserInfo(user.getIdUser());
            if (info != null) {
                dto.setUsername(info.getRealname());
            } else {
                dto.setUsername("");
            }
            dto.setMobile(user.getMobile());
            dto.setApplicationCreateTime(DateUtils.formatDate(transactionApplication.getApplicationCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            dto.setApplicationUpdateTime(DateUtils.formatDate(transactionApplication.getApplicationUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            dto.setApplicationType(EnumUtil.getByType(transactionApplication.getApplicationType(), TransactionApplicationTypeEnum.class).getMessage());
            dto.setApplicationState(EnumUtil.getByState(transactionApplication.getApplicationState(), TransactionApplicationStateEnum.class).getMessage());
            if (transactionApplication.getIdUserOrder() != null && transactionApplication.getIdUserOrder() > 0) {
                UserOrder order = orderService.findOrder(transactionApplication.getIdUserOrder());
                dto.setUserOrderNo(order.getUserOrderNo());
            }
            if (transactionApplication.getIdBankCard() != null && transactionApplication.getIdBankCard() > 0) {
                BankCard bankCard = cardService.findBankCard(transactionApplication.getIdBankCard());
                dto.setBankCardNo(bankCard.getBankCardNo());
                dto.setBankInfo(bankCard.getBankInfo());
            }
        }
        return dto;
    }

    @Override
    public List<WithdrawApplicationDetailDTO> loadWithdrawDTO(List<ApplicationDetail> applicationDetails) {
        List<WithdrawApplicationDetailDTO> withdrawApplicationDetailDTOList = new ArrayList<>();
        if (null != applicationDetails && applicationDetails.size() > 0) {
            for (ApplicationDetail applicationDetail : applicationDetails) {
                WithdrawApplicationDetailDTO withdrawApplicationDetailDTO = new WithdrawApplicationDetailDTO();
                if (null == applicationDetail.getIdAdmin()) {
                    withdrawApplicationDetailDTO.setAdminName("[系统]");
                } else {
                    AdminUser admin = adminUserService.findUser(applicationDetail.getIdAdmin());
                    String adminName = admin.getUsername();
                    withdrawApplicationDetailDTO.setAdminName("[" + adminName + "]");
                }
                if (applicationDetail.getApplicationType().equals(ApplicationTypeEnum.CHECK.getType())) {
                    withdrawApplicationDetailDTO.setApplicationType("提现初审");
                } else if (applicationDetail.getApplicationType().equals(ApplicationTypeEnum.RECHECK.getType())) {
                    withdrawApplicationDetailDTO.setApplicationType("提现复核");
                } else if (applicationDetail.getApplicationType().equals(ApplicationTypeEnum.PAYMENT.getType())) {
                    withdrawApplicationDetailDTO.setApplicationType("提现到账");
                } else {
                    withdrawApplicationDetailDTO.setApplicationType("提现申请异常");
                }
                withdrawApplicationDetailDTO.setApplicationTime(DateUtils.formatDateTime(applicationDetail.getApplicationTime()));
                withdrawApplicationDetailDTO.setApplicationResult(applicationDetail.getApplicationResult());
                withdrawApplicationDetailDTO.setApplicationMoney(applicationDetail.getApplicationMoney());
                withdrawApplicationDetailDTO.setApplicationDetail(applicationDetail.getApplicationDetail());
                withdrawApplicationDetailDTOList.add(withdrawApplicationDetailDTO);
            }
        }
        return withdrawApplicationDetailDTOList;
    }

    /**
     * 批量转换传输对象
     *
     * @param transactionApplications
     * @return
     */
    @Override
    public List<TransactionApplicationDTO> loadDTOS(List<TransactionApplication> transactionApplications) {
        if (transactionApplications != null && transactionApplications.size() > 0) {
            List<TransactionApplicationDTO> applicationDTOS = new ArrayList<>();
            for (TransactionApplication transactionApplication : transactionApplications) {
                TransactionApplicationDTO applicationDTO = loadDTO(transactionApplication);
                applicationDTOS.add(applicationDTO);
            }
            return applicationDTOS;
        } else {
            return null;
        }
    }

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
    @Override
    public Page<TransactionApplication> findAllApplication(String applicationType, String applicationState, String transactionApplicationNo, Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "idTransactionApplication");
        Page<TransactionApplication> applicationPage;
        if (applicationType == null && applicationState == null && transactionApplicationNo == null) {
            applicationPage = applicationRepository.findAll(pageable);
        } else {
            applicationPage = applicationRepository.findAll(new Specification<TransactionApplication>() {
                @Override
                public Predicate toPredicate(Root<TransactionApplication> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (applicationType != null && !applicationType.isEmpty()) {
                        Predicate predicate = criteriaBuilder.equal(root.get("applicationType").as(String.class), applicationType);
                        predicates.add(predicate);
                    }
                    if (applicationState != null && !applicationState.isEmpty()) {
                        Predicate predicate = criteriaBuilder.like(root.get("applicationState").as(String.class), applicationState + "%");
                        predicates.add(predicate);
                    }
                    if (transactionApplicationNo != null && !transactionApplicationNo.isEmpty()) {
                        Predicate predicate = criteriaBuilder.like(root.get("transactionApplicationNo").as(String.class), "%" + transactionApplicationNo + "%");
                        predicates.add(predicate);
                    }

                    query.where(predicates.toArray(new Predicate[predicates.size()]));
                    return query.getRestriction();
                }
            }, pageable);
        }
        return applicationPage;
    }

    /**
     * 查询指定用户申请
     *
     * @param idUser           查询用户
     * @param applicationType  申请类型--提现、付款
     * @param applicationState 申请进度--创建、初审、复核
     * @return
     */
    @Override
    public List<TransactionApplication> findApplicationByUser(Long idUser, String applicationType, String applicationState) {
        List<TransactionApplication> applications;
        if (applicationType != null && !applicationType.isEmpty()
                && applicationState != null && !applicationState.isEmpty()) {
            applications = applicationRepository.findByIdUserAndApplicationTypeAndApplicationState(idUser, applicationType, applicationState);
        } else {
            if (applicationType != null && !applicationType.isEmpty()) {
                applications = applicationRepository.findByIdUserAndApplicationType(idUser, applicationType);
            } else if (applicationState != null && !applicationState.isEmpty()) {
                applications = applicationRepository.findByIdUserAndApplicationState(idUser, applicationState);
            } else {
                applications = applicationRepository.findByIdUser(idUser);
            }
        }
        return applications;
    }

    @Override
    public TransactionApplication findApplicationById(Long idTransactionApplication) {
        TransactionApplication transactionApplication = null;
        if (null != idTransactionApplication) {
            transactionApplication = applicationRepository.findByIdTransactionApplication(idTransactionApplication);
        }
        return transactionApplication;
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
        TransactionApplication application = applicationRepository.findByIdTransactionApplication(idTransactionApplication);
        if (application == null) {
            throw new NullParamException("[无法找到该申请]");
        } else {
            // 生成复核详情
            ApplicationDetail applicationDetail = new ApplicationDetail();
            applicationDetail.setIdTransactionApplication(application.getIdTransactionApplication());
            applicationDetail.setTransactionType(TransactionApplicationTypeEnum.WITHDRAW.getType());
            applicationDetail.setApplicationType(ApplicationTypeEnum.RECHECK.getType());
            applicationDetail.setApplicationMoney(application.getMoney());
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

            if (result) {
                // 新增交易流水一条
                TransactionDetail transactionDetail = new TransactionDetail();
                transactionDetail.setIdUser(application.getIdUser());
                transactionDetail.setIdTransactionApplication(application.getIdTransactionApplication());
                transactionDetail.setTransactionType(TransactionApplicationTypeEnum.WITHDRAW.getType());
                transactionDetail.setTransactionMoney(application.getMoney());
                transactionDetail.setTransactionTime(new Date());
                transactionDetail.setIdUserIncome(application.getIdUser());
                transactionDetail.setIncomeAccountType(AccountTypeEnum.BANKCARD.getType());
                transactionDetail.setBankCardIncomeId(application.getIdBankCard());
                BankCard bankCard = cardService.findBankCard(application.getIdBankCard());
                transactionDetail.setBankCardIncomeNo(bankCard.getBankCardNo());
                transactionDetail.setIdUserExpense(application.getIdUser());
                transactionDetail.setExpenseAccountType(AccountTypeEnum.WALLET.getType());
                transactionDetailRepository.save(transactionDetail);
            }
            return applicationDetail;
        }
    }

}
