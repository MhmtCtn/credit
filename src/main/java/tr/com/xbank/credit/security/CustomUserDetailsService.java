package tr.com.xbank.credit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tr.com.xbank.credit.entity.Customer;
import tr.com.xbank.credit.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String role = "admin".equals(username) ? "ADMIN" : "CUSTOMER";

        return new UserPrincipal(
                customer.getId(),
                customer.getUsername(),
                customer.getPassword(),
                role
        );
    }
}
