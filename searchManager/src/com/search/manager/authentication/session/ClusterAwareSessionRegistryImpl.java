package com.search.manager.authentication.session;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;

public class ClusterAwareSessionRegistryImpl extends SessionRegistryImpl {

	private static final Logger logger = Logger.getLogger(ClusterAwareSessionRegistryImpl.class);
	
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
	public void registerNewSession(String sessionId, Object principal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSessionInformation(String sessionId) {

	}
	
	public void expireNow(String sessionId) {
		// TODO Auto-generated method stub
	}
}