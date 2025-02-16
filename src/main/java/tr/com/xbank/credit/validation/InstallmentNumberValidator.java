package tr.com.xbank.credit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class InstallmentNumberValidator implements ConstraintValidator<ValidInstallmentNumber, Integer> {
    private static final Set<Integer> VALID_INSTALLMENT_NUMBERS = Set.of(6, 9, 12, 24);

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return VALID_INSTALLMENT_NUMBERS.contains(value);
    }
}
