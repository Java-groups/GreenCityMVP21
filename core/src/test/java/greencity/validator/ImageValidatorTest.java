package greencity.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class ImageValidatorTest {
    @InjectMocks
    private ImageValidator imageValidator;

    @Test
    @DisplayName("Test isValid method with valid image")
    void isValid_WithValidContentType_ReturnsTrue() {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getContentType()).thenReturn("image/jpeg");

        assertTrue(imageValidator.isValid(mockFile, null));
    }

    @Test
    @DisplayName("Test isValid method with null image")
    void isValid_WithNullImage_ReturnsTrue() {
        assertTrue(imageValidator.isValid(null, null));
    }

    @Test
    @DisplayName("Test isValid method with invalid image")
    void isValid_WithInvalidContentType_ReturnsFalse() {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getContentType()).thenReturn("invalid/contentType");

        assertFalse(imageValidator.isValid(mockFile, null));
    }
}
