package tr.com.xbank.credit.dto.request;

import jakarta.validation.constraints.*;
import tr.com.xbank.credit.validation.ValidInstallmentNumber;

import java.math.BigDecimal;

public record CreateLoanRequest(
        @NotNull(message = "Customer ID is required")
        @Positive(message = "Customer ID must be positive")
        Long customerId,

        @NotNull(message = "Loan amount is required")
        @Positive(message = "Loan amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Interest rate is required")
        @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1")
        @DecimalMax(value = "0.5", message = "Interest rate must be at most 0.5")
        BigDecimal interestRate,

        @NotNull(message = "Number of installments is required")
        @ValidInstallmentNumber
        Integer numberOfInstallments
) {
    public CreateLoanRequest {
        if (customerId == null || amount == null || interestRate == null || numberOfInstallments == null) {
            throw new IllegalArgumentException("All fields are required");
        }
    }
}
