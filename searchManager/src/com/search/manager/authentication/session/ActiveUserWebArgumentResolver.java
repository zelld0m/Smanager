package com.search.manager.authentication.session;

import java.lang.annotation.Annotation;
import java.security.Principal;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class ActiveUserWebArgumentResolver implements WebArgumentResolver {

	@Override
	  public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest)
    {
        Annotation[] annotations = methodParameter.getParameterAnnotations();

        if(methodParameter.getParameterType().equals(User.class))
        {
            for(Annotation annotation : annotations)
            {
                if(ActiveUser.class.isInstance(annotation))
                {
                    Principal principal = webRequest.getUserPrincipal();
                    return (User)((Authentication) principal).getPrincipal();
                }
            }
        }

        return WebArgumentResolver.UNRESOLVED;
    }
}