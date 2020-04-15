package com.plansolve.farm.model.database;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/

@Data
@Entity
public class Dictionary implements Serializable {

    @Id
    @Column(nullable = false)
    private String dictKey;

    @Column(nullable = false)
    private String dictValue;

}
