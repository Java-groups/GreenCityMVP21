package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDtoResponseMapperTest {
    private CategoryDtoResponseMapper categoryDtoResponseMapper;

    @BeforeEach
    void setUp() {
        categoryDtoResponseMapper = new CategoryDtoResponseMapper();
    }

    @Test
    void convert_newObject() {
        assertEquals(new CategoryDtoResponse(), categoryDtoResponseMapper.convert(new Category()));
    }

    @Test
    void convert_filledObject() {
        Category category = Category.builder()
                .id(101L)
                .name("Test Name")
                .build();
        CategoryDtoResponse expectedCategoryDtoResponse = CategoryDtoResponse.builder()
                .id(101L)
                .name("Test Name")
                .build();
        assertEquals(expectedCategoryDtoResponse, categoryDtoResponseMapper.convert(category));
    }
}