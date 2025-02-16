package tr.com.xbank.credit.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tr.com.xbank.credit.entity.Loan;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @NonNull
    @EntityGraph(attributePaths = {"customer"})
    List<Loan> findByCustomerId(@NonNull Long customerId);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"customer"})
    Optional<Loan> findById(@NonNull Long id);
}
