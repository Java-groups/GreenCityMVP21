package greencity.mapping;

import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EcoNewsAuthorDtoMapperTest {
    private EcoNewsAuthorDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EcoNewsAuthorDtoMapper();
    }

    @Test
    void convert_newObject() {
        assertEquals(new EcoNewsAuthorDto(), mapper.convert(new User()));
    }

    @Test
    void convert_filledObject() {
        User user = User.builder()
                .id(101L)
                .name("Test name")
                .build();
        EcoNewsAuthorDto expectedEcoNewsAuthorDto = EcoNewsAuthorDto.builder()
                .id(101L)
                .name("Test name")
                .build();

        assertEquals(expectedEcoNewsAuthorDto, mapper.convert(user));
    }
}