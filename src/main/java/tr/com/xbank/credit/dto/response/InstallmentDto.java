package tr.com.xbank.credit.dto.response;

import tr.com.xbank.credit.entity.LoanInstallment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentDto(
        Long id,
        Long loanId,
        BigDecimal amount,
        BigDecimal paidAmount,
        LocalDate dueDate,
        LocalDate paymentDate,
        boolean isPaid
) {

    public static InstallmentDto mapInstallmentToDto(LoanInstallment installment) {

        return new InstallmentDto(
                installment.getId(),
                installment.getLoan().getId(),
                installment.getAmount(),
                installment.getPaidAmount(),
                installment.getDueDate(),
                installment.getPaymentDate(),
                installment.isPaid()
        );
    }
}
