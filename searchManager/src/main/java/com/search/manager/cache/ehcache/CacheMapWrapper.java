package com.search.manager.cache.ehcache;

import java.util.Map;

@Deprecated
public class CacheMapWrapper implements java.io.Serializable {
  
  @SuppressWarnings("unchecked")
  private Map cacheHashtable;
  
  /**
   * @return the cacheHashtable
   */
  @SuppressWarnings("unchecked")
  public Map getCacheHashtable() {
    return cacheHashtable;
  }
  
  /**
   * @param cacheHashtable the cacheHashtable to set
   */
  @SuppressWarnings("unchecked")
  public void setCacheHashtable(Map cacheHashtable) {
    this.cacheHashtable = cacheHashtable;
  }
  
  private static final long serialVersionUID = 8947944656264230039L;
}
