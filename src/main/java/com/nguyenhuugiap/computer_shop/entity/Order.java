package com.nguyenhuugiap.computer_shop.entity;

import com.nguyenhuugiap.computer_shop.enums.OrderStatus;
import com.nguyenhuugiap.computer_shop.enums.PaymentMethod;
import com.nguyenhuugiap.computer_shop.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseEntity {
    @Column(name = "order_code", unique = true, nullable = false)
    String orderCode;

    // Tiền bạc
    @Column(name = "total_amount")
    BigDecimal totalAmount;

    @Column(name = "shipping_fee")
    BigDecimal shippingFee;

    @Column(name = "final_amount")
    BigDecimal finalAmount;


    // Thông tin giao hàng
    @Column(name = "shipping_name")
    String shippingName;

    @Column(name = "shipping_phone")
    String shippingPhone;

    @Column(name = "shipping_address")
    String shippingAddress;

    String note;

    // Trạng thái tổng thể
    @Builder.Default
    @Enumerated(EnumType.STRING)
    OrderStatus status = OrderStatus.PENDING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    PaymentMethod paymentMethod = PaymentMethod.VNPAY;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(
            mappedBy = "order",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @ToString.Exclude
    List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(
            mappedBy = "order",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    List<OrderStatusHistory> statusHistories = new ArrayList<>();

    @OneToMany(
            mappedBy = "order",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    List<PaymentTransaction> paymentTransactions = new ArrayList<>();

    // Required to Optimistic Locking to prevent race conditions
    @Version
    Long version;

    public boolean canAutoConfirm(){
        if(this.status != OrderStatus.PENDING) return false;

        // Auto confirm if COD or if online payment is fully paid
        return this.paymentMethod == PaymentMethod.COD ||
                this.paymentStatus == PaymentStatus.PAID;
    }

    // Verify if state transition is valid to prevent logic flow errors.
    public boolean isValidTransition(OrderStatus newStatus) {
        if (this.status == OrderStatus.CANCELLED || this.status == OrderStatus.RETURNED) {
            return false; // Cannot change state of finalized orders
        }
        if (this.status == OrderStatus.PENDING && newStatus == OrderStatus.CONFIRMED) {
            return true; // Valid path
        }
        return false;
    }


    // Helper methods cho Status History
    public void addStatusHistory(OrderStatusHistory history) {
        if (statusHistories == null) statusHistories = new ArrayList<>();
        statusHistories.add(history);
        history.setOrder(this);
    }

    // Helper methods cho Payment Transaction
    public void addPaymentTransaction(PaymentTransaction transaction) {
        if (paymentTransactions == null) paymentTransactions = new ArrayList<>();
        paymentTransactions.add(transaction);
        transaction.setOrder(this);
    }

    // Helper methods cho Order Item
    public void addOrderItem(OrderItem item) {
        if (orderItems == null) orderItems = new ArrayList<>();
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }

}
