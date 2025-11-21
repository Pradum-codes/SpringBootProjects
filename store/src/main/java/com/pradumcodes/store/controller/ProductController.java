package com.pradumcodes.store.controller;

import com.pradumcodes.store.dto.ProductUpdateDto;
import com.pradumcodes.store.entity.Product;
import com.pradumcodes.store.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String viewProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "view";
    }

    @GetMapping("/add")
    public String addProducts() {
        return "add";
    }

    @PostMapping("/add")
    public String saveProduct(@RequestParam String name, @RequestParam double price) {
        productService.save(new Product(name, price));
        return "redirect:/products";
    }

    @PostMapping("/products/delete")
    public String deleteProduct(@RequestParam Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductUpdateDto dto) {

        Product product = new Product(dto.getName(), dto.getPrice()); // map DTO -> entity

        try {
            Product updated = productService.updateProduct(id, product);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        // productService.findById(...) should return the Product or null/throw if not found.
        // Adjust to your service API (Optional<Product> or Product).
        Optional<Product> product;
        try {
            product = productService.findById(id);
        } catch (RuntimeException ex) {
            return "redirect:/products";
        }
        if (product.isEmpty()) {
            return "redirect:/products";
        }
        // add the contained Product so the template can access product.id, name, price
        model.addAttribute("product", product.get());
        return "update";
    }

    @PostMapping("/products/{id}/edit")
    public String updateProductForm(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam double price) {

        try {
            productService.updateProduct(id, new Product(name, price));
        } catch (RuntimeException ex) {
            // If update fails, simply redirect back to the list (can be improved to show errors)
        }
        return "redirect:/products";
    }

}