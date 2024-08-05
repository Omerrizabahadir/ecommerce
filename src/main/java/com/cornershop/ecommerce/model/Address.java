package com.cornershop.ecommerce.model;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {

    private String country;
    private String city;
    private String district;
    private String postCode;
    private String addressLine;
}