package tr.com.xbank.credit.service;

import tr.com.xbank.credit.dto.request.CustomerRegistrationRequest;
import tr.com.xbank.credit.entity.Customer;

public interface CustomerService {
    Customer registerCustomer(CustomerRegistrationRequest request);
}
