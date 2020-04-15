package com.plansolve.farm.model.console.common;

import com.plansolve.farm.util.HtmlUtil;
import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2019/5/22
 * @Description:
 **/
@Data
public class NewsDTO {

    private Integer idNews; // 主键（数据库自增）

    private String address; // 新闻显示城市

    private String title; // 新闻标题

    private String isValid; // 是否有效

    private Integer sno; // 序号

    private String createTime;

    private String source; // 新闻来源

    private String releaseTime; // 新闻发布时间

    private String button;

    public String getButton() {
        return HtmlUtil.getButtonHtml(false, "/plansolve/manger/school/news/editNewsPage?idNews=" + idNews,
                "", "primary", "详情");
    }

}
