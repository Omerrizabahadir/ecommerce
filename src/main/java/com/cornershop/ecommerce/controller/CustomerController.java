package com.cornershop.ecommerce.controller;


import com.cornershop.ecommerce.dto.AuthDto;
import com.cornershop.ecommerce.dto.CustomerDto;
import com.cornershop.ecommerce.dto.LoginDto;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody Customer customer) {
        return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestBody AuthDto authDto) {
        return new ResponseEntity<>(customerService.login(authDto), HttpStatus.OK);
    }

}
