package com.search.manager.service;

import org.directwebremoting.annotations.RemoteMethod;

import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.xml.file.RuleXmlUtil;

public abstract class RuleService {

	public abstract RuleEntity getRuleEntity();	
	
	@RemoteMethod
	public boolean restoreRule(String ruleId, int version){
		String store = UtilityService.getStoreId();
		RuleXml ruleXml = RuleVersionUtil.getRuleVersion(store, getRuleEntity(), ruleId, version);
		
		if (ruleXml!=null){
			return RuleXmlUtil.restoreRule(ruleXml);
		}
		
		return false;
	}
}
