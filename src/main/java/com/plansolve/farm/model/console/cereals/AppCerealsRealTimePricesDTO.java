package com.plansolve.farm.model.console.cereals;

import com.plansolve.farm.util.HtmlUtil;
import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2019/4/9
 * @Description:
 **/
@Data
public class AppCerealsRealTimePricesDTO {

    private Integer idCerealsMassage; // 主键（数据库自增）

    private String cerealsMassageType; // 作物类型 玉米/水稻

    private String address; // 新闻显示城市 吉林/东北三省/全国

    private String title; // 新闻标题

    private String isValid; // 是否有效

    private Integer sno; // 序号

    private String source; // 新闻来源

    private String button;

    public String getButton() {
        String button = HtmlUtil.getButtonHtml(false, "/plansolve/manager/cereals/realTimePrice/editRealTimePricePage?idCerealsMassage=" + idCerealsMassage,
                "", "primary", "详情");
        return button;
    }

}
