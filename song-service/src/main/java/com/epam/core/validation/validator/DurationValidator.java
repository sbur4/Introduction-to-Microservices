package com.epam.core.validation.validator;

import com.epam.core.validation.constraint.DurationValidationConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.function.Function;
import java.util.function.Predicate;

public class DurationValidator implements ConstraintValidator<DurationValidationConstraint, String> {

    private static final String DURATION_FORMAT_REGEX = "^\\d{2}:\\d{2}$";

    private static final Function<String, Integer> PARSE_INT = Integer::parseInt;
    private static final Predicate<Integer> IS_VALID_TIME  = i -> i < 0 || i > 59;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        final String trimmedString = StringUtils.strip(value);

        if (StringUtils.isBlank(trimmedString)) {
            buildConstraintViolation(context, "Duration cannot be blank.");
            return false;
        }

        if (trimmedString.length() != 5 || !trimmedString.matches(DURATION_FORMAT_REGEX)) {
            buildConstraintViolation(context, "Duration must be in mm:ss format with leading zeros (e.g., 05:30).");
            return false;
        }

        String minutesStr = trimmedString.substring(0, 2);
        String secondsStr = trimmedString.substring(3, 5);

        if (!NumberUtils.isParsable(minutesStr)) {
            buildConstraintViolation(context, "Minutes must be numeric.");
            return false;
        }
        int minutes = PARSE_INT.apply(minutesStr);
        if (IS_VALID_TIME.test(minutes)) {
            buildConstraintViolation(context, "Minutes must be between 00 and 59.");
            return false;
        }

        if (!NumberUtils.isParsable(secondsStr)) {
            buildConstraintViolation(context, "Seconds must be numeric.");
            return false;
        }
        int seconds = PARSE_INT.apply(secondsStr);
        if (IS_VALID_TIME.test(seconds)) {
            buildConstraintViolation(context, "Seconds must be between 00 and 59.");
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
