package greencity.mapping;

import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import greencity.entity.Language;
import greencity.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchNewsDtoMapperTest {
    private SearchNewsDtoMapper searchNewsDtoMapper;

    @BeforeEach
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        searchNewsDtoMapper = new SearchNewsDtoMapper();
        modelMapper.addConverter(searchNewsDtoMapper);
    }

    @Test
    public void testConvertWithEnglishLocale() {
        LocaleContextHolder.setLocale(new Locale("en"));
        EcoNews ecoNews = createEcoNewsWithTranslations("Eco News", "Еко Новини", "en");
        SearchNewsDto result = searchNewsDtoMapper.convert(ecoNews);

        assertEquals(ecoNews.getId(), result.getId());
        assertEquals(ecoNews.getTitle(), result.getTitle());
        assertEquals(ecoNews.getAuthor().getId(), result.getAuthor().getId());
        assertEquals("Eco News", result.getTags().get(0));
        assertEquals(ecoNews.getCreationDate(), result.getCreationDate());
    }

    @Test
    public void testConvertWithUkrainianLocale() {
        LocaleContextHolder.setLocale(new Locale("ua"));
        EcoNews ecoNews = createEcoNewsWithTranslations("Eco News", "Еко Новини", "ua");
        SearchNewsDto result = searchNewsDtoMapper.convert(ecoNews);

        assertEquals(ecoNews.getId(), result.getId());
        assertEquals(ecoNews.getTitle(), result.getTitle());
        assertEquals(ecoNews.getAuthor().getId(), result.getAuthor().getId());
        assertEquals("Еко Новини", result.getTags().get(0));
        assertEquals(ecoNews.getCreationDate(), result.getCreationDate());
    }

    private EcoNews createEcoNewsWithTranslations(String enTagName, String uaTagName, String languageCode) {
        User author = User.builder()
                .id(1L)
                .name("John Doe")
                .build();

        List<Tag> tags = new ArrayList<>();
        Tag tag = Tag.builder()
                .id(1L)
                .tagTranslations(Arrays.asList(
                        createTagTranslation(enTagName, "en"),
                        createTagTranslation(uaTagName, "ua")
                ))
                .build();
        tags.add(tag);

        return EcoNews.builder()
                .id(1L)
                .title("Breaking News")
                .author(author)
                .tags(tags)
                .creationDate(ZonedDateTime.now())
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
