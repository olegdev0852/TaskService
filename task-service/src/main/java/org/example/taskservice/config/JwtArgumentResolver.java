package org.example.taskservice.config;

import org.example.jwtstarter.component.JwtParser;
import org.example.jwtstarter.model.ParsedJwt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class JwtArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtParser jwtParser;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return ParsedJwt.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        try {
            return jwtParser.extractJwt(request);
        } catch (Exception e) {
            System.out.println("JWT PARSING ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }
}