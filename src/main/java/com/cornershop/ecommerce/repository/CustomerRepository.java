package com.cornershop.ecommerce.repository;

import com.cornershop.ecommerce.model.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    Optional<Customer> findByEmail(String email);


        @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.email = :email")
        boolean existsByEmail(@Param("email") String email);
}