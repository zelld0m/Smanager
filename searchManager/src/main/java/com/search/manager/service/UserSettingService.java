package com.search.manager.service;

import net.sf.json.JSONObject;

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
import com.search.manager.model.User;
import com.search.manager.schema.MessagesConfig;

@Service(value = "userSettingService")
@RemoteProxy(
		name = "UserSettingServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "userSettingService")
	)
public class UserSettingService {

	private static final Logger logger = Logger.getLogger(UserSettingService.class);
	private static final String RESPONSE_STATUS_OK = "200";
	private static final String RESPONSE_STATUS_FAILED = "0";
	
	@Autowired private DaoService daoService;

	@RemoteMethod
	public User getUser() {
		User user  = null;
		try {
			user = daoService.getUser(UtilityService.getUsername());
			if (user != null) {
				// don't return the password
				user.setPassword(null);
			}
		} catch (DaoException e) {
			logger.error("Error in SecurityService.getUsers "+e);
		}
		return user;
	}
	
	@RemoteMethod
	public JSONObject updateUser(String username, String fullname, String email, String oldpassword, String newpassword) {
		JSONObject json = new JSONObject();
		int result = -1;
		
		try {
			username = StringUtils.trim(username);
			fullname = StringUtils.trim(fullname);
			email = StringUtils.trim(email);
			User user = daoService.getUser(username);
			if(user != null){
				user.setFullName(fullname);
				user.setEmail(email);
				
				if(StringUtils.isNotEmpty(oldpassword) || StringUtils.isNotEmpty(newpassword)){
					if(UtilityService.getPasswordHash(oldpassword).equals(user.getPassword())){
						user.setPassword(UtilityService.getPasswordHash(newpassword));
					}else{
						json.put("status", RESPONSE_STATUS_FAILED);
						json.put("message", MessagesConfig.getInstance().getMessage("password.not.match", username));
						return json;
					}
				}

				result = daoService.updateUser(user);
				
				if(result > -1){
					json.put("status", RESPONSE_STATUS_OK);
					json.put("message", MessagesConfig.getInstance().getMessage("common.updated", username));
					return json;	
				}
			}	
		} catch (Exception e) {
			logger.error("Failed during updateUser()",e);
		}
		
		json.put("status", RESPONSE_STATUS_FAILED);
		json.put("message", MessagesConfig.getInstance().getMessage("common.not.updated", username));
		return json;
	}

}
