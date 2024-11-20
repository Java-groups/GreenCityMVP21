package greencity.annotations;

import greencity.validator.EventValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EventValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface EventValidation {
    String message() default "Invalid data of event.";


    int titleMaxSize() default 70;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
