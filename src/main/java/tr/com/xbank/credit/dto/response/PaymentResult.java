package tr.com.xbank.credit.dto.response;

import java.math.BigDecimal;

public record PaymentResult(
        int installmentsPaid,
        BigDecimal amountSpent,
        boolean isLoanFullyPaid
) {}
