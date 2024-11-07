package greencity.mapping;

import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LanguageTranslationDtoMapperTest {
    private LanguageTranslationDtoMapper languageTranslationDtoMapper;

    @BeforeEach
    void setUp() {
        languageTranslationDtoMapper = new LanguageTranslationDtoMapper();
    }

    @Test
    void testConvert() {
        Language language = Language.builder()
                .id(1L)
                .code("en")
                .build();

        HabitFactTranslation habitFactTranslation = HabitFactTranslation.builder()
                .content("Be eco-friendly")
                .language(language)
                .build();

        LanguageTranslationDTO languageTranslationDTO = languageTranslationDtoMapper.convert(habitFactTranslation);

        assertNotNull(languageTranslationDTO);
        assertEquals("Be eco-friendly", languageTranslationDTO.getContent());
        assertNotNull(languageTranslationDTO.getLanguage());
        assertEquals(1L, languageTranslationDTO.getLanguage().getId());
        assertEquals("en", languageTranslationDTO.getLanguage().getCode());
    }
}

