package tr.com.xbank.credit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.xbank.credit.repository.LoanRepository;

@Service
@RequiredArgsConstructor
public class LoanSecurityService {

    private final LoanRepository loanRepository;

    @Transactional(readOnly = true)
    public boolean isLoanOwner(Long loanId, Long userId) {
        return loanRepository.findById(loanId)
                .map(loan -> loan.getCustomer().getId().equals(userId))
                .orElse(false);
    }
}
