package com.search.manager.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.search.manager.authentication.dao.internal.UserDetailsImpl;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.RuleEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.model.Product;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.schema.SolrSchemaUtility;
import com.search.manager.schema.model.Schema;
import com.search.manager.utility.PropertiesUtils;
import com.search.ws.ConfigManager;

@Service(value = "utilityService")
@RemoteProxy(
		name = "UtilityServiceJS",
		creator = SpringCreator.class,
		creatorParams =
		@Param(name = "beanName", value = "utilityService"))
public class UtilityService {

	private static final Logger logger = LoggerFactory.getLogger(UtilityService.class);

	@Autowired
	private ConfigManager configManager;

	private final static Map<RuleEntity, AtomicReference<String>> lockService;

	static {
		lockService = new HashMap<RuleEntity, AtomicReference<String>>();
		for (RuleEntity ruleEntity : RuleEntity.values()) {
			lockService.put(ruleEntity, new AtomicReference<String>());
		}
	}

	public boolean obtainPublishLock(RuleEntity ruleType, String username, String storeName) throws PublishLockException {
		if (ruleType != null && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(storeName)) {
			String lock = storeName + "^" + username;
			lock = lock.intern();
			String lockOwnerName = null;
			String lockOwnerStore = null;
			if (!lockService.get(ruleType).compareAndSet(null, lock)) {
				String info = getPublishLockInfo(ruleType);
				if (StringUtils.isNotBlank(info)) {
					String[] infoArray = info.split("\\^", 2);
					if (infoArray.length > 0) {
						// TODO: get store label
						lockOwnerStore = infoArray[0];
						if (infoArray.length > 1) {
							lockOwnerName = infoArray[1];
						}
					}
				}
				logger.info(String.format("Unable to obtain %s publish lock for %s of %s. $s of %s is still holding the lock.",
						String.valueOf(ruleType), username, storeName, lockOwnerName, lockOwnerStore));
				throw new PublishLockException(String.format("%s is currently publishing %s rules for %s, please try again in a while.",
						lockOwnerName, String.valueOf(ruleType), lockOwnerStore), lockOwnerName, lockOwnerStore);
			}
			logger.info(String.format("Obtained %s publish lock for %s of %s", String.valueOf(ruleType), username, storeName));
			return true;
		}
		logger.error(String.format("Missing Info for lock. RuleType = %s, User = %s, Store = %s", String.valueOf(ruleType), username, storeName));
		throw new PublishLockException("Please re-login and try to publish again.", null, null);
	}

	public static String getPublishLockInfo(RuleEntity ruleType) {
		if (ruleType != null) {
			return lockService.get(ruleType).get();
		}
		return "";
	}

	public boolean releasePublishLock(RuleEntity ruleType, String username, String storeName) {
		if (ruleType != null && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(storeName)) {
			String lock = storeName + "^" + username;
			lock = lock.intern();
			boolean released = lockService.get(ruleType).compareAndSet(lock, null);
			logger.info(String.format("%s %s lock held by %s of %s.", released ? "Released " : "Failed to release ",
					String.valueOf(ruleType), username, storeName));
			return released;
		}
		logger.error(String.format("Missing Info for lock. RuleType = %s, User = %s, Store = %s", String.valueOf(ruleType), username, storeName));
		return false;
	}

	//TODO: Think of a better implementation
	@RemoteMethod
	public String getUsername() {
		if(SecurityContextHolder.getContext().getAuthentication() != null) {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal == null || !(principal instanceof UserDetailsImpl)) {
				return "";
			}
			return ((UserDetailsImpl) principal).getUsername();
		} else {
			logger.error("failed in UtilityService.getUsername(), using 'System' instead.");
			return "System";
		}
	}

	@RemoteMethod
	public String getServerName() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String serverName = (String) attr.getAttribute("serverName", RequestAttributes.SCOPE_SESSION);
		if (StringUtils.isEmpty(serverName)) {
			// get default server for store
			if (configManager != null) {
				serverName = configManager.getServerName(getStoreId());
			}
			attr.setAttribute("serverName", serverName, RequestAttributes.SCOPE_SESSION);
		}
		return serverName;
	}

	@RemoteMethod
	public void setServerName(String serverName) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.setAttribute("serverName", serverName, RequestAttributes.SCOPE_SESSION);
	}

	@RemoteMethod
	public String getStoreId() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String storeId = (String) attr.getAttribute("storeId", RequestAttributes.SCOPE_SESSION);
		return configManager.getStoreIdByAliases(storeId);
	}

	@RemoteMethod
	public String getTimeZoneId() {
		return DateTimeZone.getDefault().getID();
	}

	@RemoteMethod
	public String getStoreCore(String storeId) {
		if (StringUtils.isNotBlank(storeId)) {
			return configManager.getStoreParameter(storeId, "core");
		}

		return configManager.getStoreParameter(getStoreId(), "core");
	}

	@RemoteMethod
	public String getStoreCore() {
		return getStoreCore(getStoreId());
	}

	@RemoteMethod
	public String getStoreName() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String storeName = (String) attr.getAttribute("storeName", RequestAttributes.SCOPE_SESSION);
		return storeName;
	}

	@RemoteMethod
	public void setStoreId(String storeId) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.setAttribute("storeId", storeId, RequestAttributes.SCOPE_SESSION);
	}

	@RemoteMethod
	public void setStoreName(String storeName) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.setAttribute("storeName", storeName, RequestAttributes.SCOPE_SESSION);
	}

	@RemoteMethod
	public String getSolrConfig() {
		JSONObject json = new JSONObject();
		String url = configManager.getServerParameter(getServerName(), "url");
		Pattern pattern = Pattern.compile("http://(.*)\\(core\\)/");
		Matcher m = pattern.matcher(url);
		if (m.matches()) {
			json.put("solrUrl", PropertiesUtils.getValue("browsejssolrurl") + m.group(1));
		}

		json.put("isFmGui", PropertiesUtils.getValue("isFmSolrGui").equals("1") ? true : false);
		return json.toString();
	}

	@SuppressWarnings("unchecked")
	@RemoteMethod
	public String getIndexedSchemaFields() {
		JSONObject json = new JSONObject();

		Schema schema = SolrSchemaUtility.getDefaultSchema(getSolrServerUrl(getServerName()), getStoreCore());
		if (schema != null) {
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			json.put("indexedFields", (List<String>) attr.getAttribute("indexedFields", RequestAttributes.SCOPE_SESSION));
			json.put("indexedWildcardFields", (List<String>) attr.getAttribute("indexedWildcardFields", RequestAttributes.SCOPE_SESSION));
		}

		return json.toString();
	}

	@RemoteMethod
	public String getStoreParameters() {
		String storeId = getStoreId();
		JSONObject json = new JSONObject();
		json.put("username", getUsername());
		json.put("solrSelectorParam", getSolrSelectorParam());
		json.put("storeId", storeId);
		json.put("storeCore", getStoreCore());
		json.put("storeName", getStoreName());
		json.put("storeDomains", getStoreDomains(storeId));
		json.put("storeFacetName", getStoreFacetName());
		json.put("storeSort", configManager.getStoreParameter(storeId, "sort"));
		json.put("storeFacetTemplate", getStoreFacetTemplate());
		json.put("storeFacetTemplateName", getStoreFacetTemplateName());
		json.put("storeGroupMembership", getStoreGroupMembership());
		json.put("storeDateFormat", getStoreDateFormat());
		json.put("storeDateTimeFormat", getStoreDateTimeFormat());
		json.put("storeDefaultBannerSize", getStoreDefaultBannerSize(storeId));
		json.put("storeAllowedBannerSizes", getStoreAllowedBannerSizes(storeId));
		json.put("storeDefaultBannerLinkPathProtocol", getStoreDefaultBannerLinkPathProtocol(storeId));
		json.put("storeRedirectSelfDomain", configManager.getPropertyList("settings", storeId, "redirect_self_domain"));
		json.put("storeRedirectRelativePath", configManager.getPropertyList("settings", storeId, "redirect_relative_path"));
		json.put("storeFacetTemplateType", getStoreFacetTemplateType(storeId));
		json.put("searchWithinEnabled", configManager.getProperty("searchWithin", storeId, "searchwithin.enable"));
		json.put("searchWithinTypes", configManager.getProperty("searchWithin", storeId, "searchwithin.type"));
		json.put("searchWithinParamName", configManager.getProperty("searchWithin", storeId, "searchwithin.paramname"));

		return json.toString();
	}

	@RemoteMethod
	public Map<String, String> getServerListForSelectedStore(boolean includeSelectedStore) {
		Map<String, String> map = configManager.getServersByStoreId(getStoreId());
		if (!includeSelectedStore) {
			map.remove(getServerName());
		}
		return map;
	}

	public boolean hasPermission(String permission) {
		boolean flag = false;
		for (GrantedAuthority auth : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
			if (permission.equals(auth.getAuthority())) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public String formatComment(String comment) {
		if (StringUtils.isNotBlank(comment)) {
			StringBuilder commentBuilder = new StringBuilder();
			commentBuilder.append(new Date().getTime())
			.append("|")
			.append(getUsername())
			.append("|")
			.append(comment.length())
			.append("|")
			.append(comment)
			.append("|");
			return commentBuilder.toString();
		}
		return null;
	}

	public String getPasswordHash(String password) {
		MessageDigest messageDigest = null;
		String hashedPass = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(password.getBytes(), 0, password.length());
			hashedPass = new BigInteger(1, messageDigest.digest()).toString(16);
			if (hashedPass.length() < 32) {
				hashedPass = "0" + hashedPass;
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error("Error in getPasswordHash. " + e.getMessage());
		}
		return hashedPass;
	}

	@RemoteMethod
	public List<String> getStoreGroupMembership() {
		return configManager.getStoreGroupMembership(getStoreId());
	}

	@RemoteMethod
	public String getStoreFacetPrefix() {
		return configManager.getStoreFacetPrefix(getStoreId());
	}

	@RemoteMethod
	public String getStoreFacetTemplate() {
		return configManager.getStoreFacetTemplate(getStoreId());
	}

	@RemoteMethod
	public String getStoreFacetTemplateName() {
		return configManager.getStoreFacetTemplateName(getStoreId());
	}

	@RemoteMethod
	public String getStoreFacetName() {
		return configManager.getStoreFacetName(getStoreId());
	}

	@RemoteMethod
	public String getSolrSelectorParam() {
		return configManager.getSolrSelectorParam();
	}

	public String getStoreSetting(String property) {
		return configManager.getProperty("settings", getStoreId(), property);
	}

	public boolean setStoreSetting(String property, String value) {
		return configManager.setStoreSetting(getStoreId(), property, value);
	}

	public String getStoreSetting(String storeId, String property) {
		return configManager.getProperty("settings", storeId, property);
	}

	public List<String> getStoreSettings(String storeId, String property) {
		return configManager.getPropertyList("settings", storeId, property);
	}

	public String getSolrServerUrl(String serverName) {
		return configManager.getServerParameter(serverName, "url");
	}

	public List<String> getStoresToExport(String storeId) {
		return getStoreSettings(storeId, DAOConstants.SETTINGS_EXPORT_TARGET);
	}

	public List<String> getStoreDomains(String storeId) {
		return getStoreSettings(storeId, DAOConstants.SETTINGS_SITE_DOMAIN);
	}

	public String getStoreDefaultBannerSize(String storeId) {
		return StringUtils.defaultIfBlank(getStoreSetting(storeId, DAOConstants.SETTINGS_DEFAULT_BANNER_SIZE), "728x90");
	}

	public List<String> getStoreAllowedBannerSizes(String storeId) {
		List<String> allowedSizes = getStoreSettings(storeId, DAOConstants.SETTINGS_ALLOWED_BANNER_SIZES);
		return allowedSizes != null && allowedSizes.size() > 0 ? allowedSizes : Arrays.asList("180x150", "728x90", "300x250", "160x600");
	}

	public String getStoreDefaultBannerLinkPathProtocol(String storeId) {
		return StringUtils.defaultIfBlank(getStoreSetting(storeId, DAOConstants.SETTINGS_DEFAULT_BANNER_LINKPATH_PROTOCOL), "728x90");
	}

	public void setFacetTemplateValues(RedirectRuleCondition condition) {
		if (condition != null) {
			condition.setFacetPrefix(getStoreFacetPrefix());
			condition.setFacetTemplate(getStoreFacetTemplate());
			condition.setFacetTemplateName(getStoreFacetTemplateName());
		}
	}

	public void setFacetTemplateValues(List<? extends Product> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			String facetPrefix = getStoreFacetPrefix();
			String facetTemplate = getStoreFacetTemplate();
			String facetTemplateName = getStoreFacetTemplateName();
			for (Product p : list) {
				RedirectRuleCondition condition = p.getCondition();
				if (condition != null) {
					condition.setFacetPrefix(facetPrefix);
					condition.setFacetTemplate(facetTemplate);
					condition.setFacetTemplateName(facetTemplateName);
				}
			}
		}
	}

	public String getStoreDateFormat() {
		return configManager.getStoreParameter(getStoreId(), "date-format");
	}

	public String getStoreDateTimeFormat() {
		return configManager.getStoreParameter(getStoreId(), "datetime-format");
	}

	public String getStoreFacetTemplateType(String storeId) {
		return getStoreSetting(storeId, DAOConstants.SETTINGS_FACET_TEMPLATE);
	}
}
