package com.search.manager.authentication.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.Group;
import com.search.manager.model.RecordSet;
import com.search.manager.model.User;

@Service("userDetailsService")
public class UserAuthenticationProvider implements UserDetailsService {

	private static final Logger logger = Logger.getLogger(UserAuthenticationProvider.class);

	private Map<String, UserDetails> userMap = new HashMap<String, UserDetails>();

	@Autowired private DaoService daoService;

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
	
	public UserAuthenticationProvider() {
		super();
	}

	/**
	 * Returns a populated {@link UserDetails} object. 
	 * The username is first retrieved from the database and then mapped to 
	 * a {@link UserDetails} object.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		UserDetailsImpl userDetails = new UserDetailsImpl();
		try {
			User user = daoService.getUser(username);
			if (user != null) {
				userDetails = new UserDetailsImpl(user);
				userDetails.setAuthorities(getAuthorities(user.getGroupId()));
				userMap.put(username, userDetails);
			}
		} catch (DaoException e) {
			logger.error(e.getMessage());
		}catch (Exception e) {
			logger.error(e.getMessage());
		}

		return userDetails;
		
	}
	
	private Collection<GrantedAuthority> getAuthorities(String groupId) {
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		try {
			RecordSet<Group> grpPermission = daoService.getGroupPermission(groupId);
			if (grpPermission.getTotalSize() > 0) {
				List<String> permissions = new ArrayList<String>();
				for (Group group : grpPermission.getList()) {
					permissions.add(group.getPermissionId());
				}
				authorities = AuthorityUtils.createAuthorityList(permissions.toArray(new String[0]));				
			}
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
		}
		return authorities;
	}
	
	public List<String> getUserNames() {
		return new ArrayList<String>(userMap.keySet());
	}

}