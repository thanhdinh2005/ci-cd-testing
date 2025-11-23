package com.software.backend.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.software.backend.dto.response.ProductResponse;
import com.software.backend.repository.ProductRepository;

public class ProductServiceUnit {
    @Autowired
    private ProductRepository productRepository;


    public List<ProductResponse> getAllProducts() {
        return produc;
        
        // Dummy method for illustration
    }
}
