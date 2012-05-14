package com.search.manager.authentication.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.search.manager.schema.SolrSchemaUtility;

@Service("userDetailsService")
public class UserAuthenticationProvider implements UserDetailsService {

	private static final Logger logger = Logger.getLogger(UserAuthenticationProvider.class);

	private Map<String, UserDetails> userMap = new HashMap<String, UserDetails>();

	private XMLConfiguration xmlConfig = new XMLConfiguration();
	
	public UserAuthenticationProvider() {
		super();
		initUserAccess();
	}

	private void initUserAccess() {
		try {
			// user config
			xmlConfig.setDelimiterParsingDisabled(true);
			xmlConfig.setExpressionEngine(new XPathExpressionEngine());
			xmlConfig.load("/home/solr/conf/user.xml");
			xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
			reloadUsers();
			xmlConfig.addConfigurationListener(new ConfigurationListener() {
				@Override
				public void configurationChanged(ConfigurationEvent event) {
					if (!event.isBeforeUpdate()) {
						reloadUsers();
					}
				}
			});			
		} catch (Exception e) {
			logger.error("Failed to load users", e);
		}
	}

	private void reloadUsers() {
		synchronized (UserAuthenticationProvider.class) {
			Map<String, UserDetails> tmpMap = new HashMap<String, UserDetails>();
	    	List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) xmlConfig.configurationsAt(("/user"));
	    	for (HierarchicalConfiguration hc: hcList) {
	    		tmpMap.put(hc.getString("userName"), new UserDetailsImpl(
    					getAuthorities(hc.getInt("role")),
    					hc.getString("password"),
    					hc.getString("userName"),
    					hc.getString("fullName"),
    					hc.getBoolean("accountNonExpired"),
    					hc.getBoolean("accountNonLocked"),
    					hc.getBoolean("credentialsNonExpired"),
    					hc.getBoolean("enabled")));
	    	}
	    	userMap = tmpMap;
		}
	}
	
	/**
	 * Returns a populated {@link UserDetails} object. 
	 * The username is first retrieved from the database and then mapped to 
	 * a {@link UserDetails} object.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		logger.info("UserAuthenticationProvider.loadUserByUsername");
		// check if need to reload
		xmlConfig.configurationsAt("/user");
		
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