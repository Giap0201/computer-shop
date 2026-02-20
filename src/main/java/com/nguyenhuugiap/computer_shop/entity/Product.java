package com.nguyenhuugiap.computer_shop.entity;

import com.nguyenhuugiap.computer_shop.enums.ProductStatus;
import com.nguyenhuugiap.computer_shop.utils.SlugUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
public class Product extends BaseEntity {
    @Column(nullable = false, unique = true, length = 255)
    String name;

    @Column(nullable = false, unique = true)
    String slug;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "thumbnail_url")
    String thumbnailUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "min_price")
    BigDecimal minPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @JoinColumn(name = "brand_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Brand brand;

    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (this.name != null && !this.name.isEmpty()) {
            slug = SlugUtils.toSlug(this.name);
        }
    }
}
