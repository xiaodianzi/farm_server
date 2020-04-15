package com.plansolve.farm.model.client;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2019/3/29
 * @Description:
 */
@Data
public class GrainMarketDTO implements Serializable {

    private Integer idGrainMarket;

    private Long idUser;//发布人

    @NotBlank(message = "姓名不能为空")
    private String name;//姓名

    @NotBlank(message = "手机号码不能为空")
    @Size(min = 11, max = 11, message = "请输入正确的手机号码")
    private String mobile;//电话

    @NotBlank(message = "买卖类型不能为空")
    private String marketType;//买卖类型

    @NotBlank(message = "作物类型不能为空")
    private String grainType;//作物类型

    @NotBlank(message = "粮食数量不能为空")
    private String amount;//粮食数量

    private String salePrice;//出售价格

    private String minimumBuyPrice;//收购最低价

    private String highestBuyPrice;//收购最高价

    @NotBlank(message = "地址不能为空")
    private String addressDetail; // 地址

    private String validTime;//有效期

    private String createTime; // 创建时间

}
