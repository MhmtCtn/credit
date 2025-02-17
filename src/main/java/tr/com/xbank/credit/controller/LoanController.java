package tr.com.xbank.credit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tr.com.xbank.credit.dto.ApiResponse;
import tr.com.xbank.credit.dto.request.CreateLoanRequest;
import tr.com.xbank.credit.dto.request.PayLoanRequest;
import tr.com.xbank.credit.dto.response.InstallmentDto;
import tr.com.xbank.credit.dto.response.LoanDto;
import tr.com.xbank.credit.dto.response.PaymentResult;
import tr.com.xbank.credit.security.UserPrincipal;
import tr.com.xbank.credit.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or #request.customerId == #userPrincipal.id")
    public ResponseEntity<ApiResponse<LoanDto>> createLoan(
            @Valid @RequestBody CreateLoanRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        LoanDto loan = loanService.createLoan(request);

        return ResponseEntity.ok(ApiResponse.success(loan, "/credit/loans/create"));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or #customerId == #userPrincipal.id")
    public ResponseEntity<ApiResponse<List<LoanDto>>> getLoansByCustomerId(
            @PathVariable Long customerId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<LoanDto> loans = loanService.getLoansByCustomerId(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(loans, "/credit/loans/customer/" + customerId)
        );
    }

    @GetMapping("/{loanId}/installments")
    @PreAuthorize("hasRole('ADMIN') or @loanSecurityService.isLoanOwner(#loanId, #userPrincipal.id)")
    public ResponseEntity<ApiResponse<List<InstallmentDto>>> getLoanInstallments(
            @PathVariable Long loanId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<InstallmentDto> installments = loanService.getLoanInstallments(loanId);

        return ResponseEntity.ok(
                ApiResponse.success(installments, "/credit/loans/" + loanId + "/installments")
        );
    }

    @PostMapping("/pay")
    @PreAuthorize("hasRole('ADMIN') or @loanSecurityService.isLoanOwner(#request.loanId, #userPrincipal.id)")
    public ResponseEntity<ApiResponse<PaymentResult>> payLoanInstallments(
            @Valid @RequestBody PayLoanRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        PaymentResult result = loanService.payLoanInstallments(request);

        return ResponseEntity.ok(ApiResponse.success(result, "/credit/loans/pay"));
    }
}
