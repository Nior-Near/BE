package nior_near.server.domain.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long price;
    private PaymentStatus paymentStatus;
    private String paymentUid;

    @Builder
    public Payment(Long price, PaymentStatus paymentStatus) {
        this.price = price;
        this.paymentStatus = paymentStatus;
    }

    public void changePaymentStatus(PaymentStatus paymentStatus, String paymentUid) {
        this.paymentStatus = paymentStatus;
        this.paymentUid = paymentUid;
    }
}
