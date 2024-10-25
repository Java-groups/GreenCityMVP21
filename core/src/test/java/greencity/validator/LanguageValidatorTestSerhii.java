package greencity.validator;

import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LanguageValidatorTestSerhii {
    private final List<String> LANGUAGE_CODES = List.of("en");

    @InjectMocks
    private LanguageValidator languageValidator;

    @Mock
    private LanguageService languageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(languageService.findAllLanguageCodes()).thenReturn(LANGUAGE_CODES);
        languageValidator.initialize(null);
    }

    @Test
    void initialize() {
        verify(languageService, times(1)).findAllLanguageCodes();
    }

    @Test
    void isValid() {
        Locale validLocale = Locale.ENGLISH;
        Locale invalidLocale = Locale.FRANCE;

        assertTrue(languageValidator.isValid(validLocale, null));
        assertFalse(languageValidator.isValid(invalidLocale, null));
    }
}