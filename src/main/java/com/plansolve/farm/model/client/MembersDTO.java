package com.plansolve.farm.model.client;

import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2018/6/14
 * @Description: 合作社成员
 */
public class MembersDTO implements Serializable {

    private Integer idCooperation; // 用户所加入合作社

    private Integer idInviter; //关联人id

    private String mobile; //关联人手机号

    private Integer idTeam; //加入小队

    private String nickname; //用户昵称

    private Integer machineryNumber; //农机数量

    private Integer farmlandNumber; //农田数量

    private Float farmlandAcreage; //农田大小（亩）

    private boolean captain; //是否队长

    private String teamName; //小队名称

    private String inviterNum; //成员编号

    private boolean busy; //忙闲状态

    private String position; //职务

    public Integer getIdCooperation() {
        return idCooperation;
    }

    public void setIdCooperation(Integer idCooperation) {
        this.idCooperation = idCooperation;
    }

    public Integer getIdInviter() {
        return idInviter;
    }

    public void setIdInviter(Integer idInviter) {
        this.idInviter = idInviter;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(Integer idTeam) {
        this.idTeam = idTeam;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getMachineryNumber() {
        return machineryNumber;
    }

    public void setMachineryNumber(Integer machineryNumber) {
        this.machineryNumber = machineryNumber;
    }

    public Integer getFarmlandNumber() {
        return farmlandNumber;
    }

    public void setFarmlandNumber(Integer farmlandNumber) {
        this.farmlandNumber = farmlandNumber;
    }

    public Float getFarmlandAcreage() {
        return farmlandAcreage;
    }

    public void setFarmlandAcreage(Float farmlandAcreage) {
        this.farmlandAcreage = farmlandAcreage;
    }

    public boolean isCaptain() {
        return captain;
    }

    public void setCaptain(boolean captain) {
        this.captain = captain;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getInviterNum() {
        return inviterNum;
    }

    public void setInviterNum(String inviterNum) {
        this.inviterNum = inviterNum;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "MembersDTO{" +
                "idInviter=" + idInviter +
                ", mobile='" + mobile + '\'' +
                ", idTeam=" + idTeam +
                ", nickname='" + nickname + '\'' +
                ", machineryNumber=" + machineryNumber +
                ", farmlandNumber=" + farmlandNumber +
                ", farmlandAcreage=" + farmlandAcreage +
                ", captain=" + captain +
                ", teamName='" + teamName + '\'' +
                ", inviterNum='" + inviterNum + '\'' +
                ", busy=" + busy +
                ", position='" + position + '\'' +
                '}';
    }

}
