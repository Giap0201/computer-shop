package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.PaymentTransaction;
import com.nguyenhuugiap.computer_shop.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByTransactionRef(String vnpTxnRef);

    List<PaymentTransaction> findByOrderIdAndStatus(Long id, TransactionStatus transactionStatus);

    List<PaymentTransaction> findByOrderIdAndStatusOrderByCreatedAtDesc(Long id, TransactionStatus transactionStatus);
}
