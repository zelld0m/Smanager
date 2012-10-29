package com.search.manager.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Service;

import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.SortType;

@Service(value = "enumUtilityService")
@RemoteProxy(
		name = "EnumUtilityServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "enumUtilityService")
)
public class EnumUtilityService {

	private static final Logger logger = Logger.getLogger(EnumUtilityService.class);

	@RemoteMethod
	public static Map<String, String> getSortOrderList(){
		Map<String, String> sortOrderList = new LinkedHashMap<String, String>();

		for (SortType st: SortType.values()) {
			sortOrderList.put(st.name(), st.getDisplayText());
		}

		return sortOrderList;
	}
	
	@RemoteMethod
	public static String getRuleEntity(Integer code){
		return RuleEntity.getValue(code);
	}
	
	@RemoteMethod
	public static Map<Integer, String> getRuleEntityList(){
		Map<Integer, String> entityList = new LinkedHashMap<Integer, String>();

		for (RuleEntity re: RuleEntity.values()) {
			entityList.put(re.getCode(), RuleEntity.getValue(re.getCode()));
		}

		return entityList;
	}
}