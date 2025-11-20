package com.pradumcodes.store.controller;

import com.pradumcodes.store.entity.Product;
import com.pradumcodes.store.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}