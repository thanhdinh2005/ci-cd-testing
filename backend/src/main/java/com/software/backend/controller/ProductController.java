package com.software.backend.controller;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.response.ApiResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll() {
        List<ProductResponse> list = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        ProductResponse res = productService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@RequestBody @Valid ProductRequest request) {
        ProductResponse res = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(res)); // hoặc ApiResponse.ok(res) nếu bạn không có created()
    }

     @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequest request
    ) {
        ProductResponse res = productService.updateProductById(id, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null));

        // Nếu bạn muốn đồng bộ format ApiResponse thay vì 204:
        // return ResponseEntity.ok(ApiResponse.ok(null));
    }
}