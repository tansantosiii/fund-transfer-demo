package com.demo.fundtransfer;

import com.demo.fundtransfer.entity.Account;
import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import com.demo.fundtransfer.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class PessimisticLockingTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private static final Account usdAccount = new Account(1L, "Alice", BigDecimal.valueOf(1000), CurrencyCodeEnum.USD);
    private static final Account audAccount = new Account(2L, "Bob", BigDecimal.valueOf(500), CurrencyCodeEnum.AUD);

    @BeforeEach
    void init() {
        accountRepository.save(usdAccount);
        accountRepository.save(audAccount);
    }

    /**
     * Integration test for pessimistic locking
     * @throws Exception
     */
    @Test
    void test_findByIdAndLock() throws Exception {
        CountDownLatch cdLatch = new CountDownLatch(1);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            AccountRepository repository = new JpaRepositoryFactory(entityManager).getRepository(AccountRepository.class);

            repository.findByIdAndLock(1L);
            repository.findByIdAndLock(2L);

            cdLatch.countDown();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            entityManager.getTransaction().commit();
            entityManager.close();
        });

        executorService.submit(() -> {
            try {
                cdLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            AccountRepository repository = new JpaRepositoryFactory(entityManager).getRepository(AccountRepository.class);

            long start = System.currentTimeMillis();
            repository.findByIdAndLock(2L);
            repository.findByIdAndLock(1L);
            long elapsed = System.currentTimeMillis() - start;

            entityManager.getTransaction().commit();
            entityManager.close();

            System.out.println("Thread 2 waited for " + elapsed + " ms");
            assertTrue(elapsed >= 5000);
        });

        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);
    }

}
