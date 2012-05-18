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
import com.search.manager.dao.DaoService;
import com.search.manager.model.NameValue;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RoleModel;
import com.search.manager.model.SecurityModel;


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

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	@RemoteMethod
	public RecordSet<SecurityModel> getUserList(String roleId, String page, String search, String member, String status, String expired) {
		List<SecurityModel> userList = new ArrayList<SecurityModel>();
		try {
			
			// dummy
			int start = 0;
			int end = 10;
			
			switch (Integer.valueOf(page)){
			case 1:
				start = 0;
				end = 10;
				break;
			case 2:
				start = 11;
				end = 20;
				break;
			case 3:
				start = 21;
				end = 30;
				break;
			case 4:
				start = 31;
				end = 40;
				break;
			case 5:
				start = 41;
				end = 50;
				break;
			case 6:
				start = 51;
				end = 60;
				break;
			case 7:
				start = 61;
				end = 70;
				break;
			case 8:
				start = 71;
				end = 80;
				break;
			case 9:
				start = 81;
				end = 90;
				break;
			case 10:
				start = 91;
				end = 100;
				break;			
			default:
				break;
			}

			if(roleId.equals("Role0")){	
				for(int i=start; i<end; i++){
					SecurityModel user = new SecurityModel();
					user.setId("User"+i);
					user.setUsername("lorem lorem Role0"+i);
					user.setStatus("Status "+i);
					user.setAction("Action"+i);
					user.setDateStarted("Date"+i);
					user.setType("Administrator");
					user.setRoleId("Role0");
					user.setExpired("No");
					user.setLastAccess("1");
					user.setFullname("Fullname User"+i);
					user.setIp("IP User"+i);
					
					if(show(user, search, member, status, expired))
						userList.add(user);
				}
			}
			
			if(roleId.equals("Role1")){	
				for(int i=start; i<end; i++){
					SecurityModel user = new SecurityModel();
					user.setId("User"+i);
					user.setUsername("lorem lorem Role1"+i);
					user.setStatus("Status "+i);
					user.setAction("Action"+i);
					user.setDateStarted("Date"+i);
					user.setType("Administrator");
					user.setRoleId("Role1");
					user.setExpired("Yes");
					user.setLastAccess("2");
					user.setFullname("Fullname User"+i);
					user.setIp("IP User"+i);
					
					if(show(user, search, member, status, expired))
						userList.add(user);
				}
			}
			
			if(roleId.equals("Role2")){	
				for(int i=start; i<end; i++){
					SecurityModel user = new SecurityModel();
					user.setId("User"+i);
					user.setUsername("lorem lorem Role2"+i);
					user.setStatus("Status "+i);
					user.setAction("Action"+i);
					user.setDateStarted("Date"+i);
					user.setType("Administrator");
					user.setRoleId("Role2");
					user.setExpired("No");
					user.setLastAccess("3");
					user.setFullname("Fullname User"+i);
					user.setIp("IP User"+i);
					
					if(show(user, search, member, status, expired))
						userList.add(user);
				}
			}
			
			if(roleId.equals("Role3")){	
				for(int i=start; i<end; i++){
					SecurityModel user = new SecurityModel();
					user.setId("User"+i);
					user.setUsername("lorem lorem Role3"+i);
					user.setStatus("Status "+i);
					user.setAction("Action"+i);
					user.setDateStarted("Date"+i);
					user.setType("Administrator");
					user.setRoleId("Role3");
					user.setExpired("No");
					user.setLastAccess("4");
					user.setFullname("Fullname User"+i);
					user.setIp("IP User"+i);
					
					if(show(user, search, member, status, expired))
						userList.add(user);
				}
			}
			
			if(roleId.equals("Role4")){	
				for(int i=start; i<end; i++){
					SecurityModel user = new SecurityModel();
					user.setId("User"+i);
					user.setUsername("lorem lorem Role4"+i);
					user.setStatus("Status "+i);
					user.setAction("Action"+i);
					user.setDateStarted("Date"+i);
					user.setType("Administrator");
					user.setRoleId("Role4");
					user.setExpired("Yes");
					user.setLastAccess("5");
					user.setFullname("Fullname User"+i);
					user.setIp("IP User"+i);
					
					if(show(user, search, member, status, expired))
						userList.add(user);
				}
			}
		} catch (Exception e) {
			logger.error("Failed during getUserList()",e);
		}
		return new RecordSet<SecurityModel>(userList,100);
	}
	
	@RemoteMethod
	public JSONObject deleteUser(String type, String userId, String username){
		JSONObject json = new JSONObject();
		json.put("status", RESPONSE_STATUS_OK);
		json.put("message", username+" was deleted successfully");
		return json;	
	}
	
	@RemoteMethod
	public JSONObject resetPassword(String roleId, String userId, String username, String lock, String expired, String password){
		JSONObject json = new JSONObject();
		json.put("status", RESPONSE_STATUS_OK);
		json.put("message", username+" password was changed successfully");
		return json;	
	}
	
	@RemoteMethod
	public JSONObject addUser(String roleId, String rolename, String username, String fullname, String lastAccess, String ip, String password, String locked, String expire){
		JSONObject json = new JSONObject();
		json.put("status", RESPONSE_STATUS_OK);
		json.put("message", username+" was added successfully.");
		return json;	
	}

	/*
	 * todo
	 * 
	 * add updateUser method
	 * 
	 */
	
	@RemoteMethod
	public RecordSet<RoleModel> getRoleList() {
		List<RoleModel> roleList = new ArrayList<RoleModel>();
		try {
			for(int i=0; i<5; i++){
				RoleModel role = new RoleModel();
				role.setId("Role"+i);
				role.setRolename("Role"+i);	
				if(i == 0)
					role.setDefault(true); // make default
				roleList.add(role);
			}
		} catch (Exception e) {
			logger.error("Failed during getUserList()",e);
		}
		return new RecordSet<RoleModel>(roleList,roleList.size());
	}
	
	@RemoteMethod
	public RoleModel getRole(String id) {

		List<RoleModel> list = new ArrayList<RoleModel>();
		
		try {
				for(int i=0; i<5; i++){
					RoleModel role = new RoleModel();
					role.setId("Role"+i);
					role.setRolename("Role"+i);	
					if(("Role"+i).equalsIgnoreCase(id)){
						role.setDefault(true); // get default
						return role;
					}
					list.add(role);
			}
		} catch (Exception e) {
			logger.error("Failed during getUserList()",e);
		}
		
		if(list.size() > 0)
			return list.get(0);
		
		return new RoleModel();
	}
	
	@RemoteMethod
	public RecordSet<NameValue> getStatList() {
		List<NameValue> statList = new ArrayList<NameValue>();
		try {
			for(int i=0; i<5; i++){
				NameValue stat = new NameValue();
				stat.setName("status "+i);
				stat.setValue("status "+i);
				statList.add(stat);
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

	private boolean show(SecurityModel user, String search, String member, String status, String expired){
		
		if(StringUtils.isNotEmpty(search) || StringUtils.isNotEmpty(member) || StringUtils.isNotEmpty(status) || StringUtils.isNotEmpty(expired)){
			if(user.getUsername().equalsIgnoreCase(search) || user.getDateStarted().equalsIgnoreCase(member) || user.getStatus().equalsIgnoreCase(status) || user.getExpired().equalsIgnoreCase(expired))
				return true;
		}else
			return true;
		return false;	
	}
}
