package greencity.resolver;

import greencity.dto.user.UserVO;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserVoResolver extends BaseArgumentResolver<UserVO> {

    private final UserVO userVO;

    public UserVoResolver(UserVO userVO) {
        super(UserVO.class);
        this.userVO = userVO;
    }

    @Override
    protected UserVO resolveArgumentInternal(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                             NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return userVO;
    }
}
