package com.nguyenhuugiap.computer_shop.entity;

import com.nguyenhuugiap.computer_shop.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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
    @Column(columnDefinition = "TEXT")
    String description;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    CategoryStatus status = CategoryStatus.ACTIVE;
    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    Set<Product> products = new HashSet<>();

    // tu dong tao slug
    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (this.slug == null && this.name != null && !this.name.isEmpty()) {
            slug = toSlug(name);
        }
    }

    private String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        String str = input.toLowerCase();
        str = str.replaceAll("đ", "d");
        String normalizer = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        str = pattern.matcher(normalizer).replaceAll("");
        str = str.replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
        return str;
    }

}
