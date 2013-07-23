package com.search.manager.authentication.session;

import groovy.lang.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Component;

import com.search.manager.authentication.dao.UserDetailsImpl;

@Singleton
@Component("sessionRegistry")
public class ClusterAwareSessionRegistryImpl extends SessionRegistryImpl {

	private static final Logger logger = Logger.getLogger(ClusterAwareSessionRegistryImpl.class);
		
	Map<String, List<String>> userMap = new ConcurrentHashMap<String, List<String>>();
	Map<String, String> sessionMap = new ConcurrentHashMap<String, String>();
	
	@Override
	public List<Object> getAllPrincipals() {
		List<Object> principals = new ArrayList<Object>();
		return principals;
	}

	@Override
	public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SessionInformation getSessionInformation(String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshLastRequest(String sessionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void registerNewSession(String sessionId, Object principal) {
		List<String> sessions = userMap.get(principal);
		if (sessions == null) {
			sessions = new ArrayList<String>();
		}
		sessions.add(sessionId);
		userMap.put(principal.toString(), sessions);
		
		sessionMap.put(sessionId, principal.toString());
		UserDetailsImpl user = new UserDetailsImpl();
		user.setUsername(String.valueOf(principal));
	}

	@Override
	public synchronized void removeSessionInformation(String sessionId) {
		String principal = sessionMap.remove(sessionId);
		if (principal != null) {
			List<String> sessions = userMap.get(principal);
			sessions.remove(sessionId);			
		}
	}
	
	public synchronized void expireNow(String sessionId) {
		removeSessionInformation(sessionId);
	}
	
}