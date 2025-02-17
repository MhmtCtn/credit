package tr.com.xbank.credit.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.xbank.credit.dto.request.CustomerRegistrationRequest;
import tr.com.xbank.credit.entity.Customer;
import tr.com.xbank.credit.repository.CustomerRepository;
import tr.com.xbank.credit.service.CustomerService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Customer registerCustomer(CustomerRegistrationRequest request) {
        if (customerRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (customerRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setSurname(request.surname());
        customer.setUsername(request.username());
        customer.setPassword(passwordEncoder.encode(request.password()));
        customer.setEmail(request.email());
        customer.setCreditLimit(BigDecimal.ZERO);
        customer.setUsedCreditLimit(BigDecimal.ZERO);

        return customerRepository.save(customer);
    }
}
