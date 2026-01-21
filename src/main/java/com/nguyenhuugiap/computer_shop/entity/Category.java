package com.nguyenhuugiap.computer_shop.entity;

import com.nguyenhuugiap.computer_shop.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Table(name = "categories")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    Set<Category> children = new HashSet<>();

    @Column(nullable = false, unique = true, length = 100)
    String name;
    @Column(nullable = false, unique = true)
    String slug;
    @Enumerated(EnumType.STRING)
    CategoryStatus status;
    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
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
