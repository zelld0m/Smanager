package com.search.manager.cache.utility;

import java.util.MissingResourceException;
import java.util.Properties;
import com.search.manager.exception.DataConfigException;
import com.search.manager.utility.PropertiesUtils;

@Deprecated
public class CacheResourceUtil {
	private static final String CACHE_STRATEGY			= "cache.strategy";
	private static final String CACHE_ENABLED			= "cache.enabled";
	private static final String CACHE_DISABLED_EXEMPTS	= "cache.disabled.exempts";
	private static final String CACHE_DISABLED_EXEMPT	= "cache.disabled.exempt.";
	
	private static final String DIST_Q_CLUSTER_ID		= "distributed.q.cluster.id";
	private static final String DIST_Q_KEY_ID			= "distributed.q.key.identifier";
	private static final String DIST_Q_KEY_ID_U_CLICKS	= "distributed.q.uclicks.key.id";
	private static final String DIST_Q_MAX_NUM_OF_ITEMS	= "distributed.q.max.number.of.items";
	private static final String DIST_Q_LOCK_TIMEOUT		= "distributed.q.lock.timeout";
	private static final String SFONDO_IDENTIFIER		= "sfondo.identifier";
	private static final String SFONDO_TASKING			= "sfondo.tasking.";
	private static final String SFONDO_DELAY			= "sfondo.period";
	private static final String BLK_ALLOW_IP_SIZE		= "allow.ip.size";
	
	public static final String BLK_ALLOW_IP            	= "allow.ip.";
	public static final String CHAT_CACHE_ONLY          = "chat.cacheable.only";
	public static final String PRE_LOADED_SERVICES      = "preloaded.services";
	public static final String PRE_LOADED_SVC          	= "preloaded.service.";
	public static final String PITCH_STRATEGY          	= "pitch.strategy";
	public static final String AUTH_TOKEN_RESTRAINT     = "auth.token.restraint";
	public static final String AUTH_REMOTE_HOST			= "auth.remote.host";
	public static final String AUTH_ALLOW_REMOTE_HOST	= "auth.allow.remote.host.";
	public static final String AUTH_ALLOW_REM_HOST_SIZE	= "auth.allow.remote.host.size";
	public static final String GENERAL_CACHE_EXPIRY		= "general.cache.expiry";
	public static final String GENERAL_CCH_MAX_DELTA_EXE= "general.cache.delta.max.run";
	
	public static final String PITCH_EXT_SIZE			= "pitch.extensions.size";
	public static final String PITCH_EXTENSIONS			= "pitch.extension.";
	
	public static final String PRELOAD_START_IDX		= "preloaded.service.startat.pidx.";
	public static final String PRELOAD_END_IDX			= "preloaded.service.endat.pidx.";
	
	public static final String FORCE_UPD_SAN_PRELOAD	= "preloaded.service.san.load.";
	public static final String PRELOAD_UPD_CCH_EXPIRY   = "preloaded.service.cached.item.expiry.";
	
	public static final String DEFAULT_STORE_ID			= "default.store.id";
	public static final String DEFAULT_IMG_PREFIX		= "default.imageServerPrefix";
	public static final String DEFAULT_ORDER_PREFIX		= "default.order.prefix";
	public static final String DEFAULT_CURRENCY			= "default.currency";
	public static final String SWITCH_VALUE_MAP			= "switch.value.map";
	
	public static final String WORK_FLOW_RULES			= "processflow.rules";
	public static final String WORK_FLOW_RULE_DETAIL	= "processflow.rule.";
	public static final String WF_RESTRICT_PAY_TYPES	= "processflow.restrictions.payment";
	public static final String WF_RESTRICT_PAY_METHOD	= "processflow.restrict.payment.";
	public static final String WF_SWITCH_PROCESS_FLOW	= "switch.process.flow";
	public static final String WF_GROUP_SORT_OVERRIDE	= "processflow.sort.group.ids.override";
	
	public static final String CUSTOM_EXPIRY			= "custom.expiry.";
	public static final String CACHE_CLIENT_STATUS      = "cache.client.status";
	
	public static final String HTTP_CLIENT_MAX_CONN_HOST= "http.client.maxConnectionsPerHost";
	public static final String HTTP_CLIENT_ACTV_CONN 	= "http.client.maxActiveConnections";
	
	public static final String PROCESSOR_INVENTORY_ID	= "processor.inventory.id";
	
	private static final String RESOURCE_MAP		= "cacheparams";
	private static Properties dbProps				= null;
	
	public static final String CATALOG_CATEGORY_BATCH_SIZE   = "preloaded.service.cached.catalog.category.batch.size";
	
	public CacheResourceUtil(){
		 try {
			 	if(dbProps == null)
			 		dbProps = PropertiesUtils.getProperties(RESOURCE_MAP);
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
	}
	
	public static CacheResourceUtil getInstance() {
		return new CacheResourceUtil();
	}
	
	/**
	 * <P> This method returns the value given the key of the Resource bundle.</P>
	 * @param key parameter key to be searched.
	 * @return String value if key was found.
	 */
	public static String getValue(String key) throws DataConfigException {
		String retValue = null;
		
		try {
			retValue = dbProps.getProperty(key);
		} catch (MissingResourceException mrex) {
			throw new DataConfigException(mrex.getMessage(), mrex);
		} catch (NullPointerException npex) {
			throw new DataConfigException(npex.getMessage(), npex);
		}
		
		return retValue;
	}
	
	/**
	 * <P> This method returns the cache strategy property value. </P>
	 * @return the cache strategy property value.
	 * @throws DataConfigException
	 */
	public static String getCacheStratgy() throws DataConfigException {
		return getValue(CACHE_STRATEGY);
	}
	
	/**
	 * <P> This method returns the cache strategy property value. </P>
	 * @return the cache strategy property value.
	 * @throws DataConfigException
	 */
	public static Integer getMaximumNumberOfDistributedItems() throws DataConfigException {
		Integer retMax = null;
		try {
			retMax = Integer.parseInt(getValue(DIST_Q_MAX_NUM_OF_ITEMS));
		} catch (NumberFormatException nfex) {
			throw new DataConfigException(nfex.getMessage(), nfex);
		}
		
		return retMax;
	}
	
	/**
	 * <P> This method returns the lock timeout value in milliseconds. </P> 
	 * @return lock timeout value in milliseconds.
	 * @throws DataConfigException thrown for configuration exceptions
	 */
	public static Integer getLockTimeoutValue() throws DataConfigException {
		Integer retMax = null;
		try {
			retMax = Integer.parseInt(getValue(DIST_Q_LOCK_TIMEOUT));
		} catch (NumberFormatException nfex) {
			throw new DataConfigException(nfex.getMessage(), nfex);
		}
		
		return retMax;
	}
	
	/**
	 * <P> This method returns the tasking property for the sfondo processor. Node is identified via the 'sfondo.identifier'. </P>
	 * @return tasking property for the sfondo processor. ('sfondo.tasking.<identifier>'; ex: 'sfondo.tasking.matrix88', where identifier
	 * is equal to 'matrix88'.)
	 * @throws DataConfigException thrown for configuration exceptions
	 */
	public static String getAssignedTasking() throws DataConfigException {
		
		String identifier = getValue(SFONDO_IDENTIFIER);
		StringBuilder tasking = new StringBuilder(SFONDO_TASKING);
		tasking.append(identifier);
		
		return getValue(tasking.toString());
	}
	
	/**
	 * <P> This method returns the distributed key ID which will be used to identify the items in the QUEUE. </P>
	 * @return
	 * @throws DataConfigException
	 */
	public static String getDistributedKeyIdentifier() throws DataConfigException {
		return getValue(DIST_Q_KEY_ID);
	}
	
	/**
	 * <P> This method returns the distributed key ID for user clicks which will be used to identify items in the QUEUE. </P>
	 * @return
	 * @throws DataConfigException
	 */
	public static String getDistributedKeyUserClicks() throws DataConfigException {
		return getValue(DIST_Q_KEY_ID_U_CLICKS);
	}
	
	/**
	 * <P> This method returns the time in seconds between successive task executions. </P>
	 * @return time seconds between successive task executions.
	 * @throws DataConfigException
	 */
	public static String getExecutionPeriod() throws DataConfigException {
		return getValue(SFONDO_DELAY);
	}
	
	/**
	 * <P> This method returns the allow IP value. </P> 
	 * @return returns the allow IP value.
	 * @throws DataConfigException thrown for configuration exceptions
	 */
	public static Integer getAllowIPValue() throws DataConfigException {
		Integer retMax = null;
		try {
			retMax = Integer.parseInt(getValue(BLK_ALLOW_IP_SIZE));
		} catch (NumberFormatException nfex) {
			throw new DataConfigException(nfex.getMessage(), nfex);
		}
		
		return retMax;
	}
	
	/**
	 * <P> This method returns the Q cluster ID. </P>
	 * @return the Q cluster ID.
	 * @throws DataConfigException
	 */
	public static String getClusterID() throws DataConfigException {
		return getValue(DIST_Q_CLUSTER_ID);
	}
	
	/**
	 * <P> This method returns the parameter value if chat config/history/conversation is cacheable only. </P>
	 * @return parameter value if chat config/history/conversation is cacheable only.
	 * @throws DataConfigException
	 */
	public static Integer getChatCacheableOnly() throws DataConfigException {
		Integer retMax = 0;
		try { retMax = Integer.parseInt(getValue(CHAT_CACHE_ONLY)); } catch (NumberFormatException nfex) {}
		
		return retMax;
	}
	
	/**
	 * <P> Get the number of pre-loaded services. </P>
	 * @return the number of pre-loaded services.
	 * @throws DataConfigException
	 */
	public static Integer getNumberOfPreloadedServices() throws DataConfigException {
		Integer retMax = null;
		try {
			retMax = Integer.parseInt(getValue(PRE_LOADED_SERVICES));
		} catch (NumberFormatException nfex) {
			throw new DataConfigException(nfex.getMessage(), nfex);
		}
		
		return retMax;
	}
	
	/**
	 * <P> Get the preloaded service. </P>
	 * @return the preloaded service class name
	 * @throws DataConfigException
	 */
	public static String getPreloadedService(Integer svcID) throws DataConfigException {
		StringBuilder service = new StringBuilder(PRE_LOADED_SVC);
		service.append(String.valueOf(svcID));
		
		return getValue(service.toString());
	}
	
	/**
	 * <P>This method returns the pitchStrategy used defined in the properties file.</P>
	 * @return returns the pitchStrategy used defined in the properties file; DEFAULT = 0;
	 * @throws DataConfigException
	 */
	public static Integer getPitchStrategy() {
		Integer pitchStrategy = null;
		try {
			pitchStrategy = Integer.parseInt(getValue(PITCH_STRATEGY));
		} catch (NumberFormatException nfex) {
			pitchStrategy = 0;
		} catch (DataConfigException dcex) {
			pitchStrategy = 0;
		}
		
		return pitchStrategy;
	}
	
	/**
	 * <P>This method returns the authentication token restraint value.</P>
	 * @return the authentication token restraint value; DEFAULT=1 token will be validated against cache; 0 = no validation will take place.
	 */
	public static Integer getAuthenticationTokenRestraint() {
		Integer pitchStrategy = null;
		try {
			pitchStrategy = Integer.parseInt(getValue(AUTH_TOKEN_RESTRAINT));
		} catch (NumberFormatException nfex) {
			pitchStrategy = 1;
		} catch (DataConfigException dcex) {
			pitchStrategy = 1;
		}
		
		return pitchStrategy;
	}
	
	/**
	 * <P> This method returns the flag if remote hosts validation is activated or not. </P>
	 * @return the flag if remote hosts validation is activated or not. DEFAULT = 1;
	 */
	public static Integer getRemoteHostValidationFlag() {
		Integer allowHosts = null;
		
		try {
			allowHosts = Integer.parseInt(getValue(AUTH_REMOTE_HOST));
		} catch (NumberFormatException nfex) {
			allowHosts = 1;
		} catch (DataConfigException dcex) {
			allowHosts = 1;
		}
		
		return allowHosts;
	}
	
	/**
	 * <P> This method returns the number or remote machines allowed to connect to the ESB server. </P>
	 * @return the flag if remote hosts validation is activated or not. DEFAULT = 0;
	 */
	public static Integer allowRemoteHostSize() {
		Integer remoteHostSize = null;
		
		try {
			remoteHostSize = Integer.parseInt(getValue(AUTH_ALLOW_REM_HOST_SIZE));
		} catch (NumberFormatException nfex) {
			remoteHostSize = 1;
		} catch (DataConfigException dcex) {
			remoteHostSize = 1;
		}
		
		return remoteHostSize;
	}
	
	/**
	 * <P>This method returns the allowed host machine connecting to the ESB server.</P>
	 * @param hostIndex index of the host machine
	 * @return the allowed host machine connecting to the ESB server.
	 * @throws DataConfigException
	 */
	public static String getRemoteHost(Integer hostIndex) throws DataConfigException {
		StringBuilder keyParam = new StringBuilder(AUTH_ALLOW_REMOTE_HOST);
		keyParam.append(hostIndex);
		
		return getValue(keyParam.toString());
	}
	
	/**
	 * <P> This method returns the number of seconds before a cached object is considered as expired. </P>
	 * @return the number of seconds before a cached object is considered as expired. DEFAULT = 0;
	 */
	public static Long getGeneralCacheExpiry() {
		Long expiresInSeconds = null;
		
		try {
			expiresInSeconds = Long.parseLong(getValue(GENERAL_CACHE_EXPIRY));
		} catch (NumberFormatException nfex) {
			expiresInSeconds = 0L;
		} catch (DataConfigException dcex) {
			expiresInSeconds = 0L;
		}
		
		return expiresInSeconds;
	}
	
	/**
	 * <P>This method returns 1 if class is considered a Pitch Extension (pitch utility should be applied); else 0 is returned. </P>
	 * @param className the complete class name to be evaluated.
	 * @return 1 if class is considered a Pitch Extension (pitch utility should be applied); else 0 is returned.
	 */
	public static Integer isAPitchExtension(String className) {
		Integer result = 0;
		
		try {
			Integer extSize = Integer.valueOf(getValue(PITCH_EXT_SIZE));
			for (int i = 1; i <= extSize; i++) {
				StringBuilder keyParam = new StringBuilder(PITCH_EXTENSIONS);
				keyParam.append(i);
				String extName = getValue(keyParam.toString());
				if (extName.equalsIgnoreCase(className))
					return 1;
			}
		} catch (NumberFormatException nfex) {
			result = 0;
		} catch (DataConfigException dcex) {
			result = 0;
		}
		
		return result;
	}
	
	/**
	 * <P>This method returns the start page index of the preloaded method.</P>
	 * @return the start index of the preloaded method.
	 */
	public static Integer getPreloadStartAtIdx(String svcMethod) {
		Integer result = null;
		
		try {
			StringBuilder keyParam = new StringBuilder(PRELOAD_START_IDX);
			keyParam.append(svcMethod);
			result = Integer.valueOf(getValue(keyParam.toString()));
		} catch (NumberFormatException nfex) {
			result = null;
		} catch (DataConfigException dcex) {
			result = null;
		}
		
		return result;
	}
	
	/**
	 * <P>This method returns the ending page index of the preloaded method.</P>
	 * @return the start index of the preloaded method.
	 */
	public static Integer getPreloadEndAtIdx(String svcMethod) {
		Integer result = null;
		
		try {
			StringBuilder keyParam = new StringBuilder(PRELOAD_END_IDX);
			keyParam.append(svcMethod);
			result = Integer.valueOf(getValue(keyParam.toString()));
		} catch (NumberFormatException nfex) {
			result = null;
		} catch (DataConfigException dcex) {
			result = null;
		}
		
		return result;
	}
	
	/**
	 * <P> Gets the default store id. </P>
	 * @return the default store id.
	 * @throws DataConfigException
	 */
	public static String getDefaultStoreID() throws DataConfigException {
		return getValue(DEFAULT_STORE_ID);
	}
	
	/**
	 * <P> Gets the switch value of map. </P>
	 * @return the switch value of map.
	 * @throws DataConfigException
	 */
	public static String getSwitchValueMap() throws DataConfigException {
		return getValue(SWITCH_VALUE_MAP);
	}
	
	public static Integer forceCacheUpdateSanPreload(String svcMethod) {
		Integer result = 0;
		
		try {
			StringBuilder keyParam = new StringBuilder(FORCE_UPD_SAN_PRELOAD);
			keyParam.append(svcMethod);
			result = Integer.valueOf(getValue(keyParam.toString()));
		} catch (NumberFormatException nfex) {
			result = 0;
		} catch (DataConfigException dcex) {
			result = 0;
		}
		
		return result;
	}
	
	/**
	 * <P>This method will maximum number of seconds that the delta load processor will run before stopping and giving other delta updates to run.</P>
	 * @return maximum number of seconds that the delta load processor will run before stopping and giving other delta updates to run
	 */
	public static Long getMaximumDeltaLoadCacheRun() {
		Long result = 0L;
		
		try {
			result = Long.valueOf(getValue(GENERAL_CCH_MAX_DELTA_EXE));
		} catch (NumberFormatException nfex) {
			result = 0L;
		} catch (DataConfigException dcex) {
			result = 0L;
		}
		
		return result;
	}
	
	/**
	 * <P>This method will return the custom expiry in seconds of cached item objects.</P>
	 * @param serviceKey name of the service key where the expiry value is stored.
	 * @return
	 */
	public static Integer getCacheLoadUpdateExpiry(String serviceKey) {
		Integer result = 0;
		
		try {
			StringBuilder keyParam = new StringBuilder(PRELOAD_UPD_CCH_EXPIRY);
			keyParam.append(serviceKey);
			result = Integer.valueOf(getValue(keyParam.toString()));
		} catch (NumberFormatException nfex) {
			result = 0;
		} catch (DataConfigException dcex) {
			result = 0;
		}
		
		return result;
	}
	
	/**
	 * <P>This method returns the number of process rules.</P>
	 * @return the number of process rules.
	 */
	public static Integer getNumberOfProcessRules() {
		Integer result = 0;
		
		try {
			result = Integer.valueOf(getValue(WORK_FLOW_RULES));
		} catch (NumberFormatException nfex) {
			result = 0;
		} catch (DataConfigException dcex) {
			result = 0;
		}
		
		return result;
	}
	
	/**
	 * <P> Gets the rule detail value. </P>
	 * @param detailNumber sequence detail number.
	 * @return the rule detail value.
	 * @throws DataConfigException
	 */
	public static String getRuleDetail(Integer detailNumber) throws DataConfigException {
		StringBuffer paramKey = new StringBuffer(WORK_FLOW_RULE_DETAIL);
		paramKey.append(detailNumber);
		
		return getValue(paramKey.toString());
	}
	
	/**
	 * <P>This method returns the custom cache expiry value.</P>
	 * @param serviceKey
	 * @return the custom cache expiry value.
	 */
	public static Long getCacheCustomeExpiry(String serviceKey) {
		Long result = 0L;
		
		try {
			StringBuilder keyParam = new StringBuilder(CUSTOM_EXPIRY);
			keyParam.append(serviceKey);
			result = Long.valueOf(getValue(keyParam.toString()));
		} catch (NumberFormatException nfex) {
			result = 0L;
		} catch (DataConfigException dcex) {
			result = 0L;
		}
		
		return result;
	}
	
	/**
	 * <P>This method returns the number of payment method that will be allowed. 0 = all payment types are allowed</P>
	 * @return the number of payment method that will be allowed.
	 */
	public static Integer getNumberOfPaymentMethodRestrictions() {
		Integer result = 0;
		
		try {
			result = Integer.valueOf(getValue(WF_RESTRICT_PAY_TYPES));
		} catch (NumberFormatException nfex) {
			result = 0;
		} catch (DataConfigException dcex) {
			result = 0;
		}
		
		return result;
	}
	
	/**
	 * <P>This method returns the payment method restriction.</P>
	 * @param detailNumber sequence detail number.
	 * @return the rule detail value.
	 * @throws DataConfigException
	 */
	public static String getPaymentMethodRestriction(Integer itemNumber) throws DataConfigException {
		StringBuffer paramKey = new StringBuffer(WF_RESTRICT_PAY_METHOD);
		paramKey.append(itemNumber);
		
		return getValue(paramKey.toString());
	}
	
	/**
	 * <P>This method returns the process flow switch value.</P>
	 * @return 1 = ACTIVE; 0 = INACTIVE
	 * @throws DataConfigException
	 */
	public static Integer getProcessFlowSwitch() throws DataConfigException {
		Integer result = 0;
		
		try {
			result = Integer.valueOf(getValue(WF_SWITCH_PROCESS_FLOW));
		} catch (NumberFormatException nfex) {
			result = 0;
		} catch (DataConfigException dcex) {
			result = 0;
		}
		
		return result;
	}
	
	/**
	 * <P>This method returns the default image prefix.</P>
	 * @return the default image prefix
	 * @throws DataConfigException
	 */
	public static String getDefaultImagePrefix() throws DataConfigException {		
		return getValue(DEFAULT_IMG_PREFIX);
	}
	
	/**
	 * <P>This method returns the default order prefix.</P>
	 * @return the default order prefix
	 * @throws DataConfigException
	 */
	public static String getDefaultOrderPrefix() throws DataConfigException {		
		return getValue(DEFAULT_ORDER_PREFIX);
	}
	
	/***
	 * <P>This method returns the default currency.</P>
	 * @return the default currency
	 * @throws DataConfigException
	 */
	public static String getDefaultCurrency() throws DataConfigException {		
		return getValue(DEFAULT_CURRENCY);
	}
	
	/**
	 * <P>This method returns the group sort override rule list.</P>
	 * @return returns the group sort override rule list.
	 * @throws DataConfigException
	 */
	public static String getWFGroupSortOverride() throws DataConfigException {		
		return getValue(WF_GROUP_SORT_OVERRIDE);
	}
	
	/**
	   * <P>This method returns the authentication token restraint value.</P>
	   * @return the authentication token restraint value; DEFAULT=1 token will be validated against cache; 0 = no validation will take place.
	   */
	public static Integer getCacheClientStatusDefaults() {
	    Integer stat = 1;
	    try {
	      stat = Integer.parseInt(getValue(CACHE_CLIENT_STATUS));
	    } catch (NumberFormatException nfex) {
	      stat = 1;
	    } catch (DataConfigException dcex) {
	      stat = 1;
	    }
	    
	    return stat;
	  }
	
	/**
	 * <P>The maximum number of connections that will be created for any particular HostConfiguration. Defaults to 2.</P>
	 * @return
	 */
	public static Integer getHttpClientMaxConnectionsPerHost() {
		Integer stat = -1;
		
	    try {
	      stat = Integer.parseInt(getValue(HTTP_CLIENT_MAX_CONN_HOST));
	    } catch (NumberFormatException nfex) {
	      stat = -1;
	    } catch (DataConfigException dcex) {
	      stat = -1;
	    }
	    
	    return stat;
	}
	
	/**
	 * <P>The maximum number of active connections. Defaults to 20.</P>
	 * @return
	 */
	public static Integer getHttpClientMaxActiveConnections() {
		Integer stat = -1;
		
	    try {
	      stat = Integer.parseInt(getValue(HTTP_CLIENT_ACTV_CONN));
	    } catch (NumberFormatException nfex) {
	      stat = -1;
	    } catch (DataConfigException dcex) {
	      stat = -1;
	    }
	    
	    return stat;
	}
	
	/**
	 * <P>This method will return 1 if caching is enabled; else 0 if disabled. 1 = (DEFAULT).</P>
	 * @return 1 if caching is enabled; else 0 if disabled.
	 */
	public static Integer getCacheEnabled() {
		Integer stat = 1;
		
		try {
			stat = Integer.parseInt(getValue(CACHE_ENABLED));
		} catch (NumberFormatException nfex) {	
			stat = 1;
		} catch (DataConfigException dcex) {
			stat = 1;
		}
		
		return stat;
	}
	
	/**
	 * <P>This method will return the number of cache exempted items when caching is disabled.</P>
	 * @return the number of cache exempted items when caching is disabled.
	 */
	public static Integer getCacheExemptions() {
		Integer result = 0;
		
		try {
			result = Integer.parseInt(getValue(CACHE_DISABLED_EXEMPTS));
		} catch (NumberFormatException nfex) {	
			result = 0;
		} catch (DataConfigException dcex) {
			result = 0;
		}
		
		return result;
	}
	
	public static String getExemptedItem(int idx) {
		String item = "";
		
		try {
			StringBuilder key = new StringBuilder(CACHE_DISABLED_EXEMPT);
			key.append(String.valueOf(idx));
			item = getValue(key.toString());
		} catch (DataConfigException dcex) {
			item = "";
		}
		
		return item;
	}
	
	public static String getProcessorInventoryId() throws DataConfigException {
		try {
			return getValue(PROCESSOR_INVENTORY_ID);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		
		return "";
	}
	
	public static int getCatalogManufCategoryBatchSize() throws DataConfigException {
		Integer result = 0;
		try {
			result = Integer.parseInt(getValue(CATALOG_CATEGORY_BATCH_SIZE));
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		
		return result;
	}
}
