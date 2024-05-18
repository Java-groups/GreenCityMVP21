package greencity.reslver;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageableResolver extends BaseArgumentResolver<Pageable> {

    public PageableResolver() {
        super(Pageable.class);
    }

    @Override
    protected Pageable resolveArgumentInternal(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                               NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return PageRequest.of(0, 10);
    }
}
