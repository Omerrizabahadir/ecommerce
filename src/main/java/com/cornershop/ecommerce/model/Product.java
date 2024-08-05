package com.cornershop.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    @Column(name = "units_in_stock")
    private Long unitsInStock;

    @Column(name = "category_id")
    private Long categoryId;

    private Boolean active;

    private String image;
    //TODO: yorumlar - puanlama
    //TODO: ürün özellikleri-açıklaması
}
