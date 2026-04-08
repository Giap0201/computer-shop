package com.nguyenhuugiap.computer_shop.entity;

import com.nguyenhuugiap.computer_shop.enums.VariantStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariant extends BaseEntity {
    @Column(name = "sku_code", unique = true, nullable = false)
    String skuCode;

    BigDecimal price;

    @Column(name = "stock_quantity")
    Long stockQuantity;

    @Version
    Long version;

    @Column(name = "image_url")
    String imageUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    VariantStatus status = VariantStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @OneToMany(
            mappedBy = "variant",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    Set<VariantAttributeValue> attributeValues = new HashSet<>();

    public void addAttributeValue(VariantAttributeValue attributeValue) {
        attributeValues.add(attributeValue);
        attributeValue.setVariant(this);
    }

}
