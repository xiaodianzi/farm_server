package com.plansolve.farm.model.database.user;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2019/5/7
 * @Description:
 **/
@Data
@Entity
public class UserAppVersion implements Serializable {

    @Id
    private Long idUser;

    private String Version;

}
