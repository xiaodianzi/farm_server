package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.model.database.school.News;
import com.plansolve.farm.repository.school.NewsRepository;
import com.plansolve.farm.service.client.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/29
 * @Description:
 **/
@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    /**
     * 分页获取新闻
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<News> getAllNews(Integer pageNo, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<News> news = newsRepository.findByIsValid(true, sort);
        return news;
    }
}
