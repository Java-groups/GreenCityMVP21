package greencity.controller;

import greencity.dto.habit.*;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.enums.HabitAssignStatus;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.HabitAssignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HabitAssignControllerTest {
    static final String habitAssignControllerLink = "/habit/assign";

    MockMvc mockMvc;

    @InjectMocks
    HabitAssignController habitAssignController;

    @Mock
    HabitAssignService habitAssignService;

    ObjectMapper objectMapper;

    final Principal principal = getPrincipal();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test assign default habit for user method")
    void assignDefault_EndpointResponse_StatusCreated() throws Exception {
        mockMvc.perform(post(habitAssignControllerLink + "/{habitId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(habitAssignService, never()).assignDefaultHabitForUser(getUserVO().getId(), getUserVO());
    }

    @Test
    @DisplayName("Test retrieve a habit assignment")
    void getHabitAssign_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getByHabitAssignIdAndUserId(1L, null, "en");
    }

    @Test
    @DisplayName("Test update status by habit assignment ID")
    void updateAssignByHabitAssignId_EndpointResponse_StatusOk() throws Exception {
        HabitAssignStatDto habitAssignStatDto = new HabitAssignStatDto();
        habitAssignStatDto.setStatus(HabitAssignStatus.EXPIRED);

        mockMvc.perform(patch(habitAssignControllerLink + "/{habitAssignId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitAssignStatDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateStatusByHabitAssignId(1L, habitAssignStatDto);
    }

    @Test
    @DisplayName("Test update habit assignment duration")
    void updateHabitAssignDuration_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/update-habit-duration", 1L)
                        .principal(principal)
                        .param("duration", "7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserHabitInfoDuration(1L, null, 7);
    }

    @Test
    @DisplayName("Test enroll in a habit")
    void enrollHabit_EndpointResponse_StatusOk() throws Exception {
        LocalDate date = LocalDate.now();

        mockMvc.perform(post(habitAssignControllerLink + "/{habitAssignId}/enroll/{date}", 1L, date)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).enrollHabit(1L, null, date, "en");
    }

    @Test
    @DisplayName("Test unenroll from a habit")
    void unenrollHabit_EndpointResponse_StatusOk() throws Exception {
        LocalDate date = LocalDate.now();

        mockMvc.perform(post(habitAssignControllerLink + "/{habitAssignId}/unenroll/{date}", 1L, date)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).unenrollHabit(1L, null, date);
    }

    @Test
    @DisplayName("Test get habit assignments between specific dates")
    void getHabitAssignBetweenDates_EndpointResponse_StatusOk() throws Exception {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(1);

        mockMvc.perform(get(habitAssignControllerLink + "/activity/{from}/to/{to}", fromDate, toDate)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignsBetweenDates(null, fromDate, toDate, "en");
    }

    @Test
    @DisplayName("Test cancel a habit assignment")
    void cancelHabitAssign_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(patch(habitAssignControllerLink + "/cancel/{habitId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).cancelHabitAssign(1L, null);
    }

    @Test
    @DisplayName("Test retrieve habit assignment by habit ID")
    void getHabitAssignByHabitId_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitId}/active", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignByUserIdAndHabitId(null, 1L, "en");
    }

    @Test
    @DisplayName("Test get current user habit assignments")
    void getCurrentUserHabitAssignsByIdAndAcquired_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/allForCurrentUser")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(null, "en");
    }

    @Test
    @DisplayName("Test delete a habit assignment")
    void deleteHabitAssign_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(delete(habitAssignControllerLink + "/delete/{habitAssignId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).deleteHabitAssign(1L, null);
    }

    @Test
    @DisplayName("Test update shopping list status")
    void updateShoppingListStatus_EndpointResponse_StatusOk() throws Exception {
        UserShoppingListItemAdvanceDto userShoppingListItemAdvanceDto = UserShoppingListItemAdvanceDto.builder()
                .shoppingListItemId(1L)
                .status(ShoppingListItemStatus.DISABLED)
                .build();

        UpdateUserShoppingListDto updateUserShoppingListDto = UpdateUserShoppingListDto.builder()
                .habitAssignId(1L)
                .userShoppingListItemId(1L)
                .userShoppingListAdvanceDto(List.of(userShoppingListItemAdvanceDto))
                .build();

        mockMvc.perform(put(habitAssignControllerLink + "/saveShoppingListForHabitAssign")
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(updateUserShoppingListDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserShoppingListItem(updateUserShoppingListDto);
    }

    @Test
    @DisplayName("Test assign custom properties to a habit")
    void assignCustom_EndpointResponse_StatusCreated() throws Exception {
        HabitAssignPropertiesDto habitAssignPropertiesDto = HabitAssignPropertiesDto.builder()
                .duration(7)
                .defaultShoppingListItems(List.of(1L))
                .build();

        HabitAssignCustomPropertiesDto habitAssignCustomPropertiesDto = HabitAssignCustomPropertiesDto.builder()
                .habitAssignPropertiesDto(habitAssignPropertiesDto)
                .friendsIdsList(List.of(1L))
                .build();

        mockMvc.perform(post(habitAssignControllerLink + "/{habitId}/custom", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitAssignCustomPropertiesDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test get all habit assignments by habit ID and acquired status")
    void getAllHabitAssignsByHabitIdAndAcquired_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitId}/all", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByHabitIdAndStatusNotCancelled(1L, "en");
    }

    @Test
    @DisplayName("Test get in-progress habit assignments on a specific date")
    void getInprogressHabitAssignOnDate_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/active/{date}", LocalDate.now())
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).findInprogressHabitAssignsOnDate(null, LocalDate.now(), "en");
    }

    @Test
    @DisplayName("Test get users' habits by habit ID")
    void getUsersHabitByHabitId_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/more", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitByUserIdAndHabitAssignId(null, 1L, "en");
    }

    @Test
    @DisplayName("Test retrieve lists for users by user ID and habit ID")
    void getUserAndCustomListByUserIdAndHabitId_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/allUserAndCustomList", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(null, 1L, "en");
    }

    @Test
    @DisplayName("Test retrieve lists for users by user ID, habit ID, and locale")
    void getUserAndCustomListByUserIdAndHabitIdAndLocale_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/allUserAndCustomList", 1L)
                        .locale(Locale.forLanguageTag("ua"))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(null, 1L, "ua");
    }

    @Test
    @DisplayName("Test get lists of user and custom shopping items in progress")
    void getListOfUserAndCustomShoppingListsInprogress_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/allUserAndCustomShoppingListsInprogress")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getListOfUserAndCustomShoppingListsWithStatusInprogress(null, "en");
    }

    @Test
    @DisplayName("Test update lists of user and custom shopping items")
    void updateUserAndCustomShoppingLists_EndpointResponse_StatusOk() throws Exception {
        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto.builder()
                .id(1L)
                .text("text")
                .status(ShoppingListItemStatus.DISABLED)
                .build();

        CustomShoppingListItemResponseDto customShoppingListItemResponseDto = CustomShoppingListItemResponseDto.builder()
                .id(1L)
                .text("text")
                .status(ShoppingListItemStatus.DISABLED)
                .build();

        UserShoppingAndCustomShoppingListsDto userShoppingAndCustomShoppingListsDto = UserShoppingAndCustomShoppingListsDto.builder()
                .userShoppingListItemDto(List.of(userShoppingListItemResponseDto))
                .customShoppingListItemDto(List.of(customShoppingListItemResponseDto))
                .build();

        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/allUserAndCustomList", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userShoppingAndCustomShoppingListsDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).fullUpdateUserAndCustomShoppingLists(null, 1L, userShoppingAndCustomShoppingListsDto, "en");
    }

    @Test
    @DisplayName("Test update the status of displayed progress notifications")
    void updateProgressNotificationHasDisplayed_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/updateProgressNotificationHasDisplayed", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).updateProgressNotificationHasDisplayed(1L, null);
    }
}
