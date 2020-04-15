package com.plansolve.farm.repository;

import com.plansolve.farm.model.database.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author: 高一平
 * @Date: 2019/4/1
 * @Description:
 **/
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
}
