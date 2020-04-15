package com.plansolve.farm.repository;

import com.plansolve.farm.model.database.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/
public interface AddressRepository extends JpaRepository<Address, Long> {

    public Address findByIdAddress(Long idAddress);

}
