package com.search.manager.authentication.session;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.cookie.CookieUtils;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.User;
import com.search.manager.service.UtilityService;
import com.search.ws.ConfigManager;

public class ClusterAwareUsernamePasswordAuthentication extends UsernamePasswordAuthenticationFilter {
	private static final Logger logger = Logger.getLogger(ClusterAwareUsernamePasswordAuthentication.class);

	@Autowired private ClusterAwareSessionRegistryImpl sessionRegistry;
	@Autowired private DaoService daoService;
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, Authentication authResult)
	throws IOException, ServletException {

		Object principal = SessionRegistryUtils.getPrincipal(authResult);
		String sessionId = SessionRegistryUtils.getSessionId(authResult);

		sessionRegistry.registerNewSession(sessionId, principal);

		ConfigManager cm = ConfigManager.getInstance();
		String storeId = ((UserDetailsImpl)authResult.getPrincipal()).getStoreId();
		String storeName = cm.getStoreName(storeId);
		String serverName = cm.getStoreParameter(storeId, "default-server");
		
		UtilityService.setStoreId(storeId);
		UtilityService.setStoreName(storeName);
		UtilityService.setServerName(serverName);
		
		//Delete cookies
		response.addCookie(CookieUtils.expireNow("server.selection", request.getContextPath()));
		response.addCookie(CookieUtils.expireNow("server.selected", request.getContextPath()));
				
		User user = new User();
		user.setUsername(obtainUsername(request));
		user.setLastAccessDate(DateTime.now());
		user.setIp(request.getRemoteAddr());
		user.setSuccessiveFailedLogin(0);
		user.setAccountNonLocked(true);
		try {
			daoService.login(user);
		} catch (DaoException e) {
			logger.error("Updating successful login failed! " + e.getMessage(), e);
		}
	
		super.successfulAuthentication(request, response, authResult);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
	throws IOException, ServletException {

		logger.info("===Failed Login===");
		try {
			if (daoService.getUser(obtainUsername(request)) == null) {
				WebApplicationContext webAppContext = ContextLoader.getCurrentWebApplicationContext();
				MessageSource messageSource = (MessageSource)webAppContext.getBean("messageSource");
				failed = new AuthenticationCredentialsNotFoundException(messageSource.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", null, null));
			}
		} catch (DaoException e) {
			logger.error("Failed to get user! " + e.getMessage(), e);
		}
		super.unsuccessfulAuthentication(request, response, failed);
		User user = new User();
		user.setUsername(obtainUsername(request));
		user.setSuccessiveFailedLogin(1);
		try {
			daoService.login(user);
		} catch (DaoException e) {
			logger.error("Updating unsuccessful login failed! " + e.getMessage(), e);
		}
	}

	public ClusterAwareSessionRegistryImpl getSessionRegistry() {
		return sessionRegistry;
	}

	public void setSessionRegistry(ClusterAwareSessionRegistryImpl sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
}