package com.search.manager.authentication.session;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.Assert;

public class SessionRegistryUtils {

	public static Object getPrincipal(Authentication auth){
		Assert.notNull(auth, "Authentication required");
		Assert.notNull(auth.getPrincipal(), "Authentication.getPrincipal() required");

		if (auth.getPrincipal() instanceof UserDetails) {
			return ((UserDetails) auth.getPrincipal()).getUsername();
		} else {
			return auth.getPrincipal();
		}
	}

	public static String getSessionId(Authentication auth){
		Assert.notNull(auth, "Authentication required");
		Assert.notNull(auth.getDetails(), "Authentication.getDetails() required");
		Assert.isInstanceOf(WebAuthenticationDetails.class, auth.getDetails());

		String sessionId = ((WebAuthenticationDetails) auth.getDetails()).getSessionId();
		Assert.hasText(sessionId, "WebAuthenticationDetails did not return a Session ID (" + auth.getDetails() + ")");
		return sessionId;
	}
}
