package com.dprudhvi.shopstream.catalog.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dprudhvi.shopstream.catalog.dto.ProductRequest;
import com.dprudhvi.shopstream.catalog.dto.ProductResponse;
import com.dprudhvi.shopstream.catalog.entity.Product;
import com.dprudhvi.shopstream.catalog.exception.ProductNotFoundException;
import com.dprudhvi.shopstream.catalog.repository.ProductRepository;

@Service 
public class ProductService {
    private final ProductRepository repository;

    /* @Autowired can also be used here, skipping it as only one arument is passed in the constructor. */
    /* this pattern is called Constructor injection pattern*/
    public ProductService(ProductRepository repository){
        this.repository = repository;
    }

    @Transactional 
    public ProductResponse create(ProductRequest request){
        Product product = new Product();
        apply(request, product);
        return toResponse(this.repository.saveAndFlush(product));
    }

    @Transactional(readOnly=true)
    public ProductResponse getById(Long id){
        return toResponse(this.repository.findById(id).orElseThrow(()-> new ProductNotFoundException(id)));
    }

    @Transactional(readOnly = true) 
    public List<ProductResponse> list(){
        return this.repository.findAll().stream().map(this::toResponse).toList();
    }
    
    @Transactional 
    public ProductResponse update(Long id, ProductRequest request){
        Product product = this.repository.findById(id).orElseThrow(()-> new ProductNotFoundException(id));
        apply(request, product);
        return toResponse(this.repository.saveAndFlush(product));
    }

    @Transactional
    public void delete(Long id){
        if (!repository.existsById(id)) {
        throw new ProductNotFoundException(id);
        }
        this.repository.deleteById(id);
    }

    private ProductResponse toResponse(Product product){
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getCreatedAt());
    }

    private void apply(ProductRequest request, Product product){
        product.setName(request.name());
        product.setPrice(request.price());
        product.setDescription(request.description());
    }
}
