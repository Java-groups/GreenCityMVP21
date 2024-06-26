package greencity.validator;

import greencity.annotations.NoLinks;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NoLinksValidator implements ConstraintValidator<NoLinks, String> {

    private static final Pattern URL_PATTERN = Pattern.compile("http[s]?://[^\\s]+");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !URL_PATTERN.matcher(value).find();
    }
}
