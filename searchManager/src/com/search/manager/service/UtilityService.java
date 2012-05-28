package com.search.manager.service;

import java.util.Date;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.utility.PropsUtils;
import com.search.ws.ConfigManager;

@Service(value = "utilityService")
@RemoteProxy(
		name = "UtilityServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "utilityService")
)
public class UtilityService {
	
	private static final Logger logger = Logger.getLogger(UtilityService.class);
	
	@RemoteMethod
	public static String getUsername(){
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal==null || !(principal instanceof UserDetailsImpl)) return "";
		return ((UserDetailsImpl) principal).getUsername();
	}

	@RemoteMethod
	public static String getServerName(){
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String serverName = (String)attr.getAttribute("serverName", RequestAttributes.SCOPE_SESSION);
		if (StringUtils.isEmpty(serverName)) {
			// get default server for store
			ConfigManager cm = ConfigManager.getInstance();
			if (cm != null) {
				serverName = cm.getParameterByCore(getStoreName(), "server-url");
			}
			attr.setAttribute("serverName", serverName, RequestAttributes.SCOPE_SESSION);
		}
		return serverName;
	}

	@RemoteMethod
	public static void setServerName(String serverName) {
		logger.info("****************************8setServerName:" + serverName);
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.setAttribute("serverName", serverName, RequestAttributes.SCOPE_SESSION);
	}
	
	@RemoteMethod
	public static String getStoreName(){
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String storeName = (String)attr.getAttribute("storeName", RequestAttributes.SCOPE_SESSION);
		return storeName;
	}

	@RemoteMethod
	public static void setStoreName(String storeName) {
		logger.info("****************************8setStorerName:" + storeName);
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.setAttribute("storeName", storeName, RequestAttributes.SCOPE_SESSION);
	}
	
	@RemoteMethod
	public static String getStoreLabel(){
		String storeLabel = null;
		ConfigManager cm = ConfigManager.getInstance();
		if (cm != null) {
			storeLabel = cm.getStoreName(getStoreName());
		}
		return storeLabel;
	}
	
	@RemoteMethod
	public static String getSolrConfig(){
		JSONObject json = new JSONObject();
		// TODO replace browsejssolrurl with selected server
		json.put("solrUrl", PropsUtils.getValue("browsejssolrurl"));
		json.put("isFmGui", PropsUtils.getValue("isFmSolrGui").equals("1")?true:false);
		return json.toString();
	}

	@RemoteMethod
	public Map<String,String> getServerListForSelectedStore(boolean includeSelectedStore){
		Map<String,String> map = ConfigManager.getInstance().getServersByCore(getStoreName());
		if (!includeSelectedStore) {
			map.remove(getServerName());			
		}
		return map;
	}
	
	public static String formatComment(String comment) {
		if (StringUtils.isNotBlank(comment)) {
			StringBuilder commentBuilder = new StringBuilder();
			commentBuilder.append(new Date().getTime())
			.append("|")
			.append(UtilityService.getUsername())
			.append("|")
			.append(comment.length())
			.append("|")
			.append(comment)
			.append("|");
			return commentBuilder.toString();
		}
		return null;
	}
}
