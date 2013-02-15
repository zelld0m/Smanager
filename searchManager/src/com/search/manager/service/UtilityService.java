package com.search.manager.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.RuleEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.utility.PropsUtils;
import com.search.ws.ConfigManager;
import com.search.ws.SolrConstants;

@Service(value = "utilityService")
@RemoteProxy(
		name = "UtilityServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "utilityService")
)
public class UtilityService {

	private static final Logger logger = Logger.getLogger(UtilityService.class);

    private final static Map<RuleEntity, AtomicReference<String>> lockService;
    static {
    	lockService = new HashMap<RuleEntity, AtomicReference<String>>();
    	for (RuleEntity ruleEntity: RuleEntity.values()) {
        	lockService.put(ruleEntity, new AtomicReference<String>());
    	}
    }

    public static boolean obtainPublishLock(RuleEntity ruleType) throws PublishLockException {
    	String username = getUsername();
    	String storeName = getStoreName();
    	if (ruleType != null && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(username)) {
        	if (!lockService.get(ruleType).compareAndSet(null, storeName + "^" + username)) {
        		String info = getPublishLockInfo(ruleType);
        		username = null;
        		String storeLabel = null;
        		if (StringUtils.isNotBlank(info)) {
        			String[] infoArray = info.split("\\^", 2);
        			if (infoArray.length > 0) {
        				// TODO: get store label
        				storeLabel = infoArray[0];
            			if (infoArray.length > 1) {
            				username = infoArray[1];
            			}
        			}
            		throw new PublishLockException(String.format("%s is currently publishing %s rules for %s, please try again in a while.", 
            				username, ruleType.toString(), storeLabel), username, storeLabel);
        		}
        	}
    	}
    	return false;
    }

    public static String getPublishLockInfo(RuleEntity ruleType) {
    	if (ruleType != null) {
        	return lockService.get(ruleType).get();
    	}
    	return "";
    }

    public static boolean releasePublishLock(RuleEntity ruleType) {
    	String username = getUsername();
    	String storeName = getStoreName();
    	if (ruleType != null && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(username)) {
        	return lockService.get(ruleType).compareAndSet(storeName + "^" + username, null);
    	}
    	return false;
    }
    
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
	public static String getStoreLogo(){
		return new StringBuilder("/images/logo").append(getStoreLabel()).append(".png").toString();
	}

	@RemoteMethod
	public static String getSolrConfig(){
		JSONObject json = new JSONObject();
		String url = ConfigManager.getInstance().getServerParameter(getServerName(), "url");
		Pattern pattern = Pattern.compile("http://(.*)\\(store\\)/");
		Matcher m = pattern.matcher(url);
		if (m.matches()) {
			json.put("solrUrl", PropsUtils.getValue("browsejssolrurl") + m.group(1));
		}
		json.put("isFmGui", PropsUtils.getValue("isFmSolrGui").equals("1")?true:false);
		return json.toString();
	}

	@RemoteMethod
	public static Map<String,String> getServerListForSelectedStore(boolean includeSelectedStore){
		Map<String,String> map = ConfigManager.getInstance().getServersByCore(getStoreName());
		if (!includeSelectedStore) {
			map.remove(getServerName());			
		}
		return map;
	}



	public static boolean hasPermission(String permission) {
		boolean flag = false;
		for (GrantedAuthority auth : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
			if (permission.equals(auth.getAuthority())) {
				flag = true;
				break;
			}
		}
		return flag;
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

	public static String getPasswordHash(String password) {
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

	@RemoteMethod
	public static String getStoreFacetTemplate(){

		ConfigManager cm = ConfigManager.getInstance();
		String storeFacetTemplate = StringUtils.EMPTY;
		if (cm != null) {
			storeFacetTemplate = cm.getParameterByCore(getStoreName(), "facet-template");
		}

		return storeFacetTemplate;
	}
	
	@RemoteMethod
	public static String getStoreFacetTemplateName(){

		ConfigManager cm = ConfigManager.getInstance();
		String storeFacetTemplateName = StringUtils.EMPTY;
		if (cm != null) {
			storeFacetTemplateName = cm.getParameterByCore(getStoreName(), SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME);
		}
		
		return storeFacetTemplateName;
	}
	
	@RemoteMethod
	public static String getStoreFacetName(){

		ConfigManager cm = ConfigManager.getInstance();
		String storeFacetTemplate = StringUtils.EMPTY;
		if (cm != null) {
			storeFacetTemplate = cm.getParameterByCore(getStoreName(), "facet-name");
		}

		return storeFacetTemplate;
	}

	
	public static String getStoreSetting(String property) {
		return ConfigManager.getInstance().getStoreSetting(getStoreName(), property);
	}
	
	public static boolean setStoreSetting(String property, String value) {
		return ConfigManager.getInstance().setStoreSetting(getStoreName(), property, value);
	}

	public static String getStoreSetting(String storeName, String property) {
		return ConfigManager.getInstance().getStoreSetting(storeName, property);
	}
	
	public static List<String> getStoreSettings(String storeName, String property) {
		return ConfigManager.getInstance().getStoreSettings(storeName, property);
	}
	
	public static List<String> getStoresToExport(String storeName) {
		List<String> list = UtilityService.getStoreSettings(storeName, DAOConstants.SETTINGS_EXPORT_TARGET);
		return list;
	}
	
	
}