package com.search.manager.cache.ehcache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.search.manager.cache.utility.CacheResourceUtil;
import com.search.manager.cache.utility.EHcacHEResourceUtil;
import com.search.manager.exception.DataConfigException;
import com.search.manager.exception.DataException;

public class EhCachEDistributedClient implements CacheClientInterface {
	
	public static List<Object> PARAM_MIME_TEXT_PLAIN	= new ArrayList<Object>();
	public static List<Object> PARAM_MIME_APP_XJAVA_SER	= new ArrayList<Object>();
	public static List<Object> PARAM_MIME_TEXT_XML		= new ArrayList<Object>();
	
	public static final Integer EH_MIME_TEXT_PLAIN		= 0;
	public static final Integer EH_MIME_APP_XJAVA_SER	= 1;
	public static final Integer EH_MIME_TEXT_XML		= 2;
	
	private static final String MIME_TEXT_PLAIN				= "text/plain";
	private static final String MIME_APP_XJAVA_SER			= "application/x-java-serialized-object";
	private static final String MIME_TEXT_XML				= "text/xml";
	
	private static final String OPN_PUT						= "PUT";
	private static final String OPN_GET						= "GET";
	
	private static int totalNumberOfCacheServers			= -1;
	private static boolean cacheServersDown             	= false;
	private static String[] cacheServers            		= null;
	private static int[] cacheServerStatus					= null;
	private static String[] persistentCacheServers      	= null;
	private static Map<String, String[]> extfodServers 		= null;
	
	private EHcacHEResourceUtil eRsrcUtil					= null;
	private String ehMimeType								= null;
	private String keyInCache								= null;
	private int serverSuffix								= -1;	
	private List<?> paramList								= null;
	private int dualMode									= 0;
	private Integer testPrimaryNode							= -999;
	private String extendedInstance							= "";
	
	private String utilClassName							= null;		
	private static Logger logger = Logger.getLogger(EhCachEDistributedClient.class);
	
	public EhCachEDistributedClient(){
		initializeCacheClient();
	}
	
	public void initialize() {
		totalNumberOfCacheServers=-1;
		initializeCacheClient(); 
	}
	public void initializeCacheClient() {
		this.utilClassName = this.getClass().getSimpleName();		
		eRsrcUtil = EHcacHEResourceUtil.newInstance();
		this.initializeCacheClientParams();
		if (totalNumberOfCacheServers < 0) {
			try {
				Integer total =  eRsrcUtil.getTotalNumberOfCacheServers();
				totalNumberOfCacheServers = total.intValue();
				if (totalNumberOfCacheServers <= 0)
					cacheServersDown = true;
				else
					this.InitializeCacheServers();
			} catch (DataConfigException scex) {
				logger.info(scex.getMessage());
			}
		}
	}
	
	private void initializeCacheClientParams() { 
		if (PARAM_MIME_TEXT_PLAIN.isEmpty()) {
			PARAM_MIME_TEXT_PLAIN.add(EH_MIME_TEXT_PLAIN);
			PARAM_MIME_APP_XJAVA_SER.add(EH_MIME_APP_XJAVA_SER);
			PARAM_MIME_TEXT_XML.add(EH_MIME_TEXT_XML);
		}
	}
	
	private void InitializeCacheServers() {
		if (cacheServers == null || cacheServerStatus == null || 
				persistentCacheServers == null || extfodServers == null) {
			
			cacheServers = new String[totalNumberOfCacheServers];
			cacheServerStatus = new int[totalNumberOfCacheServers];
			persistentCacheServers = new String[totalNumberOfCacheServers];
			for (int i = 1; i <= totalNumberOfCacheServers; i++) {
				try {
					cacheServers[i - 1] 			= this.buildCacheServerBaseURL(EHcacHEResourceUtil.EH_CACHE_NAME, i);
					cacheServerStatus[i - 1]		= 1;
					persistentCacheServers[i - 1]	= this.buildCacheServerBaseURL(EHcacHEResourceUtil.EH_CACHE_NAME_PXT, i);
				} catch (DataConfigException scex) {
					logger.info(scex.getMessage());
				}
			}
			String extfod = null;
			try {
				extfod 				= eRsrcUtil.getCacheExtendedInstances();
				extfodServers 		= new HashMap<String, String[]>();
				String[] instances 	= extfod.split(",");				
				for (String instance : instances) {
					int size = instance.trim().length();
					switch (size) {
						case 0 :
							break;
						default :
							String[] servers = new String[totalNumberOfCacheServers];
							for (int i = 1; i <= totalNumberOfCacheServers; i++) {
								StringBuilder key = new StringBuilder(EHcacHEResourceUtil.EH_CACHE_NAME);
								key.append(instance).append(".");
								servers[i - 1] = this.buildCacheServerBaseURL(key.toString(), i);
							}
							extfodServers.put(instance, servers);
							break;
					}
				}
			} catch (DataConfigException scex) {
				logger.info(scex.getMessage());
			}
		}
	}
	
	private String buildCacheServerBaseURL(String cacheInstance, int i) throws DataConfigException {
		StringBuilder strB = new StringBuilder(EHcacHEResourceUtil.EH_SERVER);
		strB.append(i);
		String host = eRsrcUtil.getValue(strB.toString());
		
		strB = new StringBuilder(EHcacHEResourceUtil.EH_PORT);
		strB.append(i);
		String port = eRsrcUtil.getValue(strB.toString());
		
		strB = new StringBuilder(cacheInstance);
		strB.append(i);
		String cacheName = eRsrcUtil.getValue(strB.toString());
		
		strB = new StringBuilder("http://");
		strB.append(host);
		strB.append(":");
		strB.append(port);
		strB.append("/ehcache/rest/");
		strB.append(cacheName);
		strB.append("/");
		
		return strB.toString();
	}
	
	/**
	 * <P> This method returns an instance of the EhCachEDistributedClient class. </P>
	 * @return EhCachEDistributedClient object.
	 */
	public static EhCachEDistributedClient getInstance() {
		return new EhCachEDistributedClient();
	}
	
	/**
	 * <P> This method returns the cache server where the value of the key is located. This is done through a 
	 * hashing function. </P>
	 * @param key key to be searched
	 * @return cache server where the 
	 */
	private String nominateCacheServer(Object key, int useDualMode) {
		String server = null;
		int cacheServerIndex = -999;
		
		int hash = Math.abs(key.hashCode());
		try {
			this.testPrimaryNode.toString();
		} catch(NullPointerException npex) {
			this.testPrimaryNode = -999;
		}
		switch (this.testPrimaryNode) {
			case -999:
				cacheServerIndex = hash % cacheServers.length;
				break;
			default:
				cacheServerIndex = this.getTestPrimaryNode() - 1;
				break;
		}
		switch (useDualMode) {
			case 1 :
				cacheServerIndex++;
		}
		server = this.getNextAvailableServer(cacheServerIndex);
		
		return server;
	}
	
	private String getNextAvailableServer(int csvrIDX) {
		String nextServer = "";
		int cacheStat = 0;
		
		try {
			this.testPrimaryNode.toString();
		} catch(NullPointerException npex) {
			this.testPrimaryNode = -999;
		}
		switch (this.testPrimaryNode) {
			case -999 :
				try {
					cacheStat = cacheServerStatus[csvrIDX];
				} catch (IndexOutOfBoundsException iobex) {
					csvrIDX = 0;
					cacheStat = cacheServerStatus[csvrIDX];
				}
				
				if (cacheStat == 0) {
					csvrIDX = 0;
					while (csvrIDX < totalNumberOfCacheServers) {
						if (cacheServerStatus[csvrIDX] == 1)
							break;
						csvrIDX++;
					}
				}
				break;
			default :
				break;
		}
		
		// Check against special BASE Persistence
		Integer result = csvrIDX - PERSISTENT_CACHE_NODE_BASE;
			// Is in Normal Mode
		if (result < 0) {
			if (csvrIDX < totalNumberOfCacheServers) {
				this.serverSuffix = csvrIDX + 1;
				switch (this.isPersistentCache()) {
					case 0 :
						nextServer = cacheServers[csvrIDX];
						break;
					case 1 :
						nextServer = this.checkExtendedInstance(csvrIDX);
						switch (nextServer.length()) {
							case 0 :
								nextServer = persistentCacheServers[csvrIDX];
								break;
							default :
								break;
						}
						break;
				}
			}
		} else {
			// Is in Health Check Mode
			nextServer = this.checkExtendedInstance(result);
			switch (nextServer.length()) {
				case 0 :
					nextServer = persistentCacheServers[result];
					break;
				default :
					break;
			}
			this.serverSuffix = result + 1;
		}
		
		return nextServer;
	}
	
	private String checkExtendedInstance(Integer serverIdx) {
		String result 	= null;
		int extISize	= this.extendedInstance.length();
		switch (extISize) {
			case 0 	:
				result = "";
				break;
			default :
				try {
					String[] servers = extfodServers.get(this.extendedInstance.trim());
					result = servers[serverIdx];
				} catch (NullPointerException npex) {
					result = "";
				}
				break;
		}
		
		return result;
	}
	
	private int isPersistentCache() {
		try {
			String value = eRsrcUtil.getValue(this.keyInCache.toLowerCase());
			int intValue = Integer.parseInt(value);
			return intValue;
		} catch (NumberFormatException nfex) {
			// DO NOTHING
		} catch (DataConfigException dcex) {
			String tempKey = this.keyInCache;
			int lastDelimIdx = -1;
			do {
				String wildCard = null;
				lastDelimIdx = tempKey.lastIndexOf(".");
				switch (lastDelimIdx) {
					case -1 :
					case 0 :
						break;
					default :
						wildCard = tempKey.substring(0, lastDelimIdx);
						try {
							String value = eRsrcUtil.getValue(wildCard.toLowerCase());
							int intValue = Integer.parseInt(value);
							return intValue;
						} catch (NumberFormatException nfex) {
							// DO NOTHING
						} catch (DataConfigException dconex) {
							// DO NOTHING
						} catch (NullPointerException npex) {
							// DO NOTHING
						}
					break;
				}
				tempKey = wildCard;
			} while (lastDelimIdx >= 0);
		} catch (NullPointerException npex) {
			// DO NOTHING
		}
		
		return 0;
	}
	
	/**
	 * <P> This method puts the object in the distributed cache server. </P>
	 * @param key the key for the value pair
	 * @param value the value to be cached
	 * @param ehMimeType EH_MIME_TEXT_PLAIN, EH_MIME_APP_XJAVA_SER
	 * @throws DataException
	 */
	public void put(String key, Object value, Integer ehMimeType) throws DataException {
		HttpURLConnection connection	= null;
        OutputStream os 				= null;
        int hasException				= 0;
        int doCache						= 1;
        
        switch (CacheResourceUtil.getCacheEnabled()) {
			case 1:
				doCache = 1;
				break;
			case 0 :
				doCache = this.isKeyExempted(key);
				break;
        }
        
        switch (doCache) {
        	case 1 :
        		if (!cacheServersDown) {
                	int idx = 1;
                	int runs = 1;
                	switch (this.dualMode) {
                		case 1 :
                			runs++;
                			break;
                	}
                	while (idx <=  runs) {
        		        this.keyInCache = key;
        		        StringBuilder server = new StringBuilder(this.nominateCacheServer(key, idx - 1));
        		        server.append(key);
        		        try {
        		        	int theMimeType = ehMimeType;
        		        	connection = this.getCacheServerConnection(server.toString(), ehMimeType, OPN_PUT);	        	
        	
        			        byte[] sampleBytes = null;
        			        switch (theMimeType) {
        			        	case 0 :
        			        	case 2 :
        			        		String strValue = (String) value;
        			        		sampleBytes = strValue.getBytes();
        			        		break;
        			        	case 1 :
        			        		ByteArrayOutputStream byteS = new ByteArrayOutputStream();
        			        		ObjectOutputStream outputS = new ObjectOutputStream(byteS);
        			        		outputS.writeObject(value);
        			        		outputS.flush();
        			        		outputS.close();
        			        		byteS.close();
        			        		sampleBytes = byteS.toByteArray();
        			        		break;
        			        }
        			        	
        			        os = connection.getOutputStream();
        			        os.write(sampleBytes, 0, sampleBytes.length);
        			        os.flush();
        		        } catch (NullPointerException npex) {
        		        	StringBuilder strMsg = new StringBuilder(" Cache Connection Exception");
        		        	strMsg.append(" | ");
        		        	strMsg.append(server).append(" | ");
        		        	strMsg.append(npex.getMessage());
        		        	logger.info(strMsg);
        		        	hasException = 1;
        		        } catch (Exception ex) {
        		        	StringBuilder strMsg = new StringBuilder(" Cache Exception");
        		        	strMsg.append(" | ");
        		        	strMsg.append(server);
        		        	logger.info(strMsg);
        		        	hasException = 1;
        		        } finally {
        		        	try {
        		        		StringBuilder strMsg = new StringBuilder(" Creating Entry");
        			        	strMsg.append(" | ");
        			        	strMsg.append(connection.getURL()).append(" | ");
        			        	strMsg.append(connection.getResponseCode()).append(" | ");
        			        	strMsg.append(connection.getResponseMessage());
        			        	logger.info(strMsg);
        			        	strMsg = null;
        		        		connection.disconnect();
        		        	} catch (Exception npex) {}
        		        }
        		       
        		        switch (hasException) {
        		        	case 1 :
        		        		switch (this.testPrimaryNode) {
        		        			case -999 :
        		        				break;
        		        			default :
        		    		        	throw new DataException(this.getPrimaryNodeErrorMessage(server).toString());
        		        		}
        		        		break;
        		        }
        		        idx++;
                	}
                } else {
        			switch (this.testPrimaryNode) {
        				case -999 :
        					break;
        				default :
        		        	throw new DataException(this.getPrimaryNodeErrorMessage(new StringBuilder("ALL CACHE")).toString());
        			}
                }
        		break;
        }
    }
	
	private StringBuilder buildLogMessage(String operation, String key) {
		StringBuilder logMsg = new StringBuilder(" ");
		logMsg.append(operation);
		logMsg.append(" |");
		logMsg.append(key);
		
		return logMsg;
	}
	
	/**
	 * <P> This method is used for getting cache statistics. </P>
	 * @param serverURL the restful server URL
	 * @throws DataException
	 */
	public void getCacheStatistics(String serverURL) throws DataException {
		HttpURLConnection connection	= null;
		InputStream is 					= null;
		
		int result = -1;
        try {
        	/*connection = this.getCacheServerConnection(serverURL, -1, OPN_PUT);
        	int status = connection.getResponseCode();
        	connection.disconnect();*/
        	
        	serverURL = this.removeEndSlashFromURL(serverURL);        	
        	connection = this.getCacheServerConnection(serverURL, -1, OPN_GET);        	
        	is = connection.getInputStream();
            byte[] response2 = new byte[4096];
            result = is.read(response2);
            while (result != -1) {
            	System.err.write(response2, 0, result);
                result = is.read(response2);
            }
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			throw new DataException(ex.getMessage(), ex);
		} finally {
			try {
				is.close();
				System.out.println("reading cache: " + connection.getResponseCode()
	                    + " " + connection.getResponseMessage());
				connection.disconnect();
			} catch (Exception npex) {}
		}
	}
	
	private String removeEndSlashFromURL(String serverURL) {
		if (serverURL != null) {
			int urlSize = serverURL.length();
			if ("//".equalsIgnoreCase(serverURL.substring((urlSize - 1), urlSize))) {
				serverURL = serverURL.substring(0, urlSize - 1);
			}
		}
		
		return serverURL;
	}
	
	/**
	 * <P> This method returns the object searched from the cache. If value is not found then the <code>null</code> is returned. </P>
	 * @param key the key to be searched
	 * @param ehMimeType returned value Mimetype. EH_MIME_TEXT_PLAIN, EH_MIME_APP_XJAVA_SER
	 * @return object searched from the cache, ff value is not found then the <code>null</code> is returned.
	 * @throws DataException
	 */
	public Object get(String key, Integer ehMimeType) throws DataException {
		Object returnedByCache			= null;
		HttpURLConnection connection	= null;
		InputStream is 					= null;
		int hasException				= 0;
		int doCache						= 1;
        
        switch (CacheResourceUtil.getCacheEnabled()) {
			case 1:
				doCache = 1;
				break;
			case 0 :
				doCache = this.isKeyExempted(key);
				break;
        }
		
		switch (doCache) {
			case 1:
				if (!cacheServersDown) {
					int idx = 1;
		        	int runs = 1;
		        	switch (this.dualMode) {
		        		case 1 :
		        			runs++;
		        			break;
		        	}
		        	while (returnedByCache == null && idx <=  runs) {
						int result = -1;
						int theMimeType = ehMimeType.intValue();
						this.keyInCache = key;
				        StringBuilder server = new StringBuilder(this.nominateCacheServer(key, idx - 1));
				        server.append(key);
				        try {
				        	connection = this.getCacheServerConnection(server.toString(), -1, OPN_GET);
			
					        is = connection.getInputStream();
					        switch (theMimeType) {
					           	case 0:
					           	case 2:
					           		byte[] response2 = new byte[8192];
					    	        result = is.read(response2);
					    	        while (result != -1) {
					    	        	String value = new String(response2);
					    	        	returnedByCache = value.trim();
					    	        	
					    	        	result = is.read(response2);
					    	        }
					           		break;
					           	case 1:
					           		BufferedInputStream bis = new BufferedInputStream(is);
					           		ObjectInputStream objIS = new ObjectInputStream(bis);
					           		returnedByCache = objIS.readObject();
					           		objIS.close();
					           		bis.close();
					          		break;
					           }	
				        } catch (FileNotFoundException fnfex) {
				        	logger.info(fnfex.getMessage());
						} catch (Exception ex) {
							StringBuilder strMsg = new StringBuilder(" Cache Exception");
				        	strMsg.append(" | ");
				        	strMsg.append(server);
				        	logger.info(strMsg);
				        	hasException = 1;
						} finally {
							try {
								is.close();
								StringBuilder strMsg = new StringBuilder(" Read Entry");
					        	strMsg.append(" | ");
					        	strMsg.append(connection.getURL()).append(" | ");
					        	strMsg.append(connection.getResponseCode()).append(" | ");
					        	strMsg.append(connection.getResponseMessage());
					        	logger.info(strMsg);
					        	strMsg = null;
								connection.disconnect();
							} catch (Exception npex) {}
						}
						switch (hasException) {
				        	case 1 :
				        		switch (this.testPrimaryNode) {
				        			case -999 :
				        				break;
				        			default :
				    		        	throw new DataException(this.getPrimaryNodeErrorMessage(server).toString());
				        		}
				        		break;
				        }
						idx++;
		        	}
				} else {
					switch (this.testPrimaryNode) {
						case -999 :
							break;
						default :
				        	throw new DataException(this.getPrimaryNodeErrorMessage(new StringBuilder("ALL CACHE")).toString());
					}
				}
				break;
			case 0 :
				returnedByCache = null;
				break;
		}
		
		return returnedByCache;
	}
	
	private StringBuilder getPrimaryNodeErrorMessage(StringBuilder server) {		
		StringBuilder strMsg = new StringBuilder(" Cache Connection Exception");
    	strMsg.append(" | ");
    	strMsg.append(server).append(" | ");
    	strMsg.append(this.testPrimaryNode);
    	
    	return strMsg;
	}
	
	/**
	 * <P> This method returns the cache server connection via http restful URL. All other cache servers are scanned if current server 
	 * is not found. Parameter cacheServersDown = true when all servers are down, thus skipping caches searches.</P>
	 * @param serverURL string server URL. ex. http://localhost:port/ehcache/rest/cache/key
	 * @param ehMimeType Request Property Content Type: EH_MIME_TEXT_PLAIN, EH_MIME_APP_XJAVA_SER (int)
	 * @param operation RESTful operation. OPN_PUT, OPN_GET.
	 * @return HttpURLConnection connection object.
	 * @throws Exception 
	 */
	private HttpURLConnection getCacheServerConnection(String serverURL, int ehMimeType, String operation) {
		URL url							= null;
        HttpURLConnection connection	= null;
        boolean scanCacheServers        = true;
        boolean useConnection			= true;
        int scanIndex					= -1;
        
        while (connection == null && scanCacheServers) {
	        try {
	        	if (useConnection) {
			        url = new URL(serverURL);
			        connection = (HttpURLConnection) url.openConnection();                
			        if (OPN_PUT.equals(operation) && ehMimeType >= 0) {
				        connection.setDoOutput(true);
				        switch (ehMimeType) {
				        	case 0:
				                connection.setRequestProperty("Content-Type", MIME_TEXT_PLAIN);
				        		break;
				        	case 1:
				        		connection.setRequestProperty("Content-Type", MIME_APP_XJAVA_SER);
				        		break;
				        	case 2:
				        		connection.setRequestProperty("Content-Type", MIME_TEXT_XML);
				        		break;
				        }
			        }
			        connection.setRequestMethod(operation);
			        connection.connect();
	        	} else
	        		throw new Exception();
	        } catch (Exception ex) {
	        	// set connection to null
	        	try {
	        		connection.disconnect();
	        	} catch (Exception connEx) {
	        	} finally {
	        		connection = null;
	        	}
	        	
	        	try {
	        		this.testPrimaryNode.toString();
	        	} catch (NullPointerException npex) {
	        		this.testPrimaryNode = -999;
	        	}
	        	switch (this.testPrimaryNode) {
	        		case -999:
	        			// check is scan was started, if started then disable cache server status
	    	        	if (scanIndex >= 0)
	    	        		cacheServerStatus[scanIndex] = 0;
	    	        	else
	    	        		cacheServerStatus[this.serverSuffix - 1] = 0;
	    	        	// increment scan index
	    	        	scanIndex++;
	    	        	if (scanIndex == totalNumberOfCacheServers) {
	    	        		scanCacheServers = false;
	    	        		cacheServersDown = true;
	    	        		StringBuilder strMsg = new StringBuilder(" Cache Exception");
	    		        	strMsg.append(" | ");
	    		        	strMsg.append("All Cache Servers DOWN!!!");
	    		        	logger.info(strMsg);
	    	        	} else  {
	    	        		StringBuilder strMsg = new StringBuilder(" Cache Exception");
	    		        	strMsg.append(" | ").append(serverURL);
	    		        	logger.info(strMsg);
	    	        		
	    		        	int serverStatus = cacheServerStatus[scanIndex];
	    		        	if (serverStatus == 1) {
	    		        		switch (this.isPersistentCache()) {
	    						case 0 :
	    							serverURL = cacheServers[scanIndex];
	    							break;
	    						case 1 :
	    							serverURL = this.checkExtendedInstance(scanIndex);
	    							switch (serverURL.length()) {
	    								case 0 :
	    									serverURL = persistentCacheServers[scanIndex];
	    									break;
	    								default :
	    									break;
	    							}
	    							break;
	    		        		}
	    		        		StringBuilder svr = new StringBuilder(serverURL);
	    		        		svr.append(this.keyInCache);
	    		        		serverURL = svr.toString();
	    		        		useConnection = true;
	    		        	} else
	    		        		useConnection = false;
	    	        	}
	        			break;
	        		default:
	        			scanCacheServers = false;
	        			break;
	        	}
	        }
        }
        
        return connection;
	}
	
	/**
	 * @return the serverSuffix
	 */
	public int getServerSuffix() {
		return serverSuffix;
	}

	/**
	 * @param ehMimeType the ehMimeType to set
	 */
	public void setEhMimeType(String ehMimeType) {
		this.ehMimeType = ehMimeType;
	}

	/**
	 * @return the ehMimeType
	 */
	public String getEhMimeType() {
		return ehMimeType;
	}

	/**
	 * @param cacheServersDown the cacheServersDown to set
	 */
	public static void setCacheServersDown(boolean cacheServersDown) {
		EhCachEDistributedClient.cacheServersDown = cacheServersDown;
	}

	/**
	 * @return the cacheServersDown
	 */
	public static boolean isCacheServersDown() {
		return cacheServersDown;
	}

	/**
	 * @param cacheServerStatus the cacheServerStatus to set
	 */
	public static void setCacheServerStatus(int[] cacheServerStatus) {
		EhCachEDistributedClient.cacheServerStatus = cacheServerStatus;
	}

	/**
	 * @return the cacheServerStatus
	 */
	public static int[] getCacheServerStatus() {
		return cacheServerStatus;
	}
	
	/**
	 * <P> This method is used to put items in the cache. </P>
	 * @param key the key which will be used as a search parameter.
	 * @param value the value to be stored in the cache.
	 * @throws DataException
	 */
	public void put(String key, Object value) throws DataException {
		Integer mimeType = (Integer) this.paramList.get(0);
		this.put(key, value, mimeType);
	}
	
	/**
	 * <P> This method is used to get items from the cache. </P>
	 * @param key the key used as a search parameter.
	 * @return get the object from the cache with the given key. Returns <code>null</code> if key was not found.
	 * @throws DataException
	 */
	public Object get(String key) throws DataException {
		Integer mimeType = (Integer) this.paramList.get(0);
		return this.get(key, mimeType);
	}
	
	/**
	 * <P> This method is used to put items in the cache. </P>
	 * @param key the key which will be used as a search parameter.
	 * @param value the value to be stored in the cache.
	 * @param extInstance extended cache instance name.
	 * @throws DataException
	 */
	public void put(String key, Object value, String extInstance) throws DataException {
		try {
			this.extendedInstance = extInstance.toLowerCase();
		} catch (NullPointerException npex) {
			this.extendedInstance = "";
		}
		this.put(key, value);
	}
	
	/**
	 * <P> This method is used to get items from the cache. </P>
	 * @param key the key used as a search parameter.
	 * @param extInstance extended cache instance name.
	 * @return get the object from the cache with the given key. Returns <code>null</code> if key was not found.
	 * @throws DataException
	 */
	public Object get(String key, String extInstance) throws DataException {
		try {
			this.extendedInstance = extInstance.toLowerCase();
		} catch (NullPointerException npex) {
			this.extendedInstance = "";
		}
		return this.get(key);
	}
	
	/**
	 * <P> This method is needed to be able to pass the required parameters for the cache implementation to use. </P>
	 * <P> For the 'PUT' & 'GET' operations, param is: <Integer ehMimeType> </P>
	 * @param paramList the parameter list
	 */
	public void setParameters(List<?> paramList) {
		this.paramList = paramList;
	}
	
	/**
	 * <P> This method can be used to check whether the cache value is stored in two servers</P>
	 * @return 0 = false; 1 = true;
	 */
	@Override
	public int isDualMode() {
		return this.dualMode;
	}
	
	/**
	 * <P> This method can be used to check whether cache value id stored in two </P>
	 * @param mode 0 = false; 1 = true;
	 */
	@Override
	public void setDualMode(int mode) {
		this.dualMode = mode;
	}
	
	/**
	 * <P>This method is used to check whether CACHE is on test Node using primarily a CACHE server index. </P>
	 * @return -999 if NOT IN TEST MODE; else index of CACHE server to be tested.
	 */
	@Override
	public Integer getTestPrimaryNode() {
		return this.testPrimaryNode;
	}
	
	/**
	 * <P>This method indicates the CACHE client to test primarily on the selected CACHE Node.</P>
	 * @param node ID of the CACHE server to be tested.
	 */
	@Override
	public void setTestPrimaryNode(Integer node) {
		this.testPrimaryNode = node;
	}
	
	/**
	 * <P>This will reset the Node Status of the affected server to ACTIVE = 1.</P>
	 * @param node the node ID of the cache server.
	 */
	public void resetNodeStatus(Integer node) {
		if (cacheServerStatus != null) {
			try {
				cacheServerStatus[node - 1] = 1;
			} catch (IndexOutOfBoundsException ioobex) {
				cacheServerStatus[node - CacheClientInterface.PERSISTENT_CACHE_NODE_BASE - 1] = 1;
			}
		} else {
			cacheServers = null;
			cacheServerStatus = null;
			persistentCacheServers = null;
			this.InitializeCacheServers();
		}
	}
	
	/**
	 * <P>This will reset the status of all the CACHE Nodes to ACTIVE = 1.</P>
	 */
	public void resetAllNodeStatus() {
		cacheServers = null;
		cacheServerStatus = null;
		persistentCacheServers = null;
		this.InitializeCacheServers();
	}
	
	/**
	 * <P>This method will return the total number of declared cache servers.</P>
	 * @return
	 */
	public Integer getNumberOfServers() {
		try {
			return this.eRsrcUtil.getTotalNumberOfCacheServers();
		} catch (DataConfigException dcex) {
			logger.info(dcex.getMessage());
			dcex.printStackTrace(System.err);
		}
		
		return 0;
	}
	
	/**
	 * <P>This method returns the server URL given the server nodeId</P>
	 * @param node 
	 * @return the server URL
	 */
	public String getServerURL(Integer node) {
		try {
			return cacheServers[node - 1];
		} catch (NullPointerException npex) {
			return "";
		} catch (IndexOutOfBoundsException ioobex) {
			try {
				return persistentCacheServers[node - CacheClientInterface.PERSISTENT_CACHE_NODE_BASE - 1];
			} catch (NullPointerException npex) {
				return "";
			} catch (IndexOutOfBoundsException pxtioobex) {
				return "";
			}
		}
	}
	
	public int isKeyExempted(String key) {
		int result = 0;
		
		int size = CacheResourceUtil.getCacheExemptions();
		for (int i = 1; i <= size; i++) {
		  try {
        String item = CacheResourceUtil.getExemptedItem(i);
        if (key.indexOf(item) >=0 ) {
          result = 1;
          break;
        } else
          result = 0;
      } catch (Exception ex) {
    	  logger.info(ex.getMessage());
        result = 0;
      }
		}
		
		return result;
	}
}
