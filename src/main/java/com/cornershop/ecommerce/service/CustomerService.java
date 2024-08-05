package com.cornershop.ecommerce.service;


import com.cornershop.ecommerce.dto.AuthDto;
import com.cornershop.ecommerce.dto.CustomerDto;
import com.cornershop.ecommerce.dto.LoginDto;
import com.cornershop.ecommerce.enums.RoleEnum;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.repository.CustomerRepository;
import com.cornershop.ecommerce.util.CustomerMapper;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public CustomerDto createCustomer(Customer customer) {
        if(Objects.isNull(customer.getRoles())) {
            customer.setRoles(RoleEnum.ROLE_USER.toString());
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return CustomerMapper.INSTANCE.customerToCustomerDto(customerRepository.save(customer));
    }

    public LoginDto login(AuthDto authDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(authentication);
        }
        throw new UsernameNotFoundException("Invalid user details.");
    }
}
