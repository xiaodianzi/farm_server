package com.plansolve.farm.controller.mp.common;

import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.client.school.NewsDTO;
import com.plansolve.farm.model.client.school.NewsDetailDTO;
import com.plansolve.farm.model.database.school.News;
import com.plansolve.farm.service.base.common.NewsBaseService;
import com.plansolve.farm.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/5/8
 * @Description:
 **/
@Slf4j
@Controller
@RequestMapping("/wx/mp/news")
public class NewsMpController {

    @Autowired
    private NewsBaseService baseService;

    /**
     * 分页获取新闻列表
     *
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(value = "/list")
    public String list(Page<News> page, Model model) {
        page = baseService.pageNews(page);
        List<News> rows = page.getRows();
        List<NewsDTO> dtos = new ArrayList<>();
        if (rows != null && rows.size() > 0) {
            for (News n : rows) {
                NewsDTO dto = new NewsDTO();
                BeanUtils.copyProperties(n, dto);
                dto.setCreateTime(DateUtils.formatDate(n.getCreateTime(), "yyyy-MM-dd"));
                dtos.add(dto);
            }
        }
        page.setRows(rows);
        model.addAttribute("items", dtos);
        model.addAttribute("total", page.getTotal());
        model.addAttribute("pageNo", page.getPageNo());
        return "common/news/list";
    }

    /**
     * 新闻详情页
     *
     * @param idNews
     * @return
     */
    @RequestMapping(value = "/detail")
    public String detail(Integer idNews, Model model) {
        News news = baseService.getNews(idNews);
        NewsDetailDTO dto = new NewsDetailDTO();
        BeanUtils.copyProperties(news, dto);
        dto.setReleaseTime(DateUtils.formatDateTime(news.getReleaseTime()));
        model.addAttribute("item", dto);
        return "common/news/detail";
    }

}
