package com.pradumcodes.ledger.service;

import com.pradumcodes.ledger.entity.Customer;
import com.pradumcodes.ledger.entity.Transaction;
import com.pradumcodes.ledger.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByShopId(Long shopId) {
        return transactionRepository.findByCustomerShopId(shopId);
    }

    @Transactional
    public Transaction createForCustomer(Customer customer, BigDecimal amount, Boolean isCredit) {
        Transaction tx = new Transaction(customer, amount, isCredit);
        return transactionRepository.save(tx);
    }
}
