package com.search.manager.utility;

/**
 * Property of OSRP
 * 
 * @author M ILAGAN JR
 * @version 1.0
 * Date Created: July 01, 2009
 * Description:
 * 		<P> This class is used to retrieve params and values from the properties file 'resources/cacheparams.properties'. </P>
 * 
 * History:
 * 
 *   DATE    MODIFIED BY  DESCRIPTION OF CHANGE
 * -------- ------------- ---------------------------------------------------------------------
 * YYYYMMDD
 * 
 * 
 * For code changes/enhancements, please enclose codes:
 * 	// ENHANCEMENT START [Bug Number] - [YYYYMMDD] - [Author Initials]
 * 	// old code
 *  // old code
 *  	...	
 *  	New Code
 *  	...
 *  // ENHANCEMENT END [Bug Number]
 * 
 */

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.search.manager.exception.DataConfigException;

public class CacheResourceUtil {
	private static final String CACHE_STRATEGY			    = "cache.strategy";
	private static final String CACHE_ENABLED           = "cache.enabled";
  private static final String CACHE_DISABLED_EXEMPTS  = "cache.disabled.exempts";
  private static final String CACHE_DISABLED_EXEMPT   = "cache.disabled.exempt.";
	
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
	
	public static final String CACHE_CLIENT_STATUS      = "cache.client.status";
	public static final String HTTP_CLIENT_MAX_CONN_HOST= "http.client.maxConnectionsPerHost";
  public static final String HTTP_CLIENT_ACTV_CONN    = "http.client.maxActiveConnections";
	
	public static final String PITCH_EXT_SIZE			= "pitch.extensions.size";
	public static final String PITCH_EXTENSIONS			= "pitch.extension.";
	
	private static final String RESOURCE_MAP			= "cacheparams";
	private static ResourceBundle dbProps				= null;
 	
	static {
		try {
			dbProps = ResourceBundle.getBundle(RESOURCE_MAP);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	/**
	 * <P> This method returns the value given the key of the Resource bundle.</P>
	 * @param key parameter key to be searched.
	 * @return String value if key was found.
	 */
	public static String getValue(String key) throws DataConfigException {
		String retValue = null;
		
		try {
			retValue = dbProps.getString(key);
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
}
