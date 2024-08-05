package com.cornershop.ecommerce.dto;


import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {

    private Long customerId;
    private List<OrderProductInfo> orderList;
}
