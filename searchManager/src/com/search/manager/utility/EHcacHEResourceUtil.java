package com.search.manager.utility;

/**
 * Property of OSRP
 * 
 * @author M ILAGAN JR
 * @version 1.0
 * Date Created: July 01, 2009
 * Description:
 * 		<P> This class is used to retrieve params and values from the properties file 'resources/ehcacheparams.properties'. </P>
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

public class EHcacHEResourceUtil {
	public static final String EH_TOTAL_SERVERS		= "ehcache.total.servers";
	public static final String EH_SERVER			= "ehcache.server.";
	public static final String EH_PORT				= "ehcache.port.";
	public static final String EH_CACHE_NAME		= "ehcache.cachename.";
	// Added a persistent cache
	public static final String EH_CACHE_NAME_PXT	= "ehcache.cachename.pxt.";
	// Extended CACHE instance key
	public static final String EH_EXT_INSTANCES		= "ehcache.ext.instances";
	
	private static final String RESOURCE_MAP		= "ehcacheparams";
	private ResourceBundle dbProps					= null;
 	
	private EHcacHEResourceUtil() {
		try {
			dbProps = ResourceBundle.getBundle(RESOURCE_MAP);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}

	/**
	 * <P> Returns an instance of the DAOResourceUtil class. </P>
	 * @return new DAOResourceUtil instance.
	 */
	public static EHcacHEResourceUtil newInstance() {
		return new EHcacHEResourceUtil();
	}
	
	/**
	 * <P> This method returns the value given the key of the Resource bundle.</P>
	 * @param key parameter key to be searched.
	 * @return String value if key was found.
	 */
	public String getValue(String key) throws DataConfigException {
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
	 * <P> This method returns the total number of nodes declared in the properties file 'resource/dbparams.properties' file. </P>
	 * @return total number of nodes declared.
	 * @throws DataConfigException thrown for configuration exceptions.
	 */
	public Integer getTotalNumberOfCacheServers() throws DataConfigException {
		Integer total = 0;
		
		try {
			total = Integer.parseInt(this.getValue(EH_TOTAL_SERVERS));
		} catch (NumberFormatException nfex) {
			throw new DataConfigException(nfex.getMessage(), nfex);
		}
		
		return total;
	}
	
	/**
	 * <P>This method returns the instance names of the extended cache instances.</P>
	 * @return instance names of the extended cache instances.
	 * @throws DataConfigException
	 */
	public String getCacheExtendedInstances() throws DataConfigException {
		
		return this.getValue(EH_EXT_INSTANCES);
	}
}
