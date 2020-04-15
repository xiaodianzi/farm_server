package com.plansolve.farm.model.database.dictionary;

import lombok.Data;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2019/3/26
 * @Description:
 */

@Data
@Entity
public class DictMachineryType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idMachineryType;

    @Column(nullable = false)
    private Integer parentId;//父id

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private boolean hasChild;//是否有子项

    @Column(nullable = false)
    private boolean deleted;//是否删除

}

