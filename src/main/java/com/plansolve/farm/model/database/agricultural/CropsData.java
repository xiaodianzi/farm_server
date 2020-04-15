package com.plansolve.farm.model.database.agricultural;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2019/2/18
 * @Description:
 */
@Entity
public class CropsData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer id;

    private String intro;//简介

    private String plantSkill;//种植技术

    private String cropType;//作物类型

    public Integer getId() {
        return id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPlantSkill() {
        return plantSkill;
    }

    public void setPlantSkill(String plantSkill) {
        this.plantSkill = plantSkill;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

}
