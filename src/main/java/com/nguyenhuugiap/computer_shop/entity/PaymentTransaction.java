package com.nguyenhuugiap.computer_shop.entity;


import com.nguyenhuugiap.computer_shop.enums.PaymentMethod;
import com.nguyenhuugiap.computer_shop.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class PaymentTransaction extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    PaymentMethod paymentMethod;

    @Column(name = "transaction_code", unique = true)
    String transactionCode; // Mã GD của VNPay. Null nếu là COD

    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "provider_response")
    String providerResponse; // Lưu cục JSON phản hồi từ VNPay

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order;

}
