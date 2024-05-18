package greencity.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public abstract class BaseArgumentResolver<T> implements HandlerMethodArgumentResolver {

    private final Class<T> parameterType;

    public BaseArgumentResolver(Class<T> parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameterType.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return resolveArgumentInternal(parameter, mavContainer, webRequest, binderFactory);
    }

    protected abstract T resolveArgumentInternal(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                 NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception;
}
