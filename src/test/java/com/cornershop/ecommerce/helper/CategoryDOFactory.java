package com.cornershop.ecommerce.helper;

import com.cornershop.ecommerce.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDOFactory {

    public Category getCategoryWithId(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName("TEST_CATEGORY");

        return category;
    }

    public Category getCategoryWithoutId() {
        Category category = new Category();
        category.setName("TEST_CATEGORY");

        return category;
    }

    public List<Category> getCategoryListWithId() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("TEST_CATEGORY1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("TEST_CATEGORY2");

        Category category3 = new Category();
        category3.setId(3L);
        category3.setName("TEST_CATEGORY3");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);
        categoryList.add(category3);

        return categoryList;
    }
}
