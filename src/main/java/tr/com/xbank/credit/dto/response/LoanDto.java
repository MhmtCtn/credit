package tr.com.xbank.credit.dto.response;

import tr.com.xbank.credit.entity.Loan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanDto(
        Long id,
        Long customerId,
        BigDecimal loanAmount,
        BigDecimal interestRate,
        Integer numberOfInstallment,
        LocalDateTime createDate,
        boolean isPaid
) {
    public static LoanDto mapLoanToDto(Loan loan) {

        return new LoanDto(
                loan.getId(),
                loan.getCustomer().getId(),
                loan.getLoanAmount(),
                loan.getInterestRate(),
                loan.getNumberOfInstallment(),
                loan.getCreateDate(),
                loan.isPaid()
        );
    }
}
