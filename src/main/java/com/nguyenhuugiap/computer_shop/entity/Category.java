package com.nguyenhuugiap.computer_shop.entity;

import com.nguyenhuugiap.computer_shop.enums.CategoryStatus;
import com.nguyenhuugiap.computer_shop.utils.SlugUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@SuperBuilder
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)

public class Category extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    Set<Category> children = new HashSet<>();

    @Column(nullable = false, length = 100)
    String name;
    @Column(nullable = false, unique = true)
    String slug;
    @Column(columnDefinition = "TEXT")
    String description;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    CategoryStatus status = CategoryStatus.ACTIVE;

    // tu dong tao slug
    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (this.name != null && !this.name.isEmpty()) {
            slug = SlugUtils.toSlug(name);
        }
    }
}
