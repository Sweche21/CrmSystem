package com.crm.repository;

import com.crm.entity.PaymentType;
import com.crm.entity.Seller;
import com.crm.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void findBySellerId_ShouldReturnSellerTransactions() {

        Seller seller = new Seller("Иван Петров", "ivan@mail.com");
        entityManager.persist(seller);
        entityManager.flush();

        Transaction transaction1 = new Transaction(seller, new BigDecimal("1000"), PaymentType.CARD);
        Transaction transaction2 = new Transaction(seller, new BigDecimal("2000"), PaymentType.CASH);
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.flush();


        List<Transaction> transactions = transactionRepository.findBySellerId(seller.getId());

        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> t.getSeller().getId().equals(seller.getId())));
    }

    @Test
    void findBySellerIdAndPeriod_ShouldReturnFilteredTransactions() {

        Seller seller = new Seller("Иван Петров", "ivan@mail.com");
        entityManager.persist(seller);
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now();
        Transaction transaction1 = new Transaction(seller, new BigDecimal("1000"), PaymentType.CARD);
        transaction1.setTransactionDate(now.minusDays(1));

        Transaction transaction2 = new Transaction(seller, new BigDecimal("2000"), PaymentType.CASH);
        transaction2.setTransactionDate(now.minusDays(5)); // Outside period

        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.flush();

        LocalDateTime startDate = now.minusDays(3);
        LocalDateTime endDate = now;


        List<Transaction> transactions = transactionRepository.findBySellerIdAndPeriod(seller.getId(), startDate, endDate);

        assertEquals(1, transactions.size());
        assertEquals(new BigDecimal("1000"), transactions.get(0).getAmount());
    }

    @Test
    void findTopSellerByPeriod_ShouldReturnTopSeller() {

        Seller seller1 = new Seller("Иван Петров", "ivan@mail.com");
        Seller seller2 = new Seller("Петр Сидоров", "petr@mail.com");
        entityManager.persist(seller1);
        entityManager.persist(seller2);
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now();

        Transaction t1 = new Transaction(seller1, new BigDecimal("1000"), PaymentType.CARD);
        t1.setTransactionDate(now.minusDays(1));
        Transaction t2 = new Transaction(seller1, new BigDecimal("2000"), PaymentType.CASH);
        t2.setTransactionDate(now.minusDays(2));

        Transaction t3 = new Transaction(seller2, new BigDecimal("1500"), PaymentType.TRANSFER);
        t3.setTransactionDate(now.minusDays(1));

        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.persist(t3);
        entityManager.flush();

        LocalDateTime startDate = now.minusDays(10);
        LocalDateTime endDate = now;


        List<Object[]> results = transactionRepository.findTopSellerByPeriod(startDate, endDate);

        assertEquals(2, results.size()); // Both sellers should be returned, ordered by total amount
        assertEquals(seller1.getId(), results.get(0)[0]); // Seller1 should be first (3000 > 1500)
        assertEquals("Иван Петров", results.get(0)[1]);
        assertEquals(new BigDecimal("3000.00"), results.get(0)[2]);
    }

    @Test
    void findBySellerIdOrderByDate_ShouldReturnOrderedTransactions() {

        Seller seller = new Seller("Иван Петров", "ivan@mail.com");
        entityManager.persist(seller);
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now();
        Transaction transaction1 = new Transaction(seller, new BigDecimal("1000"), PaymentType.CARD);
        transaction1.setTransactionDate(now.minusDays(3));

        Transaction transaction2 = new Transaction(seller, new BigDecimal("2000"), PaymentType.CASH);
        transaction2.setTransactionDate(now.minusDays(1));

        Transaction transaction3 = new Transaction(seller, new BigDecimal("1500"), PaymentType.TRANSFER);
        transaction3.setTransactionDate(now.minusDays(2));

        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.persist(transaction3);
        entityManager.flush();


        List<Transaction> transactions = transactionRepository.findBySellerIdOrderByDate(seller.getId());

        assertEquals(3, transactions.size());

        assertEquals(now.minusDays(3), transactions.get(0).getTransactionDate());
        assertEquals(now.minusDays(2), transactions.get(1).getTransactionDate());
        assertEquals(now.minusDays(1), transactions.get(2).getTransactionDate());
    }

    @Test
    void save_ShouldPersistTransactionWithAutoGeneratedFields() {

        Seller seller = new Seller("Иван Петров", "ivan@mail.com");
        entityManager.persist(seller);
        entityManager.flush();

        Transaction transaction = new Transaction(seller, new BigDecimal("1000"), PaymentType.CARD);


        Transaction saved = transactionRepository.save(transaction);

        assertNotNull(saved.getId());
        assertNotNull(saved.getTransactionDate());
        assertEquals(seller, saved.getSeller());
        assertEquals(new BigDecimal("1000"), saved.getAmount());
        assertEquals(PaymentType.CARD, saved.getPaymentType());
    }

    @Test
    void count_ShouldReturnTransactionCount() {

        Seller seller = new Seller("Иван Петров", "ivan@mail.com");
        entityManager.persist(seller);
        entityManager.flush();

        Transaction transaction1 = new Transaction(seller, new BigDecimal("1000"), PaymentType.CARD);
        Transaction transaction2 = new Transaction(seller, new BigDecimal("2000"), PaymentType.CASH);
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.flush();


        long count = transactionRepository.count();

        assertEquals(2, count);
    }
}