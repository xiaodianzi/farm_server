package com.plansolve.farm.model.database.score;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2019/3/18
 * @Description:
 **/

@Entity
@Data
public class ScoreRule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idScoreRule;

    @Column(nullable = false)
    private Boolean isValid; // 该规则是否可用（在对应动作的逻辑里，首先判断这条规则是否有效）

    @Column(nullable = false)
    private String ruleName; // 规则名称

    @Column(nullable = false, updatable = false)
    private String ruleType; // 规则类型（注册、登录、分享等类型，同一规则类型可以有不同名称的规则，只需遍历控制有效规则）

    @Column(nullable = false)
    private String memo; // 规则描述（对规则进行简单描述，使客户端用户快速理解该规则）

    @Column(nullable = false)
    private String rangeType; // 生效周期（按日生效、按周生效、按月生效、按年生效，一次性有效，永久有效）

    @Column(nullable = false)
    private Integer actNum; // 生效条件（相同动作，第几次生效，给予积分）

    @Column(nullable = false)
    private String validTimes; // 有效次数（前几次给予积分）

    @Column(nullable = false)
    private Integer increaseScore; // 可获积分数（达到要求后变化的积分数）

    @Column(nullable = false)
    private String taskLabel; // 规则标签（页面展示数据）

    @Column(nullable = false)
    private String availableTask; // 可完成的任务（页面展示数据）

}
