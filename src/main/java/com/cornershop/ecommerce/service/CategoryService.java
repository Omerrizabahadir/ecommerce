package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.exception.CategoryDeleteException;
import com.cornershop.ecommerce.exception.CategoryDuplicateException;
import com.cornershop.ecommerce.exception.CategoryNotFoundException;
import com.cornershop.ecommerce.model.Category;
import com.cornershop.ecommerce.repository.CategoryRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.cornershop.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public Category createCategory(Category category) {
        //categoryRepositoryde aynı category'yi html sayfasından girilirse eklememesi ve hata atması için
        Optional<Category> optionalCategory =categoryRepository.findCategoryByName(category.getName());
        if (optionalCategory.isPresent()){
            throw new CategoryDuplicateException("Category is already defined : "+ category.getName());
        }
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Long productCountOfCategory = productRepository.getProductCountOfCategoryId(id);
        if(productCountOfCategory > 0){       //html sayfasında silmek istediğin category'ye ait ürün varsa ve 1 taneyse silmesin
            throw  new CategoryDeleteException("You can not delete this category because category has " + productCountOfCategory + " products");
        }
        categoryRepository.deleteById(id);
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow( () -> new CategoryNotFoundException("Category not found id : " + id));
    }

    public List<Category> getAllCategoryList() {
        //category'leri id ye göre sıralamak için
        List <Category> categoryList = categoryRepository.findAll();
        categoryList.sort(Comparator.comparing(Category::getId));
        return categoryList;
    }
    public Category updateCategory(Category category){

        return categoryRepository.save(category);
    }
}
