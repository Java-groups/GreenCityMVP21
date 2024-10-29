package greencity.validator;

import greencity.constant.ValidationConstants;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.WrongCountOfTagsException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EcoNewsDtoRequestValidatorDPTest {
    @InjectMocks
    private EcoNewsDtoRequestValidator validator;

    @Test
    void validUrlAndTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("https://google.com");
        request.setTags(List.of("tag1"));
        assertThat(validator.isValid(request, null)).isTrue();
    }

    @Test
    void validNullURLAndTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setTags(List.of("tag1"));
        assertThat(validator.isValid(request, null)).isTrue();
    }

    @Test
    void validEmptyURLAndTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("");
        request.setTags(List.of("tag1"));
        assertThat(validator.isValid(request, null)).isTrue();
    }

    @Test
    void failureMalformedURLAndTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("ht://google.com");
        request.setTags(List.of("tag1"));
        assertThrows(InvalidURLException.class, () -> validator.isValid(request, null));
    }

    @Test
    void failureURISyntaxAndTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("https://google.com some");
        request.setTags(List.of("tag1"));
        assertThrows(InvalidURLException.class, () -> validator.isValid(request, null));
    }

    @Test
    void failureUrlAndNullTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("https://google.com");
        request.setTags(null);
        assertThrows(NullPointerException.class, () -> validator.isValid(request, null));
    }

    @Test
    void failureUrlAndEmptyTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("https://google.com");
        request.setTags(List.of());
        assertThrows(WrongCountOfTagsException.class, () -> validator.isValid(request, null));
    }

    @Test
    void failureUrlAndMaxTags() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource("https://google.com");
        request.setTags(Collections.nCopies(ValidationConstants.MAX_AMOUNT_OF_TAGS + 1, "tag"));
        assertThrows(WrongCountOfTagsException.class, () -> validator.isValid(request, null));
    }
}
