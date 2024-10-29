package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.config.SecurityConfig;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
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
@Import(SecurityConfig.class)
public class HabitControllerTest {
    private MockMvc mockMvc;
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

    private final Locale locale = Locale.ENGLISH;
    Long habitId = 1L;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HabitController(habitService, tagsService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

    }

    @Test
    void testGetHabitById() throws Exception {

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
    @DisplayName("Get all habits ")
    void testGetAll() {

        Pageable pageable = Pageable.unpaged();

        HabitDto habitDto = new HabitDto();
        habitDto.setId(1L);

        List<HabitDto> habitDtos = List.of(habitDto);

        PageableDto<HabitDto> pageableDto = new PageableDto<>(habitDtos, 1L, 1, 1);

        when(habitService.getAllHabitsByLanguageCode(userVO, pageable, locale.getLanguage()))
                .thenReturn(pageableDto);


        ResponseEntity<PageableDto<HabitDto>> response = habitController.getAll(userVO, locale, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        PageableDto<HabitDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1L, responseBody.getTotalElements());
        assertEquals(1, responseBody.getPage().size());

        verify(habitService, times(1)).getAllHabitsByLanguageCode(userVO, pageable, locale.getLanguage());
    }

    @Test
    @DisplayName("Get shopping list items for a specific habit ID with language preference")
    void testGetShoppingListItems() throws Exception {

        ShoppingListItemDto shoppingItem = new ShoppingListItemDto();
        shoppingItem.setId(1L);
        shoppingItem.setStatus("ACTIVE");
        List<ShoppingListItemDto> shoppingListItems = List.of(shoppingItem);

        when(habitService.getShoppingListForHabit(habitId, locale.getLanguage())).thenReturn(shoppingListItems);


        mockMvc.perform(get("/habit/{id}/shopping-list", habitId)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(shoppingListItems.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(habitService, times(1)).getShoppingListForHabit(habitId, locale.getLanguage());
    }


    @Test
    @DisplayName("Get all habits by tags and language code")
    void testGetAllByTagsAndLanguageCode() throws Exception {

        List<String> tags = List.of("ECO_NEWS", "EVENT");

        Pageable pageable = PageRequest.of(0, 20);

        List<HabitDto> habitDtos = new ArrayList<>();
        habitDtos.add(new HabitDto());

        long totalElements = 1;
        int totalPages = 1;
        int size = habitDtos.size();
        PageableDto<HabitDto> pageableDto = new PageableDto<>(habitDtos, totalElements, totalPages, size);


        when(habitService.getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage()))
                .thenReturn(pageableDto);

        mockMvc.perform(get("/habit/tags/search")
                        .header("Accept-Language", locale.getLanguage())
                        .param("tags", tags.toArray(new String[0]))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(totalElements))
                .andExpect(jsonPath("$.totalPages").value(totalPages));

        verify(habitService, times(1)).getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage());
    }

    @Test
    @DisplayName("Get all habits by different parameters with all parameters present")
    void testGetAllByDifferentParameters_AllParametersPresent() throws Exception {

        Pageable pageable = PageRequest.of(0, 20);

        Optional<List<String>> tags = Optional.of(List.of("ECO_NEWS", "EVENT"));
        Optional<Boolean> isCustomHabit = Optional.of(true);
        Optional<List<Integer>> complexities = Optional.of(List.of(1, 2, 3));

        List<HabitDto> habitDtos = List.of(new HabitDto(), new HabitDto());
        PageableDto<HabitDto> expectedResponse = new PageableDto<>(habitDtos, habitDtos.size(), 1, habitDtos.size());

        when(habitService.getAllByDifferentParameters(any(UserVO.class), eq(pageable), eq(tags), eq(isCustomHabit), eq(complexities), eq(locale.getLanguage())))
                .thenReturn(expectedResponse);

        mockMvc.perform(get("/habit/search")
                        .header("Accept-Language", locale.getLanguage())
                        .param("tags", "ECO_NEWS", "EVENT")
                        .param("isCustomHabit", String.valueOf(isCustomHabit.get()))
                        .param("complexities", "1", "2", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitService).getAllByDifferentParameters(any(UserVO.class), eq(pageable), eq(tags), eq(isCustomHabit), eq(complexities), eq(locale.getLanguage()));

    }


    @Test
    @DisplayName("Find all habits tags")
    void testFindAllHabitsTags() throws Exception {

        List<String> expectedTags = List.of("ECO_NEWS", "EVENT");

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
        requestDto.setImage("Test");
        requestDto.setComplexity(1);

        AddCustomHabitDtoResponse responseDto = new AddCustomHabitDtoResponse();
        responseDto.setId(1L);

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test-image-content".getBytes());


        when(habitService.addCustomHabit(any(AddCustomHabitDtoRequest.class), any(MultipartFile.class), anyString()))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/habit/custom")
                        .file(image)
                        .file(new MockMultipartFile("request", "", "application/json", new ObjectMapper().writeValueAsString(requestDto).getBytes()))
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Returns list of profile pictures for friends assigned to a habit")
    void testGetFriendsAssignedToHabitProfilePictures() throws Exception {

        List<UserProfilePictureDto> profilePictures = List.of(
                new UserProfilePictureDto(1L, "Vasya", "path/to/Vasya.jpg"),
                new UserProfilePictureDto(2L, "Petya", "path/to/Petya.jpg")
        );

        when(habitService.getFriendsAssignedToHabitProfilePictures(anyLong(), any(Long.class)))
                .thenReturn(profilePictures);

        mockMvc.perform(get("/habit/{habitId}/friends/profile-pictures", habitId)
                        .principal(() -> "testUser")
                        .header("Accept", "application/json")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}