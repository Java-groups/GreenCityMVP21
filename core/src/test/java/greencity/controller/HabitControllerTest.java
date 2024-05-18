package greencity.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import greencity.GreenCityApplication;
import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.resolver.PageableResolver;
import greencity.resolver.UserVoResolver;
import greencity.service.HabitService;
import greencity.service.LanguageService;
import greencity.service.TagsService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@WebMvcTest(HabitController.class)
@WithMockUser(username = "user")
@ContextConfiguration(classes = {GreenCityApplication.class})
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private LanguageService languageService;

    @MockBean
    private UserService userService;

    @MockBean
    private TagsService tagsService;

    @MockBean
    private HabitService habitService;

    private UserVO userVO;

    @BeforeEach
    void setUp() {
        userVO = UserVO.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(languageService.findAllLanguageCodes()).thenReturn(List.of("en"));
    }

    @Test
    void getHabitById_HabitExists_ReturnsHabit() throws Exception {
        HabitDto habitDto = HabitDto.builder()
                .id(1L)
                .image("someImageUrl")
                .complexity(2)
                .build();

        when(habitService.getByIdAndLanguageCode(1L, "en")).thenReturn(habitDto);

        mockMvc.perform(get("/habit/{id}", 1L)
                        .locale(Locale.ENGLISH)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.image").value("someImageUrl"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.complexity").value(2));

        verify(habitService).getByIdAndLanguageCode(1L, "en");
    }

    @Test
    void getAll_HabitsExist_ReturnsHabits() throws Exception {
        PageableDto<HabitDto> pageableDto = new PageableDto<>(
                List.of(new HabitDto()),
                1,
                1,
                1
        );

        when(habitService.getAllHabitsByLanguageCode(any(), any(), eq("en"))).thenReturn(pageableDto);

        mockMvc.perform(get("/habit")
                        .locale(Locale.ENGLISH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(habitService).getAllHabitsByLanguageCode(any(), any(), eq("en"));
    }

    @Test
    void getShoppingListItems_HabitExists_ReturnsItems() throws Exception {
        ShoppingListItemDto item = ShoppingListItemDto.builder()
                .id(1L)
                .text("Example text")
                .status("Pending")
                .build();

        List<ShoppingListItemDto> items = Collections.singletonList(item);

        when(habitService.getShoppingListForHabit(1L, "en")).thenReturn(items);

        mockMvc.perform(get("/habit/1/shopping-list")
                        .locale(Locale.ENGLISH)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].text").value("Example text"));

        verify(habitService).getShoppingListForHabit(1L, "en");
    }

    @Test
    void getAllByTagsAndLanguageCode_ValidTags_ReturnsHabits() throws Exception {
        PageableDto<HabitDto> pageableDto = new PageableDto<>(List.of(new HabitDto()), 1, 1, 1);

        when(habitService.getAllByTagsAndLanguageCode(any(Pageable.class), anyList(), eq("en")))
                .thenReturn(pageableDto);

        mockMvc.perform(get("/habit/tags/search")
                        .param("tags", "tag1", "tag2")
                        .param("page", "0")
                        .param("size", "10")
                        .locale(Locale.forLanguageTag("en"))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(habitService).getAllByTagsAndLanguageCode(any(Pageable.class), eq(Arrays.asList("tag1", "tag2")), eq("en"));
    }

    @Test
    void getAllByDifferentParameters_ValidParameters_ReturnsHabits() throws Exception {
        PageableDto<HabitDto> pageableDto = new PageableDto<>(
                List.of(new HabitDto()),
                1,
                1,
                1
        );

        HandlerMethodArgumentResolver userVoResolver = new UserVoResolver(userVO);
        HandlerMethodArgumentResolver pageableResolver = new PageableResolver();

        when(habitService.getAllByDifferentParameters(eq(userVO), any(), any(), any(), any(), eq("en")))
                .thenReturn(pageableDto);

        mockMvc = MockMvcBuilders.standaloneSetup(new HabitController(habitService, tagsService))
                .setCustomArgumentResolvers(userVoResolver, pageableResolver)
                .build();

        mockMvc.perform(get("/habit/search")
                        .param("tags", "tag1", "tag2")
                        .param("isCustomHabit", "true")
                        .param("complexities", "1", "2")
                        .locale(Locale.ENGLISH)
                        .with(authentication(createAuth(userVO))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(habitService).getAllByDifferentParameters(eq(userVO), any(), any(), any(), any(), eq("en"));
    }

    @Test
    void getAllByDifferentParameters_NoParamsProvided_ThrowsBadRequestException() throws Exception {
        when(habitService.getAllByDifferentParameters(
                eq(userVO),
                any(Pageable.class),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq("en")
        )).thenThrow(new BadRequestException("You should enter at least one parameter"));

        mockMvc.perform(get("/habit/search")
                        .locale(Locale.ENGLISH))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(
                        BadRequestException.class,
                        result.getResolvedException()
                ))
                .andExpect(result -> assertEquals(
                        "You should enter at least one parameter",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()
                ));
    }

    @Test
    void findAllHabitsTags_TagsExist_ReturnsTags() throws Exception {
        List<String> tags = List.of("Recycling", "Conservation");

        when(tagsService.findAllHabitsTags("en")).thenReturn(tags);

        mockMvc.perform(get("/habit/tags").locale(Locale.ENGLISH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("Recycling"))
                .andExpect(jsonPath("$[1]").value("Conservation"));

        verify(tagsService).findAllHabitsTags(eq("en"));
    }

    @Test
    void addCustomHabit_ValidHabit_CreatesHabit() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        AddCustomHabitDtoRequest request = AddCustomHabitDtoRequest.builder().complexity(1).build();
        AddCustomHabitDtoResponse response = AddCustomHabitDtoResponse.builder().id(1L).complexity(1).build();
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile jsonFile = new MockMultipartFile(
                "request",
                "",
                "application/json",
                requestJson.getBytes()
        );
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(habitService.addCustomHabit(any(AddCustomHabitDtoRequest.class), any(MultipartFile.class), eq("user")))
                .thenReturn(response);

        mockMvc.perform(multipart("/habit/custom")
                        .file(jsonFile)
                        .file(image)
                        .with(csrf())
                        .principal(() -> "user")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(habitService).addCustomHabit(any(AddCustomHabitDtoRequest.class), any(MultipartFile.class), eq("user"));
    }

    @Test
    void getFriendsAssignedToHabitProfilePictures_HabitExists_ReturnsPictures() throws Exception {
        Long habitId = 1L;
        UserProfilePictureDto pic1 = new UserProfilePictureDto(1L, "url1", "Description 1");
        UserProfilePictureDto pic2 = new UserProfilePictureDto(2L, "url2", "Description 2");
        List<UserProfilePictureDto> pictures = List.of(pic1, pic2);

        HandlerMethodArgumentResolver userVoResolver = new UserVoResolver(userVO);

        when(habitService.getFriendsAssignedToHabitProfilePictures(habitId, userVO.getId())).thenReturn(pictures);

        mockMvc = MockMvcBuilders.standaloneSetup(new HabitController(habitService, tagsService))
                .setCustomArgumentResolvers(userVoResolver)
                .build();

        mockMvc.perform(get("/habit/" + habitId + "/friends/profile-pictures")
                        .with(authentication(createAuth(userVO))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("url1"))
                .andExpect(jsonPath("$[1].name").value("url2"));

        verify(habitService).getFriendsAssignedToHabitProfilePictures(habitId, userVO.getId());
    }

    private Authentication createAuth(UserVO user) {
        return new UsernamePasswordAuthenticationToken(user, "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
