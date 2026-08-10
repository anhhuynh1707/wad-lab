package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    List<Product> getAllProducts();
    
    Optional<Product> getProductById(Long id);
    
    Product saveProduct(Product product);
    
    void deleteProduct(Long id);
    
    List<Product> searchProducts(String keyword);
    
    List<Product> getProductsByCategory(String category);

    List<Product> getProductsByCategory(String category, Sort sort);

    List<Product> advancedSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    List<String> getAllCategories();

    List<Product> getAllProducts(Sort sort);

    long countProductsByCategory(String category);
    
    BigDecimal getTotalValue();

    BigDecimal getAveragePrice();

    List<Product> getLowStockProducts(int threshold);

    List<Product> getRecentProducts();
}