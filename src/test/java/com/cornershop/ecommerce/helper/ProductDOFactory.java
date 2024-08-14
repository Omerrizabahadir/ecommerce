package com.cornershop.ecommerce.helper;

import com.cornershop.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDOFactory {
    public List<Product> getProductListWithId(Long categoryId) {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setActive(true);
        product1.setUnitsInStock(5L);
        product1.setName("macbook");
        product1.setPrice(50002D);
        product1.setCategoryId(categoryId);
        product1.setImage("uploads/macbook.txt");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setActive(true);
        product2.setUnitsInStock(2L);
        product2.setName("iphone");
        product2.setPrice(390D);
        product2.setCategoryId(categoryId);
        product2.setImage("uploads/iphone.png");

        List<Product> productList = new ArrayList<>();

        productList.add(product1);
        productList.add(product2);

        return productList;
    }

    public Product getProductWithId(Long categoryId){
        Product product = new Product();
        product.setId(1L);
        product.setActive(true);
        product.setUnitsInStock(5L);
        product.setName("test.txt");
        product.setPrice(50002D);
        product.setCategoryId(categoryId);
        product.setImage("uploads/macbook.png");

        return product;
    }
}
