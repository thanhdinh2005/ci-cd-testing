package com.software.backend.controller;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.response.ApiResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(){
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request){
        return ResponseEntity.ok(ApiResponse.success(productService.createProduct(request)));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@Valid @RequestBody ProductRequest request, @PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(productService.updateProductById(id, request)));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Delete successfully"));
    }
}