package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.model.database.Message;
import com.plansolve.farm.repository.MessageRepository;
import com.plansolve.farm.service.console.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/4/1
 * @Description:
 **/
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * 保存信息
     *
     * @param message
     * @return
     */
    @Override
    public Message save(Message message) {
        message.setCreateTime(new Date());
        return messageRepository.save(message);
    }

    /**
     * 分页查询
     *
     * @param mobile
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Page<Message> findAllMessage(String mobile, Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "idMessage");
        Page<Message> messagePage;
        if (mobile == null || mobile.isEmpty()) {
            messagePage = messageRepository.findAll(pageable);
        } else {
            messagePage = messageRepository.findAll(new Specification<Message>() {
                @Override
                public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    Predicate predicate1 = criteriaBuilder.like(root.get("mobile").as(String.class), "%" + mobile + "%");
                    query.where(criteriaBuilder.and(predicate1));
                    return query.getRestriction();
                }
            }, pageable);
        }
        return messagePage;
    }

}
