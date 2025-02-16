package tr.com.xbank.credit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CustomerRegistrationRequest(
        @NotEmpty(message = "Name is required")
        String name,

        @NotEmpty(message = "Surname is required")
        String surname,

        @NotEmpty(message = "Username is required")
        @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
        String username,

        @NotEmpty(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @NotEmpty(message = "Email is required")
        @Email(message = "Email should be valid")
        String email
) {
    public CustomerRegistrationRequest {
        if (name == null || surname == null || username == null || password == null || email == null) {
            throw new IllegalArgumentException("All fields are required");
        }
    }
}
