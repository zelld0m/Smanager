package com.search.manager.utility;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.service.LocalCacheService;

public class AppInitLoader implements ApplicationContextAware, ServletContextAware {
	
	private static Logger logger = Logger.getLogger(AppInitLoader.class);
	private static ServletContext CTX_WEB;
	private static ApplicationContext CTX_APP;
	
	public static final String GLOBAL_INIT = "GLOBAL_INIT_PROPERTIES";
	private static LocalCacheService<CacheModel<?>> localCacheService;
	
	public void setLocalCacheService(LocalCacheService<CacheModel<?>> localCacheService_) {
		localCacheService = localCacheService_;
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		if (CTX_APP == null) {
			CTX_APP = context;
		}
	}

	@Override
	public void setServletContext(ServletContext servletCtx) {
		if (CTX_WEB == null) {
			CTX_WEB = servletCtx;
		}
	}
	
	public void run() throws Exception {
		String globalInitProp = CTX_WEB.getInitParameter(GLOBAL_INIT);
		String[] props = globalInitProp.split(","); test
		
		logger.info(">>>> LOADING APP-GLOBAL INIT <<<<");
		
		Map<String,String> map = new HashMap<String,String>();
		for(String prop: props) {
			prop = prop.trim();

			Properties rb = null;
			
			if (!StringUtil.isBlank(prop)) {
				
				rb = PropsUtils.load(prop);
		
				Enumeration<Object> enum$ = rb.keys();
				while(enum$.hasMoreElements()) {
					String key = (String) enum$.nextElement();
					String value = rb.getProperty(key);
					key = key.replace(".", "_");
					logger.info("Setting web-application-global value [key:"+key+", value:"+value+"]");
					map.put(key, value);	
				}
			}
		}
		
		CacheModel<String> model = new CacheModel<String>();
		model.setMap(map);
		localCacheService.putLocalCache(GLOBAL_INIT, model);
	}
}
