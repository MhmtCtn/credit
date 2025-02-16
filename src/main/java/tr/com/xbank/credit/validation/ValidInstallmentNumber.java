package tr.com.xbank.credit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InstallmentNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInstallmentNumber {
    String message() default "Number of installments must be 6, 9, 12, or 24";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
