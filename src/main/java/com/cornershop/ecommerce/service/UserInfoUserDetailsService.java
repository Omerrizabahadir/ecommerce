package com.cornershop.ecommerce.service;


import com.cornershop.ecommerce.config.UserInfoDetails;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.repository.CustomerRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customerInfo = customerRepository.findByEmail(username);

        return customerInfo.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}