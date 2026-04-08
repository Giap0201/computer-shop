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
    @Column(name = "payment_method", nullable = false)
    PaymentMethod paymentMethod;

    // Mã hệ thống mình sinh ra (vnp_TxnRef). Ví dụ: ORD-123-1712345
    @Column(name = "transaction_ref", unique = true, nullable = false)
    String transactionRef;

    // Mã VNPay trả về (vnp_TransactionNo). Null lúc tạo PENDING.
    @Column(name = "transaction_code")
    String transactionCode;

    @Column(nullable = false)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    TransactionStatus status = TransactionStatus.PENDING;

    // Dùng columnDefinition = "TEXT" để không bị lỗi tràn độ dài chuỗi
    @Column(name = "provider_response", columnDefinition = "TEXT")
    String providerResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;
}