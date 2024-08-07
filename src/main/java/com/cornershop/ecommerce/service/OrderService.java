package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.dto.OrderProductInfo;
import com.cornershop.ecommerce.dto.OrderRequest;
import com.cornershop.ecommerce.exception.CustomerNotFoundException;
import com.cornershop.ecommerce.exception.ProductNotFoundException;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.model.Order;
import com.cornershop.ecommerce.model.Product;
import com.cornershop.ecommerce.repository.CustomerRepository;
import com.cornershop.ecommerce.repository.OrderRepository;
import com.cornershop.ecommerce.repository.ProductRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${spring.mail.username}")
    private String emailFrom;

    private void productUnitStockCheck(List<OrderProductInfo> orderProductInfoList) {
        orderProductInfoList.forEach(productInfo -> {
            Product product = productRepository.findById(productInfo.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("product not found id :" + productInfo.getProductId()));

            if (product.getUnitsInStock() - productInfo.getQuantity() < 0) {
                log.error("the product stock insufficient id : " + productInfo.getProductId());
                throw new RuntimeException("the product stock insufficient productName : " + product.getName());
            }
        });
    }

    public boolean doOrder(OrderRequest orderRequest) {
        log.info("Order request time {} customer :{}", LocalDateTime.now(), orderRequest.getCustomerId());
        productUnitStockCheck(orderRequest.getOrderList());
        List<Double> orderTotalCostList = new ArrayList<>();
        orderRequest.getOrderList().forEach(orderRequestInfo -> {
            Order order = new Order();
            Product product = productRepository.getProductById(orderRequestInfo.getProductId()).orElseThrow(() -> new ProductNotFoundException("product not found id :" + orderRequestInfo.getProductId()));
            Double totalPrice = orderRequestInfo.getQuantity() * product.getPrice();
            orderTotalCostList.add(totalPrice);
            order.setTotalPrice(totalPrice);

            order.setQuantity(orderRequestInfo.getQuantity());
            order.setProductId(orderRequestInfo.getProductId());
            order.setCustomerId(orderRequest.getCustomerId());


            order.setPrice(product.getPrice());
            if (product.getUnitsInStock() - orderRequestInfo.getQuantity() == 0) {
                product.setActive(false);
            }

            orderRepository.save(order);

            product.setUnitsInStock(product.getUnitsInStock() - orderRequestInfo.getQuantity());
            productRepository.save(product);
        });

        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(orderRequest.getCustomerId() + " customer not found!"));


        Double orderTotalCost = orderTotalCostList.stream().mapToDouble(Double::doubleValue).sum();
        sendMail(customer.getEmail(), customer.getFirstName(), orderTotalCost);
        return true;
    }

    //TODO: kullanıcı register olduğunda da hoşgeldin maili atılsın.
    //NOTE: you can use Mustache library.***********
    public void sendMail(String emailTo, String firstName, double totalCost) {
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
            helper.setSubject("Merhaba " + firstName + ", Siparişiniz İşleme Alındı");

            String content = "<p>Merhaba " + firstName + "</p><p>Toplam maliyet " + totalCost + "</p>";
            helper.setText(content, true);

            mailSender.send(message);
            log.info("E-posta {} adresine gönderildi", emailTo);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("E-posta {} adresine gönderilemedi: {}", emailTo, e.getMessage());
            throw new RuntimeException("E-posta gönderilemedi", e);
        }
    }
}