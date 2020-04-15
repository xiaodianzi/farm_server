package com.plansolve.farm.model.client;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: Andrew
 * @Date: 2018/6/14
 * @Description: 查询合作社输出参数
 */
@Data
public class CooperationInfoDTO implements Serializable {

    private Integer idCooperation; // 合作社id

    private String cooperationNum;// 合作社编号

    private String cooperationName; //合作社名称

    private String primaryName; //社长姓名

    private String mobile; //社长手机号

    private String avatar; //社长头像地址

    private AddressDTO addressDTO; //地址

    private String business_license; //合作社营业执照号

    private String license_pic; //营业执照照片

    private List<MembersDTO> members; //合作社成员集合

    private Map<String, List<MembersDTO>> teamOprators; //合作社小队农机手集合

    private List<MembersDTO> newMembers; //申请加入合作社的新成员

    private String cooperationState; //合作社状态

    private String createTime; //创建时间

    private Integer farmlandCount; //土地数量

    private Float farmlandsAcreage; //土地总亩数

    private Integer machineryCount; //农机总数

    private Integer idInviter; //关联人id

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

}
