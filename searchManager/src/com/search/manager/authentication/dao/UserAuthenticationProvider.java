package com.search.manager.authentication.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserAuthenticationProvider implements UserDetailsService {

	private static final Logger logger = Logger.getLogger(UserAuthenticationProvider.class);

	private Map<String, UserDetails> userMap = new HashMap<String, UserDetails>();

	public UserAuthenticationProvider() {
		super();
		initUserAccess();
	}

	private void initUserAccess() {
		userMap.put("admin", new UserDetailsImpl(getAuthorities(1), "21232f297a57a5a743894a0e4a801fc3", "admin", "Admin User", true, true, true, true));
		userMap.put("user", new UserDetailsImpl(getAuthorities(2), "ee11cbb19052e40b07aac0ca060c23ee", "user", "Regular User", true, true, true, true));
		userMap.put("ChrisS", new UserDetailsImpl(getAuthorities(2), "5f4dcc3b5aa765d61d8327deb882cf99", "ChrisS", "Chris", true, true, true, true));
		userMap.put("DanD", new UserDetailsImpl(getAuthorities(2), "5f4dcc3b5aa765d61d8327deb882cf99", "DanD", "Dan", true, true, true, true));
		userMap.put("MattClark", new UserDetailsImpl(getAuthorities(2), "5f4dcc3b5aa765d61d8327deb882cf99", "MattClark", "MattClark", true, true, true, true));
		userMap.put("BongR", new UserDetailsImpl(getAuthorities(2), "5f4dcc3b5aa765d61d8327deb882cf99", "BongR", "Bong", true, true, true, true));
		userMap.put("MarixT", new UserDetailsImpl(getAuthorities(2), "5f4dcc3b5aa765d61d8327deb882cf99", "MarixT", "Marix Trivino", true, true, true, true));
		userMap.put("QAuser1", new UserDetailsImpl(getAuthorities(2), "5f4dcc3b5aa765d61d8327deb882cf99", "QAuser1", "QA Test User 1", true, true, true, true));
		userMap.put("QAuser2", new UserDetailsImpl(getAuthorities(2), "5f4dcc3b5aa765d61d8327deb882cf99", "QAuser2", "QA Test User 2", true, true, true, true));
	}

	/**
	 * Returns a populated {@link UserDetails} object. 
	 * The username is first retrieved from the database and then mapped to 
	 * a {@link UserDetails} object.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		logger.info("UserAuthenticationProvider.loadUserByUsername");

		if (userMap.containsKey(username)) return userMap.get(username);

		return new UserDetailsImpl();
	}
	
	/**
	 * Retrieves a collection of {@link GrantedAuthority} based on a numerical role
	 * @param role the numerical role
	 * @return a collection of {@link GrantedAuthority
	 */
	public Collection<GrantedAuthority> getAuthorities(Integer role) {
		List<GrantedAuthority> authList = getGrantedAuthorities(getRoles(role));
		return authList;
	}

	/**
	 * Converts a numerical role to an equivalent list of roles
	 * @param role the numerical role
	 * @return list of roles as as a list of {@link String}
	 */
	public List<String> getRoles(Integer role) {
		List<String> roles = new ArrayList<String>();

		if (role.intValue() == 1) {
			roles.add("ROLE_USER");
			roles.add("ROLE_ADMIN");

		} else if (role.intValue() == 2) {
			roles.add("ROLE_USER");
		}

		return roles;
	}

	/**
	 * Wraps {@link String} roles to {@link SimpleGrantedAuthority} objects
	 * @param roles {@link String} of roles
	 * @return list of granted authorities
	 */
	public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		if (CollectionUtils.isNotEmpty(roles))
			authorities = AuthorityUtils.createAuthorityList(roles.toArray(new String[0]));
		
		return authorities;
	}
	
	public List<String> getUserNames() {
		return new ArrayList<String>(userMap.keySet());
	}
}