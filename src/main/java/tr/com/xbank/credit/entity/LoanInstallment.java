package tr.com.xbank.credit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "loan_installments")
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @NotNull
    @PositiveOrZero(message = "Amount must be zero or positive")
    private BigDecimal amount;

    @PositiveOrZero(message = "Paid amount must be zero or positive")
    private BigDecimal paidAmount;

    @NotNull
    private LocalDate dueDate;

    private LocalDate paymentDate;

    private boolean isPaid;

    @PrePersist
    protected void onCreate() {
        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }
        isPaid = false;
    }
}
