package com.plansolve.farm.model.console;

import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2018/9/26
 * @Description:
 **/
@Data
public class DictionaryDTO {

    private String key;

    private String value;

    private String button;

    /**
     * 编辑：<a href="#editForm" onclick="editForm('','')" class="btn btn-xs btn-primary" data-toggle="modal">编辑</a>
     */
    public DictionaryDTO(String key, String value) {
        this.key = key;
        this.value = value;
        this.button = "<a href=\"#editForm\" onclick=\"editForm('" + key + "','" + value + "')\" class=\"btn btn-xs btn-primary\" data-toggle=\"modal\">编辑</a>";
    }
}
