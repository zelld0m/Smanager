package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.Product;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.ElevateItemXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.StringUtil;
import com.search.ws.SearchHelper;

@Repository(value="elevateVersionDAO")
public class ElevateVersionDAO extends RuleVersionDAO<ElevateRuleXml>{

	@Autowired private DaoService daoService;
	
	@Override
	public String getRuleVersionFilename(String store, String ruleId) {
		return RuleVersionUtil.getFilename(store, RuleEntity.ELEVATE, StringUtil.escapeKeyword(ruleId));
	}

	@Override
	@SuppressWarnings("unchecked")
	public RuleVersionListXml<ElevateRuleXml> getRuleVersionList(String store, String ruleId) {
		return (RuleVersionListXml<ElevateRuleXml>) RuleVersionUtil.getRuleVersionList(store, RuleEntity.ELEVATE, ruleId);
	}
	
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes){
		RuleVersionListXml<ElevateRuleXml> ruleVersionListXml = getRuleVersionList(store, ruleId);
		LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();
		
		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<ElevateRuleXml> elevateRuleXmlList = ruleVersionListXml.getVersions();
			List<ElevateItemXml> elevateItemXmlList = new ArrayList<ElevateItemXml>();

			// Get all items
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(new ElevateResult(new StoreKeyword(store, ruleId)));

			try {
				List<ElevateResult> elevateItemList = daoService.getElevateResultList(criteria).getList();
				
				
				for (ElevateResult e:elevateItemList) {
					ElevateProduct ep = new ElevateProduct(e);
					ep.setStore(store);
					if (e.getMemberType() == MemberTypeEntity.PART_NUMBER) {
						map.put(e.getEdp(), ep);
					} 
				}
				
				if(MapUtils.isNotEmpty(map)){
					SearchHelper.getProducts(map, store, UtilityService.getServerName(), ruleId);
				}
				
				for (ElevateResult er : elevateItemList) {
					Product p = er.getMemberType()==MemberTypeEntity.PART_NUMBER ? map.get(er.getEdp()): null;
					elevateItemXmlList.add(new ElevateItemXml(er, p));
				}
			} catch (DaoException e) {
				return false;
			}	

			elevateRuleXmlList.add(new ElevateRuleXml(store, version, name, notes, username, ruleId, elevateItemXmlList));

			ruleVersionListXml.setRuleId(ruleId);
			ruleVersionListXml.setRuleName(ruleId);
			ruleVersionListXml.setVersions(elevateRuleXmlList);

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.ELEVATE, ruleId, ruleVersionListXml);
		}

		return false;
	}
}