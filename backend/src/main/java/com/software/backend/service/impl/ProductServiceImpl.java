package com.software.backend.service.impl;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.entity.Category;
import com.software.backend.entity.Product;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.mapper.ProductMapper;
import com.software.backend.repository.CategoryRepository;
import com.software.backend.repository.ProductRepository;
import com.software.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id)));
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: "+ request.getCategoryName()));
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProductById(Long id, ProductRequest request) {
        Product currentProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Product newProduct = productMapper.toEntity(request);
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: "+ request.getCategoryName()));
        newProduct.setCategory(category);

        currentProduct.setName(newProduct.getName());
        currentProduct.setCategory(newProduct.getCategory());
        currentProduct.setPrice(newProduct.getPrice());
        currentProduct.setQuantity(newProduct.getQuantity());

        return productMapper.toResponse(productRepository.save(currentProduct));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.deleteById(id);
    }
}
