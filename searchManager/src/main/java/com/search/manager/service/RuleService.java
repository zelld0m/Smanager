package com.search.manager.service;

import org.directwebremoting.annotations.RemoteMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.xml.file.RuleXmlUtil;

public abstract class RuleService {

	@Autowired
	private UtilityService utilityService;
	@Autowired
	private RuleVersionUtil ruleVersionUtil;
	@Autowired
	private RuleXmlUtil ruleXmlUtil;

	public abstract RuleEntity getRuleEntity();

	@RemoteMethod
	public boolean restoreRule(String ruleId, int version) {
		String store = utilityService.getStoreId();
		RuleXml ruleXml = ruleVersionUtil.getRuleVersion(store,
				getRuleEntity(), ruleId, version);

		if (ruleXml != null) {
			return ruleXmlUtil.restoreRule(ruleXml);
		}

		return false;
	}
	
}
