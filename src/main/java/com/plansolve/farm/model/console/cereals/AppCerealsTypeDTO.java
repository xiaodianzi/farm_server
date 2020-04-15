package com.plansolve.farm.model.console.cereals;

import com.plansolve.farm.util.HtmlUtil;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/4/9
 * @Description:
 **/
@Data
public class AppCerealsTypeDTO {

    private Integer idCerealsMassageType; // 主键（数据库自增）

    private String crop;

    private String banner; // 新闻相关图片

    private String button;

    public String getButton() {
        String button1 = HtmlUtil.getButtonHtml(false, "/plansolve/manger/application/detail?idCerealsMassageType=" + idCerealsMassageType,
                "", "info", "更改图片");
        String button2 = HtmlUtil.getButtonHtml(false, "/plansolve/manager/cereals/realTimePrice/realTimePricePage?idCerealsMassageType=" + idCerealsMassageType,
                "", "primary", "信息列表");
        return button1 + "&nbsp;&nbsp;&nbsp;&nbsp;" + button2;
    }

}
