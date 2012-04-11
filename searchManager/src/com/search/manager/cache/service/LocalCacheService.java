package com.search.manager.cache.service;

import com.search.manager.cache.ehcache.CacheClient;
import com.search.manager.cache.lccache.ConcurrentCacheMap;
import com.search.manager.cache.lccache.LocalCache;
import com.search.manager.cache.model.CacheModel;

public class LocalCacheService<E extends CacheModel<?>>{
	
	protected static CacheClient cacheClient		= null;
	protected String className						= null;
	protected StringBuilder genKey					= null;
	
	public E getLocalCache(String key) {
		LocalCache localCache = LocalCache.getInstance();
		ConcurrentCacheMap<String, Object> cache = localCache.getCache();
		return (E) cache.get(key);
	}

	public void putLocalCache(String key, Object object) {
		LocalCache localCache = LocalCache.getInstance();
		ConcurrentCacheMap<String, Object> cache = localCache.getCache();
		cache.put(key, object);
	}
	
	public void resetLocalCache(String key){
		LocalCache localCache = LocalCache.getInstance();
		ConcurrentCacheMap<String, Object> cache = localCache.getCache();
		cache.remove(key);
	}
}
