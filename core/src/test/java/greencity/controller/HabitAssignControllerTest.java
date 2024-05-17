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
class HabitAssignControllerTest {
    static final String habitAssignControllerLink = "/habit/assign";

    MockMvc mockMvc;

    @InjectMocks
    HabitAssignController habitAssignController;

    @Mock
    HabitAssignService habitAssignService;

    final ObjectMapper objectMapper = new ObjectMapper();

    final Principal principal = getPrincipal();
    final Long HABIT_ID = 1L;
    final Long USER_ID = 1L;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
                .build();
    }

    @Test
    @DisplayName("Test assign default habit for user method")
    void assignDefault_EndpointResponse_StatusCreated() throws Exception {
        mockMvc.perform(post(habitAssignControllerLink + "/{habitId}", HABIT_ID)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(habitAssignService, never()).assignDefaultHabitForUser(getUserVO().getId(), getUserVO());
    }

    @Test
    @DisplayName("Test retrieve a habit assignment")
    void getHabitAssign_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getByHabitAssignIdAndUserId(HABIT_ID, USER_ID, "en");
    }

    @Test
    @DisplayName("Test update status by habit assignment ID")
    void updateAssignByHabitAssignId_EndpointResponse_StatusOk() throws Exception {
        HabitAssignStatDto habitAssignStatDto = new HabitAssignStatDto();
        habitAssignStatDto.setStatus(HabitAssignStatus.EXPIRED);

        mockMvc.perform(patch(habitAssignControllerLink + "/{habitAssignId}", HABIT_ID)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitAssignStatDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateStatusByHabitAssignId(HABIT_ID, habitAssignStatDto);
    }

    @Test
    @DisplayName("Test update habit assignment duration")
    void updateHabitAssignDuration_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/update-habit-duration", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .param("duration", "7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserHabitInfoDuration(HABIT_ID, USER_ID, 7);
    }

    @Test
    @DisplayName("Test enroll in a habit")
    void enrollHabit_EndpointResponse_StatusOk() throws Exception {
        LocalDate date = LocalDate.now();

        mockMvc.perform(post(habitAssignControllerLink + "/{habitAssignId}/enroll/{date}", HABIT_ID, date)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).enrollHabit(HABIT_ID, USER_ID, date, "en");
    }

    @Test
    @DisplayName("Test unenroll from a habit")
    void unenrollHabit_EndpointResponse_StatusOk() throws Exception {
        LocalDate date = LocalDate.now();

        mockMvc.perform(post(habitAssignControllerLink + "/{habitAssignId}/unenroll/{date}", HABIT_ID, date)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).unenrollHabit(HABIT_ID, USER_ID, date);
    }

    @Test
    @DisplayName("Test get habit assignments between specific dates")
    void getHabitAssignBetweenDates_EndpointResponse_StatusOk() throws Exception {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(1);

        mockMvc.perform(get(habitAssignControllerLink + "/activity/{from}/to/{to}", fromDate, toDate)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignsBetweenDates(USER_ID, fromDate, toDate, "en");
    }

    @Test
    @DisplayName("Test cancel a habit assignment")
    void cancelHabitAssign_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(patch(habitAssignControllerLink + "/cancel/{habitId}", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).cancelHabitAssign(HABIT_ID, USER_ID);
    }

    @Test
    @DisplayName("Test retrieve habit assignment by habit ID")
    void getHabitAssignByHabitId_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitId}/active", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignByUserIdAndHabitId(USER_ID, HABIT_ID, "en");
    }

    @Test
    @DisplayName("Test get current user habit assignments")
    void getCurrentUserHabitAssignsByIdAndAcquired_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/allForCurrentUser")
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(USER_ID, "en");
    }

    @Test
    @DisplayName("Test delete a habit assignment")
    void deleteHabitAssign_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(delete(habitAssignControllerLink + "/delete/{habitAssignId}", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).deleteHabitAssign(HABIT_ID, USER_ID);
    }

    @Test
    @DisplayName("Test update shopping list status")
    void updateShoppingListStatus_EndpointResponse_StatusOk() throws Exception {
        UserShoppingListItemAdvanceDto userShoppingListItemAdvanceDto = UserShoppingListItemAdvanceDto.builder()
                .status(ShoppingListItemStatus.DISABLED)
                .build();

        UpdateUserShoppingListDto updateUserShoppingListDto = UpdateUserShoppingListDto.builder()
                .habitAssignId(HABIT_ID)
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
                .defaultShoppingListItems(null)
                .build();

        HabitAssignCustomPropertiesDto habitAssignCustomPropertiesDto = HabitAssignCustomPropertiesDto.builder()
                .habitAssignPropertiesDto(habitAssignPropertiesDto)
                .friendsIdsList(null)
                .build();

        mockMvc.perform(post(habitAssignControllerLink + "/{habitId}/custom", HABIT_ID)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitAssignCustomPropertiesDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test get all habit assignments by habit ID and acquired status")
    void getAllHabitAssignsByHabitIdAndAcquired_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitId}/all", HABIT_ID)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByHabitIdAndStatusNotCancelled(HABIT_ID, "en");
    }

    @Test
    @DisplayName("Test get in-progress habit assignments on a specific date")
    void getInprogressHabitAssignOnDate_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/active/{date}", LocalDate.now())
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).findInprogressHabitAssignsOnDate(USER_ID, LocalDate.now(), "en");
    }

    @Test
    @DisplayName("Test get users' habits by habit ID")
    void getUsersHabitByHabitId_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/more", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitByUserIdAndHabitAssignId(USER_ID, HABIT_ID, "en");
    }

    @Test
    @DisplayName("Test retrieve lists for users by user ID and habit ID")
    void getUserAndCustomListByUserIdAndHabitId_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/allUserAndCustomList", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(USER_ID, HABIT_ID, "en");
    }

    @Test
    @DisplayName("Test retrieve lists for users by user ID, habit ID, and locale")
    void getUserAndCustomListByUserIdAndHabitIdAndLocale_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/allUserAndCustomList", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .locale(Locale.forLanguageTag("ua"))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(USER_ID, HABIT_ID, "ua");
    }

    @Test
    @DisplayName("Test get lists of user and custom shopping items in progress")
    void getListOfUserAndCustomShoppingListsInprogress_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(get(habitAssignControllerLink + "/allUserAndCustomShoppingListsInprogress")
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getListOfUserAndCustomShoppingListsWithStatusInprogress(USER_ID, "en");
    }

    @Test
    @DisplayName("Test update lists of user and custom shopping items")
    void updateUserAndCustomShoppingLists_EndpointResponse_StatusOk() throws Exception {
        UserShoppingListItemResponseDto userShoppingListItemResponseDto = UserShoppingListItemResponseDto.builder()
                .text("text")
                .status(ShoppingListItemStatus.DISABLED)
                .build();

        CustomShoppingListItemResponseDto customShoppingListItemResponseDto = CustomShoppingListItemResponseDto.builder()
                .text("text")
                .status(ShoppingListItemStatus.DISABLED)
                .build();

        UserShoppingAndCustomShoppingListsDto userShoppingAndCustomShoppingListsDto = UserShoppingAndCustomShoppingListsDto.builder()
                .userShoppingListItemDto(List.of(userShoppingListItemResponseDto))
                .customShoppingListItemDto(List.of(customShoppingListItemResponseDto))
                .build();

        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/allUserAndCustomList", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userShoppingAndCustomShoppingListsDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).fullUpdateUserAndCustomShoppingLists(USER_ID, HABIT_ID, userShoppingAndCustomShoppingListsDto, "en");
    }

    @Test
    @DisplayName("Test update the status of displayed progress notifications")
    void updateProgressNotificationHasDisplayed_EndpointResponse_StatusOk() throws Exception {
        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/updateProgressNotificationHasDisplayed", HABIT_ID)
                        .param("id", String.valueOf(USER_ID))
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).updateProgressNotificationHasDisplayed(HABIT_ID, USER_ID);
    }
}
