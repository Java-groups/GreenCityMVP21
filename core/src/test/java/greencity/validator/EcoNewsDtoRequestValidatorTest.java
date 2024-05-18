package greencity.validator;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.WrongCountOfTagsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static greencity.ModelUtils.getAddEcoNewsDtoRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EcoNewsDtoRequestValidatorTest {
    @InjectMocks
    private EcoNewsDtoRequestValidator validator;

    @Test
    void isUrlValid_ValidSourceURL_ReturnsTrue() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource("https://eco-lavca.ua/");
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isUrlValid_InvalidSourceURL_ThrowsInvalidURLException() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource("invalidURL");
        assertThrows(InvalidURLException.class, () -> validator.isValid(request, null));
    }

    @Test
    void isUrlValid_NullSourceURL_ReturnsTrue() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isUrlValid_EmptySourceURL_ReturnsTrue() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource("");
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isTagsValid_ValidAmountOfTags_ReturnsTrue() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(List.of("tag1"));
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isTagsValid_InvalidAmountOfTags_ThrowsWrongCountOfTagsException() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(List.of("tag1", "tag2", "tag3", "tag4"));
        assertThrows(WrongCountOfTagsException.class, () -> validator.isValid(request, null));
    }

    @Test
    void isTagsValid_EmptyTags_ThrowsWrongCountOfTagsException() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(List.of());
        assertThrows(WrongCountOfTagsException.class, () -> validator.isValid(request, null));
    }
}
