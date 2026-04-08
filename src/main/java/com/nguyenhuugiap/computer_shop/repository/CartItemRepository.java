package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE CartItem c set c.quantity = c.quantity+ :addedQuantity " +
            "where c.cart.id = :cartId and c.productVariant.id=:variantId")
    int addQuantityToExistingItem(@Param("cartId") Long cartId,
                                  @Param("variantId") Long variantId,
                                  @Param("addedQuantity") Long addedQuantity);

    @Query("""
            SELECT DISTINCT ci
            FROM CartItem ci
            JOIN FETCH ci.productVariant pv
            JOIN FETCH pv.product
            LEFT JOIN FETCH pv.attributeValues av
            LEFT JOIN FETCH av.attributeDefinition
            WHERE ci.cart.id = :cartId
            """)
    List<CartItem> findAllByCart_Id(Long cartId);

    void deleteAllByCart_Id(Long id);
}
