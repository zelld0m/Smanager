package com.search.manager.authentication.session;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

		String storeName = ((UserDetailsImpl)authResult.getPrincipal()).getStoreId();
		String serverName = ConfigManager.getInstance().getStoreParameter(storeName, "default-server");
		if (StringUtils.isBlank(storeName)) {
			storeName = "macmall";			
		}
		
		UtilityService.setStoreName(storeName);
		UtilityService.setServerName(serverName);
		
		//Delete cookies
		response.addCookie(CookieUtils.expireNow("server.selection", request.getContextPath()));
		response.addCookie(CookieUtils.expireNow("server.selected", request.getContextPath()));
				
		System.out.println(request.getContextPath());
		User user = new User();
		user.setUsername(obtainUsername(request));
		user.setLastAccessDate(new Date());
		user.setIp(request.getRemoteAddr());
		user.setSuccessiveFailedLogin(0);
		user.setAccountNonLocked(true);
		try {
			daoService.updateUser(user);
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
		super.unsuccessfulAuthentication(request, response, failed);
		User user = new User();
		user.setUsername(obtainUsername(request));
		user.setSuccessiveFailedLogin(1);
		try {
			daoService.updateUser(user);
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