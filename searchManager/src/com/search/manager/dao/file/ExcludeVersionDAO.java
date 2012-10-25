package com.search.manager.dao.file;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Product;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.ExcludeItemXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.service.UtilityService;
import com.search.ws.SearchHelper;

@Repository(value="excludeVersionDAO")
public class ExcludeVersionDAO implements RuleVersionDAO{
	
	private static Logger logger = Logger.getLogger(ExcludeVersionDAO.class);
	
	@Autowired private DaoService daoService;
	
	@SuppressWarnings("unchecked")
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes){
		RuleVersionListXml<ExcludeRuleXml> ruleVersionListXml = (RuleVersionListXml<ExcludeRuleXml>) RuleVersionUtil.getRuleVersionFile(store, RuleEntity.EXCLUDE, ruleId);

		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<ExcludeRuleXml> excludeRuleXmlList = ruleVersionListXml.getVersions();
			List<ExcludeItemXml> excludeItemXmlList = new ArrayList<ExcludeItemXml>();

			// Get all items
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(new ExcludeResult(new StoreKeyword(store, ruleId)));

			try {
				List<ExcludeResult> excludeItemList = daoService.getExcludeResultList(criteria).getList();
				for (ExcludeResult excludeResult : excludeItemList) {
					excludeItemXmlList.add(new ExcludeItemXml(excludeResult));
				}
			} catch (DaoException e) {
				return false;
			}	

			excludeRuleXmlList.add(new ExcludeRuleXml(store, version, name, notes, username, ruleId, excludeItemXmlList));

			ruleVersionListXml.setVersions(excludeRuleXmlList);

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.EXCLUDE, ruleId, ruleVersionListXml);
		}

		return false;
	}
	
	public List<Product> readExcludeRuleVersion(String filePath, String store, String server){
		List<Product> list = Collections.emptyList();
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(ExcludeRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();
				ExcludeRuleXml excludeRule = (ExcludeRuleXml) um.unmarshal(new FileReader(filePath));
				for (ExcludeItemXml e : excludeRule.getExcludeItem()) {
					Product product = new Product();
					product.setEdp(e.getEdp());
					product.setExpiryDate(e.getExpiryDate());
					product.setCreatedDate(e.getCreatedDate());
					product.setLastModifiedDate(e.getLastModifiedDate());
					product.setLastModifiedBy(e.getLastModifiedBy());
					product.setCreatedBy(e.getCreatedBy());
					product.setStore(store);
					map.put(e.getEdp(), product);
				}
				SearchHelper.getProducts(map, store, UtilityService.getServerName(), excludeRule.getKeyword());
				list = new ArrayList<Product>(map.values());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return list;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<RuleVersionInfo> getRuleVersions(String store, String ruleId) {
		List<RuleVersionInfo> ruleVersionInfoList = new ArrayList<RuleVersionInfo>();
		RuleVersionListXml<ExcludeRuleXml> ruleVersionListXml = (RuleVersionListXml<ExcludeRuleXml>) RuleVersionUtil.getRuleVersionFile(store, RuleEntity.EXCLUDE, ruleId);

		if (ruleVersionListXml!=null){
			List<ExcludeRuleXml> excludeRuleXmlList = ruleVersionListXml.getVersions();
			if(CollectionUtils.isNotEmpty(excludeRuleXmlList)){
				ruleVersionInfoList = ListUtils.transformedList(excludeRuleXmlList, new Transformer() { 
					  @Override
				      public Object transform(Object o) {  
				          return new RuleVersionInfo((ExcludeRuleXml) o);  
				      }  
				  });  
			}
		}
		
		return ruleVersionInfoList;
	}
	
	@Override
	public boolean restoreRuleVersion(String store, String ruleId,
			String username, long version) {
		// TODO Auto-generated method stub
		return false;
	}
}