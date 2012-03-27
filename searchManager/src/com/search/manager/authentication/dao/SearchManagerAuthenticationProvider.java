package com.search.manager.authentication.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * A custom authentication provider for Spring Security 3 backed by a database.
 * This class adapts the AuthenticationDao class to the Spring interface UserDetailsService.
 */
public class SearchManagerAuthenticationProvider implements UserDetailsService {

	/**
	 * Read a user from the database.
	 */
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {
		// TODO: use DB implementation
		return new UserDetailsImpl(null, "admin", "admin", "", true, true, true, true);
//		return checkIfEmpty(authenticationDao.readUser(userName));
	}
	
	private UserDetailsImpl checkIfEmpty(UserDetailsImpl user){
		if(user==null){
			return new UserDetailsImpl();
		}else{
			return user;
		}
	}
	
}
