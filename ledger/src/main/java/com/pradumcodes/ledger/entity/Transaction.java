package com.pradumcodes.ledger.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "is_credit", nullable = false)
    private Boolean isCredit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Transaction() {}

    public Transaction(Customer customer, BigDecimal amount, Boolean isCredit) {
        this.customer = customer;
        this.amount = amount;
        this.isCredit = isCredit;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Boolean getIsCredit() {
        return isCredit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setIsCredit(Boolean isCredit) {
        this.isCredit = isCredit;
    }
}
