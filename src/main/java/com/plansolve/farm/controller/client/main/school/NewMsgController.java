package com.plansolve.farm.controller.client.main.school;

import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.client.school.NewsDTO;
import com.plansolve.farm.model.database.school.News;
import com.plansolve.farm.service.client.NewsService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.ResultUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/29
 * @Description:
 **/
@RestController
@RequestMapping(value = "/farm/school/news")
public class NewMsgController {

    @Autowired
    private NewsService newsService;

    /**
     * 获取新闻信息
     *
     * @param pageSize
     * @param pageNo
     * @return
     */
    @PostMapping(value = "/newsList")
    public Result getNewsList(@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "0") Integer pageNo) {
        List<News> news = newsService.getAllNews(pageNo, pageSize);
        List<NewsDTO> dtos = new ArrayList<>();
        if (news != null && news.size() > 0) {
            for (News n : news) {
                NewsDTO dto = new NewsDTO();
                BeanUtils.copyProperties(n, dto);
                dto.setCreateTime(DateUtils.formatDate(n.getCreateTime(), "yyyy-MM-dd"));
                dto.setDetail(n.getSketch());
                dtos.add(dto);
            }
        }
        return ResultUtil.success(dtos);
    }

}
