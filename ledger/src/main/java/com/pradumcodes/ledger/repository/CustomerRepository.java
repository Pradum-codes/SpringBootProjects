package com.pradumcodes.ledger.repository;

import com.pradumcodes.ledger.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByShopId(Long shopId);
}
