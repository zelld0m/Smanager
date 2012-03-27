package com.search.manager.cache;

import com.search.manager.cache.CacheClient;
import com.search.manager.cache.ehcache.EhCachEDistributedClient;
import com.search.manager.exception.DataException;
import com.search.manager.model.Cacheable;


/**
 * Property of OSRP
 * 
 * @author M ILAGAN JR
 * @version 1.0
 * Date Created: June 02, 2010
 * Description:
 *		<P> This is the product cache utility class for direct DIST CACHE call. </P>
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

public class CacheService<E extends Cacheable>{
	
	protected static CacheClient cacheClient		= null;
	protected String className						= null;
	protected StringBuilder genKey					= null;
	
	public CacheService() {
		className = this.getClass().getSimpleName();
		if (cacheClient == null)
			cacheClient = CacheClient.getInstance();
	}
	
	/**
	 * <P>This method loads the header cache value of the list items to be cached.</P>
	 * @param genKey the key parameter for idetifying the number in the cache.
	 * @param primaryValue indicator flag, "0" if there's no return list, "1", if there's a return list.
	 * @throws DataException
	 */
	protected void primaryLoadToCache(String genKey, String primaryValue) throws DataException {
		cacheClient.resetParameterMatrix();
		cacheClient.addToParameterMatrix(EhCachEDistributedClient.PARAM_MIME_TEXT_PLAIN);
		
		cacheClient.put(genKey, primaryValue);
	}
	
	
	/**
	 * <P>This method loads the object in the distributed cache.</P>
	 * @param paramValue object to be cached
	 * @param paramKey key used for the cached object
	 * @throws DataException
	 */
	public void put(String paramKey, String paramValue) throws DataException {
		cacheClient.resetParameterMatrix();
		cacheClient.addToParameterMatrix(EhCachEDistributedClient.PARAM_MIME_TEXT_PLAIN);
		
		cacheClient.put(this.generateKey(paramKey), paramValue);
	}
	
	/**
	 * <P>This method loads the object in the distributed cache.</P>
	 * @param cacheObject object to be cached
	 * @param paramKey key used for the cached object
	 * @throws DataException
	 */
	public void put(String paramKey, E cacheObject) throws DataException {
		cacheClient.resetParameterMatrix();
		cacheClient.addToParameterMatrix(EhCachEDistributedClient.PARAM_MIME_APP_XJAVA_SER);
		
		cacheClient.put(this.generateKey(paramKey), cacheObject);
	}
	
	private String generateKey(String paramKey) throws DataException {
		String returnKey = null;
		try {
			String lowerKey = paramKey.toLowerCase();
			
			StringBuilder strBuild = new StringBuilder(this.className);
			strBuild.append(".");
			strBuild.append(lowerKey);
			returnKey = strBuild.toString();
		} catch (NullPointerException npex) {
			throw new DataException(npex);
		}
		
		return returnKey;
	}
	
	/**
	 * <P>This method returns the object stored in the cache given the param key value. </P>
	 * @param paramKey key value to be searched from the cache.
	 * @return model object stored in the cache given the param key value.
	 * @throws DataException
	 */
	@SuppressWarnings("unchecked")
	public E get(String paramKey) throws DataException {
		cacheClient.resetParameterMatrix();
		cacheClient.addToParameterMatrix(EhCachEDistributedClient.PARAM_MIME_APP_XJAVA_SER);
		E cacheObj = (E) cacheClient.get(this.generateKey(paramKey).toString());
		return cacheObj;
	}
	
	/**
	 * <P> This method will reset the cache values of the given param to be searched. </P>
	 * @param paramValue value to be reset in cache.
	 */
	public void reset(String paramKey) throws DataException {
		this.primaryLoadToCache(this.generateKey(paramKey), null);
	}
}
