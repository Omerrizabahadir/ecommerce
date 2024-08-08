package com.cornershop.ecommerce.service;


import com.cornershop.ecommerce.dto.AuthDto;
import com.cornershop.ecommerce.dto.CustomerDto;
import com.cornershop.ecommerce.dto.LoginDto;
import com.cornershop.ecommerce.enums.RoleEnum;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.repository.CustomerRepository;
import com.cornershop.ecommerce.util.CustomerMapper;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String emailFrom;

    public CustomerDto createCustomer(Customer customer) {
        if(Objects.isNull(customer.getRoles())) {
            customer.setRoles(RoleEnum.ROLE_USER.toString());
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        //müşteri register olunca Hoşgeldiniz emaili atsın
        sendWelcomeMail(customer.getEmail(), customer.getFirstName(), customer.getLastName());
        return CustomerMapper.INSTANCE.customerToCustomerDto(customerRepository.save(customer));
    }

    public LoginDto login(AuthDto authDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(authentication);
        }
        throw new UsernameNotFoundException("Invalid user details.");
    }
    //TODO: kullanıcı register olduğunda da hoşgeldin maili atılsın.mail gönderme metodlarının isimlerini farklı yap. aynı olursa hepsinin bilgisini atıyor
    //NOTE: you can use Mustache library.***********
    public void sendWelcomeMail(String emailTo, String firstName, String lastName) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        try {
            helper.setFrom(emailFrom, "CornerShop");
            helper.setTo("omrbahadir@gmail.com");
            helper.setSubject("Merhaba " + firstName + lastName +", Hoşgeldiniz");

            String content = "<p>Merhaba " + firstName + "</p><p>CornerShop ticaret sitesine üyeliğiniz başarıyla gerçekleştirildi</p>";
            helper.setText(content, true);


            mailSender.send(message);
            log.info("E-posta {} adresine gönderildi", emailTo);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("E-posta {} adresine gönderilemedi: {}", emailTo, e.getMessage());
            throw new RuntimeException("E-posta gönderilemedi", e);
        }
    }


}
