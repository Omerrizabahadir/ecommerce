package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.exception.ProductNotFoundException;
import com.cornershop.ecommerce.helper.ProductDOFactory;
import com.cornershop.ecommerce.model.Product;
import com.cornershop.ecommerce.repository.ProductRepository;
import io.jsonwebtoken.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private ProductDOFactory productDOFactory;    //burada sadece tekrar tekrar aunı ürün ve Liste oluşturmamak için başka sınıfa aldık.işlem yok o nedenle @Mock kullanılmaz.Ancak @BeforeEach içindede göstermelisin

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.productDOFactory = new ProductDOFactory();
    }

    @Test
    void createProduct_successful() {
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());     //Multipart oluşturma stackoverflowdan bulduk bu şekildeymiş
        Product product = new Product();
        product.setActive(true);
        product.setUnitsInStock(5L);
        product.setName("macbook");
        product.setPrice(5000D);
        product.setCategoryId(1L);
        product.setImage("uploads/macbook.txt");


        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setActive(true);
        savedProduct.setUnitsInStock(5L);
        savedProduct.setName("macbook");
        savedProduct.setPrice(5000D);
        savedProduct.setCategoryId(1L);
        savedProduct.setImage("uploads/macbook.txt");

        when(productRepository.save(product)).thenReturn(savedProduct);

        Product response = productService.createProduct(firstFile, product);

        assertEquals(savedProduct.getName(), response.getName());
        assertEquals(savedProduct.getId(), response.getId());
        assertEquals(savedProduct.getImage(), response.getImage());
        verify(productRepository, times(1)).save(product);
        verify(productRepository, times(0)).findById(product.getId());
    }

    /*
     } else {
                Product existProduct = productRepository.findById(product.getId()).orElseThrow(() -> new ProductNotFoundException("product not found id :" + product.getId()));
                product.setImage(existProduct.getImage());
            } bunun testi -->void createProductUpdate_successful()
     */
    @Test
    void createProductUpdate_successful() {
        MockMultipartFile file = null;
        Product product = new Product();
        product.setId(1L);
        product.setActive(true);
        product.setUnitsInStock(5L);
        product.setName("macbook");
        product.setPrice(50002D);
        product.setCategoryId(1L);
        product.setImage("uploads/macbook.txt");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setActive(true);
        savedProduct.setUnitsInStock(5L);
        savedProduct.setName("macbook");
        savedProduct.setPrice(5000D);
        savedProduct.setCategoryId(1L);
        savedProduct.setImage("uploads/macbook.txt");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(savedProduct));
        when(productRepository.save(product)).thenReturn(product);

        Product response = productService.createProduct(file, product);

        assertEquals(product.getImage(), response.getImage());
        assertEquals(product.getPrice(), response.getPrice());
        verify(productRepository, times(1)).findById(product.getId());
        verify(productRepository, times(1)).save(product);
    }

    //TODO:  createProduct methodundaki "else" kısmı için new ProductNotFoundException hatası fırlatacak şekilde fail case testi yazın.
    /*alttaki -->createProductUpdate_fail() için
     else {
            Product existProduct = productRepository.findById(product.getId()).orElseThrow(() -> new ProductNotFoundException("product not found id :" + product.getId()));
            product.setImage(existProduct.getImage());
        } bu kısımdaki-->orElseThrow(() -> new ProductNotFoundException("product not found id :" + product.getId()));
            product.setImage(existProduct.getImage()); için yazıcaz
     */
    @Test
    void createProductUpdate_fail() {
        MockMultipartFile file = null;
        Product product = new Product();
        product.setId(1L);

        //file yok ise(null) ise ve id yi bulamadıysa throw'a girecek ve sonra alttaki kodu okumaz başa dönecek
        //when(productRepository.findById(product.getId())).thenReturn(null);   null yerine aşağıdaki gibi Optional.empty() daha güzel
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());       //findById(product.getId())) --> bu yukarda id yi 1L ile çağırdığını gösteriyor


        ProductNotFoundException thrown = Assertions.assertThrows(ProductNotFoundException.class,
                () -> productService.createProduct(file, product));                   //productSerevice te createProduct mtodunun iki parametresini döner

        assertEquals("product not found id :1", thrown.getMessage());
        verify(productRepository, times(1)).findById(product.getId());
        // return productRepository.save(product); --> buna yukarısında hata fırlattığı için gitmiyor. O zaman verify da buna 0 kere(veya never (hiç gitmiyor)) giditor dersin
        //verify(productRepository,times(0)).save(product); alttakinin aynısı
        verify(productRepository, never()).save(product);

        //throw atıyorsa -> bulunduğu satırdan geriye ,başa döner.ALT SATIRA girmtmez!!!!

    }

    @Test
    void getProductListByCategoryId_successful() {
        Long categoryId = 1L;

        List<Product> productList = productDOFactory.getProductListWithId(categoryId);

        when(productRepository.findProductListByCategoryId(categoryId)).thenReturn(productList);

        List<Product> response = productService.getProductListByCategoryId(categoryId);

        assertEquals(categoryId, response.get(0).getCategoryId());
        assertEquals(categoryId, response.get(1).getCategoryId());

        assertEquals(2, response.size());
        //assertEquals(productList.size(), response.size());    //üstteki ile aynı
        assertEquals(productList.get(0).getPrice(), response.get(0).getPrice());
        assertEquals(productList.get(1).getImage(), response.get(1).getImage());
        assertEquals(productList.get(1).getUnitsInStock(), response.get(1).getUnitsInStock());

        verify(productRepository, times(1)).findProductListByCategoryId(categoryId);
    }

    @Test
    void getProduct_successful() {
        Long productId = 1L;
        Product product = productDOFactory.getProductWithId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product response = productService.getProduct(productId);

        assertEquals(product.getId(), response.getId());
        assertEquals(product.getName(), response.getName());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getUnitsInStock(), response.getUnitsInStock());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getProduct_fail() {
        Long productId = 1L;

        Product product = productDOFactory.getProductWithId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.ofNullable(null));
        //when(productRepository.findById(productId)).thenReturn(Optional.empty());             //yukardaki Optional.ofNullable(null) yerine Optional.empty() de yazabilirsin. Aynı şey

        ProductNotFoundException thrown = Assertions.assertThrows(ProductNotFoundException.class,
                () -> productService.getProduct(productId));

        assertEquals("Product Not Found id : " + productId, thrown.getMessage());
        verify(productRepository, times(1)).findById(productId);

    }

    @Test
    void activeOrDeActiveProduct_successful() {
        Long productId = 1L;
        boolean isActive = true;

        //void method bir şey döndürmediğinden bunu ekledik--> Mockito.doNothing().when
        Mockito.doNothing().when(productRepository).updateProductActive(isActive, productId);

        //yine activeOrDeActiveProduct metodu void method old. için response da dönmez. Sadece productServiceteki metodu gösterdik
        productService.activeOrDeActiveProduct(productId, isActive);

        //activeOrDeActiveProduct metodu void method old. için assertEquls, assertTrue,vb... olmayacak
        verify(productRepository, times(1)).updateProductActive(isActive, productId);
    }
    @Test
    void deleteProduct_successful() throws IOException {
        Long productId = 1L;
        String filePath = "uploads/test.txt";   //test.txt dosyasını uploads içinde oluşturmalısın

        File file = new File(filePath);

        Product product = new Product();
        product.setId(productId);
        product.setImage(filePath);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Mockito.doNothing().when(productRepository).deleteById(productId);

        productService.deleteProduct(productId);

        /*assert leri kullanamazsın db'ye gitmiyorsun.
        void deleteProduct -> void metot old. için bir şey dönmüyor o nedenle assert ler kullanılmaz.
        response yoksa assert te yok

         */
        verify(productRepository,times(1)).findById(productId);
        verify(productRepository,times(1)).deleteById(productId);

    }
   /* @Test
    void deleteProduct_fail() {
        Long productId = 1L;
        Product product = productDOFactory.getProductWithId(productId);
        product.setImage("test");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(productId));

        assertEquals("IO exception while deleting image of " + product.getName(), thrown.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(0)).deleteById(productId);
    }


    */
}