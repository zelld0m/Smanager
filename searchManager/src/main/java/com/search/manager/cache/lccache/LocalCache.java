package com.search.manager.cache.lccache;

@Deprecated
public class LocalCache{
	private static LocalCache instance = new LocalCache();
	private final ConcurrentCacheMap<String, Object> cache;

	public static LocalCache getInstance(){
		return instance;
	}

	private LocalCache(){
		cache = new ConcurrentCacheMap<String, Object>(1000, 1000L * 60L * 15L, 1000L * 120L);
	}

	public ConcurrentCacheMap<String, Object> getCache(){
		return cache;
	}
}
