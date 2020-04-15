package com.plansolve.farm.repository.school;

import com.plansolve.farm.model.database.school.News;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/29
 * @Description:
 **/
public interface NewsRepository extends JpaRepository<News, Integer>, JpaSpecificationExecutor<News> {

    public List<News> findByIsValid(Boolean isValid, Sort sort);

    public News findByIdNews(Integer idNews);

}
