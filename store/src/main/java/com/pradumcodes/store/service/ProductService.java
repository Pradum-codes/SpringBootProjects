package com.pradumcodes.store.service;

import com.pradumcodes.store.entity.Product;
import com.pradumcodes.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> findAll() {
        return repo.findAll();
    }

    public Product save(Product product) {
        return repo.save(product);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public Optional<Product> findById(Long id) {
        return repo.findById(id);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        return repo.findById(id).map(product -> {
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());
            return repo.save(product);
        }).orElse(null);
    }

    public long countProducts() {
        return repo.count();
    }
}
