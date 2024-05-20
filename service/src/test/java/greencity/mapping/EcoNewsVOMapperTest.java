package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsVO;
import greencity.entity.EcoNews;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EcoNewsVOMapperTest {

    @InjectMocks
    EcoNewsVOMapper mapper;

    @Test
    @DisplayName("Test convert from EcoNews to EcoNewsVO")
    void convert() {

        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO result = mapper.convert(ecoNews);

        assertNotNull(result);
        assertEquals(ecoNews.getId(), result.getId());
        assertEquals(ecoNews.getCreationDate(), result.getCreationDate());
        assertEquals(ecoNews.getImagePath(), result.getImagePath());
        assertEquals(ecoNews.getSource(), result.getSource());
        assertNotNull(result.getAuthor());
        assertEquals(ecoNews.getAuthor().getId(), result.getAuthor().getId());
        assertEquals(ecoNews.getTitle(), result.getTitle());
        assertEquals(ecoNews.getText(), result.getText());
        assertEquals(ecoNews.getTags().size(), result.getTags().size());
        assertEquals(ecoNews.getEcoNewsComments().size(), result.getEcoNewsComments().size());
        assertEquals(ecoNews.getUsersLikedNews().size(), result.getUsersLikedNews().size());
        assertEquals(ecoNews.getUsersDislikedNews().size(), result.getUsersDislikedNews().size());


    }
}