package greencity.annotations;

import greencity.validator.NoVideosAndPicturesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoVideosAndPicturesValidator.class)
public @interface NoVideosAndPictures {
    /**
     * Defines the message that will be showed when the input data is not valid.
     *
     * @return message
     */
    String message() default "Videos and Pictures are not allowed";

    /**
     * Let you select to split the annotations into different groups to apply
     * different validations to each group.
     *
     * @return groups
     */
    Class<?>[] groups() default {};

    /**
     * Payloads are typically used to carry metadata information consumed by a
     * validation client.
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};
}
