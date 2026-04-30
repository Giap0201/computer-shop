package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.PaymentTransaction;
import com.nguyenhuugiap.computer_shop.enums.TransactionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    @Modifying
    @Query("update PaymentTransaction p set p.status='EXPIRED' where p.order.id = :orderId and p.status = 'PENDING'")
    void expireOldPendingTransactions(@Param(("orderId")) Long orderId);

    List<PaymentTransaction> findByOrderIdAndStatusOrderByCreatedAtDesc(Long id, TransactionStatus transactionStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentTransaction p WHERE p.transactionRef = :transactionRef")
    Optional<PaymentTransaction> findByTransactionRefForUpdate(@Param("transactionRef") String transactionRef);
}
