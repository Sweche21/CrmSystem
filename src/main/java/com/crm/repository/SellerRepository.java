package com.crm.repository;

import com.crm.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByName(String name);

    @Query("SELECT s.id, s.name, SUM(t.amount) " +
            "FROM Seller s JOIN s.transactions t " +
            "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.id, s.name " +
            "HAVING SUM(t.amount) < :minAmount")
    List<Object[]> findSellersWithTotalLessThan(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate,
                                                @Param("minAmount") BigDecimal minAmount);

}