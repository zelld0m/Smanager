package com.search.manager.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.service.LocalCacheService;

public class PropsUtils {
	
	private static LocalCacheService<CacheModel<?>> localCacheService;
	
	public void setLocalCacheService(LocalCacheService<CacheModel<?>> localCacheService_) {
		localCacheService = localCacheService_;
	}

	private PropsUtils() {}

    public static Properties load(String propsName) throws Exception {
    	Properties props = new Properties();
    	FileInputStream fis = new FileInputStream(new File(propsName));
    	props.load(fis);
    	fis.close();
		return props;
    }

    /**
     * Load a Properties File
     * @param propsFile
     * @return Properties
     * @throws IOException
     */
    public static Properties load(File propsFile) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(propsFile);
        props.load(fis);    
        fis.close();
        return props;
    }
    
    public static Properties getProperties(String propsName){
    	try{
    		return load(getValue(propsName));	
    	}catch (Exception e) {}
    	return null;
    }
    
    public static String getValue(String propsName){
    	try{
			CacheModel<String> model = (CacheModel<String>) localCacheService.getLocalCache(Constants.GLOBAL_INIT);
    		return model.getMap().get(propsName);
    	}catch (Exception e) {}
    	return "";
    }
}
