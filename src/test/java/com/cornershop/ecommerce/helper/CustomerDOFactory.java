package com.cornershop.ecommerce.helper;

import com.cornershop.ecommerce.model.Customer;

public class CustomerDOFactory {

    public Customer getCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");
        customer.setFirstName("testFirstName");
        customer.setLastName("testLastName");
        customer.setPassword("Password1!");
        customer.setRoles("ROLE_USER");

        return customer;
    }
}
