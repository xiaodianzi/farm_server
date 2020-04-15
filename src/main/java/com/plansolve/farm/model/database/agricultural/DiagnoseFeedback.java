package com.plansolve.farm.model.database.agricultural;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Andrew
 * @Date: 2019/2/18
 * @Description:
 */
@Data
@Entity
public class DiagnoseFeedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idDiagnoseFeedback;//主键id

    @Column(nullable = false, updatable = false)
    private Long idUser;//用户id

    @Column(nullable = false)
    private String diagnosePictureUrl;//已保存的诊断图片地址

    private String percentage;//相似度百分比

    private String feedbackCropsName;//反馈的农作物名称

    @Column(nullable = false)
    private boolean diagnoseResult;//诊断结果-0:不正确；1：正确

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime;

}
