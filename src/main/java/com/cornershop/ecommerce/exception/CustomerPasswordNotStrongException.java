package com.cornershop.ecommerce.exception;


public class CustomerPasswordNotStrongException extends RuntimeException{
    public CustomerPasswordNotStrongException(String message) {

        super(message);
    }
}
