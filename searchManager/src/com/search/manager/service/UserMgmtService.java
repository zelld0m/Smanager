package com.search.manager.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.Group;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.User;

@Service(value = "usermgmtService")
@RemoteProxy(
		name = "UserMgmtServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "usermgmtService")
	)
public class UserMgmtService {

	private static final Logger logger = Logger.getLogger(UserMgmtService.class);
	
	@Autowired private DaoService daoService;

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	@RemoteMethod
	public RecordSet<User> getUsers(String groupId, String username, Date memberSince, String expired, String locked, int page,int itemsPerPage) {
		RecordSet<User> rSet = null;
		try {
			User user = new User();
			user.setGroupId(StringUtils.isBlank(groupId)?null:groupId);
			user.setUsernameLike(StringUtils.isBlank(username)?null:username);
			if ("Y".equalsIgnoreCase(expired)) {
				user.setAccountNonExpired(false);
			} else if ("N".equalsIgnoreCase(expired)) {
				user.setAccountNonExpired(true);
			}
			if ("Y".equalsIgnoreCase(locked)) {
				user.setAccountNonLocked(false);
			} else if ("N".equalsIgnoreCase(locked)) {
				user.setAccountNonLocked(true);
			}
			SearchCriteria<User> searchCriteria =new SearchCriteria<User>(user,null,null,page,itemsPerPage);
			searchCriteria.setStartDate(memberSince);
			searchCriteria.setEndDate(memberSince);
			rSet = daoService.getUsers(searchCriteria);
		} catch (DaoException e) {
			logger.error("Failed during getUsers()",e);
		}
		return rSet;	
	}

	@RemoteMethod
	public User getUser(String username) {
		User result = null;
		
		try {
			result = daoService.getUser(username);
		} catch (DaoException e) {
			logger.error("Failed during getUser()",e);
		}
		return result;
	}

	@RemoteMethod
	public int addUser(User user) {
		int result = -1;
		String password = StringUtils.isBlank(user.getPassword())?generatePassword():user.getPassword();
		try {
			user.setPassword(getPasswordHash(password));
			result = daoService.addUser(user);
			if (result == 1) {
				//send password
			}
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		return result;
	}

	@RemoteMethod
	public int updateUser(User user) {
		int result = -1;
		try {
			if (StringUtils.isNotBlank(user.getPassword())) {
				user.setPassword(getPasswordHash(user.getPassword()));
			}
			result = daoService.updateUser(user);
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		return result;
	}

	@RemoteMethod
	public int removeUser(String username) {
		int result = -1;
		try {
			result = daoService.removeUser(username);
		} catch (DaoException e) {
			logger.error("Failed during removeUser()",e);
		}
		return result;
	}
	
	@RemoteMethod
	public int resetPassword(String username) {
		int result = -1;
		try {
			String password = generatePassword();
			User user = new User();
			user.setUsername(username);
			user.setPassword(getPasswordHash(password));
			result = daoService.updateUser(user);
			if (result == 1) {
				//send email
			}
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		return result;
	}

	@RemoteMethod
	public List<String> getAllPermissions() throws DaoException {
		return daoService.getAllPermissions();
	}
	
	@RemoteMethod
	public List<String> getGroups() throws DaoException {
		return daoService.getGroups();
	}
	
	@RemoteMethod
	public RecordSet<Group> getGroupPermission(String groupId) throws DaoException {
		return daoService.getGroupPermission(groupId);
	}
	
	private String getPasswordHash(String password) {
		MessageDigest messageDigest = null;
		String hashedPass = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(password.getBytes(),0, password.length());  
			hashedPass = new BigInteger(1,messageDigest.digest()).toString(16);  
			if (hashedPass.length() < 32) {
			   hashedPass = "0" + hashedPass; 
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error("Error in getPasswordHash. " + e.getMessage());
		}  
		return hashedPass;
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphabetic(8);
	}
}
