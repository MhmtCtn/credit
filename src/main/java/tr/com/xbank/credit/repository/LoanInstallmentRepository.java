package tr.com.xbank.credit.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tr.com.xbank.credit.entity.LoanInstallment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    @EntityGraph(attributePaths = {"loan"})
    List<LoanInstallment> findByLoanId(Long loanId);

    @Query(
        "SELECT li FROM LoanInstallment li " +
        "WHERE li.loan.id = :loanId AND li.isPaid = false AND li.dueDate <= :maxDueDate " +
        "ORDER BY li.dueDate ASC"
    )
    List<LoanInstallment> findUnpaidInstallmentsByLoanIdAndMaxDueDate(Long loanId, LocalDate maxDueDate);

    @Query(
        "SELECT COUNT(li) FROM LoanInstallment li " +
        "WHERE li.loan.id = :loanId AND li.isPaid = false"
    )
    long countUnpaidInstallmentsByLoanId(Long loanId);
}
