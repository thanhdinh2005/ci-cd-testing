package com.software.backend.controller;

import com.software.backend.dto.response.ApiResponse;
import com.software.backend.entity.Category;
import com.software.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories(){
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories()));
    }

    
}
