package tr.com.xbank.credit.service;

import tr.com.xbank.credit.dto.request.CreateLoanRequest;
import tr.com.xbank.credit.dto.request.PayLoanRequest;
import tr.com.xbank.credit.dto.response.InstallmentDto;
import tr.com.xbank.credit.dto.response.LoanDto;
import tr.com.xbank.credit.dto.response.PaymentResult;

import java.util.List;

public interface LoanService {
    LoanDto createLoan(CreateLoanRequest request);
    List<LoanDto> getLoansByCustomerId(Long customerId);
    List<InstallmentDto> getLoanInstallments(Long loanId);
    PaymentResult payLoanInstallments(PayLoanRequest request);
}
