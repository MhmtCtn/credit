package tr.com.xbank.credit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tr.com.xbank.credit.dto.request.CreateLoanRequest;
import tr.com.xbank.credit.dto.request.PayLoanRequest;
import tr.com.xbank.credit.dto.response.InstallmentDto;
import tr.com.xbank.credit.dto.response.LoanDto;
import tr.com.xbank.credit.dto.response.PaymentResult;
import tr.com.xbank.credit.security.LoanSecurityService;
import tr.com.xbank.credit.security.UserPrincipal;
import tr.com.xbank.credit.service.LoanService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @MockBean
    private LoanSecurityService loanSecurityService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateLoanRequest validLoanRequest;
    private LoanDto loanDto;
    private InstallmentDto installmentDto;
    private PayLoanRequest payLoanRequest;
    private UserPrincipal adminPrincipal;
    private UserPrincipal customerPrincipal;

    @BeforeEach
    void setUp() {
        validLoanRequest = new CreateLoanRequest(
                1L,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(0.1),
                6
        );

        loanDto = new LoanDto(
                1L,
                1L,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(0.1),
                6,
                LocalDateTime.now(),
                false
        );

        installmentDto = new InstallmentDto(
                1L,
                1L,
                BigDecimal.valueOf(183.33),
                BigDecimal.ZERO,
                LocalDateTime.now().plusMonths(1).toLocalDate(),
                null,
                false
        );

        payLoanRequest = new PayLoanRequest(
                1L,
                BigDecimal.valueOf(183.33)
        );

        adminPrincipal = new UserPrincipal(0L, "admin", "password", "ADMIN");
        customerPrincipal = new UserPrincipal(1L, "customer1", "password", "CUSTOMER");
    }

    @Nested
    @DisplayName("Create Loan Tests")
    class CreateLoanTests {

        @Test
        @DisplayName("Admin should be able to create loan for any customer")
        void createLoan_AdminSuccess() throws Exception {
            when(loanService.createLoan(any(CreateLoanRequest.class))).thenReturn(loanDto);

            mockMvc.perform(post("/loans/create")
                            .with(csrf())
                            .with(user(adminPrincipal))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validLoanRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(loanDto.id()));
        }

        @Test
        @DisplayName("Customer should only be able to create loan for themselves")
        void createLoan_CustomerSuccess() throws Exception {
            validLoanRequest = new CreateLoanRequest(1L, BigDecimal.valueOf(1000), BigDecimal.valueOf(0.1), 6);
            when(loanService.createLoan(any(CreateLoanRequest.class))).thenReturn(loanDto);

            mockMvc.perform(post("/loans/create")
                            .with(csrf())
                            .with(user(customerPrincipal))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validLoanRequest)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return bad request for invalid installment number")
        void createLoan_InvalidInstallmentNumber() throws Exception {
            CreateLoanRequest invalidRequest = new CreateLoanRequest(
                    1L,
                    BigDecimal.valueOf(1000),
                    BigDecimal.valueOf(0.1),
                    7  // Invalid installment number
            );

            mockMvc.perform(post("/loans/create")
                            .with(csrf())
                            .with(user(adminPrincipal))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errors", hasItem(containsString("Number of installments must be 6, 9, 12, or 24"))));
        }
    }

    @Nested
    @DisplayName("Get Loans Tests")
    class GetLoansTests {

        @Test
        @DisplayName("Admin should be able to get loans for any customer")
        void getLoansByCustomerId_AdminSuccess() throws Exception {
            List<LoanDto> loans = Arrays.asList(loanDto);
            when(loanService.getLoansByCustomerId(anyLong())).thenReturn(loans);

            mockMvc.perform(get("/loans/customer/1")
                            .with(user(adminPrincipal)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data", hasSize(1)));
        }

        // TODO
        /*@Test
        @DisplayName("Customer should not be able to get other customers' loans")
        void getLoansByCustomerId_CustomerSuccess() throws Exception {
            mockMvc.perform(get("/loans/customer/2")
                            .with(user(customerPrincipal)))
                    .andExpect(status().isForbidden());
        }*/
    }

    @Nested
    @DisplayName("Get Installments Tests")
    class GetInstallmentsTests {

        @Test
        @DisplayName("Admin should be able to get installments for any loan")
        void getLoanInstallments_AdminSuccess() throws Exception {
            List<InstallmentDto> installments = Arrays.asList(installmentDto);
            when(loanService.getLoanInstallments(anyLong())).thenReturn(installments);

            mockMvc.perform(get("/loans/1/installments")
                            .with(user(adminPrincipal)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data", hasSize(1)));
        }

        // TODO
        /*@Test
        @DisplayName("Customer should not be able to get installments for other customers' loans")
        void getLoanInstallments_CustomerUnauthorized() throws Exception {

            when(loanSecurityService.isLoanOwner(anyLong(), anyLong())).thenReturn(false);

            mockMvc.perform(get("/loans/1/installments")
                            .with(user(customerPrincipal)))
                    .andExpect(status().isForbidden());
        }*/

        @Test
        @DisplayName("Customer should be able to get installments for their own loans")
        void getLoanInstallments_CustomerSuccess() throws Exception {
            when(loanSecurityService.isLoanOwner(1L, customerPrincipal.getId())).thenReturn(true);
            List<InstallmentDto> installments = Arrays.asList(installmentDto);
            when(loanService.getLoanInstallments(1L)).thenReturn(installments);

            mockMvc.perform(get("/loans/1/installments")
                            .with(user(customerPrincipal)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("Pay Loan Tests")
    class PayLoanTests {

        @Test
        @DisplayName("Admin should be able to pay any loan")
        void payLoanInstallments_AdminSuccess() throws Exception {
            PaymentResult paymentResult = new PaymentResult(1, BigDecimal.valueOf(183.33), false);
            when(loanService.payLoanInstallments(any(PayLoanRequest.class))).thenReturn(paymentResult);

            mockMvc.perform(post("/loans/pay")
                            .with(csrf())
                            .with(user(adminPrincipal))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payLoanRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Customer should be able to pay their own loans")
        void payLoanInstallments_CustomerSuccess() throws Exception {
            when(loanSecurityService.isLoanOwner(1L, customerPrincipal.getId())).thenReturn(true);
            PaymentResult paymentResult = new PaymentResult(1, BigDecimal.valueOf(183.33), false);
            when(loanService.payLoanInstallments(any(PayLoanRequest.class))).thenReturn(paymentResult);

            mockMvc.perform(post("/loans/pay")
                            .with(csrf())
                            .with(user(customerPrincipal))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payLoanRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Should return bad request for invalid payment amount")
        void payLoanInstallments_InvalidAmount() throws Exception {
            PayLoanRequest invalidRequest = new PayLoanRequest(1L, BigDecimal.valueOf(-100));

            mockMvc.perform(post("/loans/pay")
                            .with(csrf())
                            .with(user(adminPrincipal))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errors", hasItem(containsString("Payment amount must be positive"))));
        }
    }

    @Test
    @DisplayName("Should require authentication")
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/loans/customer/1"))
                .andExpect(status().isUnauthorized());
    }
}
