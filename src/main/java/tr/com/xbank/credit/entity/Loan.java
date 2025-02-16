package tr.com.xbank.credit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @Positive(message = "Loan amount must be positive")
    private BigDecimal loanAmount;

    @NotNull
    private Integer numberOfInstallment;

    private BigDecimal interestRate;

    private LocalDateTime createDate;

    private boolean isPaid;

    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
        isPaid = false;
    }
}
