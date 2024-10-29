package greencity.mapping;

import greencity.dto.tag.NewTagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import greencity.entity.Language;
import greencity.enums.TagType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NewTagDtoMapperTest {
    private NewTagDtoMapper newTagDtoMapper;

    @BeforeEach
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        newTagDtoMapper = new NewTagDtoMapper();
        modelMapper.addConverter(newTagDtoMapper);
    }

    @Test
    public void testConvertWithBothTranslations() {
        Tag tag = createTagWithTranslations("Eco", "Еко", TagType.ECO_NEWS);
        NewTagDto result = newTagDtoMapper.convert(tag);

        assertEquals("Eco", result.getName());
        assertEquals("Еко", result.getNameUa());
        assertEquals(tag.getId(), result.getId());
    }

    @Test
    public void testConvertWithOnlyEnglishTranslation() {
        Tag tag = createTagWithTranslations("Eco", null, TagType.ECO_NEWS);
        NewTagDto result = newTagDtoMapper.convert(tag);

        assertEquals("Eco", result.getName());
        assertNull(result.getNameUa());
        assertEquals(tag.getId(), result.getId());
    }

    @Test
    public void testConvertWithOnlyUkrainianTranslation() {
        Tag tag = createTagWithTranslations(null, "Еко", TagType.HABIT);
        NewTagDto result = newTagDtoMapper.convert(tag);

        assertNull(result.getName());
        assertEquals("Еко", result.getNameUa());
        assertEquals(tag.getId(), result.getId());
    }

    @Test
    public void testConvertWithNoTranslations() {
        Tag tag = createTagWithTranslations(null, null, TagType.EVENT);
        NewTagDto result = newTagDtoMapper.convert(tag);

        assertNull(result.getName());
        assertNull(result.getNameUa());
        assertEquals(tag.getId(), result.getId());
    }

    private Tag createTagWithTranslations(String enName, String uaName, TagType type) {
        List<TagTranslation> translations = new ArrayList<>();
        if (enName != null) {
            translations.add(createTagTranslation(enName, "en"));
        }
        if (uaName != null) {
            translations.add(createTagTranslation(uaName, "ua"));
        }

        return Tag.builder()
                .id(1L)
                .type(type)
                .tagTranslations(translations)
                .build();
    }

    private TagTranslation createTagTranslation(String name, String languageCode) {
        Language language = new Language(1L, languageCode, null, null);
        return TagTranslation.builder()
                .id(1L)
                .name(name)
                .language(language)
                .build();
    }
}
