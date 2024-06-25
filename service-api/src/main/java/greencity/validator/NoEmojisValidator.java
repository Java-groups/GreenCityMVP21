package greencity.validator;

import greencity.annotations.NoEmojis;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NoEmojisValidator implements ConstraintValidator<NoEmojis, String> {

    private static final Pattern EMOJI_PATTERN = Pattern.compile("[\\p{So}\\p{Cn}]");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !EMOJI_PATTERN.matcher(value).find();
    }
}
