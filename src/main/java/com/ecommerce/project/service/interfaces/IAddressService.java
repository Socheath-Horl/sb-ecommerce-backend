package com.ecommerce.project.service.interfaces;

import com.ecommerce.project.dtos.AddressDto;
import com.ecommerce.project.model.User;

import java.util.List;

public interface IAddressService {
    AddressDto createAddress(AddressDto addressDTO, User user);

    List<AddressDto> getAddresses();

    AddressDto getAddressesById(Long addressId);

    List<AddressDto> getUserAddresses(User user);

    AddressDto updateAddress(Long addressId, AddressDto addressDTO);

    String deleteAddress(Long addressId);
}
