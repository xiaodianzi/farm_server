package com.plansolve.farm.repository.order;

import com.plansolve.farm.model.database.order.OrderOperator;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/
public interface OrderOperatorRepository extends JpaRepository<OrderOperator, Long>, JpaSpecificationExecutor<OrderOperator> {

    public List<OrderOperator> findByIdUserAndOperatorState(Long idUserOrder, String operatorState, Sort sort);

    public List<OrderOperator> findByIdUserAndOperatorStateIn(Long idUserOrder, List<String> operatorState, Sort sort);

    public List<OrderOperator> findByIdUserAndIdUserOrderAndOperatorStateNotIn(Long idUser, Long idUserOrder, List<String> operatorState);

    public List<OrderOperator> findByIdUserAndEndTimeAfterAndOperatorStateNotIn(Long idUser, Date date, List<String> operatorState);

    public List<OrderOperator> findByIdUserAndStartTimeAfterAndOperatorStateNotIn(Long idUser, Date date, List<String> operatorState);

    public List<OrderOperator> findByIdUserAndEndTimeBetweenAndOperatorStateNotIn(Long idUser, Date dateStart, Date dateEnd, List<String> operatorState);

    public List<OrderOperator> findByIdUserOrderAndOperatorState(Long idUserOrder, String operatorState, Sort sort);

    public List<OrderOperator> findByIdUserOrderAndOperatorStateIn(Long idUserOrder, List<String> operatorState, Sort sort);

    public List<OrderOperator> findByIdUserOrderAndOperatorStateNotIn(Long idUserOrder, List<String> operatorState);

    public List<OrderOperator> findByIdUserAndOperatorStateNotIn(Long idUser, List<String> operatorState);

}
