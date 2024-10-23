package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitService;
import greencity.service.TagsService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HabitControllerTest {

    private static final String habitLink = "/habit";
    private MockMvc mockMvc;

    @InjectMocks
    private HabitController habitController;

    @Mock
    private HabitService habitService;

    @Mock
    private TagsService tagsService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(habitController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper)
                )
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void getHabitByIdTest() throws Exception {
        Long id = 0L;
        String languageCode = "en";

        mockMvc.perform(get(habitLink + "/" + id))
                .andExpect(status().isOk());
        verify(habitService).getByIdAndLanguageCode(id, languageCode);
    }

    @Test
    void getAllTest() throws Exception {
        String languageCode = "en";
        UserVO userVO = ModelUtils.getUserVO();
        Pageable pageable = getPageable();

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitLink + "?id=1")
                        .principal(userVO::getEmail))
                .andExpect(status().isOk());

        verify(habitService).getAllHabitsByLanguageCode(userVO, pageable, languageCode);
    }

    @Test
    void getShoppingListItemsTest() throws Exception {
        Long id = 0L;
        String languageCode = "en";

        mockMvc.perform(get(habitLink + "/" + id + "/shopping-list"))
                .andExpect(status().isOk());

        verify(habitService).getShoppingListForHabit(id, languageCode);
    }

    @Test
    void getAllByTagsAndLanguageCodeTest() throws Exception {
        String languageCode = "en";
        List<String> tags = List.of("0", "1");
        Pageable pageable = getPageable();

        mockMvc.perform(get(habitLink + "/tags/search?lang=en&tags=0,1"))
                .andExpect(status().isOk());

        verify(habitService).getAllByTagsAndLanguageCode(pageable, tags, languageCode);
    }

    @Test
    void getAllByDifferentParametersTest() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        String languageCode = "en";
        Pageable pageable = getPageable();

        String tagsStr = "0,1";
        String isCustomHabitStr = "true";
        String complexetiesStr = "0,1";

        List<String> tagsList = Arrays.asList(tagsStr.split(","));
        boolean isCustomHabitBoolean = Boolean.parseBoolean(isCustomHabitStr);
        List<Integer> complexitiesList = Arrays.stream(complexetiesStr.split(",")).map(Integer::parseInt).toList();

        Optional<List<String>> tags = Optional.of(tagsList);
        Optional<Boolean> isCustomHabit = Optional.of(isCustomHabitBoolean);
        Optional<List<Integer>> complexities = Optional.of(complexitiesList);

        ResultMatcher resultMatcher;

        if(isValid(tags, isCustomHabit, complexities)) {
            resultMatcher = status().isOk();
        } else {
            resultMatcher = status().isBadRequest();
        }

        String requestParams = "?tags=%s&isCustomHabit=%b&complexities=%s".formatted(tagsStr, isCustomHabitStr, complexetiesStr);

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitLink + "/search" + requestParams)
                        .principal(userVO::getEmail))
                .andExpect(resultMatcher);

        verify(habitService).getAllByDifferentParameters(userVO, pageable, tags, isCustomHabit, complexities, languageCode);
    }

    @Test
    void findAllHabitsTagsTest() throws Exception {
        String languageCode = "en";

        mockMvc.perform(get(habitLink + "/tags"))
                .andExpect(status().isOk());
        verify(tagsService).findAllHabitsTags(languageCode);
    }

    @Test
    void addCustomHabitTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        String principalName = "Olivia.Johnson@gmail.com";

        String json = """
                {
                  "complexity": 2,
                  "defaultDuration": 1,
                  "habitTranslations": [
                    {
                      "description": "some description",
                      "habitItem": "some habitItem",
                      "languageCode": "en",
                      "name": "some name"
                    }
                  ],
                  "image": null,
                  "customShoppingListItemDto": [
                    {
                      "id": 2,
                      "text": "not empty text",
                      "status": "ACTIVE"
                    }
                  ],
                  "tagIds": [1,2]
                }
                """;

        MockMultipartFile imageFile = new MockMultipartFile("image","0.png","image/png", "image-content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("request", "", "application/json", json.getBytes());

        when(principal.getName()).thenReturn(principalName);

        mockMvc.perform(multipart(habitLink + "/custom")
                        .file(jsonFile)
                        .file(imageFile)
                        .principal(principal))
                .andExpect(status().isCreated());

        ObjectMapper objectMapper = new ObjectMapper();
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = objectMapper.readValue(json, AddCustomHabitDtoRequest.class);

        verify(habitService).addCustomHabit(eq(addCustomHabitDtoRequest), eq(imageFile), eq(principalName));
    }

    @Test
    void getFriendsAssignedToHabitProfilePicturesTest() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        Long habitId = 1L;

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitLink + "/" + habitId + "/friends/profile-pictures")
                        .principal(userVO::getEmail))
                .andExpect(status().isOk());

        verify(habitService).getFriendsAssignedToHabitProfilePictures(habitId, userVO.getId());
    }

    private boolean isValid(Optional<List<String>> tags, Optional<Boolean> isCustomHabit,
                            Optional<List<Integer>> complexities) {
        return ((tags.isPresent() && !tags.get().isEmpty()) || isCustomHabit.isPresent()
                || (complexities.isPresent() && !complexities.get().isEmpty()));
    }

    private Pageable getPageable() {
        int pageNumber = 0;
        int pageSize = 20;
        return PageRequest.of(pageNumber, pageSize);
    }

}

