package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.client.account.BankCardDTO;
import com.plansolve.farm.model.database.account.Bank;
import com.plansolve.farm.model.database.account.BankCard;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.BankCardStateEnum;
import com.plansolve.farm.repository.account.BankCardRepository;
import com.plansolve.farm.service.client.BankCardService;
import com.plansolve.farm.service.client.BankService;
import com.plansolve.farm.service.client.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/22
 * @Description:
 **/
@Service
public class BankCardServiceImpl implements BankCardService {

    @Autowired
    private BankCardRepository bankCardRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BankService bankService;

    /**
     * 添加银行卡
     *
     * @param bankCardDTO
     * @param user
     * @return
     */
    @Override
    public BankCard create(BankCardDTO bankCardDTO, User user) {
        // 银行卡号唯一，需验证银行卡是否已存在
        BankCard bankCard = findBankCard(bankCardDTO.getBankCardNo());
        if (bankCard == null) {
            bankCard = new BankCard();
            BeanUtils.copyProperties(bankCardDTO, bankCard);
            bankCard.setIdUser(user.getIdUser());
            bankCard.setBankCardState(BankCardStateEnum.NORMAL.getState());
            Date date = new Date();
            bankCard.setCreateTime(date);
            bankCard.setUpdateTime(date);
            bankCard = bankCardRepository.save(bankCard);
            return bankCard;
        } else {
            throw new ParamErrorException("[该银行卡账户已被注册]");
        }
    }

    /**
     * 删除银行卡
     *
     * @param idBankCard
     * @param user
     * @return
     */
    @Override
    public BankCard delete(Long idBankCard, User user) {
        BankCard bankCard = findBankCard(idBankCard);
        if (bankCard == null) {
            throw new ParamErrorException("[银行卡号码错误]");
        } else {
            if (user.getIdUser().equals(bankCard.getIdUser())) {
                bankCard.setBankCardState(BankCardStateEnum.DELETE.getState());
                bankCard.setUpdateTime(new Date());
                bankCard = bankCardRepository.save(bankCard);
                return bankCard;
            } else {
                throw new PermissionException("[该用户无权删除此银行卡]");
            }
        }
    }

    /**
     * 封装银行卡
     *
     * @param bankCard
     * @return
     */
    @Override
    public BankCardDTO loadDTO(BankCard bankCard) {
        BankCardDTO dto = new BankCardDTO();
        BeanUtils.copyProperties(bankCard, dto);
        User user = userService.findUser(bankCard.getIdUser());
        dto.setUserMobile(user.getMobile());
        Bank bank = bankService.findBankByAcronym(bankCard.getBankType());
        dto.setBank(bank);
        return dto;
    }

    /**
     * 批量封装
     * @param bankCards
     * @return
     */
    @Override
    public List<BankCardDTO> loadDTOS(List<BankCard> bankCards) {
        List<BankCardDTO> dtos = new ArrayList<>();
        if (bankCards != null && bankCards.size() > 0) {
            for (BankCard bankCard : bankCards) {
                BankCardDTO dto = loadDTO(bankCard);
                dtos.add(dto);
            }
        }
        return dtos;
    }

    /**
     * 根据银行卡号查询
     *
     * @param bankCardNo
     * @return
     */
    public BankCard findBankCard(String bankCardNo) {
        BankCard bankCard = bankCardRepository.findByBankCardNoAndBankCardStateNot(bankCardNo, BankCardStateEnum.DELETE.getState());
        return bankCard;
    }

    /**
     * 根据银行卡ID查询
     *
     * @param idBankCard
     * @return
     */
    @Override
    public BankCard findBankCard(Long idBankCard) {
        return bankCardRepository.findByIdBankCardAndBankCardStateNot(idBankCard, BankCardStateEnum.DELETE.getState());
    }

    /**
     * 获取用户银行卡信息
     * @param idUser
     * @return
     */
    @Override
    public List<BankCard> findUserBankCard(Long idUser) {
        return bankCardRepository.findByIdUserAndBankCardStateNot(idUser, BankCardStateEnum.DELETE.getState());
    }
}
