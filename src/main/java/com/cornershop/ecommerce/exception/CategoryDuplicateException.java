package com.cornershop.ecommerce.exception;

public class CategoryDuplicateException extends RuntimeException{

    //html de aynı category ismi kaydedilmek istenirse duplicate olmaması için hata vermeli
    public CategoryDuplicateException(String message){
        super(message);
    }
}
