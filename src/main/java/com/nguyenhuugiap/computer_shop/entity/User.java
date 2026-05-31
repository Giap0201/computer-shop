package com.nguyenhuugiap.computer_shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nguyenhuugiap.computer_shop.enums.AuthProvider;
import com.nguyenhuugiap.computer_shop.enums.Gender;
import com.nguyenhuugiap.computer_shop.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    String email;

    @Column(unique = true, length = 20)
    String phone;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "password_hash")
    String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    UserStatus status = UserStatus.ACTIVE;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    @Builder.Default
    Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", length = 20)
    @Builder.Default
    AuthProvider authProvider = AuthProvider.LOCAL;

    @Column(name = "provider_id")
    String providerId;

    @Column(name = "avatar_url")
    String avatarUrl;
}
