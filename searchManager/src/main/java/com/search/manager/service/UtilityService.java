package com.search.manager.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import com.search.ws.SolrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(value = "utilityService")
@RemoteProxy(
        name = "UtilityServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "utilityService"))
public class UtilityService {

    private static final Logger logger =
            LoggerFactory.getLogger(UtilityService.class);
    private final static Map<RuleEntity, AtomicReference<String>> lockService;

    static {
        lockService = new HashMap<RuleEntity, AtomicReference<String>>();
        for (RuleEntity ruleEntity : RuleEntity.values()) {
            lockService.put(ruleEntity, new AtomicReference<String>());
        }
    }

    public static boolean obtainPublishLock(RuleEntity ruleType, String username, String storeName) throws PublishLockException {
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

    public static boolean releasePublishLock(RuleEntity ruleType, String username, String storeName) {
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

    @RemoteMethod
    public static String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null || !(principal instanceof UserDetailsImpl)) {
            return "";
        }
        return ((UserDetailsImpl) principal).getUsername();
    }

    @RemoteMethod
    public static String getServerName() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String serverName = (String) attr.getAttribute("serverName", RequestAttributes.SCOPE_SESSION);
        if (StringUtils.isEmpty(serverName)) {
            // get default server for store
            ConfigManager cm = ConfigManager.getInstance();
            if (cm != null) {
                serverName = cm.getStoreParameter(getStoreId(), "server-url");
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
    public static String getStoreId() {
        ConfigManager cm = ConfigManager.getInstance();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String storeId = (String) attr.getAttribute("storeId", RequestAttributes.SCOPE_SESSION);
        return cm.getStoreIdByAliases(storeId);
    }

    @RemoteMethod
    public static String getTimeZoneId() {
        return DateTimeZone.getDefault().getID();
    }

    @RemoteMethod
    public static String getStoreCore(String storeId) {
        ConfigManager cm = ConfigManager.getInstance();
        if (StringUtils.isNotBlank(storeId)) {
            return cm.getStoreParameter(storeId, "core");
        }

        return cm.getStoreParameter(getStoreId(), "core");
    }

    @RemoteMethod
    public static String getStoreCore() {
        return getStoreCore(getStoreId());
    }

    @RemoteMethod
    public static String getStoreName() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String storeName = (String) attr.getAttribute("storeName", RequestAttributes.SCOPE_SESSION);
        return storeName;
    }

    @RemoteMethod
    public static void setStoreId(String storeId) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        attr.setAttribute("storeId", storeId, RequestAttributes.SCOPE_SESSION);
    }

    @RemoteMethod
    public static void setStoreName(String storeName) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        attr.setAttribute("storeName", storeName, RequestAttributes.SCOPE_SESSION);
    }

    @RemoteMethod
    public static String getSolrConfig() {
        JSONObject json = new JSONObject();
        String url = ConfigManager.getInstance().getServerParameter(getServerName(), "url");
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
    public static String getIndexedSchemaFields() {
        JSONObject json = new JSONObject();

        Schema schema = SolrSchemaUtility.getDefaultSchema();
        if (schema != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            json.put("indexedFields", (List<String>) attr.getAttribute("indexedFields", RequestAttributes.SCOPE_SESSION));
            json.put("indexedWildcardFields", (List<String>) attr.getAttribute("indexedWildcardFields", RequestAttributes.SCOPE_SESSION));
        }

        return json.toString();
    }

    @RemoteMethod
    public static String getStoreParameters() {
        JSONObject json = new JSONObject();
        String storeId = getStoreId();
        json.put("username", getUsername());
        json.put("solrSelectorParam", getSolrSelectorParam());
        json.put("storeId", storeId);
        json.put("storeCore", getStoreCore());
        json.put("storeName", getStoreName());
        json.put("storeDomains", getStoreDomains(storeId));
        json.put("storeFacetName", getStoreFacetName());
        json.put("storeFacetTemplate", getStoreFacetTemplate());
        json.put("storeFacetTemplateName", getStoreFacetTemplateName());
        json.put("storeGroupMembership", getStoreGroupMembership());
        json.put("storeDateFormat", getStoreDateFormat());
        json.put("storeDefaultBannerSize", getStoreDefaultBannerSize(storeId));
        json.put("storeAllowedBannerSizes", getStoreAllowedBannerSizes(storeId));
        json.put("storeDefaultBannerLinkPathProtocol", getStoreDefaultBannerLinkPathProtocol(storeId));
        json.put("storeRedirectSelfDomain", getStoreSelfDomains(storeId));
        json.put("storeRedirectRelativePath", getStoreRelativePath(storeId));
        
        return json.toString();
    }

    @RemoteMethod
    public static Map<String, String> getServerListForSelectedStore(boolean includeSelectedStore) {
        Map<String, String> map = ConfigManager.getInstance().getServersByStoreId(getStoreId());
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
    public static String getStoreFacetTemplate() {

        ConfigManager cm = ConfigManager.getInstance();
        String storeFacetTemplate = StringUtils.EMPTY;
        if (cm != null) {
            storeFacetTemplate = cm.getStoreParameter(getStoreId(), SolrConstants.SOLR_PARAM_FACET_TEMPLATE);
        }

        return storeFacetTemplate;
    }

    @RemoteMethod
    public static List<String> getStoreGroupMembership() {
        List<String> groupMembershipList = new ArrayList<String>();

        ConfigManager cm = ConfigManager.getInstance();
        if (cm != null) {
            groupMembershipList = cm.getStoreParameterList(getStoreId(), "group-membership/group");
        }

        return groupMembershipList;
    }

    @RemoteMethod
    public static String getStoreFacetPrefix() {

        ConfigManager cm = ConfigManager.getInstance();
        String storeFacetPrefix = StringUtils.EMPTY;
        if (cm != null) {
            storeFacetPrefix = cm.getStoreParameter(getStoreId(), SolrConstants.SOLR_PARAM_FACET_NAME);
        }

        return storeFacetPrefix;
    }

    @RemoteMethod
    public static String getStoreFacetTemplateName() {

        ConfigManager cm = ConfigManager.getInstance();
        String storeFacetTemplateName = StringUtils.EMPTY;
        if (cm != null) {
            storeFacetTemplateName = cm.getStoreParameter(getStoreId(), SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME);
        }

        return storeFacetTemplateName;
    }

    @RemoteMethod
    public static String getStoreFacetName() {
        ConfigManager cm = ConfigManager.getInstance();
        String storeFacetTemplate = StringUtils.EMPTY;
        if (cm != null) {
            storeFacetTemplate = cm.getStoreParameter(getStoreId(), SolrConstants.SOLR_PARAM_FACET_NAME);
        }

        return storeFacetTemplate;
    }

    @RemoteMethod
    public static String getSolrSelectorParam() {
        return ConfigManager.getInstance().getSolrSelectorParam();
    }

    public static String getStoreSetting(String property) {
        return ConfigManager.getInstance().getStoreSetting(getStoreId(), property);
    }

    public static boolean setStoreSetting(String property, String value) {
        return ConfigManager.getInstance().setStoreSetting(getStoreId(), property, value);
    }

    public static String getStoreSetting(String storeId, String property) {
        return ConfigManager.getInstance().getStoreSetting(storeId, property);
    }

    public static List<String> getStoreSettings(String storeId, String property) {
        return ConfigManager.getInstance().getStoreSettings(storeId, property);
    }

    public static List<String> getStoresToExport(String storeId) {
        return UtilityService.getStoreSettings(storeId, DAOConstants.SETTINGS_EXPORT_TARGET);
    }

    public static List<String> getStoreDomains(String storeId) {
        return UtilityService.getStoreSettings(storeId, DAOConstants.SETTINGS_SITE_DOMAIN);
    }
    
    public static String getStoreDefaultBannerSize(String storeId) {
        return StringUtils.defaultIfBlank(UtilityService.getStoreSetting(storeId, DAOConstants.SETTINGS_DEFAULT_BANNER_SIZE), "728x90");
    }

    public static List<String> getStoreAllowedBannerSizes(String storeId) {
        List<String> allowedSizes = UtilityService.getStoreSettings(storeId, DAOConstants.SETTINGS_ALLOWED_BANNER_SIZES);
        return allowedSizes != null && allowedSizes.size() > 0 ? allowedSizes : Arrays.asList("180x150", "728x90", "300x250", "160x600");
    }

    public static String getStoreDefaultBannerLinkPathProtocol(String storeId) {
        return StringUtils.defaultIfBlank(UtilityService.getStoreSetting(storeId, DAOConstants.SETTINGS_DEFAULT_BANNER_LINKPATH_PROTOCOL), "728x90");
    }
     
    public static void setFacetTemplateValues(RedirectRuleCondition condition) {
        if (condition != null) {
            condition.setFacetPrefix(getStoreFacetPrefix());
            condition.setFacetTemplate(getStoreFacetTemplate());
            condition.setFacetTemplateName(getStoreFacetTemplateName());
        }
    }

    public static void setFacetTemplateValues(List<? extends Product> list) {
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

    public static String getStoreDateFormat() {
        return ConfigManager.getInstance().getStoreParameter(getStoreId(), "date-format");
    }

    public static String getStoreDateTimeFormat() {
        return ConfigManager.getInstance().getStoreParameter(getStoreId(), "datetime-format");
    }
    
    public static List<String> getStoreSelfDomains(String storeId) {
    	return UtilityService.getStoreSettings(storeId, DAOConstants.SETTINGS_REDIRECT_SELF_DOMAIN);
    }
    
    public static List<String> getStoreRelativePath(String storeId) {
    	return UtilityService.getStoreSettings(storeId, DAOConstants.SETTINGS_REDIRECT_RELATIVE_PATH);
    }
    
    public static String getFacetTemplate(String storeId) {
    	return UtilityService.getStoreSetting(storeId, DAOConstants.SETTINGS_FACET_TEMPLATE);
    }
    
}
