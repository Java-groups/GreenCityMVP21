package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDtoMapperTest {
    private CategoryDtoMapper categoryDtoMapper;

    @BeforeEach
    void setUp() {
        categoryDtoMapper = new CategoryDtoMapper();
    }

    @Test
    void convert_newObject() {
        assertEquals(new Category(), categoryDtoMapper.convert(new CategoryDto()));
    }

    @Test
    void convert_filledObject() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("Test Name")
                .build();
        Category expectedCategory = Category.builder()
                .name("Test Name")
                .build();

        assertEquals(expectedCategory, categoryDtoMapper.convert(categoryDto));
    }
}