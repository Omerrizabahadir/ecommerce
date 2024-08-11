package com.cornershop.ecommerce.service;


import com.cornershop.ecommerce.dto.AuthDto;
import com.cornershop.ecommerce.dto.CustomerDto;
import com.cornershop.ecommerce.dto.LoginDto;
import com.cornershop.ecommerce.enums.RoleEnum;
import com.cornershop.ecommerce.exception.EmailAlreadyExistsException;
import com.cornershop.ecommerce.exception.CustomerPasswordNotStrongException;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.repository.CustomerRepository;
import com.cornershop.ecommerce.util.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    public CustomerDto createCustomer(Customer customer) {
        //// E-posta adresinin benzersizliğini kontrol et
        String email = customer.getEmail().trim().toLowerCase();
        if (customerRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already in use!");
        }
        // Şifrenin güçlü olduğunu kontrol et
        if (!isPasswordStrong(customer.getPassword())) {
            throw new CustomerPasswordNotStrongException("Password does not meet the security criteria.");
        }
        //customer ROLE ayarlama
        if (Objects.isNull(customer.getRoles())) {
            customer.setRoles(RoleEnum.ROLE_USER.toString());
        }
        //şifreleme
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        // Müşteri kaydını gerçekleştir
        Customer savedCustomer = customerRepository.save(customer);

        //müşteri register olunca Hoşgeldiniz emaili atsın
        emailService.sendWelcomeMail(customer.getEmail(), customer.getFirstName(), customer.getLastName());
        return CustomerMapper.INSTANCE.customerToCustomerDto(customerRepository.save(savedCustomer));
    }

    public LoginDto login(AuthDto authDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword())
            );
            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(authentication);
            }
        } catch (AuthenticationException e) {
            throw new UsernameNotFoundException("Invalid user details.");
        }
        return null; // or appropriate error handling
    }

    private boolean isPasswordStrong(String password) {
        // Şifre en az 8 karakter olmalı, büyük harf, küçük harf, rakam ve özel karakter içermeli
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[0-9.*].*") &&
                password.matches((".*[!@#$%^&*].*"));
    }

}
