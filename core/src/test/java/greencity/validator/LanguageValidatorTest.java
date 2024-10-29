package greencity.validator;

import greencity.service.LanguageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LanguageValidatorTest {

    @InjectMocks
    private LanguageValidator languageValidator;

    @Mock
    private LanguageService languageService;

    @Test
    void isValidTrueTest() {
        Locale locale = Locale.of("en");
        List<String> languageCodes = List.of("en", "fr", "uk");

        when(languageService.findAllLanguageCodes()).thenReturn(languageCodes);

        languageValidator.initialize(null);

        Assertions.assertTrue(languageValidator.isValid(locale, null));
    }

    @Test
    void isValidFalseTest() {
        Locale locale = Locale.of("gr");
        List<String> languageCodes = List.of("en", "fr", "uk");

        when(languageService.findAllLanguageCodes()).thenReturn(languageCodes);

        languageValidator.initialize(null);

        Assertions.assertFalse(languageValidator.isValid(locale, null));
    }
}
