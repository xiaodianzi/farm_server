package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.model.console.account.AccountLogConsoleDTO;
import com.plansolve.farm.model.database.log.AccountLog;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.code.AdminAccountEnum;
import com.plansolve.farm.model.enums.type.AccountLogTypeEnum;
import com.plansolve.farm.model.enums.type.AccountTypeEnum;
import com.plansolve.farm.repository.log.AccountLogRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.service.console.AccountManageService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EnumUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @Author: Andrew
 * @Date: 2019/4/10
 * @Description:
 */
@Service
public class AccountManageServiceImpl implements AccountManageService {

    @Autowired
    private AccountLogRepository accountLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<AccountLog> getAccountLogInfo(Integer pageNumber, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "idAccountLog");
        List<AccountLog> accountLogs = accountLogRepository.findAllByChangeTypeNotAndChangeTypeNot(pageable, AccountLogTypeEnum.CREATE.getType(), AccountLogTypeEnum.FROZEN.getType());
        return accountLogs;
    }

    @Override
    public Integer countAccountLogs() {
        Integer count = accountLogRepository.countByChangeTypeNotAndChangeTypeNot(AccountLogTypeEnum.CREATE.getType(), AccountLogTypeEnum.FROZEN.getType());
        return count;
    }

    @Override
    public Page<AccountLog> findAllBySpecification(Integer pageNumber, Integer pageSize, User user, String accountLogType) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "idAccountLog");
        Page<AccountLog> accountLogs;
        if (null != user || StringUtils.isNotBlank(accountLogType)) {
            accountLogs = accountLogRepository.findAll(new Specification<AccountLog>() {
                @Override
                public Predicate toPredicate(Root<AccountLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    Predicate predicate = null;
                    if (null != user){
                        Predicate idUserpredicate = criteriaBuilder.equal(root.get("idUser").as(Long.class), user.getIdUser());
                        if(predicate!=null){
                            predicate = criteriaBuilder.and(predicate, idUserpredicate);
                        }else{
                            predicate = criteriaBuilder.and(idUserpredicate);
                        }
                    }
                    if (StringUtils.isNotBlank(accountLogType)) {
                        if ("all".equals(accountLogType)){
                            Predicate createTypePredicate = criteriaBuilder.notEqual(root.get("changeType").as(String.class), AccountLogTypeEnum.CREATE.getType());
                            if(predicate!=null){
                                predicate = criteriaBuilder.and(predicate, createTypePredicate);
                            }else{
                                predicate = criteriaBuilder.and(createTypePredicate);
                            }
                            Predicate frozenTypePredicate = criteriaBuilder.notEqual(root.get("changeType").as(String.class), AccountLogTypeEnum.FROZEN.getType());
                            if(predicate!=null){
                                predicate = criteriaBuilder.and(predicate, frozenTypePredicate);
                            }else{
                                predicate = criteriaBuilder.and(frozenTypePredicate);
                            }
                        }else{
                            Predicate changeTypePredicate = criteriaBuilder.equal(root.get("changeType").as(String.class), accountLogType);
                            if(predicate!=null){
                                predicate = criteriaBuilder.and(predicate, changeTypePredicate);
                            }else{
                                predicate = criteriaBuilder.and(changeTypePredicate);
                            }
                        }
                    }
                    predicates.add(predicate);
                    query.where(criteriaBuilder.and(predicates.get(0)));
                    return query.getRestriction();
                }
            }, pageable);
        } else {
            accountLogs = accountLogRepository.findAll(pageable);
        }
        return accountLogs;
    }

    @Override
    public List<AccountLogConsoleDTO> loadAccountLogDTO(List<AccountLog> accountLogs) {
        List<AccountLogConsoleDTO> accountLogList = new ArrayList<>();
        if (accountLogs.size() > 0) {
            for (AccountLog accountLog : accountLogs) {
                AccountLogConsoleDTO accountLogConsoleDTO = new AccountLogConsoleDTO();
                accountLogConsoleDTO.setIdAccountLog(accountLog.getIdAccountLog());
                if (null != accountLog.getIdUser()) {
                    if (accountLog.getIdUser()>0){
                        User user = userRepository.findByIdUser(accountLog.getIdUser());
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
        }
        return accountLogList;
    }

}
