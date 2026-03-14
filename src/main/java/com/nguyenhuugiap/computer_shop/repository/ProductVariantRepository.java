package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.ProductVariant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    boolean existsBySkuCode(String skuCode);

    @EntityGraph(attributePaths = {"attributeValues"})
    List<ProductVariant> findAllByProduct_Id(long productId);

    Optional<ProductVariant> findByIdAndProductId(long variantId, long productId);
}
