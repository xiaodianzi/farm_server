package com.plansolve.farm.controller.console.main.system;

import com.plansolve.farm.model.console.DictionaryDTO;
import com.plansolve.farm.model.database.Dictionary;
import com.plansolve.farm.service.console.AppDictService;
import com.plansolve.farm.util.AppHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/9/26
 * @Description:
 **/
@Slf4j
@Controller
@RequestMapping(value = "/manger/system")
public class SystemController {

    @Autowired
    private AppDictService dictService;

    /**
     * 数据字典页
     *
     * @return
     */
    @GetMapping(value = "/dictPage")
    public String dictPage() {
        return "system/dict";
    }

    /**
     * 查询所有系统设置
     *
     * @return
     */
    @GetMapping(value = "/dict/findAll")
    @ResponseBody
    public List<DictionaryDTO> findAll() {
        List<Dictionary> dictionaries = dictService.findAll();
        List<DictionaryDTO> dictionaryDTOS = new ArrayList<>();
        if (dictionaries != null && dictionaries.size() > 0) {
            for (Dictionary dictionary : dictionaries) {
                DictionaryDTO dictionaryDTO = new DictionaryDTO(dictionary.getDictKey(), dictionary.getDictValue());
                dictionaryDTOS.add(dictionaryDTO);
            }
        }
        return dictionaryDTOS;
    }

    /**
     * 更新数据字典中的数据
     *
     * @param dictionary
     * @return
     */
    @PostMapping(value = "/dict/update")
    public String updateDict(Dictionary dictionary) {
        dictService.save(dictionary);
        return "redirect:/manger/system/dictPage";
    }

}
