package com.dprudhvi.shopstream.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc 
@Testcontainers  
public class ProductIntegrationTest {
    
    @Container 
    @ServiceConnection 
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16");

    @Autowired 
    MockMvc mockMvc;

    static final String CREATE_BODY = "{\"name\":\"KeyBoard\",\"description\":\"Mechanical\",\"price\":4500.00}";
    static final String UPDATE_BODY = "{\"name\":\"KeyBoard Pro\",\"description\":\"Wireless\",\"price\":4500.00}";

    @Test
    void fullLifeCycle_CreateReadUpdateDelete() throws Exception{
        String response = mockMvc.perform(post("/api/products")
                                    .contentType(MediaType.APPLICATION_JSON)
                                .content(CREATE_BODY))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value("KeyBoard"))
                                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(response, "$.id");

        mockMvc.perform(get("/api/products/{id}",id)).andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("KeyBoard"));
        
        mockMvc.perform(put("/api/products/{id}",id).contentType(MediaType.APPLICATION_JSON)
                            .content(UPDATE_BODY))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.name").value("KeyBoard Pro"));
        
        mockMvc.perform(get("/api/products/{id}", id)).andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("KeyBoard Pro"));
        
        mockMvc.perform(delete("/api/products/{id}", id)).andExpect(status().isNoContent());
        
        mockMvc.perform(get("/api/products/{id}", id)).andExpect(status().isNotFound());

    }
    @Test
    void create_returns400_whenBodyInvalid() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":-5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    void getById_returns404_whenMissing() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Product Not Found"));
    }

}
