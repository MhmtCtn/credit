package tr.com.xbank.credit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tr.com.xbank.credit.entity.Customer;
import tr.com.xbank.credit.repository.CustomerRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("Starting data initialization...");

            List<Customer> customers = new ArrayList<>();

            // Create admin user
            Customer adminUser = new Customer();
            adminUser.setName("Admin");
            adminUser.setSurname("User");
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@example.com");
            adminUser.setCreditLimit(BigDecimal.valueOf(1000000));
            adminUser.setUsedCreditLimit(BigDecimal.ZERO);
            customers.add(adminUser);

            // Create regular customers
            customers.addAll(List.of(
                    createCustomer(
                            "Mehmet", "Çetin",
                            "mcetin", "pass123",
                            "mehmet.cetin@example.com", 400000),
                    createCustomer("Elif", "Demir",
                            "edemir", "pass234",
                            "elif.demir@example.com", 75000),
                    createCustomer("Ahmet", "Yılmaz",
                            "ayilmaz", "pass345",
                            "ahmet.yilmaz@example.com", 50000),
                    createCustomer("Zeynep", "Çelik",
                            "zcelik", "pass456",
                            "zeynep.celik@example.com",100000),
                    createCustomer("Burak", "Şahin",
                            "bsahin", "pass567",
                            "burak.sahin@example.com",150000)
            ));

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

            log.info("Test credentials for development:");
            log.info("----------------------------------------");
            log.info("Admin credentials:");
            log.info("Username: admin");
            log.info("Password: admin123");
            log.info("----------------------------------------");
            log.info("Sample customer credentials:");
            log.info("Username: mcetin");
            log.info("Password: pass123");
            log.info("----------------------------------------");
        };
    }

    private Customer createCustomer(String name, String surname, String username,
                                    String password, String email, double creditLimit) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setSurname(surname);
        customer.setUsername(username);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setEmail(email);
        customer.setCreditLimit(BigDecimal.valueOf(creditLimit));
        customer.setUsedCreditLimit(BigDecimal.ZERO);
        return customer;
    }
}
