package com.plansolve.farm.repository.order;

import com.plansolve.farm.model.database.order.BidOrderOperator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/8/6
 * @Description:
 **/
public interface BidOrderOperatorRepository extends JpaRepository<BidOrderOperator, Integer> {

    public List<BidOrderOperator> findByIdUser(Long idUser);

    public List<BidOrderOperator> findByIdBidOrder(Integer idBidOrder);

}
