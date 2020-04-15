package com.plansolve.farm.service.console;

import com.plansolve.farm.model.database.Message;
import org.springframework.data.domain.Page;

/**
 * @Author: 高一平
 * @Date: 2019/4/1
 * @Description:
 **/
public interface MessageService {

    /**
     * 保存信息
     *
     * @param message
     * @return
     */
    public Message save(Message message);

    /**
     * 分页查询
     *
     * @param mobile
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<Message> findAllMessage(String mobile, Integer pageNo, Integer pageSize);

}
