package com.pradumcodes.ledger.repository;

import com.pradumcodes.ledger.entity.Shop;
import com.pradumcodes.ledger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query("select t from Transaction t where t.customer.shop.id = :shopId")
    List<Transaction> findByShopId(@Param("shopId") Long shopId);

    List<Shop> findByShopName(String shopName);
}
