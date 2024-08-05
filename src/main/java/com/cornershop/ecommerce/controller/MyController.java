package com.cornershop.ecommerce.controller;

import com.cornershop.ecommerce.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/send-email")
    public String sendEmail() {
        emailService.sendEmailWithAttachment("omrbahadir@gmail.com", "Merhaba", "Bu bir test emailidir.", "/Users/macbook/Desktop/file.txt");
        return "Email gönderildi";
    }
}
