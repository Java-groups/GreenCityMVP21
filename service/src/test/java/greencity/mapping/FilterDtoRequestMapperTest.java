package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserFilterDtoRequest;
import greencity.entity.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FilterDtoRequestMapperTest {
    @InjectMocks
    private FilterDtoRequestMapper mapper;

    @Test
    public void convertTest() {
        UserFilterDtoRequest userFilterDtoRequest = ModelUtils.getUserFilterDtoRequest();
        Filter filter = mapper.convert(userFilterDtoRequest);
        Assertions.assertEquals(filter.getName(), userFilterDtoRequest.getName());
        Assertions.assertEquals(filter.getValues(), String.join(
                ";",
                userFilterDtoRequest.getSearchCriteria(),
                userFilterDtoRequest.getUserRole(),
                userFilterDtoRequest.getUserStatus()
        ));
    }
}
