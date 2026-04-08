package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @EntityGraph(attributePaths = {
            "cartItems",
            "cartItems.productVariant",
            "cartItems.productVariant.product",
            "cartItems.productVariant.attributeValues"
    })
    Optional<Cart> findBySessionId(String sessionId);

    @EntityGraph(attributePaths = {
            "cartItems",
            "cartItems.productVariant",
            "cartItems.productVariant.product",
            "cartItems.productVariant.attributeValues"
    })
    Optional<Cart> findByUser_Id(Long userId);


    List<Cart> findAllByUpdatedAtBeforeAndUserIsNull(LocalDateTime threshold);
}
