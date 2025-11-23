package com.software.backend.service.impl;

import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.request.ProductSearchRequest;
import com.software.backend.dto.response.PageResponse;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.entity.Category;
import com.software.backend.entity.Product;
import com.software.backend.exception.BadRequestException;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.mapper.ProductMapper;
import com.software.backend.repository.CategoryRepository;
import com.software.backend.repository.ProductRepository;
import com.software.backend.service.ProductService;
import com.software.backend.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

        if (productRepository.existsByNameIgnoreCase(request.getName())) {
    throw new BadRequestException("Product name already exists");
}
        Product product = productMapper.toEntity(request);
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name"));
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProductById(Long id, ProductRequest request) {
        Product currentProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (productRepository.existsByNameIgnoreCase(request.getName())) {
    throw new BadRequestException("Product name already exists");
}
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

    @Override
    public PageResponse<ProductResponse> searchProduct(ProductSearchRequest request, int page, int size) {
        Specification<Product> spec = Specification.allOf(
                ProductSpecification.hasName(request.name()),
                ProductSpecification.quantityGreaterThan(request.quantityMin()),
                ProductSpecification.quantityLessThan(request.quantityMax()),
                ProductSpecification.priceGreaterThan(request.priceMin()),
                ProductSpecification.priceLessThan(request.priceMax()),
                ProductSpecification.hasCategory(request.categoryId())
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());
        Page<Product> products = productRepository.findAll(spec ,pageable);
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::toResponse)
                .toList();

        return new PageResponse<>(
                productResponses,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }
}
