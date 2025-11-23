package com.software.backend.specification;

import com.software.backend.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Product> quantityGreaterThan(Long min) {
        return (root, query, cb) -> {
            if (min == null) return null;
            return cb.greaterThanOrEqualTo(root.get("quantity"), min);
        };
    }

    public static Specification<Product> quantityLessThan(Long max) {
        return (root, query, cb) -> {
            if (max == null) return null;
            return cb.lessThanOrEqualTo(root.get("quantity"), max);
        };
    }

    public static Specification<Product> priceGreaterThan(Double min) {
        return (root, query, cb) -> {
            if (min == null) return null;
            return cb.greaterThanOrEqualTo(root.get("price"), min);
        };
    }

    public static Specification<Product> priceLessThan(Double max) {
        return (root, query, cb) -> {
            if (max == null) return null;
            return cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }
}
