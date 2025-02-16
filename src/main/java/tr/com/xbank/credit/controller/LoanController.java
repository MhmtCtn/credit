package tr.com.xbank.credit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.xbank.credit.dto.ApiResponse;
import tr.com.xbank.credit.dto.request.CreateLoanRequest;
import tr.com.xbank.credit.dto.request.PayLoanRequest;
import tr.com.xbank.credit.dto.response.InstallmentDto;
import tr.com.xbank.credit.dto.response.LoanDto;
import tr.com.xbank.credit.dto.response.PaymentResult;
import tr.com.xbank.credit.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<LoanDto>> createLoan(@Valid @RequestBody CreateLoanRequest request) {

        try {

            LoanDto loan = loanService.createLoan(request);

            return ResponseEntity.ok(ApiResponse.success(loan, "/credit/loans/create"));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<LoanDto>>> getLoansByCustomerId(@PathVariable Long customerId) {

        try {

            List<LoanDto> loans = loanService.getLoansByCustomerId(customerId);

            return ResponseEntity.ok(
                ApiResponse.success(loans, "/credit/loans/customer/" + customerId)
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @GetMapping("/{loanId}/installments")
    public ResponseEntity<ApiResponse<List<InstallmentDto>>> getLoanInstallments(@PathVariable Long loanId) {

        try {

            List<InstallmentDto> installments = loanService.getLoanInstallments(loanId);

            return ResponseEntity.ok(
                ApiResponse.success(installments, "/credit/loans/" + loanId + "/installments")
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<PaymentResult>> payLoanInstallments(@Valid @RequestBody PayLoanRequest request) {

        try {

            PaymentResult result = loanService.payLoanInstallments(request);

            return ResponseEntity.ok(ApiResponse.success(result, "/api/loans/pay"));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
