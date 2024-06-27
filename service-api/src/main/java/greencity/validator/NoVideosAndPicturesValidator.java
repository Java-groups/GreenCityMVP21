package greencity.validator;

import greencity.annotations.NoVideosAndPictures;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NoVideosAndPicturesValidator implements ConstraintValidator<NoVideosAndPictures, String> {

    private static final Pattern MEDIA_PATTERN = Pattern.compile("<img.*?>|<video.*?>");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !MEDIA_PATTERN.matcher(value).find();
    }
}
