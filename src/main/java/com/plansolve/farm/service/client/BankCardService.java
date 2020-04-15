package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.account.BankCardDTO;
import com.plansolve.farm.model.database.account.BankCard;
import com.plansolve.farm.model.database.user.User;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/22
 * @Description:
 **/
public interface BankCardService {

    /**
     * 添加银行卡
     *
     * @param bankCardDTO
     * @param user
     * @return
     */
    public BankCard create(BankCardDTO bankCardDTO, User user);

    /**
     * 删除银行卡
     *
     * @param idBankCard
     * @param user
     * @return
     */
    public BankCard delete(Long idBankCard, User user);

    /**
     * 封装银行卡
     *
     * @param bankCard
     * @return
     */
    public BankCardDTO loadDTO(BankCard bankCard);

    /**
     * 批量封装
     * @param bankCards
     * @return
     */
    public List<BankCardDTO> loadDTOS(List<BankCard> bankCards);

    /**
     * 根据银行卡号查询
     *
     * @param bankCardNo
     * @return
     */
    public BankCard findBankCard(String bankCardNo);

    /**
     * 根据银行卡ID查询
     *
     * @param idBankCard
     * @return
     */
    public BankCard findBankCard(Long idBankCard);

    /**
     * 获取用户银行卡信息
     *
     * @param idUser
     * @return
     */
    public List<BankCard> findUserBankCard(Long idUser);

}
