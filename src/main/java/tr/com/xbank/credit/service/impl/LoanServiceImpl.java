package tr.com.xbank.credit.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.xbank.credit.dto.request.CreateLoanRequest;
import tr.com.xbank.credit.dto.request.PayLoanRequest;
import tr.com.xbank.credit.dto.response.InstallmentDto;
import tr.com.xbank.credit.dto.response.LoanDto;
import tr.com.xbank.credit.dto.response.PaymentResult;
import tr.com.xbank.credit.entity.Customer;
import tr.com.xbank.credit.entity.Loan;
import tr.com.xbank.credit.entity.LoanInstallment;
import tr.com.xbank.credit.repository.CustomerRepository;
import tr.com.xbank.credit.repository.LoanInstallmentRepository;
import tr.com.xbank.credit.repository.LoanRepository;
import tr.com.xbank.credit.service.LoanService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    @Override
    @Transactional
    public LoanDto createLoan(CreateLoanRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Calculate total loan amount with interest
        BigDecimal totalAmount = request.amount().multiply(BigDecimal.ONE.add(request.interestRate()));

        // Check if customer has enough credit limit
        if (customer.getCreditLimit().subtract(customer.getUsedCreditLimit()).compareTo(totalAmount) < 0) {
            throw new IllegalArgumentException("Insufficient credit limit");
        }

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(request.amount());
        loan.setInterestRate(request.interestRate());
        loan.setNumberOfInstallment(request.numberOfInstallments());

        loan = loanRepository.save(loan);

        // Calculate installment amount
        BigDecimal installmentAmount =
                totalAmount.divide(
                        BigDecimal.valueOf(request.numberOfInstallments()), 2, RoundingMode.HALF_UP);

        List<LoanInstallment> installments = new ArrayList<>();
        LocalDate firstDueDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);

        for (int i = 0; i < request.numberOfInstallments(); i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setDueDate(firstDueDate.plusMonths(i));
            installments.add(installment);
        }

        loanInstallmentRepository.saveAll(installments);

        // Update customer's used credit limit
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(totalAmount));
        customerRepository.save(customer);

        return LoanDto.mapLoanToDto(loan);
    }

    @Override
    public List<LoanDto> getLoansByCustomerId(Long customerId) {

        return loanRepository.findByCustomerId(customerId)
                .stream()
                .map(LoanDto::mapLoanToDto)
                .toList();
    }

    @Override
    public List<InstallmentDto> getLoanInstallments(Long loanId) {

        return loanInstallmentRepository.findByLoanId(loanId)
                .stream()
                .map(InstallmentDto::mapInstallmentToDto)
                .toList();
    }

    @Override
    public PaymentResult payLoanInstallments(PayLoanRequest request) {
        return null;
    }
}
