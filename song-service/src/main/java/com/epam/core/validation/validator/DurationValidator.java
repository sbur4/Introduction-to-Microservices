package com.epam.core.validation.validator;

import com.epam.core.validation.constraint.DurationValidationConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.function.Predicate;

public class DurationValidator implements ConstraintValidator<DurationValidationConstraint, String> {

    private static final String DURATION_FORMAT_REGEX = "^\\d{2}:\\d{2}$";

    private final Predicate<String> isBlankOrInvalidLength = value -> StringUtils.isBlank(value) || value.length() != 5;
    private final Predicate<String> matchesFormat = value -> !value.matches(DURATION_FORMAT_REGEX);
    private final Predicate<String> isCorrectTime = value -> !StringUtils.isNumeric(value) || NumberUtils.createInteger(value) > 60;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlankOrInvalidLength.test(value)) {
            buildConstraintViolation(context, "Duration cannot be blank");
            return false;
        }

        if (matchesFormat.test(value)) {
            return false;
        }

        String minutes = value.substring(0, 2);
        if (isCorrectTime.test(minutes)) {
            buildConstraintViolation(context, "Minutes cannot be more than 60.");
            return false;
        }

        String seconds = value.substring(3, 5);
        if (isCorrectTime.test(seconds)) {
            buildConstraintViolation(context, "Seconds cannot be more than 60.");
            return false;
        }
        return true;
    }

    private void buildConstraintViolation(ConstraintValidatorContext context, String defaultMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(defaultMessage)
                .addConstraintViolation();
    }
}
