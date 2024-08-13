package com.cornershop.ecommerce.helper;

import com.cornershop.ecommerce.model.Customer;

public class CustomerDOFactory {

    public Customer getCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("hkdemircan01@gmail.com");
        customer.setFirstName("test");
        customer.setRoles("ROLE_USER");

        return customer;
    }
}
