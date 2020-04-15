package com.plansolve.farm.model.database.agricultural;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2019/2/18
 * @Description:
 */
@Entity
public class CropsDisease implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer id;

    private String samples;//示例图片

    private String diseaseName;//病害名称

    private String diseaseFeature;//病害特征

    private String regularity;//发病规律

    private String pathogen;//病原

    private String treatment;//治疗方法

    private String folderName;//文件夹名称

    private String diseaseType;//病虫害类别

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getSamples() {
        return samples;
    }

    public void setSamples(String samples) {
        this.samples = samples;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseFeature() {
        return diseaseFeature;
    }

    public void setDiseaseFeature(String diseaseFeature) {
        this.diseaseFeature = diseaseFeature;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getRegularity() {
        return regularity;
    }

    public void setRegularity(String regularity) {
        this.regularity = regularity;
    }

    public String getPathogen() {
        return pathogen;
    }

    public void setPathogen(String pathogen) {
        this.pathogen = pathogen;
    }

    public String getFolderName() { return folderName; }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }

}
