package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FilterDtoResponseMapperTest {
    @InjectMocks
    private FilterDtoResponseMapper filterDtoResponseMapper;

    @Test
    public void convertTest() {
        Filter filter = ModelUtils.getFilter();
        String[] values = filter.getValues().split(";");
        UserFilterDtoResponse response = filterDtoResponseMapper.convert(filter);
        Assertions.assertEquals(response.getId(), filter.getId());
        Assertions.assertEquals(response.getName(), filter.getName());
        Assertions.assertEquals(response.getSearchCriteria(), values[0]);
        Assertions.assertEquals(response.getUserRole(), values[1]);
        Assertions.assertEquals(response.getUserStatus(), values[2]);
    }
}
