package com.cornershop.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;


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
