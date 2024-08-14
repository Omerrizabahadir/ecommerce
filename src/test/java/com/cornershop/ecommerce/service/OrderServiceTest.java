package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.dto.OrderRequest;
import com.cornershop.ecommerce.exception.CustomerNotFoundException;
import com.cornershop.ecommerce.exception.ProductNotFoundException;
import com.cornershop.ecommerce.helper.OrderRequestDOFactory;
import com.cornershop.ecommerce.helper.ProductDOFactory;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.repository.CustomerRepository;
import com.cornershop.ecommerce.repository.OrderRepository;
import com.cornershop.ecommerce.repository.ProductRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @InjectMocks                              //Burası hangi sınıfı test ediyorsan onun için.Diğer @Mock 'ları buraya inject et
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    private OrderRequestDOFactory orderRequestDOFactory;

    private ProductDOFactory productDOFactory;
    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);
        this.orderRequestDOFactory = new OrderRequestDOFactory();
        this.productDOFactory = new ProductDOFactory();

    }

    @Test
    void doOrder_success(){

        Long productId = 3L;
        Long customerId = 5L;
        int quantity = 2;

        OrderRequest orderRequest = orderRequestDOFactory.getOrderRequest(quantity,productId,customerId);

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail("test@email.com");
        customer.setFirstName("test");

        when(productRepository.findById(productId)).thenReturn(Optional.of(productDOFactory.getProductWithId(productId)));
        //alttaki için--> dönen datayı kullanıyorsak yine when ile yazmalısın
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        //mimemeessage için de dönen datayı kullanacağımız için when kullanmalıyız
        //sendEmail() metoduna gidiyor
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        /* alttaki --> ReflectionTestUtils.setField  @Value("${spring.mail.username}")
          private String emailFrom;  için kullanıldı
          */
        ReflectionTestUtils.setField(orderService, "emailFrom", "test@mail.com");   //emailFrom da artık -> test@mail.com   bu var

        boolean response = orderService.doOrder(orderRequest);

        //assertEquals(true, response); //alttaki ile aynı
        assertTrue(response);
        verify(orderRepository, times(1)).save(any());
        verify(productRepository, times(1)).save(any());
        verify(javaMailSender).send(mimeMessage);
        verify(productRepository, times(2)).findById(productId);
    }
    @Test
    void doOrder_fail_ProductNotFoundException(){
        Long productId = 3L;
        Long customerId = 5L;
        int quantity = 2;

        OrderRequest orderRequest = orderRequestDOFactory.getOrderRequest(quantity,productId,customerId);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ProductNotFoundException thrown = Assertions.assertThrows(ProductNotFoundException.class,
                ()-> orderService.doOrder(orderRequest));

        assertEquals("product not found id :3" , thrown.getMessage());
        verify(productRepository,times(1)).findById(productId);
    }
    @Test
    void doOrder_fail_RuntimeException() {
        Long productId=3L;
        Long customerId = 5L;
        int quantity = 6;

        OrderRequest orderRequest = orderRequestDOFactory.getOrderRequest(quantity,productId,customerId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productDOFactory.getProductWithId(productId)));

       RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
               () -> orderService.doOrder(orderRequest));

       assertEquals("the product stock insufficient productName : macbook",thrown.getMessage());
       verify(productRepository,times(1)).findById(productId);
    }
    @Test
    void doOrder_fail_CustomerNotFoundException(){
        Long productId=3L;
        Long customerId = 5L;
        int quantity = 2;

        OrderRequest orderRequest = orderRequestDOFactory.getOrderRequest(quantity,productId,customerId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productDOFactory.getProductWithId(productId)));
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());


        CustomerNotFoundException thrown = Assertions.assertThrows(CustomerNotFoundException.class,
                () -> orderService.doOrder(orderRequest));


        assertEquals(customerId+" customer not found!",thrown.getMessage());
        verify(productRepository,times(2)).findById(productId);
        verify(productRepository,times(1)).save(any());
        verify(orderRepository,times(1)).save(any());
        verify(customerRepository,times(1)).findById(customerId);

    }
}
