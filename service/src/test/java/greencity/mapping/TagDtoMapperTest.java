package greencity.mapping;

import greencity.dto.tag.TagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TagDtoMapperTest {
    private TagDtoMapper tagDtoMapper;

    @BeforeEach
    void setUp() {
        tagDtoMapper = new TagDtoMapper();
    }

    @Test
    void testConvert() {
        Tag tag = Tag.builder()
                .id(1L)
                .build();
        TagTranslation tagTranslation = TagTranslation.builder()
                .id(2L)
                .name("Environment")
                .tag(tag)
                .build();

        TagDto tagDto = tagDtoMapper.convert(tagTranslation);

        assertNotNull(tagDto);
        assertEquals(1L, tagDto.getId());
        assertEquals("Environment", tagDto.getName());
    }
}

