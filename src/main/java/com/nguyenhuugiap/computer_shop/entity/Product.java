package com.nguyenhuugiap.computer_shop.entity;

import com.nguyenhuugiap.computer_shop.enums.ProductStatus;
import com.nguyenhuugiap.computer_shop.utils.SlugUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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

    @Builder.Default
    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, // luu cha tu luu con va xoa cha tu xoa con
            orphanRemoval = true // Xoa variant khoi list thi xoa luon trong db
    )
    @ToString.Exclude
    Set<ProductVariant> productVariants = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @ToString.Exclude
    Set<ProductImage> productImages = new HashSet<>();

    public void addImage(ProductImage image) {
        image.setProduct(this);
        productImages.add(image);
    }

    public void removeImage(ProductImage image) {
        productImages.remove(image);
        image.setProduct(null);
    }

    public void addVariant(ProductVariant variant) {
        variant.setProduct(this);
        productVariants.add(variant);
    }

    public void removeVariant(ProductVariant variant) {
        productVariants.remove(variant);
        variant.setProduct(null);
    }

    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (this.name != null && !this.name.isEmpty()) {
            slug = SlugUtils.toSlug(this.name);
        }
    }
}
