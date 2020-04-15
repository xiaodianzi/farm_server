package com.plansolve.farm.controller.console.messages;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.console.MessageDTO;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.model.database.Message;
import com.plansolve.farm.service.console.MessageService;
import com.plansolve.farm.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/2
 * @Description:
 **/
@Slf4j
@Controller
@RequestMapping(value = "/manger/app/user/message")
public class UserMessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    /**
     * 跳转短信列表页
     *
     * @return
     */
    @GetMapping(value = "/listPage")
    public String listPage() {
        return "console/user/message/list";
    }

    /**
     * 短信列表页信息
     *
     * @param limit
     * @param offset
     * @param mobile
     * @return
     */
    @GetMapping(value = "/list")
    @ResponseBody
    public PageDTO<MessageDTO> list(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset,
                                    String mobile) {
        Integer page = getPage(limit, offset);
        Page<Message> messagePage = messageService.findAllMessage(mobile, page, limit);
        PageDTO<MessageDTO> pageDTO = new PageDTO<>();
        List<MessageDTO> dtos = new ArrayList<>();
        if (messagePage.getContent() != null && messagePage.getContent().size() > 0) {
            for (Message message : messagePage.getContent()) {
                MessageDTO dto = new MessageDTO();
                BeanUtils.copyProperties(message, dto);
                dto.setCreateTime(DateUtils.formatDateTime(message.getCreateTime()));
                dtos.add(dto);
            }
        }
        pageDTO.setRows(dtos);
        pageDTO.setTotal(messagePage.getTotalElements());
        return pageDTO;
    }

}
