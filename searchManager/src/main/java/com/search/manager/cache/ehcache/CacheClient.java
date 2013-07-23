package com.search.manager.cache.ehcache;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.search.manager.cache.utility.CacheResourceUtil;
import com.search.manager.exception.DataConfigException;
import com.search.manager.exception.DataException;

@Deprecated
public class CacheClient {
	
	public static final Integer TEST_PRIMARY_NODE_DEFAULT 	= -999;
	private CacheClientInterface client          = null;
	private String classSimpleName               = null;
	private List<List<Object>> cacheGenricParam  = null;
	private int strategyUsed                     = -1;
	private int dualStoreMode                    = 0;
	private Integer primaryNode                  = TEST_PRIMARY_NODE_DEFAULT;
	
	private String cacheSvrInstance              = "";
	private static Logger logger = Logger.getLogger(CacheClient.class);
	
	public static CacheClient getInstance() {
		return new CacheClient();
	}
	
	public CacheClient(){
		initializeCacheClient();
	}
	
	public void initializeCacheClient() {
		this.resetParameterMatrix();
		this.classSimpleName = this.getClass().getSimpleName();
		
		String cStrat;
		try {
			cStrat = CacheResourceUtil.getInstance().getCacheStratgy();
			this.strategyUsed = Integer.parseInt(cStrat);
			switch (this.strategyUsed) {
				case 0 :
					client = EhCachEDistributedClient.getInstance();
					break;
			}
		} catch (DataConfigException scex) {
			logger.info(scex.getMessage());
		} catch (NumberFormatException nfex) {
			logger.info(nfex.getMessage());
		}
	}
	
	/**
	 * <P> This method is used to put items in the cache. </P>
	 * @param key the key which will be used as a search parameter.
	 * @param value the value to be stored in the cache.
	 * @throws DataException
	 */
	public void put(String key, Object value) throws DataException {
		this.pushParameterList();
		this.client.setDualMode(this.dualStoreMode);
		this.client.setTestPrimaryNode(this.primaryNode);
		
		switch (this.cacheSvrInstance.length()) {
			case 0 :
			  String nullValue = null;
				this.client.put(key, value, nullValue);
				break;
			default :
				this.client.put(key, value, this.cacheSvrInstance);
				break;
		}
	}
	
	/**
	 * <P> This method is used to get items from the cache. </P>
	 * @param key the key used as a search parameter.
	 * @return get the object from the cache with the given key. Returns <code>null</code> if key was not found.
	 * @throws DataException
	 */
	public Object get(String key) throws DataException {
		this.pushParameterList();
		this.client.setDualMode(this.dualStoreMode);
		this.client.setTestPrimaryNode(this.primaryNode);
		
		switch (this.cacheSvrInstance.length()) {
			case 0 :
			  String nullValue = null;
				return this.client.get(key, nullValue);
			default :
				return this.client.get(key, this.cacheSvrInstance);
		}
	}
	
	/**
	 * <P> This method is used to set the parameter list in the client implementation based on the strategy used. This is called prior
	 * to a 'PUT' or 'GET' operation. <code>Null</code> is pushed when no parameters are needed for the cache strategy. </P>
	 */
	private void pushParameterList() {
		try {
			this.client.setParameters(this.cacheGenricParam.get(this.strategyUsed));
		} catch (Exception ex) {
			this.client.setParameters(null);
			logger.info(ex.getMessage());
		}
	}
	
	/**
	 * <P> This method resets the Parameter List Matrix to an empty list. Make sure to reset before a new 'PUT' or 'GET' operation is called. </P>
	 */
	public void resetParameterMatrix() {
		this.cacheGenricParam = new ArrayList<List<Object>>();
	}

	/**
	 * <P> This method is needed to be able to pass the required parameters for the cache implementation to use. </P>
	 * <P> List - <List 0> = Parameters for the EHCACHE Strategy.
	 * <P> List - <List 1> = Parameters for the MEMCACHE Strategy.
	 * @param paramList the parameter list which contains the parameters needed for a cache implementation.
	 */
	public void addToParameterMatrix(List<Object> paramList) {
		this.cacheGenricParam.add(paramList);
	}
	
	/**
	 * <P> This method can be used to check whether cache value id stored in two </P>
	 * @return mode 0 = false; 1 = true;
	 */
	public int isDualMode() { 
		return this.dualStoreMode; 
	}
	
	/**
	 * <P> This method can be used to check whether cache value id stored in two </P>
	 * @param mode 0 = false; 1 = true;
	 */
	public void setDualMode(int mode) {
		this.dualStoreMode = mode;
	}
	
	/**
	 * <P>This method sets the primary node indicator. DEFAULT = -999</P>
	 * @param primaryNode
	 */
	public void setTestPrimaryNode(Integer primaryNode) {
		this.primaryNode = primaryNode;
	}
	
	/**
	 * <P>This method returns the primary test node indicator. DEFAULT = -999</P>
	 * @return the primary test node indicator. DEFAULT = -999 (NOT using the node in test mode)
	 */
	public Integer getTestPrimaryNode() {
		return this.primaryNode;
	}
	
	/**
	 * <P>This will reset the Node Status of the affected server to ACTIVE = 1.</P>
	 * @param node the node ID of the cache server.
	 */
	public void resetNodeStatus(Integer node) {
		this.client.resetNodeStatus(node);
	}
	
	/**
	 * <P>This will reset the status of all the CACHE Nodes to ACTIVE = 1.</P>
	 */
	public void resetAllNodeStatus() {
		this.client.resetAllNodeStatus();
	}

	/**
	 * @return the cacheSvrInstance
	 */
	public String getCacheSvrInstance() {
		return cacheSvrInstance;
	}

	/**
	 * @param cacheSvrInstance the cacheSvrInstance to set
	 */
	public void setCacheSvrInstance(String cacheSvrInstance) {
		this.cacheSvrInstance = cacheSvrInstance;
	}
	
	/**
	 * <P>This method will return the total number of declared cache servers.</P>
	 * @return
	 */
	public Integer getNumberOfServers() {
		return this.client.getNumberOfServers();
	}
	
	/**
	 * <P>This method returns the server URL given the server nodeId</P>
	 * @param node 
	 * @return the server URL
	 */
	public String getServerURL(Integer node) {
		return this.client.getServerURL(node);
	}
	
	public String getServerStatus() {
		return this.client.getServerStatus();
	}
}
