package greencity.mapping;

import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import greencity.enums.TagType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TagVOMapperTest {
    private TagVOMapper tagVOMapper;

    @BeforeEach
    void setUp() {
        tagVOMapper = new TagVOMapper();
    }

    @Test
    void testConvert() {
        TagTranslation translation1 = TagTranslation.builder()
                .id(1L)
                .name("Environment")
                .language(Language.builder().id(1L).code("en").build())
                .build();

        TagTranslation translation2 = TagTranslation.builder()
                .id(2L)
                .name("Nature")
                .language(Language.builder().id(2L).code("fr").build())
                .build();

        List<TagTranslation> translations = Arrays.asList(translation1, translation2);

        Tag tag = Tag.builder()
                .id(10L)
                .type(TagType.ECO_NEWS)
                .tagTranslations(translations)
                .build();

        TagVO tagVO = tagVOMapper.convert(tag);

        assertNotNull(tagVO);
        assertEquals(10L, tagVO.getId());
        assertEquals(TagType.ECO_NEWS, tagVO.getType());
        assertNotNull(tagVO.getTagTranslations());
        assertEquals(2, tagVO.getTagTranslations().size());

        TagTranslationVO tagTranslationVO1 = tagVO.getTagTranslations().get(0);
        assertEquals(1L, tagTranslationVO1.getId());
        assertEquals("Environment", tagTranslationVO1.getName());
        assertNotNull(tagTranslationVO1.getLanguageVO());
        assertEquals(1L, tagTranslationVO1.getLanguageVO().getId());
        assertEquals("en", tagTranslationVO1.getLanguageVO().getCode());

        TagTranslationVO tagTranslationVO2 = tagVO.getTagTranslations().get(1);
        assertEquals(2L, tagTranslationVO2.getId());
        assertEquals("Nature", tagTranslationVO2.getName());
        assertNotNull(tagTranslationVO2.getLanguageVO());
        assertEquals(2L, tagTranslationVO2.getLanguageVO().getId());
        assertEquals("fr", tagTranslationVO2.getLanguageVO().getCode());
    }
}

