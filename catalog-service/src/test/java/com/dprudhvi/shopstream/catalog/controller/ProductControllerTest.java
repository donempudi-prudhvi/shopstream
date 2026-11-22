package com.dprudhvi.shopstream.catalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import com.dprudhvi.shopstream.catalog.dto.ProductRequest;
import com.dprudhvi.shopstream.catalog.dto.ProductResponse;
import com.dprudhvi.shopstream.catalog.exception.ProductNotFoundException;
import com.dprudhvi.shopstream.catalog.service.ProductService;


@WebMvcTest(ProductController.class) 
public class ProductControllerTest {
    
    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean 
    private ProductService service;


    private static final String VALID_BODY =
            "{\"name\":\"Keyboard\",\"description\":\"Mechanical\",\"price\":4500.00}";


    @Test
    void create_Return201_withBody() throws Exception{
        when(service.create(any(ProductRequest.class))).thenReturn(response(1L,"KeyBoard"));
        
        mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                        .andExpect(status().isCreated())
                        .andExpect(header().string("Location", endsWith("/api/products/1")))
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.name").value("KeyBoard"));
    }

    @Test
    void create_returns400WithfieldError_whenBodyInvalid() throws Exception{
        mockMvc.perform(post("/api/products").
                        contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\", \"price\":-5}"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errors.name").exists())
                        .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test 
    void getById_return200_whenFound() throws Exception{
        when(service.getById(1L)).thenReturn(response(1L, "KeyBoard"));

        mockMvc.perform(get("/api/products/1")).
                        andExpect(status().isOk()).
                        andExpect(jsonPath("$.name").value("KeyBoard"));
    }

    @Test 
    void getById_return404_whenMissing() throws Exception{
        when(service.getById(10L)).thenThrow(new ProductNotFoundException(10L));

        mockMvc.perform(get("/api/products/10")).
                        andExpect(status().isNotFound()).
                        andExpect(jsonPath("$.title").value("Product Not Found")).
                        andExpect(jsonPath("$.detail").value("Product not found with id : 10"));
    }
    

    @Test 
    void list_return200_with_allProducts() throws Exception{
        when(service.list()).thenReturn(List.of(response(1L, "KeyBoard")));
        mockMvc.perform(get("/api/products")).
                        andExpect(status().isOk()).
                        andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void update_return200_whenProductExists() throws Exception{
        when(service.update(eq(1L), any(ProductRequest.class))).thenReturn(response(1L, "KeyBoard Pro"));   

        mockMvc.perform(put("/api/products/1").
                        contentType(MediaType.APPLICATION_JSON).
                        content(VALID_BODY)).
                        andExpect(status().isOk()).
                        andExpect(jsonPath("$.name").value("KeyBoard Pro"));
    }

    @Test
    void update_return400_whenProductDoesNotExists() throws Exception{
        when(service.update(eq(10L), any(ProductRequest.class))).thenThrow(new ProductNotFoundException(10L));   

        mockMvc.perform(put("/api/products/10").
                        contentType(MediaType.APPLICATION_JSON).
                        content(VALID_BODY)).
                        andExpect(status().isNotFound()).
                        andExpect(jsonPath("$.title").value("Product Not Found"));
    }

    @Test
    void update_return400_whenBody_isInvalid() throws Exception{

        mockMvc.perform(put("/api/products/1").
                        contentType(MediaType.APPLICATION_JSON).
                        content("{\"name\":\"\", \"price\":-1}")).
                        andExpect(status().isBadRequest()).
                        andExpect(jsonPath("$.errors.name").exists()).
                        andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    void delete_returns204_whenProductExists() throws Exception{
        mockMvc.perform(delete("/api/products/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(service).delete(1L);
    }

    private ProductResponse response(Long id, String name){
        return new ProductResponse(id, name, "Mechanical", new BigDecimal("4500.00"), Instant.now());
    }

}
