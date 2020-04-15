package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.database.Address;

/**
 * @Author: 高一平
 * @Date: 2018/6/19
 * @Description:
 **/
public interface AddressService {

    /**
     * 地址添加
     *
     * @param addressDTO
     * @return 地址保存主键
     */
    public Long insert(AddressDTO addressDTO);

    /**
     * 地址更新和保存
     *
     * @param address
     * @return
     */
    public Address save(Address address);

    /**
     * 地址更改（省、市、区县、乡镇不可改）
     *
     * @param addressDTO
     * @param idAddress
     * @return
     */
    public AddressDTO update(AddressDTO addressDTO, Long idAddress);

    /**
     * 获取地址传输对象
     *
     * @param idAddress
     * @return
     */
    public AddressDTO getAddress(Long idAddress);

    /**
     * 获取地址
     *
     * @param idAddress
     * @return
     */
    public Address getOne(Long idAddress);

}
