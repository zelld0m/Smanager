package com.mall;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class RedirectFilter implements Filter{

	private static String solrUrl = null;
	public static final Pattern PATTERN = Pattern.compile("solr14/(.*)/",Pattern.DOTALL);
	private static Logger logger = Logger.getLogger(RedirectFilter.class);
	
    public void init(FilterConfig filterConfig) throws ServletException {
    	solrUrl = filterConfig.getInitParameter("solrUrl");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	HttpServletRequest req = (HttpServletRequest) request;
    	String uri = req.getRequestURI();
    	String store = null;
    	Matcher matcher = PATTERN.matcher(uri);
    	if (matcher.find()) {
    		store = matcher.group(1);
    	}
    	StringBuilder url = new StringBuilder(solrUrl).append(store).append("/select?").append(req.getQueryString());
    	logger.info("-=URL=->> " +url.toString());
    	HttpServletResponse res = (HttpServletResponse) response;
    	res.sendRedirect(url.toString());
    }

    public void destroy() {
    }

}