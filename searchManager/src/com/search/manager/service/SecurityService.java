package com.search.manager.service;

import java.util.ArrayList;
import java.util.List;

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
import com.search.manager.mail.AccessNotificationMailService;
import com.search.manager.model.NameValue;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RoleModel;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.SecurityModel;
import com.search.manager.model.User;
import com.search.manager.schema.MessagesConfig;
import com.search.manager.utility.DateAndTimeUtils;

@Service(value = "securityService")
@RemoteProxy(
		name = "SecurityServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "securityService")
	)
public class SecurityService {

	private static final Logger logger = Logger.getLogger(SecurityService.class);
	private static final String RESPONSE_STATUS_OK = "200";
	private static final String RESPONSE_STATUS_FAILED = "0";
	
	@Autowired private DaoService daoService;
	@Autowired private AccessNotificationMailService  mailService;

	@RemoteMethod
	public RecordSet<SecurityModel> getUserList(String roleId, String page, String search, String memberSince, String status, String expired) {
		User user = new User();
		user.setGroupId(roleId);
		user.setFullName(StringUtils.isBlank(search)?null:search);
		
		if(StringUtils.isNotEmpty(status))
			user.setAccountNonLocked("YES".equalsIgnoreCase(status)?false:true);
		if(StringUtils.isNotEmpty(expired))
			user.setAccountNonExpired("YES".equalsIgnoreCase(expired)?false:true);
		
		SearchCriteria<User> searchCriteria = new SearchCriteria<User>(user,null,null,Integer.parseInt(page),10);
		searchCriteria.setEndDate(DateAndTimeUtils.getDateWithEndingTime(DateAndTimeUtils.toSQLDate(UtilityService.getStoreName(), memberSince)));
		return getUsers(searchCriteria, MatchType.LIKE_NAME);
	}
	
	@RemoteMethod
	public JSONObject deleteUser(String username){
		JSONObject json = new JSONObject();
		int result = -1;
		try {
			result = daoService.removeUser(username);
			if(result > -1){
				json.put("status", RESPONSE_STATUS_OK);
				json.put("message", MessagesConfig.getInstance().getMessage("common.deleted", username));
				return json;	
			}
		} catch (DaoException e) {
			logger.error("Failed during deleteUser()",e);
		}
		json.put("status", RESPONSE_STATUS_FAILED);
		json.put("message", MessagesConfig.getInstance().getMessage("common.not.deleted", username));
		return json;	
	}
	
	@RemoteMethod
	public JSONObject resetPassword(String roleId, String username, String password){
		
		JSONObject json = new JSONObject();
		int result = -1;
		
		try {
			User user = new User();
			user.setGroupId(roleId);
			user.setUsername(username);
	
			SearchCriteria<User> searchCriteria = new SearchCriteria<User>(user,null,null,null,1);
			RecordSet<SecurityModel> record = getUsers(searchCriteria, MatchType.MATCH_ID);
			
			if(record != null && record.getTotalSize() > 0){
				user.setEmail(record.getList().get(0).getEmail());
				user.setFullName(record.getList().get(0).getFullname());
				if (StringUtils.isNotBlank(password)) 
					user.setPassword(UtilityService.getPasswordHash(password));
				result = daoService.updateUser(user);
			}

			if(result > -1){
				user.setPassword(password);
				mailService.sendResetPassword(user);
				json.put("status", RESPONSE_STATUS_OK);
				json.put("message", MessagesConfig.getInstance().getMessage("password.updated", username));
				return json;	
			}
		} catch (Exception e) {
			logger.error("Failed during resetPassword()",e);
		}
		
		json.put("status", RESPONSE_STATUS_FAILED);
		json.put("message", MessagesConfig.getInstance().getMessage("password.not.updated", username));
		return json;	
	}
	
	@RemoteMethod
	public JSONObject addUser(String roleId, String rolename, String username, String fullname, String password, String expire, String locked, String email){
		JSONObject json = new JSONObject();
		
		int result = -1;
		try {
			//check if username already exist
			User user = daoService.getUser(username);
		
			if(user != null){
				json.put("status", RESPONSE_STATUS_FAILED);
				json.put("message", MessagesConfig.getInstance().getMessage("username.exist"));
				return json;
			}

			user = new User();
			user.setFullName(fullname);
			user.setUsername(username);
			user.setEmail(email);
			user.setGroupId(roleId);
			user.setStoreId(UtilityService.getStoreName());
			
			if(StringUtils.isNotEmpty(locked))
				user.setAccountNonLocked(!"true".equalsIgnoreCase(locked));

			user.setThruDate(DateAndTimeUtils.toSQLDate(UtilityService.getStoreName(), expire));
			user.setPassword(UtilityService.getPasswordHash(password));
			result = daoService.addUser(user);
			
			if(result > -1){
				user.setPassword(password);
				mailService.sendAddUser(user);
				json.put("status", RESPONSE_STATUS_OK);
				json.put("message", MessagesConfig.getInstance().getMessage("common.added", username));
				return json;
			}
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		
		json.put("status", RESPONSE_STATUS_FAILED);
		json.put("message", MessagesConfig.getInstance().getMessage("common.not.added", username));
		
		return json;	
	}
	
	@RemoteMethod
	public RecordSet<RoleModel> getRoleList() {
		List<RoleModel> roleList = new ArrayList<RoleModel>();	
		try {
			List<String> gpList = daoService.getGroups();
			int cnt = 0;
			
			for(String gp : gpList){
				RoleModel role = new RoleModel();
				role.setId(gp);
				role.setRolename(gp);
				if(cnt == 0)
					role.setDefault(true); // make default
				roleList.add(role);
			}	
		} catch (DaoException e) {
			logger.error("Error in SecurityService.getRoleList "+e, e);
		}
		return new RecordSet<RoleModel>(roleList,roleList.size());
	}
	
	@RemoteMethod
	public RoleModel getRole(String id) {
		List<RoleModel> list = new ArrayList<RoleModel>();
		try {
			List<String> gpList = daoService.getGroups();
				for(String gp : gpList){
					RoleModel role = new RoleModel();
					role.setId(gp);
					role.setRolename(gp);	
					if((gp).equalsIgnoreCase(id)){
						role.setDefault(true); // get default
						return role;
					}
					list.add(role);
			}
		} catch (Exception e) {
			logger.error("Error in SecurityService.getRole "+e, e);
		}
		
		if(list.size() > 0)
			return list.get(0);
		
		return new RoleModel();
	}
	
	@RemoteMethod
	public RecordSet<NameValue> getStatList() {
		List<NameValue> statList = new ArrayList<NameValue>();
		try {
			for(int i=0; i<1; i++){
				NameValue stat1 = new NameValue();
				stat1.setName("No");
				stat1.setValue("No");
				
				NameValue stat2 = new NameValue();
				stat2.setName("Yes");
				stat2.setValue("Yes");
				
				statList.add(stat1);
				statList.add(stat2);
			}
		} catch (Exception e) {
			logger.error("Failed during getUserList()",e);
		}
		return new RecordSet<NameValue>(statList,statList.size());
	}
	
	@RemoteMethod
	public RecordSet<NameValue> getExpList() {
		List<NameValue> expList = new ArrayList<NameValue>();
		try {
			NameValue exp1 = new NameValue();
			exp1.setName("No");
			exp1.setValue("No");
			
			NameValue exp2 = new NameValue();
			exp2.setName("Yes");
			exp2.setValue("Yes");
			
			expList.add(exp1);
			expList.add(exp2);

		} catch (Exception e) {
			logger.error("Failed during getUserList()",e);
		}
		return new RecordSet<NameValue>(expList,expList.size());
	}

	private RecordSet<SecurityModel> getUsers(SearchCriteria<User> searchCriteria, MatchType matchTypeUser){
		
		List<SecurityModel> secList = new ArrayList<SecurityModel>();
		
		try {
			RecordSet<User> recSet = daoService.getUsers(searchCriteria, matchTypeUser);
			
			if(recSet != null && recSet.getTotalSize() > 0){
				List<User> users = recSet.getList();
				
				for(User user : users){
					SecurityModel secModel = new SecurityModel();
					secModel.setId(user.getUsername());
					secModel.setUsername(user.getUsername());
					secModel.setType(user.getGroupId());
					secModel.setFullname(user.getFullName());
					secModel.setLastAccess(user.getLastAccessDate() != null?DateAndTimeUtils.getDateStringMMDDYYYY(user.getLastAccessDate()):"");
					secModel.setIp(user.getIp());
					secModel.setDateStarted(user.getCreatedDate() != null?DateAndTimeUtils.getDateStringMMDDYYYY(user.getCreatedDate()):"");
					secModel.setRoleId(user.getGroupId());
					secModel.setStatus(user.isAccountNonLocked()?"no":"yes");
					secModel.setExpired(user.isAccountNonExpired()?"no":"yes"); // compute expiration
					secModel.setEmail(user.getEmail());
					secModel.setLocked(user.isAccountNonLocked());
					secModel.setThruDate(user.getThruDate() != null?DateAndTimeUtils.getDateStringMMDDYYYY(user.getThruDate()):"");
					secList.add(secModel);
				}	
				return new RecordSet<SecurityModel>(secList,recSet.getTotalSize());
			}
		} catch (DaoException e) {
			logger.error("Error in SecurityService.getUsers "+e, e);
		}
		return new RecordSet<SecurityModel>(secList,secList.size());
	}
	
	@RemoteMethod
	public JSONObject updateUser(String roleId, String username, String expire, String locked, String email) {
		JSONObject json = new JSONObject();
		int result = -1;
		
		try {
			User user = new User();
			user.setGroupId(roleId);
			user.setUsername(username);
	
			SearchCriteria<User> searchCriteria = new SearchCriteria<User>(user,null,null,null,1);
			RecordSet<SecurityModel> record = getUsers(searchCriteria, MatchType.MATCH_ID);
			
			if(record != null && record.getTotalSize() > 0){
				user.setThruDate(DateAndTimeUtils.toSQLDate(UtilityService.getStoreName(), expire));
				if(StringUtils.isNotEmpty(locked))
					user.setAccountNonLocked(!"true".equalsIgnoreCase(locked));
				user.setEmail(email);
				result = daoService.updateUser(user);
			}

			if(result > -1){
				json.put("status", RESPONSE_STATUS_OK);
				json.put("message", MessagesConfig.getInstance().getMessage("common.updated", username));
				return json;	
			}
		} catch (Exception e) {
			logger.error("Failed during updateUser()",e);
		}
		
		json.put("status", RESPONSE_STATUS_FAILED);
		json.put("message", MessagesConfig.getInstance().getMessage("common.not.updated", username));
		return json;
	}
	

}
