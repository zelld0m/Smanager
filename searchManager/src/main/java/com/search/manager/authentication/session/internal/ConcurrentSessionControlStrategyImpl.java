package com.search.manager.authentication.session.internal;

import com.search.manager.authentication.session.SessionRegistryUtils;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("sessionAuthenticationStrategy")
public class ConcurrentSessionControlStrategyImpl extends ConcurrentSessionControlStrategy {

	private SessionRegistry sessionRegistry;

	@Autowired
	public ConcurrentSessionControlStrategyImpl(SessionRegistry sessionRegistry) {
		super(sessionRegistry);
		setMaximumSessions(1);
		this.sessionRegistry = sessionRegistry;
	}
	
	@Override
	public void onAuthentication(Authentication auth, HttpServletRequest request, HttpServletResponse response) {
		Assert.notNull(request, "Authentication request cannot be null (violation of interface contract)");
		
		Object principal = SessionRegistryUtils.getPrincipal(auth);
		String sessionId = SessionRegistryUtils.getSessionId(auth);

		List<SessionInformation> sessions = getSessionRegistry().getAllSessions(principal, false);

		int sessionCount = 0;

		if (CollectionUtils.isNotEmpty(sessions)){
			sessionCount = CollectionUtils.size(sessions);
		}

		int allowableSessions = getMaximumSessionsForThisUser(auth);
		Assert.isTrue(allowableSessions != 0, "getMaximumSessionsForThisUser() must return either -1 to allow unlimited logins, or a positive integer to specify a maximum");

		if (sessionCount < allowableSessions){
			// They haven't got too many login sessions running at present
			return;
		}
		else if (allowableSessions == -1){
			// We permit unlimited logins
			return;
		}
		else if (sessionCount == allowableSessions){
			// Only permit it though if this request is associated with one of the sessions
			for (int i = 0; i < sessionCount; i++){
				if (sessions.get(i).getSessionId().equals(sessionId)){
					return;
				}
			}
		}

		allowableSessionsExceeded(sessionId, sessions, allowableSessions, getSessionRegistry(), auth.getDetails());
	}

	protected void allowableSessionsExceeded(String sessionId, List<SessionInformation> sessions, int allowableSessions, SessionRegistry registry, Object authenticationDetail){
//		boolean exceptionIfMaximumExceeded = true;
		
//		if (authenticationDetail instanceof CustomAuthenticationDetailsSource)
//		{
//			CustomAuthenticationDetailsSource authDetails = (CustomAuthenticationDetailsSource) authenticationDetail;
//			exceptionIfMaximumExceeded = authDetails.isExceptionIfMaximumSessionsExceeded();
//		}
//
//		if (exceptionIfMaximumExceeded || (sessions == null))
//		{
//			throw new ConcurrentLoginException(messages.getMessage("ConcurrentSessionControllerImpl.exceededAllowed",
//					new Object[] { new Integer(allowableSessions) }, "Maximum sessions of {0} for this principal exceeded"));
//		}

		// Determine least recently used session, and mark it for invalidation
//		SessionInformation leastRecentlyUsed = null;

		/* expire the last recently used session
		for (int i = 0; i < sessions.length; i++){
			if ((leastRecentlyUsed == null) || sessions[i].getLastRequest().before(leastRecentlyUsed.getLastRequest())){
			leastRecentlyUsed = sessions[i];
			}
		}
	
		//this class expects the use of ClustereAwareSessionRegistryImpl .
		if (getSessionRegistry() instanceof ClustereAwareSessionRegistryImpl){
			ClusterAwareSessionRegistryImpl sessRegistry = (ClustereAwareSessionRegistryImpl) getSessionRegistry();
			sessRegistry.expireNow(leastRecentlyUsed.getSessionId());
		}
		*/

		//expire all the sessions except this one.
		for (int i = 0; i < sessions.size(); i++)
		{
			if ( !sessions.get(i).getSessionId().equals(sessionId))
			{
				//this class expects the use of ClustereAwareSessionRegistryImpl .
				if (getSessionRegistry() instanceof ClusterAwareSessionRegistryImpl)
				{
					ClusterAwareSessionRegistryImpl sessRegistry = (ClusterAwareSessionRegistryImpl) getSessionRegistry();
					sessRegistry.expireNow(sessions.get(i).getSessionId());
				}
			}
		}
	}

	public SessionRegistry getSessionRegistry() {
		return sessionRegistry;
	}

	public void setSessionRegistry(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
}