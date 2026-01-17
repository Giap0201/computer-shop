package com.nguyenhuugiap.computer_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserRole {
    @EmbeddedId
    UserRoleId id = new UserRoleId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    Role role;
    @Builder.Default
    @Column(name = "assigned_at")
    LocalDateTime assignedAt = LocalDateTime.now();
}
