package com.dprudhvi.shopstream.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dprudhvi.shopstream.catalog.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    
} 
