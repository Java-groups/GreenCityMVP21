package greencity.validator;

import greencity.ModelUtils;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.WrongCountOfTagsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EcoNewsDtoRequestValidatorSecondaryTest {
    @InjectMocks
    EcoNewsDtoRequestValidator validator;

    AddEcoNewsDtoRequest addEcoNewsDtoRequest;

    @BeforeEach
    void setup() {
        addEcoNewsDtoRequest = ModelUtils.getAddEcoNewsDtoRequest();
    }

    @Test
    void isValidTrueTest() {
        addEcoNewsDtoRequest.setSource("https://spring.io/");
        Assertions.assertTrue(validator.isValid(addEcoNewsDtoRequest, null));
    }

    @Test
    void testEmptyTagList() {
        boolean hasPassed = false;
        try {
            addEcoNewsDtoRequest.setTags(Collections.emptyList());
            Assertions.assertTrue(validator.isValid(addEcoNewsDtoRequest, null));
        } catch (WrongCountOfTagsException e) {
            hasPassed = true;
        }
        Assertions.assertTrue(hasPassed);
    }

    @Test
    void testOverflowingTagList() {
        boolean hasPassed = false;
        try {
            addEcoNewsDtoRequest.setTags(List.of("tag1", "tag2", "tag3", "tag4"));
            Assertions.assertTrue(validator.isValid(addEcoNewsDtoRequest, null));
        } catch (WrongCountOfTagsException e) {
            hasPassed = true;
        }
        Assertions.assertTrue(hasPassed);
    }
}
