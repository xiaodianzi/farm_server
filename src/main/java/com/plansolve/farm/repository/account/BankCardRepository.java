package com.plansolve.farm.repository.account;

import com.plansolve.farm.model.database.account.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/19
 * @Description:
 **/
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

    public BankCard findByIdBankCard(Long idBankCard);

    public BankCard findByBankCardNoAndBankCardStateNot(String BankCardNo, String BankCardState);

    public BankCard findByIdBankCardAndBankCardStateNot(Long idBankCard, String BankCardState);

    public List<BankCard> findByIdUserAndBankCardStateNot(Long idUser, String BankCardState);

}
