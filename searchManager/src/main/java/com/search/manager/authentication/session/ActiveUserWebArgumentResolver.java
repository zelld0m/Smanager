package com.search.manager.authentication.session;

import java.lang.annotation.Annotation;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import com.search.manager.authentication.dao.internal.UserDetailsImpl;

@Component("activeUserResolver")
public class ActiveUserWebArgumentResolver implements WebArgumentResolver {

    public ActiveUserWebArgumentResolver() {
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) {
        Annotation[] annotations = methodParameter.getParameterAnnotations();

        if (methodParameter.getParameterType().equals(User.class)) {
            for (Annotation annotation : annotations) {
                if (ActiveUser.class.isInstance(annotation)) {
                    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                    if (principal != null && principal instanceof UserDetailsImpl) {
                        return principal;
                    }
                }
            }
        }

        return WebArgumentResolver.UNRESOLVED;
    }
}