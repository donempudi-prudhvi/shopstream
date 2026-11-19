package com.dprudhvi.shopstream.catalog.exception;

public class ProductNotFoundException extends  RuntimeException{
   public  ProductNotFoundException(Long id){
    super("Product not found with id : "+id);
   }
}
