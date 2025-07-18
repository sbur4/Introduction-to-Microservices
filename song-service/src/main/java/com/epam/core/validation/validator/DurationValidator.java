package com.epam.core.validation.validator;

import com.epam.core.validation.constraint.DurationValidationConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class DurationValidator implements ConstraintValidator<DurationValidationConstraint, String> {

    private static final String DURATION_FORMAT_REGEX = "^\\d{2}:\\d{2}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            buildConstraintViolation(context, "Duration cannot be blank.");
            return false;
        }

        if (value.length() != 5 || !value.matches(DURATION_FORMAT_REGEX)) {
            buildConstraintViolation(context, "Duration must be in mm:ss format with leading zeros (e.g., 05:30).");
            return false;
        }

        String minutesStr = value.substring(0, 2);
        String secondsStr = value.substring(3, 5);

        if (!NumberUtils.isParsable(minutesStr)) {
            buildConstraintViolation(context, "Minutes must be numeric.");
            return false;
        }
        int minutes = Integer.parseInt(minutesStr);
        if (minutes < 0 || minutes > 59) {
            buildConstraintViolation(context, "Minutes must be between 00 and 59.");
            return false;
        }

        if (!NumberUtils.isParsable(secondsStr)) {
            buildConstraintViolation(context, "Seconds must be numeric.");
            return false;
        }
        int seconds = Integer.parseInt(secondsStr);
        if (seconds < 0 || seconds > 59) {
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
