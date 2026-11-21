package com.dprudhvi.shopstream.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dprudhvi.shopstream.catalog.dto.ProductRequest;
import com.dprudhvi.shopstream.catalog.dto.ProductResponse;
import com.dprudhvi.shopstream.catalog.entity.Product;
import com.dprudhvi.shopstream.catalog.exception.ProductNotFoundException;
import com.dprudhvi.shopstream.catalog.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock ProductRepository repository;
    @InjectMocks ProductService service;

    @Test 
    void create_savesProductFromRequest_andReturnsResponse(){
        ProductRequest request = new ProductRequest("Mouse", "Wired Mouse", new BigDecimal("450.00"));
        Product saveProduct = product(1L, "Mouse", "Wired Mouse", "450.00");
        Instant now= Instant.now();
        saveProduct.setCreatedAt(now);
        when(repository.saveAndFlush(any(Product.class))).thenReturn(saveProduct);

        ProductResponse response = service.create(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).saveAndFlush(captor.capture());
        Product passedIn = captor.getValue();
        assertNull(passedIn.getId());
        assertEquals("Mouse", passedIn.getName());
        assertEquals("Wired Mouse", passedIn.getDescription());
        assertEquals(new BigDecimal("450.00"), passedIn.getPrice());

        assertEquals(1L, response.id());
        assertEquals("Mouse", response.name());
        assertEquals(now, response.createdAt());

    }

    @Test
    void getById_returnProduct_whenExists(){
        when(repository.findById(1L)).thenReturn(Optional.of(defaultProduct()));
        
        ProductResponse response = service.getById(1L);

        assertEquals(1L, response.id());
        assertEquals("Monitor", response.name());
        assertEquals("LED Monitor", response.description());
        assertEquals(new BigDecimal("500.00"), response.price());
    }

    @Test
    void getById_throwsError_whenMissing(){
        when(repository.findById(10L)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> service.getById(10L));

        assertEquals("Product not found with id : " + 10L, exception.getMessage());
    }

    @Test 
    void list_returnEmptyList_whenNoProducts(){
        when(repository.findAll()).thenReturn(List.of());
        List<ProductResponse> responses = service.list();
        assertTrue(responses.isEmpty());
    }

    @Test 
    void list_returnEmptyList_whenProducts(){
        when(repository.findAll()).thenReturn(List.of(defaultProduct()));
        List<ProductResponse> responses = service.list();
        assertFalse(responses.isEmpty());
    }

    @Test 
    void update_changesFields_whenExists(){
        Product product = defaultProduct();
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.saveAndFlush(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        ProductRequest request = new ProductRequest("Keyboard Pro", "Updated", new BigDecimal("5200.00"));
        
        ProductResponse response = service.update(1L, request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).saveAndFlush(captor.capture());
        assertEquals(1L, captor.getValue().getId());
        assertEquals("Keyboard Pro", captor.getValue().getName());
        assertEquals("Keyboard Pro", response.name());
        assertEquals("Updated", response.description());
        assertEquals(new BigDecimal("5200.00"), response.price());
    }

    @Test
    void update_throwsNotFound_NeverSaves_whenMissingId(){
        when(repository.findById(99L)).thenReturn(Optional.empty());
        ProductRequest request = new ProductRequest("Keyboard Pro", "Updated", new BigDecimal("5200.00"));
        
        assertThrows(ProductNotFoundException.class, () -> service.update(99L, request));

        verify(repository, never()).saveAndFlush(any(Product.class));
    }

    @Test
    void delete_removesProduct_whenExists() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_throwsNotFound_andNeverDeletes_whenMissing() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> service.delete(99L));

        verify(repository, never()).deleteById(any());
    }

    private  Product product(Long id, String name, String description, String price){
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(new BigDecimal(price));
        return p;
    }
    private Product defaultProduct(){
        return  product(1L,"Monitor", "LED Monitor", "500.00");
    }
    
}
