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
import tr.com.xbank.credit.exception.ResourceNotFoundException;
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

        Customer customer = findAndValidateCustomer(request.customerId());
        BigDecimal totalAmount = calculateTotalLoanAmount(request.amount(), request.interestRate());
        validateCustomerCreditLimit(customer, totalAmount);

        Loan loan = createAndSaveLoan(customer, request);

        createAndSaveInstallments(loan, totalAmount, request.numberOfInstallments());

        updateCustomerCreditLimit(customer, totalAmount);

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
    @Transactional
    public PaymentResult payLoanInstallments(PayLoanRequest request) {

        Loan loan = findAndValidateLoan(request.loanId());

        List<LoanInstallment> eligibleInstallments = findAndValidateEligibleInstallments(loan.getId());

        PaymentResult result = processPayment(eligibleInstallments, request.amount());
        updateLoanAndCustomerStatus(loan, result.isLoanFullyPaid());

        return result;
    }


    /***
     *  Private methods for createLoan method
    ***/
    private Customer findAndValidateCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
    }

    private BigDecimal calculateTotalLoanAmount(BigDecimal principal, BigDecimal interestRate) {
        return principal.multiply(BigDecimal.ONE.add(interestRate));
    }

    private void validateCustomerCreditLimit(Customer customer, BigDecimal requiredAmount) {
        BigDecimal availableCredit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
        if (availableCredit.compareTo(requiredAmount) < 0) {
            throw new IllegalArgumentException("Insufficient credit limit");
        }
    }

    private Loan createAndSaveLoan(Customer customer, CreateLoanRequest request) {
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(request.amount());
        loan.setInterestRate(request.interestRate());
        loan.setNumberOfInstallment(request.numberOfInstallments());
        return loanRepository.save(loan);
    }

    private void createAndSaveInstallments(Loan loan, BigDecimal totalAmount, int numberOfInstallments) {
        BigDecimal installmentAmount = calculateInstallmentAmount(totalAmount, numberOfInstallments);
        List<LoanInstallment> installments = generateInstallments(loan, installmentAmount, numberOfInstallments);
        loanInstallmentRepository.saveAll(installments);
    }

    private BigDecimal calculateInstallmentAmount(BigDecimal totalAmount, int numberOfInstallments) {
        return totalAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
    }

    private List<LoanInstallment> generateInstallments(Loan loan, BigDecimal installmentAmount, int numberOfInstallments) {
        List<LoanInstallment> installments = new ArrayList<>();
        LocalDate firstDueDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);

        for (int i = 0; i < numberOfInstallments; i++) {
            installments.add(createInstallment(loan, installmentAmount, firstDueDate.plusMonths(i)));
        }
        return installments;
    }

    private LoanInstallment createInstallment(Loan loan, BigDecimal amount, LocalDate dueDate) {
        LoanInstallment installment = new LoanInstallment();
        installment.setLoan(loan);
        installment.setAmount(amount);
        installment.setDueDate(dueDate);
        return installment;
    }

    private void updateCustomerCreditLimit(Customer customer, BigDecimal amount) {
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(amount));
        customerRepository.save(customer);
    }


    /***
     *  Private methods for payLoanInstallments method
     ***/
    private Loan findAndValidateLoan(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        if (loan.isPaid()) {
            throw new IllegalArgumentException("Loan is already fully paid");
        }

        return loan;
    }

    private List<LoanInstallment> findAndValidateEligibleInstallments(Long loanId) {

        LocalDate maxDueDate = LocalDate.now().plusMonths(3);
        List<LoanInstallment> installments =
                loanInstallmentRepository.findUnpaidInstallmentsByLoanIdAndMaxDueDate(loanId, maxDueDate);

        if (installments.isEmpty()) {
            throw new ResourceNotFoundException("Eligible installments not found");
        }

        return installments;
    }

    private PaymentResult processPayment(List<LoanInstallment> installments, BigDecimal paymentAmount) {
        validateInstallments(installments);
        Long loanId = installments.get(0).getLoan().getId();

        BigDecimal remainingAmount = paymentAmount;
        int installmentsPaid = 0;
        BigDecimal amountSpent = BigDecimal.ZERO;
        List<LoanInstallment> updatedInstallments = new ArrayList<>();

        for (LoanInstallment installment : installments) {
            if (remainingAmount.compareTo(installment.getAmount()) >= 0) {

                updatePaidInstallment(installment);
                updatedInstallments.add(installment);

                remainingAmount = remainingAmount.subtract(installment.getAmount());
                amountSpent = amountSpent.add(installment.getAmount());
                installmentsPaid++;
            } else {
                break;
            }
        }

        validatePaymentProcessed(installmentsPaid);
        loanInstallmentRepository.saveAll(updatedInstallments);
        boolean isLoanFullyPaid = checkIfLoanFullyPaid(loanId);

        return new PaymentResult(installmentsPaid, amountSpent, isLoanFullyPaid);
    }

    private void validateInstallments(List<LoanInstallment> installments) {
        if (installments == null || installments.isEmpty()) {
            throw new ResourceNotFoundException("No installments found for payment processing");
        }

        LoanInstallment firstInstallment = installments.get(0);
        if (firstInstallment == null) {
            throw new ResourceNotFoundException("Invalid installment data");
        }

        Loan loan = firstInstallment.getLoan();
        if (loan == null) {
            throw new ResourceNotFoundException("Loan not found for installment");
        }
    }

    private void validatePaymentProcessed(int installmentsPaid) {
        if (installmentsPaid == 0) {
            throw new IllegalArgumentException("Payment amount is insufficient for any installment");
        }
    }

    private void updatePaidInstallment(LoanInstallment installment) {
        installment.setPaidAmount(installment.getAmount());
        installment.setPaymentDate(LocalDate.now());
        installment.setPaid(true);
    }

    private boolean checkIfLoanFullyPaid(Long loanId) {
        return loanInstallmentRepository.countUnpaidInstallmentsByLoanId(loanId) == 0;
    }

    private void updateLoanAndCustomerStatus(Loan loan, boolean isFullyPaid) {
        if (isFullyPaid) {
            loan.setPaid(true);
            loanRepository.save(loan);

            Customer customer = loan.getCustomer();
            customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(loan.getLoanAmount()));
            customerRepository.save(customer);
        }
    }
}
