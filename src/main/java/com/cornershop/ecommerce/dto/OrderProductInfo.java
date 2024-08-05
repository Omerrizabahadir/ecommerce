package com.cornershop.ecommerce.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductInfo {

    private Long productId;
    private int quantity;
}