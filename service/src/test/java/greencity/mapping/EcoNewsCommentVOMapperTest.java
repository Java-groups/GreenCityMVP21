package greencity.mapping;

import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class EcoNewsCommentVOMapperTest {
    private EcoNewsCommentVOMapper mapper;
    private LocalDateTime now;
    EcoNewsComment parentEcoNewsComment;
    EcoNewsCommentVO expectedParentEcoNewsCommentVO;

    @BeforeEach
    void setUp() {
        mapper = new EcoNewsCommentVOMapper();
        now = LocalDateTime.now();
        parentEcoNewsComment = EcoNewsComment.builder()
                .id(101L)
                .user(User.builder()
                        .id(102L)
                        .role(Role.ROLE_ADMIN)
                        .name("Test User name")
                        .build())
                .createdDate(now)
                .modifiedDate(now)
                .text("Test text")
                .deleted(false)
                .currentUserLiked(true)
                .usersLiked(new HashSet<>())
                .ecoNews(EcoNews.builder()
                        .id(103L)
                        .build())
                .build();
        expectedParentEcoNewsCommentVO = EcoNewsCommentVO.builder()
                .id(101L)
                .user(UserVO.builder()
                        .id(102L)
                        .role(Role.ROLE_ADMIN)
                        .name("Test User name")
                        .build())
                .modifiedDate(now)
                .text("Test text")
                .deleted(false)
                .currentUserLiked(true)
                .createdDate(now)
                .usersLiked(new HashSet<>())
                .ecoNews(EcoNewsVO.builder()
                        .id(103L)
                        .build())
                .build();
    }

    @Test
    void convert_parentCommentIsNull() {
        assertEquals(expectedParentEcoNewsCommentVO, mapper.convert(parentEcoNewsComment));
    }

    @Test
    void convert_parentCommentIsNotNull() {
        EcoNewsComment ecoNewsComment = EcoNewsComment.builder()
                .id(101L)
                .user(User.builder()
                        .id(102L)
                        .role(Role.ROLE_ADMIN)
                        .name("Test User name")
                        .build())
                .createdDate(now)
                .modifiedDate(now)
                .parentComment(parentEcoNewsComment)
                .text("Test text")
                .deleted(false)
                .currentUserLiked(true)
                .usersLiked(new HashSet<>())
                .ecoNews(EcoNews.builder()
                        .id(103L)
                        .build())
                .build();
        EcoNewsCommentVO expectedEcoNewsCommentVO = EcoNewsCommentVO.builder()
                .id(101L)
                .user(UserVO.builder()
                        .id(102L)
                        .role(Role.ROLE_ADMIN)
                        .name("Test User name")
                        .build())
                .modifiedDate(now)
                .parentComment(expectedParentEcoNewsCommentVO)
                .text("Test text")
                .deleted(false)
                .currentUserLiked(true)
                .createdDate(now)
                .usersLiked(new HashSet<>())
                .ecoNews(EcoNewsVO.builder()
                        .id(103L)
                        .build())
                .build();

        assertEquals(expectedEcoNewsCommentVO, mapper.convert(ecoNewsComment));
    }
}