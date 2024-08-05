package com.cornershop.ecommerce.util;

import com.cornershop.ecommerce.dto.CustomerDto;
import com.cornershop.ecommerce.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper( CustomerMapper.class );
    CustomerDto customerToCustomerDto(Customer customer);
}
