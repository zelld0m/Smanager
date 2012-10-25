package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.ElevateItemXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;

@Repository(value="elevateVersionDAO")
public class ElevateVersionDAO implements RuleVersionDAO{

	private static Logger logger = Logger.getLogger(ElevateVersionDAO.class);

	@Autowired private DaoService daoService;
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes){
		RuleVersionListXml<ElevateRuleXml> ruleVersionListXml = (RuleVersionListXml<ElevateRuleXml>) RuleVersionUtil.getRuleVersionFile(store, RuleEntity.ELEVATE, ruleId);

		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<ElevateRuleXml> elevateRuleXmlList = ruleVersionListXml.getVersions();
			List<ElevateItemXml> elevateItemXmlList = new ArrayList<ElevateItemXml>();

			// Get all items
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(new ElevateResult(new StoreKeyword(store, ruleId)));

			try {
				List<ElevateResult> elevateItemList = daoService.getElevateResultList(criteria).getList();
				for (ElevateResult elevateResult : elevateItemList) {
					elevateItemXmlList.add(new ElevateItemXml(elevateResult));
				}
			} catch (DaoException e) {
				return false;
			}	

			elevateRuleXmlList.add(new ElevateRuleXml(store, version, name, notes, username, ruleId, elevateItemXmlList));

			ruleVersionListXml.setVersions(elevateRuleXmlList);

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.ELEVATE, ruleId, ruleVersionListXml);
		}

		return false;
	}


	@Override
	public boolean restoreRuleVersion(String store, String ruleId,
			String username, long version) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RuleVersionInfo> getRuleVersions(String store, String ruleId) {
		List<RuleVersionInfo> ruleVersionInfoList = new ArrayList<RuleVersionInfo>();
		RuleVersionListXml<ElevateRuleXml> ruleVersionListXml = (RuleVersionListXml<ElevateRuleXml>) RuleVersionUtil.getRuleVersionFile(store, RuleEntity.ELEVATE, ruleId);

		if (ruleVersionListXml!=null){
			List<ElevateRuleXml> elevateRuleXmlList = ruleVersionListXml.getVersions();
			if(CollectionUtils.isNotEmpty(elevateRuleXmlList)){
				ruleVersionInfoList = ListUtils.transformedList(elevateRuleXmlList, new Transformer() { 
					  @Override
				      public Object transform(Object o) {  
				          return new RuleVersionInfo((ElevateRuleXml) o);  
				      }  
				  });  
			}
		}
		
		return ruleVersionInfoList;
	}
}