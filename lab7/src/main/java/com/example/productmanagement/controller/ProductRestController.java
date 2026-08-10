package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    // GET all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> list = productService.getAllProducts();
        return ResponseEntity.ok(list); // 200 OK + JSON array
    }

    // GET product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);

        return product
                .map(ResponseEntity::ok) // 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404 NOT FOUND
    }

    // CREATE new product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.saveProduct(product);

            return ResponseEntity.created(
                    URI.create("/api/products/" + savedProduct.getId())
            ).body(savedProduct); // 201 CREATED + JSON body

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 BAD REQUEST
        }
    }

    // UPDATE existing product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product productData) {

        Optional<Product> existing = productService.getProductById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }

        Product product = existing.get();

        // Update fields
        product.setProductCode(productData.getProductCode());
        product.setName(productData.getName());
        product.setPrice(productData.getPrice());
        product.setQuantity(productData.getQuantity());
        product.setCategory(productData.getCategory());
        product.setDescription(productData.getDescription());

        Product updatedProduct = productService.saveProduct(product);

        return ResponseEntity.ok(updatedProduct); // 200 OK
    }

    // DELETE product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);

        if (product.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204 NO CONTENT
    }
}