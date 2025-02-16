package tr.com.xbank.credit.dto.request;

import java.math.BigDecimal;

public record CreateLoanRequest(Long customerId,
                                BigDecimal amount,
                                BigDecimal interestRate,
                                Integer numberOfInstallments) {
    public CreateLoanRequest {
        if (customerId == null || amount == null || interestRate == null || numberOfInstallments == null) {
            throw new IllegalArgumentException("All fields are required");
        }
    }
}
