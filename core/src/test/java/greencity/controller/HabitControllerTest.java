package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.config.SecurityConfig;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserProfilePictureDto;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.service.HabitService;
import greencity.service.TagsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
@RestController
public class HabitControllerTest {
    MockMvc mockMvc;
    @Mock
    private HabitService habitService;

    @Mock
    private TagsService tagsService;

    @InjectMocks
    private HabitController habitController;

    @Mock
    private UserVO userVO;

    @Mock
    private Principal principal;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HabitController(habitService, tagsService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Get habit by ID in specified language")
    void testGetHabitById() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        HabitDto habitDto = new HabitDto();
        habitDto.setId(habitId);

        when(habitService.getByIdAndLanguageCode(habitId, locale.getLanguage())).thenReturn(habitDto);

        mockMvc.perform(get("/habit/{id}", habitId)
                        .header("Accept-Language", locale.getLanguage())
                        .header("Accept", "application/json")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(habitId));

        verify(habitService, times(1)).getByIdAndLanguageCode(habitId, locale.getLanguage());
    }

    @Test
    @DisplayName("Get all habits")
    void testGetAll() {
        Pageable pageable = Pageable.unpaged();
        Locale locale = Locale.ENGLISH;

        List<HabitDto> habitDtos = new ArrayList<>();
        habitDtos.add(new HabitDto());
        long totalElements = 1;
        int totalPages = 1;
        int size = 1;

        PageableDto<HabitDto> pageableDto = new PageableDto<>(habitDtos, totalElements, totalPages, size);

        when(habitService.getAllHabitsByLanguageCode(userVO, pageable, locale.getLanguage()))
                .thenReturn(pageableDto);

        ResponseEntity<PageableDto<HabitDto>> response = habitController.getAll(userVO, locale, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pageableDto, response.getBody());
        verify(habitService, times(1)).getAllHabitsByLanguageCode(userVO, pageable, locale.getLanguage());
    }

    @Test
    @DisplayName("Get shopping list items for a specific habit")
    void testGetShoppingListItems() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        List<ShoppingListItemDto> shoppingListItems = new ArrayList<>();
        shoppingListItems.add(new ShoppingListItemDto());

        when(habitService.getShoppingListForHabit(habitId, locale.getLanguage())).thenReturn(shoppingListItems);

        mockMvc.perform(get("/habit/{id}/shopping-list", habitId)
                        .header("Accept-Language", locale.getLanguage())
                        .header("Accept", "application/json")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(shoppingListItems.size()));

        verify(habitService, times(1)).getShoppingListForHabit(habitId, locale.getLanguage());
    }

    @Test
    @DisplayName("Get all habits by tags and language code")
    void testGetAllByTagsAndLanguageCode() throws Exception {
        Locale locale = Locale.ENGLISH;
        List<String> tags = List.of("eco", "health");

        Pageable pageable = PageRequest.of(0, 20);

        List<HabitDto> habitDtos = new ArrayList<>();
        habitDtos.add(new HabitDto());
        long totalElements = 1;
        int totalPages = 1;
        int size = 1;

        PageableDto<HabitDto> pageableDto = new PageableDto<>(habitDtos, totalElements, totalPages, size);

        when(habitService.getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage()))
                .thenReturn(pageableDto);

        mockMvc.perform(get("/habit/tags/search")
                        .header("Accept-Language", locale.getLanguage())
                        .param("tags", "eco", "health"))
                .andExpect(status().isOk());

        verify(habitService, times(1)).getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage());
    }

    @Test
    @DisplayName("Successful query with all parameters specified")
    void testGetAllByDifferentParameters_AllParametersPresent() throws Exception {
        Locale locale = Locale.ENGLISH;
        Pageable pageable = PageRequest.of(0, 20);

        Optional<List<String>> tags = Optional.of(List.of("eco", "health"));
        Optional<Boolean> isCustomHabit = Optional.of(true);
        Optional<List<Integer>> complexities = Optional.of(List.of(1, 2, 3));

        List<HabitDto> habitDtos = List.of(new HabitDto(), new HabitDto());
        PageableDto<HabitDto> expectedResponse = new PageableDto<>(habitDtos, habitDtos.size(), 1, habitDtos.size());

        when(habitService.getAllByDifferentParameters(any(UserVO.class), eq(pageable), eq(tags), eq(isCustomHabit), eq(complexities), eq(locale.getLanguage())))
                .thenReturn(expectedResponse);

        mockMvc.perform(get("/habit/search")
                        .header("Accept-Language", locale.getLanguage())
                        .param("tags", "eco", "health")
                        .param("isCustomHabit", String.valueOf(isCustomHabit.get()))
                        .param("complexities", "1", "2", "3")
                )
                .andExpect(status().isOk());

        verify(habitService).getAllByDifferentParameters(any(UserVO.class), eq(pageable), eq(tags), eq(isCustomHabit), eq(complexities), eq(locale.getLanguage()));
    }

    @Test
    @DisplayName("BadRequestException when no parameters are provided")
    void testGetAllByDifferentParameters_MissingRequiredParameters() {
        Pageable pageable = PageRequest.of(0, 20);
        Locale locale = Locale.ENGLISH;
        Optional<List<String>> tags = Optional.empty();
        Optional<Boolean> isCustomHabit = Optional.empty();
        Optional<List<Integer>> complexities = Optional.empty();

        BadRequestException exception = org.junit.jupiter.api.Assertions.assertThrows(
                BadRequestException.class,
                () -> habitController.getAllByDifferentParameters(userVO, locale, tags, isCustomHabit, complexities, pageable)
        );

        assertEquals("You should enter at least one parameter", exception.getMessage());
    }

    @Test
    @DisplayName("Find all habits tags")
    void testFindAllHabitsTags() throws Exception {
        Locale locale = Locale.ENGLISH;
        List<String> expectedTags = List.of("health", "eco");

        when(tagsService.findAllHabitsTags(locale.getLanguage())).thenReturn(expectedTags);

        mockMvc.perform(get("/habit/tags")
                        .header("Accept-Language", locale.getLanguage())
                        .header("Accept", "application/json")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(expectedTags.size())))
                .andExpect(jsonPath("$[0]").value(expectedTags.get(0)))
                .andExpect(jsonPath("$[1]").value(expectedTags.get(1)));

        verify(tagsService, times(1)).findAllHabitsTags(locale.getLanguage());
    }

    @Test
    @DisplayName("Add new custom habit")
    void testAddCustomHabit() throws Exception {
        AddCustomHabitDtoRequest requestDto = new AddCustomHabitDtoRequest();
        requestDto.setImage("Test image");
        requestDto.setComplexity(1);

        AddCustomHabitDtoResponse responseDto = new AddCustomHabitDtoResponse();
        responseDto.setId(1L);

        MockMultipartFile image = new MockMultipartFile("image", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test-image-content".getBytes());

        when(habitService.addCustomHabit(any(AddCustomHabitDtoRequest.class), any(MultipartFile.class), anyString()))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/habit/custom")
                        .file(image)
                        .file(new MockMultipartFile("request", "", "application/json", new ObjectMapper().writeValueAsString(requestDto).getBytes()))
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Returns list of profile pictures for friends assigned to a habit")
    void testGetFriendsAssignedToHabitProfilePictures() throws Exception {
        Long habitId = 1L;

        List<UserProfilePictureDto> profilePictures = List.of(
                new UserProfilePictureDto(1L, "Friend 1", "path/to/friend1.jpg"),
                new UserProfilePictureDto(2L, "Friend 2", "path/to/friend2.jpg")
        );

        when(habitService.getFriendsAssignedToHabitProfilePictures(anyLong(), any(Long.class)))
                .thenReturn(profilePictures);

        mockMvc.perform(get("/habit/{habitId}/friends/profile-pictures", habitId)
                        .principal(() -> "testUser")
                        .header("Accept", "application/json")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}

