package com.nguyenhuugiap.computer_shop.entity;

import com.nguyenhuugiap.computer_shop.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, length = 200, unique = true)
    String name;
    @Column(nullable = false, unique = true)
    String slug;
    @Column(nullable = false, unique = true, length = 50)
    String code;
    @Column(length = 500)
    String thumbnail;
    @Column(columnDefinition = "TEXT")
    String description;
    @Enumerated(EnumType.STRING)
    ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    @ToString.Exclude
    Brand brand;

    // Tu dong tao slug khi khong set
    @PrePersist
    private void prePersist() {
        // Tao slug tu  name, neu ma slug khong co -> tao moi
        if (this.name != null && this.slug == null) {
            this.slug = this.name.toLowerCase()
                    .replace(" ", "-")
                    .replace("đ", "d")
                    .replace("Đ", "d")
                    .replaceAll("[^a-z0-9-]", "");
        }
        if (this.code == null) {
            this.code = "p" + System.currentTimeMillis();
        }
    }
}
