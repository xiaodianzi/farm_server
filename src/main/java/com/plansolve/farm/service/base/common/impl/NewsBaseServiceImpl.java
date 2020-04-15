package com.plansolve.farm.service.base.common.impl;

import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.school.News;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.repository.school.NewsRepository;
import com.plansolve.farm.service.base.common.NewsBaseService;
import com.plansolve.farm.service.common.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/5/8
 * @Description:
 **/
@Service
public class NewsBaseServiceImpl implements NewsBaseService {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private FileService fileService;

    /**
     * 查询单个新闻
     *
     * @param idNews
     * @return
     */
    @Override
    public News getNews(Integer idNews) {
        return newsRepository.findByIdNews(idNews);
    }

    /**
     * 分页查询有效新闻
     *
     * @param page
     * @return
     */
    @Override
    public Page<News> pageNews(Page<News> page) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<News> newsPage = newsRepository.findAll(new Specification<News>() {
            @Override
            public Predicate toPredicate(Root<News> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.equal(root.get("isValid").as(Boolean.class), true);
                criteriaBuilder.and(predicate);
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(newsPage.getTotalElements());
        page.setRows(newsPage.getContent());
        return page;
    }
}
