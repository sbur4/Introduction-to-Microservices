package com.epam.core.validation.constraint;

import com.epam.core.validation.validator.DurationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

@Documented
@Target({ANNOTATION_TYPE, FIELD, METHOD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationValidator.class)
public @interface DurationValidationConstraint {

    String message() default "Duration must be in mm:ss format with leading zeros.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
