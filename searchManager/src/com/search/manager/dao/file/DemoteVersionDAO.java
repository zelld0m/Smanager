package com.search.manager.dao.file;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.DemoteItemXml;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.ws.SearchHelper;

@Repository(value="demoteVersionDAO")
public class DemoteVersionDAO extends RuleVersionDAO<DemoteRuleXml>{
	
	private static Logger logger = Logger.getLogger(DemoteVersionDAO.class);
	
	@Autowired private DaoService daoService;
	
	@Override
	@SuppressWarnings("unchecked")
	public RuleVersionListXml<DemoteRuleXml> getRuleVersionFile(String store, String ruleId) {
		return (RuleVersionListXml<DemoteRuleXml>) RuleVersionUtil.getRuleVersionFile(store, RuleEntity.DEMOTE, ruleId);
	}
	
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes){
		RuleVersionListXml<DemoteRuleXml> ruleVersionListXml = getRuleVersionFile(store, ruleId);

		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<DemoteRuleXml> demoteRuleXmlList = ruleVersionListXml.getVersions();
			List<DemoteItemXml> demoteItemXmlList = new ArrayList<DemoteItemXml>();

			// Get all items
			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(new DemoteResult(new StoreKeyword(store, ruleId)));

			try {
				List<DemoteResult> demoteItemList = daoService.getDemoteResultList(criteria).getList();
				for (DemoteResult demoteResult : demoteItemList) {
					demoteItemXmlList.add(new DemoteItemXml(demoteResult));
				}
			} catch (DaoException e) {
				return false;
			}	

			demoteRuleXmlList.add(new DemoteRuleXml(store, version, name, notes, username, ruleId, demoteItemXmlList));

			ruleVersionListXml.setRuleId(ruleId);
			ruleVersionListXml.setRuleName(ruleId);
			ruleVersionListXml.setVersions(demoteRuleXmlList);

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.DEMOTE, ruleId, ruleVersionListXml);
		}

		return false;
	}
	
	public List<DemoteProduct> readDemoteVersion(String filePath, String store, String server){
		List<DemoteProduct> list = Collections.emptyList();
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(DemoteRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				LinkedHashMap<String, DemoteProduct> map = new LinkedHashMap<String, DemoteProduct>();
				DemoteRuleXml demoteRule = (DemoteRuleXml) um.unmarshal(new FileReader(filePath));
				for (DemoteItemXml e : demoteRule.getDemoteItem()) {
					DemoteProduct ep = new DemoteProduct();
					ep.setEdp(e.getEdp());
					ep.setLocation(e.getLocation());
					ep.setExpiryDate(e.getExpiryDate());
					ep.setCreatedDate(e.getCreatedDate());
					ep.setLastModifiedDate(e.getLastModifiedDate());
					ep.setLastModifiedBy(e.getLastModifiedBy());
					ep.setCreatedBy(e.getCreatedBy());
					ep.setStore(store);
					map.put(e.getEdp(), ep);
				}
				SearchHelper.getProducts(map, store, server, demoteRule.getKeyword());
				list = new ArrayList<DemoteProduct>(map.values());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return list;
	}

	public boolean restoreRuleVersion(String store, String ruleId, String username, long version) {
		// TODO Auto-generated method stub
		return false;
	}
}