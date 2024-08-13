package com.cornershop.ecommerce.helper;

import com.cornershop.ecommerce.dto.OrderProductInfo;
import com.cornershop.ecommerce.dto.OrderRequest;
import com.cornershop.ecommerce.model.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderRequestDOFactory {

    public OrderRequest getOrderRequest(int quantity, Long productId, Long customerId) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderList(List.of(getOrderProductInfo(quantity, productId)));
        orderRequest.setCustomerId(customerId);

        return orderRequest;
    }
    public OrderProductInfo getOrderProductInfo(int quantity, Long productId) {
        OrderProductInfo orderProductInfo = new OrderProductInfo();
        orderProductInfo.setQuantity(quantity);
        orderProductInfo.setProductId(productId);

        return orderProductInfo;
    }
}
