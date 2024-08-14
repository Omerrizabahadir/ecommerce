package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.exception.ProductNotFoundException;
import com.cornershop.ecommerce.model.Product;
import com.cornershop.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    private static final String UPLOAD_DIR = "uploads";
    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(MultipartFile file, Product product) {
        if (Objects.nonNull(file)) {
            String imagePath = saveFile(file, product.getName());
            product.setImage(imagePath);
        } else {
            Product existProduct = productRepository.findById(product.getId()).orElseThrow(() -> new ProductNotFoundException("product not found id :" + product.getId()));
            product.setImage(existProduct.getImage());
        }
        return productRepository.save(product);
    }

    public List<Product> getProductListByCategoryId(Long categoryId) {
        return productRepository.findProductListByCategoryId(categoryId);
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not Found id : " + id));
    }

    private String saveFile(MultipartFile file, String productName) {
        productName = productName.replaceAll("\\s", "");  //image ismi boşluk içeriyorsa boşluğu kaldırması için
        String fileName = productName + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
        Path uploadPath = Path.of(UPLOAD_DIR);
        Path filePath;
        try {
            Files.createDirectories(uploadPath);
            filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filePath.toString();
    }

    public void activeOrDeActiveProduct(Long id, boolean isActive) {
        productRepository.updateProductActive(isActive, id);
    }

    public void deleteProduct(Long id) {
        //ürün silindiğinde uploads içine kaydedilen resim yoluda silinsin
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id + " product is not found"));
        try {
            Files.delete(Paths.get(product.getImage()));
        } catch (IOException e) {
            throw new RuntimeException("IO exception while deleting image of " + product.getName());

        }
        productRepository.deleteById(id);
    }

    public List<Product> getAllProductList() {

        return productRepository.getAllProductList();
    }

}
