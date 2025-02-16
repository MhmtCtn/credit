package tr.com.xbank.credit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.com.xbank.credit.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
