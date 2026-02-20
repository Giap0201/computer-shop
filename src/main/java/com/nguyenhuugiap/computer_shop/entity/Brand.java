package com.nguyenhuugiap.computer_shop.entity;

import com.nguyenhuugiap.computer_shop.utils.SlugUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Brand extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    String name;

    @Column(unique = true, nullable = false)
    String slug;

    String logo;

    // Tu dong cap nhap slug
    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (this.name != null && !this.name.isEmpty()) {
            this.slug = SlugUtils.toSlug(name);
        }
    }
}
