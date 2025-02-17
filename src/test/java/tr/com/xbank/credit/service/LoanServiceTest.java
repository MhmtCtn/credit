package tr.com.xbank.credit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import tr.com.xbank.credit.service.impl.LoanServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Customer customer;
    private Loan loan;
    private List<LoanInstallment> installments;
    private CreateLoanRequest validLoanRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setCreditLimit(BigDecimal.valueOf(10000));
        customer.setUsedCreditLimit(BigDecimal.ZERO);

        loan = new Loan();
        loan.setId(1L);
        loan.setCustomer(customer);
        loan.setLoanAmount(BigDecimal.valueOf(1000));
        loan.setInterestRate(BigDecimal.valueOf(0.1));
        loan.setNumberOfInstallment(6);
        loan.setCreateDate(LocalDateTime.now());
        loan.setPaid(false);

        LoanInstallment installment = new LoanInstallment();
        installment.setId(1L);
        installment.setLoan(loan);
        installment.setAmount(BigDecimal.valueOf(183.33));
        installment.setPaidAmount(BigDecimal.ZERO);
        installment.setDueDate(LocalDate.now().plusMonths(1));
        installment.setPaid(false);

        installments = Arrays.asList(installment);

        validLoanRequest = new CreateLoanRequest(
                1L,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(0.1),
                6
        );
    }

    @Nested
    @DisplayName("Create Loan Tests")
    class CreateLoanTests {

        @Test
        @DisplayName("Should successfully create a loan")
        void createLoan_Success() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(loanRepository.save(any(Loan.class))).thenReturn(loan);
            when(loanInstallmentRepository.saveAll(any())).thenReturn(installments);

            LoanDto result = loanService.createLoan(validLoanRequest);

            assertNotNull(result);
            assertEquals(loan.getId(), result.id());
            assertEquals(loan.getLoanAmount(), result.loanAmount());
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when customer not found")
        void createLoan_CustomerNotFound() {
            when(customerRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> loanService.createLoan(validLoanRequest));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when credit limit is insufficient")
        void createLoan_InsufficientCreditLimit() {
            customer.setUsedCreditLimit(BigDecimal.valueOf(9500));
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

            assertThrows(IllegalArgumentException.class, () -> loanService.createLoan(validLoanRequest));
        }
    }

    @Nested
    @DisplayName("Pay Loan Tests")
    class PayLoanTests {

        @Test
        @DisplayName("Should successfully process loan payment")
        void payLoanInstallments_Success() {
            PayLoanRequest request = new PayLoanRequest(1L, BigDecimal.valueOf(183.33));

            when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
            when(loanInstallmentRepository.findUnpaidInstallmentsByLoanIdAndMaxDueDate(eq(1L), any()))
                    .thenReturn(installments);
            when(loanInstallmentRepository.countUnpaidInstallmentsByLoanId(1L)).thenReturn(0L);

            PaymentResult result = loanService.payLoanInstallments(request);

            assertNotNull(result);
            assertEquals(1, result.installmentsPaid());
            assertTrue(result.isLoanFullyPaid());
            verify(loanInstallmentRepository).saveAll(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when loan not found")
        void payLoanInstallments_LoanNotFound() {
            PayLoanRequest request = new PayLoanRequest(1L, BigDecimal.valueOf(183.33));
            when(loanRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> loanService.payLoanInstallments(request));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when payment amount insufficient")
        void payLoanInstallments_InsufficientAmount() {
            PayLoanRequest request = new PayLoanRequest(1L, BigDecimal.valueOf(100));

            when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
            when(loanInstallmentRepository.findUnpaidInstallmentsByLoanIdAndMaxDueDate(eq(1L), any()))
                    .thenReturn(installments);

            assertThrows(IllegalArgumentException.class, () -> loanService.payLoanInstallments(request));
        }
    }

    @Nested
    @DisplayName("Get Loans Tests")
    class GetLoansTests {

        @Test
        @DisplayName("Should return list of loans for customer")
        void getLoansByCustomerId_Success() {
            when(loanRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(loan));

            List<LoanDto> result = loanService.getLoansByCustomerId(1L);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(loan.getId(), result.get(0).id());
        }

        @Test
        @DisplayName("Should return empty list when no loans found")
        void getLoansByCustomerId_NoLoansFound() {
            when(loanRepository.findByCustomerId(1L)).thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class, () -> loanService.getLoansByCustomerId(1L));
        }
    }

    @Nested
    @DisplayName("Get Installments Tests")
    class GetInstallmentsTests {

        @Test
        @DisplayName("Should return list of installments for loan")
        void getLoanInstallments_Success() {
            when(loanInstallmentRepository.findByLoanId(1L)).thenReturn(installments);

            List<InstallmentDto> result = loanService.getLoanInstallments(1L);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(installments.get(0).getId(), result.get(0).id());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when no installments found")
        void getLoanInstallments_NoInstallmentsFound() {
            when(loanInstallmentRepository.findByLoanId(1L)).thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class, () -> loanService.getLoanInstallments(1L));
        }
    }
}
