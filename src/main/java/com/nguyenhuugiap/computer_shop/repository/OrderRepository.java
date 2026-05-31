package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    boolean existsByOrderCode(String orderCode);

    Optional<Order> findByOrderCode(String orderCode);


    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.paymentStatus = 'UNPAID' AND o.paymentMethod = 'VNPAY' AND o.createdAt <= :timeoutThreshold")
    List<Order> findExpiredOrders(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);

    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Long id);
}
