package com.nguyenhuugiap.computer_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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
    String logo;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    Set<Product> products = new HashSet<>();

    // Tu dong cap nhap slug
    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (this.name != null && !this.name.isEmpty()) {
            this.slug = toSlug(name);
        }
    }

    private String toSlug(String input) {
        if (input.isEmpty()) {
            return null;
        }
        String str = input.toLowerCase();
        str = str.replaceAll("đ", "d");
        String normalizer = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        str = pattern.matcher(normalizer).replaceAll("");
        str = str.replaceAll("[^a-z0-9\\s-]", "");
        str = str.replaceAll("\\s+", "-");
        return str;
    }
}
