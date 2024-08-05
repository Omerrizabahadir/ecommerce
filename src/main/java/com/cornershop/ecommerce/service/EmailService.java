package com.cornershop.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailWithAttachment(String to, String subject, String body, String fileToAttach) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true: ekler için

            helper.setTo(to);
            helper.setFrom("ecommerceproje06@hotmail.com"); // Gönderen e-posta adresi
            helper.setSubject(subject);
            helper.setText(body);

            // Dosyayı ekleme
            File file = new File(fileToAttach);
            if (file.exists()) {
                FileSystemResource fileResource = new FileSystemResource(file);
                helper.addAttachment(file.getName(), fileResource); // Dosya adı otomatik olarak alınır
            } else {
                throw new IllegalArgumentException("Dosya mevcut değil: " + fileToAttach);
            }

            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            System.err.println("E-posta gönderme hatası: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            System.err.println("E-posta ek hatası: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
