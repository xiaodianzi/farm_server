package com.plansolve.farm.service.base.common.impl;

import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.repository.AddressRepository;
import com.plansolve.farm.service.base.common.AddressBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 高一平
 * @Date: 2019/6/4
 * @Description:
 **/
@Service
public class AddressBaseServiceImpl implements AddressBaseService {

    @Autowired
    private AddressRepository repository;

    @Override
    public Address getAddress(Long idAddress) {
        return repository.findByIdAddress(idAddress);
    }

    @Override
    public String getAddress(Address address) {
        String addressStr = address.getProvince() + address.getCity() + address.getCounty() + address.getTown() + address.getDetail();
        return addressStr.replace("null","");
    }
}
