package com.search.manager.cache.lccache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Deprecated
public class ConcurrentCacheMap<K, V> extends Thread implements ConcurrentMap<K, V>
{
	private ConcurrentLinkedQueue<K> queue;
	private ConcurrentHashMap<K, V> cache;
	private ConcurrentHashMap<K, Long> cacheDates;
	private Long lastSpringCleaning;
	private Long timeToLive;
	private Long springCleaningInterval;
	private Integer size;

	public ConcurrentCacheMap()
	{
		queue = new ConcurrentLinkedQueue<K>();
		cache = new ConcurrentHashMap<K, V>();
		size = 5000;
		lastSpringCleaning = System.currentTimeMillis();
		cacheDates = new ConcurrentHashMap<K, Long>();
		timeToLive = 1000L * 60L * 15L;
		springCleaningInterval = 1000L * 60L;
	}

	public ConcurrentCacheMap(int size)
	{
		queue = new ConcurrentLinkedQueue<K>();
		cache = new ConcurrentHashMap<K, V>();
		this.size = size;
		lastSpringCleaning = System.currentTimeMillis();
		cacheDates = new ConcurrentHashMap<K, Long>();
		timeToLive = 1000L * 60L * 15L;
		springCleaningInterval = 1000L * 60L;
	}

	public ConcurrentCacheMap(int size, long timeToLive)
	{
		queue = new ConcurrentLinkedQueue<K>();
		cache = new ConcurrentHashMap<K, V>();
		this.size = size;
		lastSpringCleaning = System.currentTimeMillis();
		cacheDates = new ConcurrentHashMap<K, Long>();
		this.timeToLive = timeToLive;
		springCleaningInterval = 1000L * 60L;
	}

	public ConcurrentCacheMap(int size, long timeToLive, long springCleaningInterval)
	{
		queue = new ConcurrentLinkedQueue<K>();
		cache = new ConcurrentHashMap<K, V>();
		this.size = size;
		lastSpringCleaning = System.currentTimeMillis();
		cacheDates = new ConcurrentHashMap<K, Long>();
		this.timeToLive = timeToLive;
		this.springCleaningInterval = springCleaningInterval;
	}

	public V get(Object key)
	{
		V result = cache.get(key);
		housekeeping();
		return result;
	}

	public boolean isCached(K key)
	{
		housekeeping();
		return cache.containsKey(key);
	}

	public void clear()
	{
		queue.clear();
		cache.clear();
		cacheDates.clear();
	}

	public Set<K> keySet()
	{
		housekeeping();
		return cache.keySet();
	}

	public Collection<V> values()
	{
		housekeeping();
		return cache.values();
	}

	public Set<Map.Entry<K, V>> entrySet()
	{
		housekeeping();
		return cache.entrySet();
	}

	public V replace(K key, V value)
	{
		housekeeping();
		return cache.replace(key, value);
	}

	public boolean replace(K key, V oldValue, V newValue)
	{
		housekeeping();
		return cache.replace(key, oldValue, newValue);
	}

	public void setMaxSize(Integer size)
	{
		this.size = size;
	}

	public void setTimeToLive(Long timeToLive)
	{
		this.timeToLive = timeToLive;
	}

	public void setSpringCleaningInterval(Long springCleaningInterval)
	{
		this.springCleaningInterval = springCleaningInterval;
	}

	public Integer getMaxSize()
	{
		return size;
	}

	public Long getTimeToLive()
	{
		return timeToLive;
	}

	public boolean containsKey(Object key)
	{
		return cache.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return cache.containsValue(value);
	}

	public boolean isEmpty()
	{
		return cache.isEmpty();
	}

	public int size()
	{
		return cache.size();
	}

	public Long getSpringCleaningInterval()
	{
		return springCleaningInterval;
	}

	public V remove(Object key)
	{
		V result = cache.remove(key);
		queue.remove(key);
		cacheDates.remove(key);
		housekeeping();
		return result;
	}

	public boolean remove(Object key, Object value)
	{
		boolean result = false;
		if (cache.get(key).equals(value))
		{
			result = cache.remove(key, value);
			queue.remove(key);
			cacheDates.remove(key);
		}
		housekeeping();
		return result;
	}

	public V putIfAbsent(K key, V value)
	{
		V result = null;
		if (!cache.containsKey(key))
		{
			cache.put(key, value);
			queue.add(key);
			cacheDates.put(key, System.currentTimeMillis());
		}
		else
		{
			result = cache.get(key);
		}
		housekeeping();
		return result;
	}

	public V put(K key, V object)
	{
		V result = null;
		if (!cache.containsKey(key))
		{
			cache.put((K) key, (V) object);
			queue.add((K) key);
			cacheDates.put((K) key, System.currentTimeMillis());
		}
		else
		{
			result = cache.get(key);
			cache.replace((K) key, (V) object);
			cacheDates.replace((K) key, System.currentTimeMillis());
		}
		housekeeping();
		return result;
	}

	@SuppressWarnings("unchecked")
	public void putAll(Map<? extends K, ? extends V> map)
	{
		housekeeping();
		for (Object o : map.entrySet())
		{
			Map.Entry me = (Map.Entry) o;
			put((K) me.getKey(), (V) me.getValue());
		}
	}

	public void housekeeping()
	{
		Thread t = new Thread();
		t.start();
	}

	public void run()
	{
		while (queue.size() > size)
		{
			Object key = queue.poll();
			if (key != null)
			{
				cache.remove(key);
				queue.remove(key);
				cacheDates.remove(key);
			}
		}
		Long now = System.currentTimeMillis();
		if (lastSpringCleaning + springCleaningInterval < now)
		{
			springCleaning(now);
		}
	}

	public void springCleaning(Long now)
	{
		Object key;
		for (Map.Entry<? extends K, Long> me : cacheDates.entrySet())
		{
			if (me.getValue() + timeToLive < now)
			{
				key = me.getKey();
				cache.remove(key);
				queue.remove(key);
				cacheDates.remove(key);
			}
		}
	}
}
