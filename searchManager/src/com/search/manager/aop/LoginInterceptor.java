package com.search.manager.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Aspect
@Component("loginInterceptor")
public class LoginInterceptor {
	private static final Logger authLog = Logger.getLogger(LoginInterceptor.class);

	private void logAuthPass (String userName)  
	{  
		if (authLog.isInfoEnabled()) {  
			authLog.info("auth=pass;user=" + userName);  
		}  
		
	}  

	private void logAuthFail (String userName)  
	{  
		if (authLog.isInfoEnabled()) {  
			authLog.info("auth=fail;user=" + userName);  
		}  
	}  

	/** 
	 * Wraps a call to Spring Security's  
	 * AuthenticationProvider.authenticate(). 
	 */  
	public Object logAuth (ProceedingJoinPoint call) throws Throwable  
	{  
		Authentication result;  
		String user = "UNKNOWN";  

		try { /*[3a]*/  
			Authentication auth = (Authentication) call.getArgs()[0];  
			user = auth.getName();  
		}  
		catch (Exception e) {  
			// ignore  
		}  

		try { /*[3b]*/  
			result = (Authentication) call.proceed();  
		}  
		catch (Exception e) {  
			logAuthFail(user);  
			throw e;  
		}  

		if (result != null) { /*[3c]*/  
			logAuthPass(user); 
		}  

		return result;  
	}  
}