package greencity.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ImageValidatorTestSerhii {
    @InjectMocks
    private ImageValidator imageValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void initialize() {
    }

    @Test
    void isValid_nullImage() {
        MultipartFile nullImage = null;
        assertTrue(imageValidator.isValid(nullImage, null));
    }

    @Test
    void isValid_validImage() {
        MultipartFile validImage = new MockMultipartFile("image", "image.png", "image/png", new byte[0]);
        assertTrue(imageValidator.isValid(validImage, null));
    }

    @Test
    void isValid_invalidImage() {
        MultipartFile invalidImage = new MockMultipartFile("image", "image.txt", "text/plain", new byte[0]);
        assertFalse(imageValidator.isValid(invalidImage, null));
    }
}