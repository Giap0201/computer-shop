package com.nguyenhuugiap.computer_shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
@Embeddable
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserRoleId implements Serializable {
    @Column(name = "user_id")
    Long userId;
    @Column(name = "role_id")
    Integer roleId;
}
