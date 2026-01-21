package com.nguyenhuugiap.computer_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Brand extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, unique = true, length = 100)
    String name;
    @Column(unique = true, nullable = false)
    String slug;
    @Column(name = "logo_url")
    String logoUrl;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    Set<Product> products = new HashSet<>();

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
    }
}
