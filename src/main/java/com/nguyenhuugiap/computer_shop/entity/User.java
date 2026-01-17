package com.nguyenhuugiap.computer_shop.entity;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, unique = true)
    String email;
    @Column(nullable = false, unique = true, length = 20)
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
    UserStatus status;
    @Column(name = "preferred_size", length = 50)
    String preferredSize;
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<UserRole> userRoles = new HashSet<>();
    public void addRole(Role role){
        UserRole userRole = UserRole.builder()
                .id(new UserRoleId(this.id, role.getId()))
                .user(this)
                .role(role)
                .build();
        if(this.userRoles == null) this.userRoles = new HashSet<>();
        this.userRoles.add(userRole);
    }

}
