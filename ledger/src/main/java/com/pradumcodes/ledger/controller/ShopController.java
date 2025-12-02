package com.pradumcodes.ledger.controller;

import com.pradumcodes.ledger.entity.Customer;
import com.pradumcodes.ledger.entity.Shop;
import com.pradumcodes.ledger.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    // GET /api/shops
    @GetMapping
    public List<Shop> findAll() {
        return shopService.findAll();
    }

    // POST /api/shops
    @PostMapping
    public Shop save(@RequestBody @Valid Shop shop) {
        return shopService.save(shop);
    }

    // POST /api/shops/{shopId}/customers
    @PostMapping("/{shopId}/customers")
    public Customer createCustomer(
            @PathVariable Long shopId,
            @RequestBody @Valid Customer customerRequest
    ) {
        // 1. Find the shop or return 404
        Shop shop = shopService.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Shop not found with id: " + shopId));

        // 2. Delegate actual creation to the service
        return shopService.createCustomer(
                shop,
                customerRequest.getName(),
                customerRequest.getEmail(),
                customerRequest.getPhoneNumber()
        );
    }
}
