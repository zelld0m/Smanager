package com.search.manager.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class BaseInterceptor implements HandlerInterceptor {

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final String key = this.getClass().getName() + "$" + this.hashCode();

    private List<String> includes;

    private List<String> excludes;

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    private boolean isIncluded(HttpServletRequest request) {
        return CollectionUtils.isEmpty(includes) || match(request, includes);
    }

    private boolean isExcluded(HttpServletRequest request) {
        return CollectionUtils.isNotEmpty(excludes) && match(request, excludes);
    }

    /**
     * This method assumes both parameters are not null and patterns is not empty.
     * 
     * @param request Request to be matched
     * @param patterns List of patterns
     * 
     * @return true if at least one of patterns match str
     */
    private boolean match(HttpServletRequest request, List<String> patterns) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();

        for (String pattern : patterns) {
            if (matcher.match(context + pattern, uri))
                return true;
        }

        return false;
    }

    private boolean canHandle(HttpServletRequest request) {
        if (request.getAttribute(key) == null) {
            // Store result as request attribute to minimize computation.
            request.setAttribute(key, !isExcluded(request) && isIncluded(request));
        }

        return (Boolean) request.getAttribute(key);
    }

    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return canHandle(request) ? before(request, response, handler) : true;
    }

    @Override
    public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (canHandle(request))
            after(request, response, handler, modelAndView);
    }

    @Override
    public final void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        if (canHandle(request))
            complete(request, response, handler, ex);
    }

    protected boolean before(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    protected void after(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    protected void complete(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }
}
