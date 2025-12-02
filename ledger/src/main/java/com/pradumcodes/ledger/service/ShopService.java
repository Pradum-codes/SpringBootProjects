package com.pradumcodes.ledger.service;

import com.pradumcodes.ledger.entity.Customer;
import com.pradumcodes.ledger.entity.Shop;
import com.pradumcodes.ledger.entity.Transaction;
import com.pradumcodes.ledger.repository.CustomerRepository;
import com.pradumcodes.ledger.repository.ShopRepository;
import com.pradumcodes.ledger.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public ShopService(ShopRepository shopRepository,
                       CustomerRepository customerRepository,
                       TransactionRepository transactionRepository) {
        this.shopRepository = shopRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Shop> findById(Long id) {
        return shopRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Shop> findAll() {
        return shopRepository.findAll();
    }

    @Transactional
    public Shop save(Shop shop) {
        return shopRepository.save(shop);
    }

    @Transactional
    public void delete(Shop shop) {
        shopRepository.delete(shop);
    }

    @Transactional
    public Customer createCustomer(Shop shop, String name, String email, String phone) {
        Customer newCustomer = new Customer(shop, name, email, phone);
        return customerRepository.save(newCustomer);
    }

    @Transactional
    public Transaction createTransaction(Customer customer, BigDecimal amount, Boolean isCredit) {
        // You can add extra validation here if you want
        Transaction newTransaction = new Transaction(customer, amount, isCredit);
        return transactionRepository.save(newTransaction);
    }
}
