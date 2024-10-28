package greencity.mapping;

import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactTranslationDto;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageVO;
import greencity.enums.FactOfDayStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HabitFactDtoResponseMapperTest {
    @Test
    public void testConvert() {

        LanguageVO languageVO = LanguageVO.builder()
                .id(1L)
                .code("en")
                .build();

        HabitFactTranslationVO translationVO = HabitFactTranslationVO.builder()
                .id(1L)
                .content("Test Content")
                .factOfDayStatus(FactOfDayStatus.POTENTIAL)
                .language(languageVO)
                .build();

        HabitVO habitVO = HabitVO.builder()
                .id(1L)
                .build();

        HabitFactVO habitFactVO = HabitFactVO.builder()
                .id(1L)
                .habit(habitVO)
                .translations(List.of(translationVO))
                .build();

        HabitFactDtoResponseMapper mapper = new HabitFactDtoResponseMapper();
        HabitFactDtoResponse result = mapper.convert(habitFactVO);

        assertNotNull(result);
        assertEquals(habitFactVO.getId(), result.getId());
        assertEquals(habitFactVO.getHabit(), result.getHabit());
        assertEquals(habitFactVO.getTranslations().size(), result.getTranslations().size());

        HabitFactTranslationDto resultTranslation = result.getTranslations().get(0);
        HabitFactTranslationVO originalTranslation = habitFactVO.getTranslations().get(0);
        assertEquals(originalTranslation.getId(), resultTranslation.getId());
        assertEquals(originalTranslation.getContent(), resultTranslation.getContent());
        assertEquals(originalTranslation.getFactOfDayStatus(), resultTranslation.getFactOfDayStatus());
        assertEquals(originalTranslation.getLanguage().getId(), resultTranslation.getLanguage().getId());
        assertEquals(originalTranslation.getLanguage().getCode(), resultTranslation.getLanguage().getCode());
    }
}
