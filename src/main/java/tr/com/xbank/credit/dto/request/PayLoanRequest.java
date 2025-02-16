package tr.com.xbank.credit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PayLoanRequest(
        @NotNull(message = "Loan ID is required")
        @Positive(message = "Loan ID must be positive")
        Long loanId,

        @NotNull(message = "Payment amount is required")
        @Positive(message = "Payment amount must be positive")
        BigDecimal amount
) {
    public PayLoanRequest {
        if (loanId == null || amount == null) {
            throw new IllegalArgumentException("All fields are required");
        }
    }
}
