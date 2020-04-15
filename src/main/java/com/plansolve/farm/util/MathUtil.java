package com.plansolve.farm.util;

import com.plansolve.farm.model.database.promotion.Prize;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/10/10
 * @Description:
 **/
@Slf4j
public class MathUtil {

    private static final Double MONEY_RANGE = 0.01;

    /**
     * 比较两个金额是否相等
     *
     * @param d1
     * @param d2
     * @return
     */
    public static Boolean equals(Double d1, Double d2) {
        Double result = Math.abs(d1 - d2);
        if (result < MONEY_RANGE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 适用于幸运大转盘（奖品固定，概率确定）
     * @param prizes 奖品集合
     * @return random：奖品prizes列表中的index
     */
    public static int getPrizeIndex(List<Prize> prizes) {
        int random = -1;
        try {
            //计算总权重
            double sumWeight = 0;
            for (Prize p : prizes) {
                sumWeight += p.getPrizeWeight();
            }
            //产生随机数
            double randomNumber = Math.random();

            //根据随机数在所有奖品分布的区域并确定所抽奖品
            double d1 = 0;
            double d2 = 0;
            for (int i = 0; i < prizes.size(); i++) {
                d2 += Double.parseDouble(String.valueOf(prizes.get(i).getPrizeWeight())) / sumWeight;
                if (i == 0) {
                    d1 = 0;
                } else {
                    d1 += Double.parseDouble(String.valueOf(prizes.get(i - 1).getPrizeWeight())) / sumWeight;
                }
                if (randomNumber >= d1 && randomNumber <= d2) {
                    random = i;
                    break;
                }
            }
        } catch (Exception e) {
            log.error("生成抽奖随机数出错，出错原因：" + e.getMessage());
        }
        return random;
    }

}
