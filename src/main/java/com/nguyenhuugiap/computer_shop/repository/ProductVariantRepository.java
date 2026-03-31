package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.ProductVariant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    boolean existsBySkuCode(String skuCode);

    @EntityGraph(attributePaths = {"attributeValues"})
    List<ProductVariant> findAllByProduct_Id(long productId);

    Optional<ProductVariant> findByIdAndProductId(long variantId, long productId);

    @Modifying
    @Query("UPDATE ProductVariant pv set pv.stockQuantity = pv.stockQuantity - :quantity " +
            " where pv.id = :variantId and pv.stockQuantity >= :quantity")
    Long deductStock(@Param("variantId") Long variantId,
                    @Param("quantity") Long quantity);

    @Modifying
    @Query("UPDATE ProductVariant pv set pv.stockQuantity = pv.stockQuantity + :quantity " +
            " where pv.id = :variantId")
    Long addStock(@Param("variantId") Long variantId, @Param("quantity") Long quantity);

    @Query("SELECT pv FROM ProductVariant pv JOIN FETCH pv.product" +
            " join fetch pv.attributeValues av" +
            " left join fetch av.attributeDefinition" +
            " WHERE pv.id IN :variantIds")
    List<ProductVariant> findVariantsWithProductByIds(@Param("variantIds") List<Long> variantIds);

}
