package com.search.manager.interceptor;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.*;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.springframework.web.servlet.ModelAndView;

public class BaseInterceptorTest {

    // Class under test
    private BaseInterceptor interceptor;

    // Mock Request
    private HttpServletRequest mockRequest;

    // Flags for verification
    private boolean beforeCalled;
    private boolean afterCalled;
    private boolean completeCalled;

    // Attribute holder
    private Map<String, Object> attributes;

    // Constants
    private static final List<String> paths = Arrays.asList("/dwr/**", "/security/**");
    private static final List<String> empty = Collections.emptyList();
    private static final String CONTEXT = "/searchManager";
    private static final String DWR_URL = "/searchManager/dwr/afs-pl-schpd07/macmall/select?q=ipad";
    private static final String SEARCH_URL = "/searchManager/search/afs-pl-schpd07/macmall/select?q=ipad";

    @Before
    public void setup() {
        interceptor = new BaseInterceptor() {
            @Override
            protected boolean before(HttpServletRequest request, HttpServletResponse response, Object handler) {
                beforeCalled = true;
                return true;
            }

            @Override
            protected void after(HttpServletRequest request, HttpServletResponse response, Object handler,
                    ModelAndView modelAndView) {
                afterCalled = true;
            }

            @Override
            protected void complete(HttpServletRequest request, HttpServletResponse response, Object handler,
                    Exception ex) {
                completeCalled = true;
            }
        };

        attributes = new HashMap<String, Object>();
        mockRequest = PowerMock.createMock(HttpServletRequest.class);

        final Capture<String> setKey = new Capture<String>();
        final Capture<Object> setValue = new Capture<Object>();
        final Capture<String> getKey = new Capture<String>();

        mockRequest.setAttribute(and(capture(setKey), isA(String.class)), and(capture(setValue), isA(Object.class)));
        expectLastCall().andAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                attributes.put(setKey.getValue(), setValue.getValue());
                return null;
            }
        }).anyTimes();

        expect(mockRequest.getAttribute(and(capture(getKey), isA(String.class)))).andAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return attributes.get(getKey.getValue());
            }
        }).anyTimes();

        expect(mockRequest.getContextPath()).andReturn(CONTEXT).anyTimes();
    }

    @Test
    public void testWhenNoIncludesAndNoExcludes() throws Exception {
        prepare(SEARCH_URL, empty, empty);
        verify(true);
    }

    @Test
    public void testWhenURLisIncluded() throws Exception {
        prepare(DWR_URL, paths, empty);
        verify(true);
    }

    @Test
    public void testWhenURLisNotIncluded() throws Exception {
        prepare(SEARCH_URL, paths, empty);
        verify(false);
    }

    @Test
    public void testWhenURLisExcluded() throws Exception {
        prepare(DWR_URL, empty, paths);
        verify(false);
    }

    @Test
    public void testWhenURLisNotExcluded() throws Exception {
        prepare(SEARCH_URL, empty, paths);
        verify(true);
    }

    @Test
    public void testWhenURLisIncludedAndExcluded() {
        prepare(DWR_URL, paths, paths);
        verify(false);
    }

    @Test
    public void testWhenURLisNotIncludedAndNotExcluded() {
        prepare(SEARCH_URL, paths, paths);
        verify(false);
    }

    private void prepare(String url, List<String> includes, List<String> excludes) {
        interceptor.setIncludes(includes);
        interceptor.setExcludes(excludes);

        expect(mockRequest.getRequestURI()).andReturn(url).anyTimes();
        replay(mockRequest);
    }

    private void verify(boolean accepted) {
        try {
            interceptor.preHandle(mockRequest, null, null);
            assertEquals(accepted, beforeCalled);

            interceptor.postHandle(mockRequest, null, null, null);
            assertEquals(accepted, afterCalled);

            interceptor.afterCompletion(mockRequest, null, null, null);
            assertEquals(accepted, completeCalled);

            EasyMock.verify(mockRequest);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }
}
