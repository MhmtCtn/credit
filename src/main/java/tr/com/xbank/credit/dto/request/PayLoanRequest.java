package tr.com.xbank.credit.dto.request;

import java.math.BigDecimal;

public record PayLoanRequest(Long loanId, BigDecimal amount) {
    public PayLoanRequest {
        if (loanId == null || amount == null) {
            throw new IllegalArgumentException("All fields are required");
        }
    }
}
