package greencity.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class MultipartBase64ImageMapperTest {
    private MultipartBase64ImageMapper multipartBase64ImageMapper;

    @BeforeEach
    void setUp() {
        multipartBase64ImageMapper = new MultipartBase64ImageMapper();
    }

    @Test
    void testConvertValidBase64() {
        // Arrange
        String base64Image = "data:image/png;base64," +
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAwAB/AL+" +
                "YkVnAAAACAAAAAAAElFTkSuQmCC="; // Добавлен padding "="

        // Act
        MultipartFile multipartFile = multipartBase64ImageMapper.convert(base64Image);

        // Assert
        assertNotNull(multipartFile, "MultipartFile should not be null");
        assertEquals("tempImage.jpg", multipartFile.getOriginalFilename());
        assertEquals("image/png", multipartFile.getContentType());
    }

    @Test
    void testConvertInvalidBase64() {
        // Arrange
        String invalidBase64Image = "invalid base64 string";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> multipartBase64ImageMapper.convert(invalidBase64Image),
                "Expected an IllegalArgumentException for invalid Base64 string");
    }

    @Test
    void testConvertIncompleteBase64() {
        // Arrange
        String incompleteBase64Image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAwAB"; // Неполная строка Base64

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> multipartBase64ImageMapper.convert(incompleteBase64Image),
                "Expected an IllegalArgumentException for incomplete Base64 string");
    }
}
