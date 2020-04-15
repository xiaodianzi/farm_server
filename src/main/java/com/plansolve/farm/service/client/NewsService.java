package com.plansolve.farm.service.client;

import com.plansolve.farm.model.database.school.News;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/29
 * @Description:
 **/
public interface NewsService {

    /**
     * 分页获取新闻
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<News> getAllNews(Integer pageNo, Integer pageSize);

}
