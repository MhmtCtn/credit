package tr.com.xbank.credit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tr.com.xbank.credit.dto.ApiResponse;
import tr.com.xbank.credit.dto.request.CustomerRegistrationRequest;
import tr.com.xbank.credit.entity.Customer;
import tr.com.xbank.credit.service.CustomerService;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Customer>> registerCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request) throws URISyntaxException {

        Customer customer = customerService.registerCustomer(request);

        URI location = new URI("/credit/customers/register/" + customer.getId());

        return ResponseEntity.created(location)
                .body(ApiResponse.success(customer, "/credit/customers/register/"));
    }
}
