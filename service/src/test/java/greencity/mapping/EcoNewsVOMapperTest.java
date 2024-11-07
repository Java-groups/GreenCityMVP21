package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsVO;
import greencity.entity.EcoNews;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class EcoNewsVOMapperTest {
    @InjectMocks
    private EcoNewsVOMapper mapper;

    @Test
    public void convertTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        ecoNews.setTags(Collections.emptyList());
        ecoNews.setEcoNewsComments(Collections.emptyList());
        ecoNews.setUsersDislikedNews(Collections.emptySet());
        ecoNews.setUsersLikedNews(Collections.emptySet());

        EcoNewsVO ecoNewsVO = mapper.convert(ecoNews);
        Assertions.assertEquals(ecoNews.getId(), ecoNewsVO.getId());
        Assertions.assertEquals(ecoNews.getTitle(), ecoNewsVO.getTitle());
        Assertions.assertEquals(ecoNews.getCreationDate(), ecoNewsVO.getCreationDate());
        Assertions.assertEquals(ecoNews.getImagePath(), ecoNewsVO.getImagePath());
        Assertions.assertEquals(ecoNews.getSource(), ecoNewsVO.getSource());
    }
}
