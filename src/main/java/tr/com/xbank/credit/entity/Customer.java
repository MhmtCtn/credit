package tr.com.xbank.credit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Surname is required")
    private String surname;

    @NotEmpty(message = "Username is required")
    @Column(unique = true)
    private String username;

    @JsonIgnore
    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @PositiveOrZero(message = "Credit limit must be zero or positive")
    private BigDecimal creditLimit;

    @PositiveOrZero(message = "Used credit limit must be zero or positive")
    private BigDecimal usedCreditLimit;
}
