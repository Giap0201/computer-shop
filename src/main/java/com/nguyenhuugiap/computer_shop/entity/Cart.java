package com.nguyenhuugiap.computer_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Cart extends BaseEntity {
    @Column(name = "session_id", unique = true, nullable = true)
    String sessionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    User user;

    @OneToMany(
            mappedBy = "cart",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, // luu cha tu luu con va xoa cha tu xoa con
            orphanRemoval = true // Xoa variant khoi list thi xoa luon trong db
    )
    List<CartItem> cartItems = new ArrayList<>();

    public void addItem(CartItem item){
        if(cartItems == null) cartItems = new ArrayList<>();
        cartItems.add(item);
        item.setCart(this);
    }
}