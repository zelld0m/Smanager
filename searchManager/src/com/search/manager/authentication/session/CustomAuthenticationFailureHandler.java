package com.search.manager.authentication.session;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component("authenticationFailureHandler")
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	public CustomAuthenticationFailureHandler() {
		setDefaultFailureUrl("/login.jsp?login_error=1");
	}
}
