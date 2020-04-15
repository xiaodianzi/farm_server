package com.plansolve.farm.service.base.common;

import com.plansolve.farm.model.database.Address;

/**
 * @Author: 高一平
 * @Date: 2019/6/4
 * @Description:
 **/
public interface AddressBaseService {

    public Address getAddress(Long idAddress);

    public String getAddress(Address address);

}
