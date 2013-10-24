package com.search.manager.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class BaseInterceptor extends HandlerInterceptorAdapter {

    private AntPathMatcher matcher = new AntPathMatcher();

    private List<String> inclusions;

    private List<String> exclusions;

    public void setInclusions(List<String> inclusions) {
        this.inclusions = inclusions;
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }

    private boolean isIncluded(String context, String uri) {
        if (CollectionUtils.isEmpty(inclusions)) {
            return true;
        } else {
            return match(context, uri, inclusions);
        }
    }

    private boolean isExcluded(String context, String uri) {
        if (CollectionUtils.isEmpty(exclusions)) {
            return false;
        } else {
            return match(context, uri, exclusions);
        }
    }

    /**
     * This method assumes both parameters are not null and patterns is not
     * empty
     * 
     * @param str
     *            String to be matched
     * @param patterns
     *            List of patterns
     * 
     * @return true if at least one of patterns match str
     */
    private boolean match(String context, String str, List<String> patterns) {
        for (String pattern : patterns) {
            if (matcher.match(context + pattern, str))
                return true;
        }

        return false;
    }

    private boolean canHandle(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();

        return isIncluded(context, uri) && !isExcluded(context, uri);
    }

    @Override
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
