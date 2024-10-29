package greencity.mapping;

import greencity.dto.language.LanguageVO;
import greencity.dto.tag.TagVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import greencity.enums.TagType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TagMapperTest {
    private TagMapper tagMapper;

    @BeforeEach
    void setUp() {
        tagMapper = new TagMapper();
    }

    @Test
    void testConvert() {
        TagTranslationVO translationVO1 = TagTranslationVO.builder()
                .id(1L)
                .name("Environment")
                .languageVO(LanguageVO.builder().id(1L).code("en").build())
                .build();

        TagTranslationVO translationVO2 = TagTranslationVO.builder()
                .id(2L)
                .name("Nature")
                .languageVO(LanguageVO.builder().id(2L).code("fr").build())
                .build();

        List<TagTranslationVO> translationVOs = Arrays.asList(translationVO1, translationVO2);

        TagVO tagVO = TagVO.builder()
                .id(10L)
                .type(TagType.ECO_NEWS)
                .tagTranslations(translationVOs)
                .build();

        Tag tag = tagMapper.convert(tagVO);

        assertNotNull(tag);
        assertEquals(10L, tag.getId());
        assertEquals(TagType.ECO_NEWS, tag.getType());
        assertNotNull(tag.getTagTranslations());
        assertEquals(2, tag.getTagTranslations().size());

        TagTranslation tagTranslation1 = tag.getTagTranslations().get(0);
        assertEquals(1L, tagTranslation1.getId());
        assertEquals("Environment", tagTranslation1.getName());
        assertNotNull(tagTranslation1.getLanguage());
        assertEquals(1L, tagTranslation1.getLanguage().getId());
        assertEquals("en", tagTranslation1.getLanguage().getCode());

        TagTranslation tagTranslation2 = tag.getTagTranslations().get(1);
        assertEquals(2L, tagTranslation2.getId());
        assertEquals("Nature", tagTranslation2.getName());
        assertNotNull(tagTranslation2.getLanguage());
        assertEquals(2L, tagTranslation2.getLanguage().getId());
        assertEquals("fr", tagTranslation2.getLanguage().getCode());
    }
}
