package com.cornershop.ecommerce.repository;


import com.cornershop.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

   //HTML SAYFAMDA AYNI category kaydetmeye çalışılırsa hata atmasını istiyoryuz.
    Optional<Category> findCategoryByName(String name);
}
