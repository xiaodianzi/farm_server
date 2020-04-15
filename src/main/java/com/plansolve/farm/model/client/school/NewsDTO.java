package com.plansolve.farm.model.client.school;

import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2019/5/7
 * @Description:
 **/
@Data
public class NewsDTO {

    private Integer idNews; // 主键（数据库自增）

    private String address; // 新闻显示城市

    private String title; // 新闻标题

    private String picture; // 新闻相关图片

    private String detail; // 文字详情

    private Boolean isValid; // 是否有效

    private Integer sno; // 序号

    private String createTime;

    private String url; // 新闻详情页链接

}
