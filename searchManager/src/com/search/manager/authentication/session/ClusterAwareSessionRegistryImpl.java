package com.search.manager.authentication.session;

import groovy.lang.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Component;

import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.cache.dao.DaoCacheService;

@Singleton
@Component("sessionRegistry")
public class ClusterAwareSessionRegistryImpl extends SessionRegistryImpl {

	private static final Logger logger = Logger.getLogger(ClusterAwareSessionRegistryImpl.class);
	
	@Autowired DaoCacheService daoCacheService;
	
	Map<String, String> userMap = new ConcurrentHashMap<String, String>();
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
		userMap.put(principal.toString(), sessionId);
		userMap.put(sessionId, principal.toString());
		UserDetailsImpl user = new UserDetailsImpl();
		user.setUsername(String.valueOf(principal));
		try {
			daoCacheService.loginUser(user);
		} catch (Exception e) {
			logger.error("Failed to add user to cache: " + user, e);
		}
	}

	@Override
	public synchronized void removeSessionInformation(String sessionId) {
		String user = sessionMap.remove(sessionId);
		String sessId = userMap.get(user);
		if (sessId.equals(sessionId)) {
			userMap.remove(sessId);
			try {
				daoCacheService.logoutUser(user);
			} catch (Exception e) {
				logger.error("Failed to remove user from cache: " + user, e);
			}
		}
	}
	
	public synchronized void expireNow(String sessionId) {
		removeSessionInformation(sessionId);
	}
	
}