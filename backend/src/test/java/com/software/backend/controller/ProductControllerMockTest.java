package com.software.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.backend.dto.response.ProductResponse;
import com.software.backend.exception.ResourceNotFoundException;
import com.software.backend.dto.request.ProductRequest;
import com.software.backend.service.JwtService;
import com.software.backend.service.ProductService;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // bỏ qua security filter
class ProductControllerMockTest {
    
    
    
    @Autowired
    private MockMvc mockMvc;

   
 @MockBean
    private JwtService jwtService;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("API get all products - trả về danh sách sản phẩm")
    void getAllProducts_returnsProductList() throws Exception {
        // Arrange
       

        ProductResponse product1 = new ProductResponse(1L, "Product 1", 20L, 10.0, "electronics");
        ProductResponse product2 = new ProductResponse(2L, "Product 2", 20L, 20.0, "electronics");
when(productService.getAllProducts()).thenReturn(List.of(product1, product2));
        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("Product 1"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].name").value("Product 2"));
        
        verify(productService, times(1)).getAllProducts();
    }
    
        @Test
    @DisplayName("API get product by id - trả về sản phẩm với id hợp lệ")
    void getProductById_validId_returnsProduct() throws Exception {
        // Arrange
        Long productId = 1L;
        ProductResponse product = new ProductResponse(1L, "Product 1", 20L, 10.0, "electronics");
        when(productService.getById(productId)).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(productId))
                .andExpect(jsonPath("$.data.name").value("Product 1"));

        verify(productService, times(1)).getById(productId);
    }

    @Test
    @DisplayName("API get product by id - trả về 404 với id không tồn tại")
    void getProductById_invalidId_returnsNotFound() throws Exception {
        // Arrange
        Long productId = 999L;
        when(productService.getById(productId))
                .thenThrow(new ResourceNotFoundException("Product not found with id: " + productId));

        // Act & Assert
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Product not found with id: " + productId));

        verify(productService, times(1)).getById(productId);
    }

    
    @Test
    @DisplayName("API create product - trả về sản phẩm đã tạo")
    void createProduct_validRequest_returnsCreatedProduct() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest("New Product", 50L, 15.0, "books");
        ProductResponse createdProduct = new ProductResponse(1L, "New Product", 50L, 15.0, "books");

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(createdProduct);
        
        mockMvc.perform(post("/api/products")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("New Product"));
        

        verify(productService, times(1)).createProduct(any(ProductRequest.class));

}
 @Test
    @DisplayName("POST /api/products - trả về 400 khi request không hợp lệ")
    void create_invalidRequest_returns400() throws Exception {
        // name bị null → vi phạm @Valid
        ProductRequest request = new ProductRequest(null, 50L, 15.0, "books");

        mockMvc.perform(post("/api/products")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

        // PUT /api/products/{id}
    @Test
    @DisplayName("API update - trả về sản phẩm đã cập nhật")
    void update_returnsUpdatedProduct() throws Exception {
         ProductRequest request = new ProductRequest("New Product", 50L, 15.0, "books");
        ProductResponse createdProduct = new ProductResponse(1L, "New Product", 50L, 15.0, "books");


        when(productService.updateProductById(eq(1L), any(ProductRequest.class))).thenReturn(createdProduct);

        mockMvc.perform(put("/api/products/{id}", 1L)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("New Product"));

        verify(productService, times(1)).updateProductById(eq(1L), any(ProductRequest.class));
    }
 @Test
    @DisplayName("PUT /api/products/{id} - trả về 404 khi không tìm thấy")
    void update_notFound_returns404() throws Exception {
         ProductRequest request = new ProductRequest("New Product", 50L, 15.0, "books");

        when(productService.updateProductById(eq(999L), any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        mockMvc.perform(put("/api/products/{id}", 999L)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }

    @Test
    @DisplayName("API Delete - xóa sản phẩm thành công")
    void delete_existingProduct_deletesSuccessfully() throws Exception {
        // Arrange
        Long productId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(productService, times(1)).deleteProduct(productId);
    }
     // ❌ Error case: DELETE not found
    @Test
    @DisplayName("DELETE /api/products/{id} - trả về 404 khi không tìm thấy")
    void delete_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }


}
