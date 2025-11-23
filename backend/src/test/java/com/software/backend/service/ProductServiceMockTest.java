package com.software.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import com.software.backend.dto.request.ProductRequest;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.entity.Category;
import com.software.backend.entity.Product;
import com.software.backend.exception.BadRequestException;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.mapper.ProductMapper;
import com.software.backend.repository.CategoryRepository;
import com.software.backend.repository.ProductRepository;
import com.software.backend.service.impl.ProductServiceImpl;



@ExtendWith(MockitoExtension.class)
public class ProductServiceMockTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductServiceImpl productServiceImpl;
    
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Test
    @DisplayName("Create product - tên trùng -> ném BadRequestException")
    void createProduct_duplicateName_throwsBadRequestException(){

        ProductRequest request = ProductRequest.builder()
                .name("laptop")
                .price(150.00)
                .quantity((long) 10)
                .categoryName("Electronics")
                .build();
        
when(productRepository.existsByNameIgnoreCase("laptop")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> productServiceImpl.createProduct(request));
        verify(productRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Create product - category không tồn tại -> ném ResourceNotFoundException")
    void createProduct_categoryNotExists_throwsResourceNotFoundException() {
        ProductRequest request = ProductRequest.builder()
                .name("laptop")
                .price(150.00)
                .quantity((long) 10)
                .categoryName("Electronics")
                .build();

        Product product = Product.builder()
        .name("laptop")
        .price(150.0)
        .quantity(10L)
        .build();

        

        when(productRepository.existsByNameIgnoreCase("laptop")).thenReturn(false);

    // STRICT: category phải tồn tại, ở đây không có
    when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());
    when(productMapper.toEntity(request)).thenReturn(product);


    

    // Act + Assert
    assertThrows(ResourceNotFoundException.class,
            () -> productServiceImpl.createProduct(request));

    verify(productRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Create product - hop le -> tra ve ProductRespone")
    void createProduct_validRequest_returnsProductResponse(){
        ProductRequest request = ProductRequest.builder()
                .name("laptop")
                .price(150.00)
                .quantity((long) 10)
                .categoryName("Electronics")
                .build();
        Category category = new Category(1L, "Electronics");
        Product product = Product.builder()
        .name("laptop")
        .price(150.0)
        .quantity(10L)
        .build();

        ProductResponse response = ProductResponse.builder()
        .id(1L)
        .name("laptop")
        .categoryName("Electronics")
        .price(150.0)
        .quantity(10L)
        .build();

        Product savedProduct = Product.builder()
            .id(1L)
            .name("laptop")
            .price(150.0)
            .quantity(10L)
            .category(category)
            .build();

        when(productRepository.existsByNameIgnoreCase("laptop")).thenReturn(false);

    // STRICT: category phải tồn tại, ở đây không có
    when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));
    when(productMapper.toEntity(request)).thenReturn(product);
    when(productMapper.toResponse(savedProduct)).thenReturn(response);
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    ProductResponse result = productServiceImpl.createProduct(request);
    assertNotNull(result);
    assertEquals("laptop", result.getName());
    assertEquals("Electronics", result.getCategoryName());
    verify(productRepository).save(any(Product.class));
    }

    @Test
@DisplayName("getProductById - không tồn tại -> ném ResourceNotFoundException")
void getProductById_notFound_throwsResourceNotFound() {
    // Arrange
    Long id = 99L;
    when(productRepository.findById(id)).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(ResourceNotFoundException.class,
            () -> productServiceImpl.getById(id));

    verify(productRepository, times(1)).findById(id);
    verify(productMapper, never()).toResponse(any());
}

    @Test
@DisplayName("getProductById - tồn tại -> trả ProductResponse đúng")
void getProductById_found_returnsResponse() {
    // Arrange
    Long id = 1L;
    Category category = new Category(1L, "Electronics");

    Product entity = Product.builder()
            .id(id)
            .name("laptop")
            .price(150.0)
            .quantity(10L)
            .category(category)
            .build();

    ProductResponse response = ProductResponse.builder()
            .id(id)
            .name("laptop")
            .price(150.0)
            .quantity(10L)
            .categoryName("Electronics")
            .build();

    when(productRepository.findById(id)).thenReturn(Optional.of(entity));
    when(productMapper.toResponse(entity)).thenReturn(response);
    
    ProductResponse result = productServiceImpl.getById(id);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("laptop", result.getName());
    assertEquals("Electronics", result.getCategoryName());
    verify(productRepository, times(1)).findById(id);
    verify(productMapper, times(1)).toResponse(entity);
}

    @Test
    @DisplayName("updateProduct - id không tồn tại -> ResourceNotFoundException")
    void updateProduct_idNotFound_throwsResourceNotFound(){
        Long id = 99L;
        ProductRequest request = ProductRequest.builder()
                .name("laptop")
                .price(150.00)
                .quantity((long) 10)
                .categoryName("Electronics")
                .build();
        
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.updateProductById(id, request));
        verify(productRepository, times(1)).findById(id);
    verify(productRepository, never()).save(any());
    }

    @Test
@DisplayName("updateProduct - đổi sang tên đã tồn tại (khác id) -> BadRequestException")
void updateProduct_duplicateName_throwsBadRequest() {
    Long id = 1L;
    ProductRequest req = ProductRequest.builder()
            .name("laptop-pro").price(200.0).quantity(5L).categoryName("Electronics").build();

    // Sản phẩm hiện tại
    Category cat = new Category(1L, "Electronics");
    Product current = Product.builder()
            .id(id).name("laptop").price(150.0).quantity(10L).category(cat).build();

    when(productRepository.findById(id)).thenReturn(Optional.of(current));
    // Tên mới đã tồn tại ở nơi khác
    when(productRepository.existsByNameIgnoreCase("laptop-pro")).thenReturn(true);

    assertThrows(BadRequestException.class, () -> productServiceImpl.updateProductById(id, req));

    verify(productRepository, never()).save(any());
}

    @Test
    @DisplayName("updateProduct - category không tồn tại -> ResourceNotFoundException")
    void updateProduct_categoryNotFound_throwsResourceNotFound() {
    Long id = 1L;
    ProductRequest req = ProductRequest.builder()
            .name("laptop").price(150.0).quantity(10L).categoryName("Ghost").build();

    Product current = Product.builder()
            .id(id).name("laptop").price(140.0).quantity(7L)
            .category(new Category(1L, "Electronics"))
            .build();

    when(productRepository.findById(id)).thenReturn(Optional.of(current));
    // giữ nguyên tên → bỏ qua duplicate
    when(categoryRepository.findByName("Ghost")).thenReturn(Optional.empty());
    when(productRepository.existsByNameIgnoreCase("laptop")).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.updateProductById(id, req));

    verify(productRepository, never()).save(any());
}

@Test
@DisplayName("updateProduct - hợp lệ -> cập nhật và trả ProductResponse")
void updateProduct_valid_updatesAndReturnsResponse() {
    Long id = 1L;
    ProductRequest req = ProductRequest.builder()
            .name("laptop-pro").price(200.0).quantity(5L).categoryName("Electronics").build();

    Category cat = new Category(1L, "Electronics");

    Product current = Product.builder()
            .id(id).name("laptop").price(150.0).quantity(10L).category(cat).build();

    Product updated = Product.builder()
            .id(id).name("laptop-pro").price(200.0).quantity(5L).category(cat).build();

    ProductResponse resp = ProductResponse.builder()
            .id(id).name("laptop-pro").price(200.0).quantity(5L).categoryName("Electronics").build();

    when(productRepository.findById(id)).thenReturn(Optional.of(current));
    // tên mới khác tên cũ nhưng chưa tồn tại ở nơi khác
    when(productRepository.existsByNameIgnoreCase("laptop-pro")).thenReturn(false);
    when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(cat));
    when(productMapper.toEntity(req)).thenReturn(updated);
    // Nếu bạn dùng mapper để update, có thể không cần toEntity; ở đây chỉ cần map response
    when(productRepository.save(any(Product.class))).thenReturn(updated);
    when(productMapper.toResponse(updated)).thenReturn(resp);
    // hoặc an toàn: when(productMapper.toResponse(any(Product.class))).thenReturn(resp);

    ProductResponse result = productServiceImpl.updateProductById(id, req);

    assertEquals("laptop-pro", result.getName());
    assertEquals(200.0, result.getPrice());
    assertEquals(5L, result.getQuantity());
    assertEquals("Electronics", result.getCategoryName());
    verify(productRepository).save(any(Product.class));
}

    @Test
@DisplayName("deleteProduct - id không tồn tại -> ResourceNotFoundException")
void deleteProduct_idNotFound_throwsResourceNotFound() {
    Long id = 99L;
    when(productRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
            () -> productServiceImpl.deleteProduct(id));

    verify(productRepository, never()).deleteById(anyLong());
}

@Test
@DisplayName("deleteProduct - hợp lệ -> gọi deleteById 1 lần")
void deleteProduct_valid_callsDeleteOnce() {
    Long id = 1L;
    Category category = new Category(1L, "Electronics");

    Product entity = Product.builder()
            .id(id)
            .name("laptop")
            .price(150.0)
            .quantity(10L)
            .category(category)
            .build();
    when(productRepository.findById(id)).thenReturn(Optional.of(entity));

    productServiceImpl.deleteProduct(id);

    verify(productRepository, times(1)).deleteById(id);
}

@Test
@DisplayName("getAllProducts - rỗng -> trả list rỗng")
void getAllProducts_empty_returnsEmptyList() {
    when(productRepository.findAll()).thenReturn(List.of());

    List<ProductResponse> rs = productServiceImpl.getAllProducts();

    assertNotNull(rs);
    assertTrue(rs.isEmpty());
    verify(productRepository).findAll();
    verify(productMapper, never()).toResponse(any());
}

@Test
@DisplayName("getAllProducts - có dữ liệu -> map đúng")
void getAllProducts_hasData_mapsCorrectly() {
    Category cat = new Category(1L, "Electronics");

    Product p1 = Product.builder().id(1L).name("laptop").price(150.0).quantity(10L).category(cat).build();
    Product p2 = Product.builder().id(2L).name("phone").price(800.0).quantity(5L).category(cat).build();

    when(productRepository.findAll()).thenReturn(List.of(p1, p2));

    // mapper: an toàn dùng any()
    when(productMapper.toResponse(any(Product.class))).thenAnswer(inv -> {
        Product p = inv.getArgument(0);
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .categoryName(p.getCategory().getName())
                .build();
    });

    List<ProductResponse> rs = productServiceImpl.getAllProducts();

    assertEquals(2, rs.size());
    assertEquals("laptop", rs.get(0).getName());
    assertEquals("phone",  rs.get(1).getName());
    verify(productRepository).findAll();
    verify(productMapper, times(2)).toResponse(any(Product.class));
}

}
