package com.software.backend.config;

import com.software.backend.entity.Category;
import com.software.backend.entity.Product;
import com.software.backend.entity.User;
import com.software.backend.repository.CategoryRepository;
import com.software.backend.repository.ProductRepository;
import com.software.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataSeederConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSeederConfig.class);

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {

        return args -> {
            log.info("Starting database seeding process...");
            seedUsers(userRepository, passwordEncoder);
            List<Category> categories = seedCategories(categoryRepository);

            if (!categories.isEmpty()) {
                seedProducts(productRepository, categories);
            }

            log.info("Database seeding finished.");
        };
    }

    private void seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        final String ADMIN_USERNAME = "admin";
        final String USER_PASSWORD = "123456";

        if (userRepository.findByUsername(ADMIN_USERNAME).isEmpty()) {
            User admin = User.builder()
                    .username(ADMIN_USERNAME)
                    .password(passwordEncoder.encode(USER_PASSWORD))
                    .build();
            userRepository.save(admin);
            log.info("Seeded Admin user: {}. Password: {}", ADMIN_USERNAME, USER_PASSWORD);
        } else {
            log.info("Admin user already exists.");
        }
    }

    private List<Category> seedCategories(CategoryRepository categoryRepository) {
        if (categoryRepository.count() == 0) {
            Category electronics = Category.builder().name("Electronics").build();
            Category clothing = Category.builder().name("Clothing").build();
            Category books = Category.builder().name("Books").build();

            List<Category> categories = List.of(electronics, clothing, books);
            categoryRepository.saveAll(categories);
            log.info("Seeded {} categories.", categories.size());
            return categories;
        }
        return categoryRepository.findAll();
    }

    private void seedProducts(ProductRepository productRepository, List<Category> categories) {
        if (productRepository.count() == 0) {
            Category electronics = categories.stream().filter(c -> c.getName().equals("Electronics")).findFirst().orElse(null);
            Category clothing = categories.stream().filter(c -> c.getName().equals("Clothing")).findFirst().orElse(null);

            if (electronics != null && clothing != null) {
                Product laptop = Product.builder()
                        .name("Laptop A")
                        .quantity(15L)
                        .price(1200.00)
                        .category(electronics)
                        .build();

                Product tShirt = Product.builder()
                        .name("Cotton T-Shirt")
                        .quantity(150L)
                        .price(25.99)
                        .category(clothing)
                        .build();

                productRepository.saveAll(List.of(laptop, tShirt));
                log.info("Seeded 2 sample products.");
            }
        }
    }
}