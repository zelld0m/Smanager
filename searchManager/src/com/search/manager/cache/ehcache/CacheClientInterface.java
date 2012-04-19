package com.search.manager.cache.ehcache;

import java.util.List;

import com.search.manager.exception.DataException;

public interface CacheClientInterface {
	
	public static final Integer PERSISTENT_CACHE_NODE_BASE			= 8800; 
	
	/**
	 * <P> This method is used to put items in the cache. </P>
	 * @param key the key which will be used as a search parameter.
	 * @param value the value to be stored in the cache.
	 * @throws DataException
	 */
	public abstract void put(String key, Object value) throws DataException;
	
	/**
	 * <P> This method is used to get items from the cache. </P>
	 * @param key the key used as a search parameter.
	 * @return get the object from the cache with the given key. Returns <code>null</code> if key was not found.
	 * @throws DataException
	 */
	public abstract Object get(String key) throws DataException;
	
	/**
	 * <P> This method is used to put items in the cache. </P>
	 * @param key the key which will be used as a search parameter.
	 * @param value the value to be stored in the cache.
	 * @param extInstance extended cache instance name.
	 * @throws DataException
	 */
	public abstract void put(String key, Object value, String extInstance) throws DataException;
	
	/**
	 * <P> This method is used to get items from the cache. </P>
	 * @param key the key used as a search parameter.
	 * @param extInstance extended cache instance name.
	 * @return get the object from the cache with the given key. Returns <code>null</code> if key was not found.
	 * @throws DataException
	 */
	public abstract Object get(String key, String extInstance) throws DataException;
	
	/**
	 * <P> This method is needed to be able to pass the required parameters for the cache implementation to use. </P>
	 * @param paramList
	 */
	public abstract void setParameters(List<?> paramList);
	
	/**
	 * <P> This method can be used to check whether cache value id stored in two </P>
	 * @return mode 0 = false; 1 = true;
	 */
	public abstract int isDualMode();
	
	/**
	 * <P> This method can be used to check whether cache value id stored in two </P>
	 * @param mode 0 = false; 1 = true;
	 */
	public abstract void setDualMode(int mode);
	
	/**
	 * <P>This method is used to check whether CACHE is on test Node using primarily a CACHE server index. </P>
	 * @return -999 if NOT IN TEST MODE; else index of CACHE server to be tested.
	 */
	public abstract Integer getTestPrimaryNode();
	
	/**
	 * <P>This method indicates the CACHE client to test primarily on the selected CACHE Node.</P>
	 * @param node ID of the CACHE server to be tested.
	 */
	public abstract void setTestPrimaryNode(Integer node);
	
	/**
	 * <P>This will reset the Node Status of the affected server to ACTIVE = 1.</P>
	 * @param node the node ID of the cache server.
	 */
	public abstract void resetNodeStatus(Integer node);
	
	/**
	 * <P>This will reset the status of all the CACHE Nodes to ACTIVE = 1.</P>
	 */
	public abstract void resetAllNodeStatus();
	
	/**
	 * <P>This method will return the total number of declared cache servers.</P>
	 * @return
	 */
	public abstract Integer getNumberOfServers();
	
	/**
	 * <P>This method returns the server URL given the server nodeId</P>
	 * @param node 
	 * @return the server URL
	 */
	public abstract String getServerURL(Integer node);
}
