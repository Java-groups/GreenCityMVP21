package greencity.validator;

import greencity.annotations.ValidAddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public class EventCommentsValidator implements ConstraintValidator<ValidAddEventCommentDtoRequest, AddEventCommentDtoRequest> {
    private static Set<String> bannedWords;
    @Value("${slug.filter.file.en}")
    private String pathToFile;

    @Override
    public void initialize(ValidAddEventCommentDtoRequest constraintAnnotation) {
        if (bannedWords == null) {
            try {
                bannedWords = loadBannedWords();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isValid(AddEventCommentDtoRequest addEventCommentDtoRequest, ConstraintValidatorContext constraintValidatorContext) {
        return bannedWords.stream()
                .anyMatch(addEventCommentDtoRequest.getComment().toLowerCase()::contains);
    }

    private Set<String> loadBannedWords() throws IOException {
        ClassPathResource resource = new ClassPathResource(pathToFile);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toSet());
        }
    }
}
