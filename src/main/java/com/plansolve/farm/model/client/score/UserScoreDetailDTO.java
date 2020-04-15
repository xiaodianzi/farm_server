package com.plansolve.farm.model.client.score;

import lombok.Data;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2019/3/27
 * @Description:
 */
@Data
public class UserScoreDetailDTO implements Serializable {

    private String detail;

    private Integer changeScore;

    private String changTime;

}
