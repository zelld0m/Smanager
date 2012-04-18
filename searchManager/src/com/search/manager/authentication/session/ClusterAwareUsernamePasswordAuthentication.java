package com.search.manager.authentication.session;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class ClusterAwareUsernamePasswordAuthentication extends UsernamePasswordAuthenticationFilter {
	private static final Logger logger = Logger.getLogger(ClusterAwareUsernamePasswordAuthentication.class);
	
	@Autowired 
	private ClusterAwareSessionRegistryImpl sessionRegistry;
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, Authentication authResult)
			throws IOException, ServletException {
		
		Object principal = SessionRegistryUtils.getPrincipal(authResult);
		String sessionId = SessionRegistryUtils.getSessionId(authResult);
		
		sessionRegistry.registerNewSession(sessionId, principal);
		
		super.successfulAuthentication(request, response, authResult);
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {
		
		logger.info("===Failed Login===");
		super.unsuccessfulAuthentication(request, response, failed);
	}
	
	public ClusterAwareSessionRegistryImpl getSessionRegistry() {
		return sessionRegistry;
	}

	public void setSessionRegistry(ClusterAwareSessionRegistryImpl sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
}