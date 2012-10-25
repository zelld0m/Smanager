package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Transformer;

import com.search.manager.model.RuleVersionInfo;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionXml;

public abstract class RuleVersionDAO<T extends RuleVersionXml>{

	public abstract RuleVersionListXml<T> getRuleVersionFile(String store, String ruleId);
	public abstract boolean createRuleVersion(String store, String ruleId, String username, String name, String notes);
	public abstract boolean restoreRuleVersion(String store, String ruleId, String username, long version);

	@SuppressWarnings("unchecked")
	public  List<RuleVersionInfo> getRuleVersions(String store, String ruleId) {
		List<RuleVersionInfo> ruleVersionInfoList = new ArrayList<RuleVersionInfo>();
		RuleVersionListXml<T> ruleVersionListXml = (RuleVersionListXml<T>) getRuleVersionFile(store, ruleId);

		if (ruleVersionListXml!=null){
			List<T> ruleXmlList =  ruleVersionListXml.getVersions();
			if(CollectionUtils.isNotEmpty(ruleXmlList)){
				
				Collections.sort(ruleXmlList, new Comparator<RuleVersionXml>() {
					@Override
					public int compare(RuleVersionXml r1, RuleVersionXml r2) {
						return r2.getVersion() < r1.getVersion() ? 0 : 1;
					}
				});
				
				ruleVersionInfoList = ListUtils.transformedList(ruleXmlList, new Transformer() { 
					@Override
					public Object transform(Object o) {  
						return (RuleVersionInfo) new RuleVersionInfo((RuleVersionXml) o);  
					}  
				});  
			}
		}

		return ruleVersionInfoList;
	}
}