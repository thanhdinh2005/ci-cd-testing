package com.software.backend.service;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProductById(Long id, ProductRequest request);
    void deleteProduct(Long id);
}

