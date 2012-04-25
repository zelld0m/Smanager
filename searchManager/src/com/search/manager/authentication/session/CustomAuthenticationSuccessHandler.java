package com.search.manager.authentication.session;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component("authenticationSuccessHandler")
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	public CustomAuthenticationSuccessHandler() {
		setDefaultTargetUrl("/");
	}
}
