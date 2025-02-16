package tr.com.xbank.credit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tr.com.xbank.credit.entity.Customer;
import tr.com.xbank.credit.repository.CustomerRepository;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CustomerRepository customerRepository;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("Starting data initialization...");

            List<Customer> customers = List.of(
                    createCustomer("Mehmet", "Çetin", 400000),
                    createCustomer("Elif", "Demir", 75000),
                    createCustomer("Ahmet", "Yılmaz", 50000),
                    createCustomer("Zeynep", "Çelik", 100000),
                    createCustomer("Burak", "Şahin", 150000)
            );

            customerRepository.saveAll(customers);

            log.info("Data initialization completed. Created {} customers", customers.size());

            // Log created customers for easy reference
            customers.forEach(customer ->
                    log.info("Created customer: ID={}, Name={} {}, Credit Limit={}",
                            customer.getId(),
                            customer.getName(),
                            customer.getSurname(),
                            customer.getCreditLimit()
                    )
            );
        };
    }

    private Customer createCustomer(String name, String surname, double creditLimit) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setSurname(surname);
        customer.setCreditLimit(BigDecimal.valueOf(creditLimit));
        customer.setUsedCreditLimit(BigDecimal.ZERO);
        return customer;
    }
}
