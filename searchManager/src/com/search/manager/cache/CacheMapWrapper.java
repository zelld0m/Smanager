package com.search.manager.cache;

/**
 * Property of OSRP
 * 
 * @author M ILAGAN JR
 * @version 1.0
 * Date Created: April 27, 2011
 * Description:
 *		<P> This is the cache wrapper class for storing Ma. </P>
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

import java.util.Map;

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
