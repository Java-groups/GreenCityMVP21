package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.CommentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class EcoNewsCommentDtoMapperTest {
    private EcoNewsCommentDtoMapper mapper;
    private LocalDateTime now;
    private EcoNewsComment ecoNewsComment;
    private EcoNewsCommentDto expectedEcoNewsCommentDto;

    @BeforeEach
    void setUp() {
        mapper = new EcoNewsCommentDtoMapper();
        now = LocalDateTime.now();
        ecoNewsComment = EcoNewsComment.builder()
                .id(101L)
                .modifiedDate(now)
                .text("Test text")
                .user(User.builder()
                        .id(102L)
                        .name("Test User name")
                        .profilePicturePath("Test Profile Picture Path")
                        .build())
                .usersLiked(new HashSet<>())
                .currentUserLiked(true)
                .deleted(false)
                .build();
        expectedEcoNewsCommentDto = EcoNewsCommentDto.builder()
                .id(101L)
                .modifiedDate(now)
                .text("Test text")
                .author(EcoNewsCommentAuthorDto.builder()
                        .id(102L)
                        .name("Test User name")
                        .userProfilePicturePath("Test Profile Picture Path")
                        .build())
                .likes(0)
                .currentUserLiked(true)
                .build();
    }

    @Test
    void convert_commentDeletedTrue() {
        ecoNewsComment.setDeleted(true);
        EcoNewsCommentDto expectedDeletedEcoNewsCommentDto = EcoNewsCommentDto.builder()
                .id(101L)
                .modifiedDate(now)
                .status(CommentStatus.DELETED)
                .build();

        assertEquals(expectedDeletedEcoNewsCommentDto, mapper.convert(ecoNewsComment));
    }

    @Test
    void convert_commentDeletedFalseAndCreatedDateEqualToModifiedDate() {
        ecoNewsComment.setCreatedDate(now);
        expectedEcoNewsCommentDto.setStatus(CommentStatus.ORIGINAL);

        assertEquals(expectedEcoNewsCommentDto, mapper.convert(ecoNewsComment));
    }

    @Test
    void convert_commentDeletedFalseAndCreatedDateNotEqualToModifiedDate() {
        ecoNewsComment.setCreatedDate(now.minusDays(1));
        expectedEcoNewsCommentDto.setStatus(CommentStatus.EDITED);

        assertEquals(expectedEcoNewsCommentDto, mapper.convert(ecoNewsComment));
    }
}