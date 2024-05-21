package greencity.validator;

import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static greencity.repository.ModelUtils.getAllLanguages;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LanguageValidatorTest {

    @InjectMocks
    LanguageValidator languageValidator;

    @Mock
    LanguageService languageService;

    @BeforeEach
    void setup() {
        when(languageService.findAllLanguageCodes()).thenReturn(getAllLanguages());
        languageValidator.initialize(null);
    }

    @Test
    void isValidTrueTest() {
        assertTrue(languageValidator.isValid(Locale.ENGLISH, null));
        assertTrue(languageValidator.isValid(Locale.UK, null));
    }

    @Test
    void isValidFalseTest() {
        assertFalse(languageValidator.isValid(Locale.CHINA, null));
        assertFalse(languageValidator.isValid(Locale.of("dssf"), null));
    }
}
