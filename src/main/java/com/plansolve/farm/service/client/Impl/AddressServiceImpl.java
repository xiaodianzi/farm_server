package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.repository.AddressRepository;
import com.plansolve.farm.service.client.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/19
 * @Description:
 **/
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    /**
     * 地址添加
     *
     * @param addressDTO
     * @return 地址保存主键
     */
    @Override
    public Long insert(AddressDTO addressDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);

        address.setCreateDate(new Date());
        address.setUpdateDate(new Date());
        address = addressRepository.save(address);
        return address.getIdAddress();
    }

    /**
     * 地址更新和保存
     *
     * @param address
     * @return
     */
    @Override
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    /**
     * 地址更改（省、市、区县、乡镇不可改）
     *
     * @param addressDTO
     * @param idAddress
     * @return
     */
    @Override
    public AddressDTO update(AddressDTO addressDTO, Long idAddress) {
        Address address = addressRepository.findByIdAddress(idAddress);
        address.setLatitude(addressDTO.getLatitude());
        address.setLongitude(addressDTO.getLongitude());
        address.setDetail(addressDTO.getDetail());

        address.setUpdateDate(new Date());
        addressRepository.save(address);
        BeanUtils.copyProperties(address, addressDTO);
        return addressDTO;
    }

    /**
     * 获取地址传输对象
     *
     * @param idAddress
     * @return
     */
    @Override
    public AddressDTO getAddress(Long idAddress) {
        Address address = addressRepository.findByIdAddress(idAddress);
        AddressDTO addressDTO = new AddressDTO();
        BeanUtils.copyProperties(address, addressDTO);
        return addressDTO;
    }

    /**
     * 获取地址
     *
     * @param idAddress
     * @return
     */
    @Override
    public Address getOne(Long idAddress) {
        return addressRepository.findByIdAddress(idAddress);
    }
}
