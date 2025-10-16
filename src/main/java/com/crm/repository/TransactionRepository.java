package com.crm.repository;

import com.crm.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySellerId(Long sellerId);

    @Query("SELECT t FROM Transaction t WHERE t.seller.id = :sellerId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findBySellerIdAndPeriod(@Param("sellerId") Long sellerId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s.id, s.name, SUM(t.amount) " +
            "FROM Transaction t JOIN t.seller s " +
            "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.id, s.name " +
            "ORDER BY SUM(t.amount) DESC")
    List<Object[]> findTopSellerByPeriod(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE t.seller.id = :sellerId ORDER BY t.transactionDate")
    List<Transaction> findBySellerIdOrderByDate(@Param("sellerId") Long sellerId);


    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t")
    BigDecimal getTotalSalesAmount();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.paymentType = :paymentType")
    BigDecimal getTotalSalesByPaymentType(@Param("paymentType") String paymentType);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.seller.id = :sellerId")
    Long countBySellerId(@Param("sellerId") Long sellerId);
}