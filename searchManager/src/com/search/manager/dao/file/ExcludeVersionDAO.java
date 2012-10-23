package com.search.manager.dao.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Product;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.ExcludedSkuXml;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.StringUtil;
import com.search.ws.SearchHelper;

@Repository(value="excludeVersionDAO")
public class ExcludeVersionDAO {
	
	private static Logger logger = Logger.getLogger(ExcludeVersionDAO.class);
	
	@Autowired private DaoService daoService;
	
	public boolean createExcludeRuleVersion(String store, String ruleId, String username, String name, String reason){
		
		boolean success = false;
		ExcludeResult excludeFilter = new ExcludeResult();
		List<ExcludeResult> excludeList = null;
		
		try{
			StoreKeyword sk = new StoreKeyword(store, ruleId);
			excludeFilter.setStoreKeyword(sk); 
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(excludeFilter,null,null,0,0);
			
			excludeList = daoService.getExcludeResultList(criteria).getList();	
			
			if(CollectionUtils.isNotEmpty(excludeList)){
				ExcludeRuleXml excludeRuleXml = new ExcludeRuleXml();
				excludeRuleXml.setKeyword(ruleId);
				excludeRuleXml.setReason(reason);
				excludeRuleXml.setName(name);
				
				List<ExcludedSkuXml> skuList = new ArrayList<ExcludedSkuXml>();
				ruleId = StringUtil.escapeKeyword(ruleId);
				for (ExcludeResult excludeResult : excludeList) {
					ExcludedSkuXml sku = new ExcludedSkuXml();
					sku.setEdp(excludeResult.getEdp());
					sku.setExpiryDate(excludeResult.getExpiryDate());
					sku.setCreatedBy(excludeResult.getCreatedBy());
					sku.setLastModifiedBy(excludeResult.getLastModifiedBy());
					sku.setCreatedDate(excludeResult.getCreatedDate());
					sku.setLastModifiedDate(excludeResult.getLastModifiedDate());
					skuList.add(sku);
				}
				excludeRuleXml.setExcludedSku(skuList);
				JAXBContext context = JAXBContext.newInstance(ExcludeRuleXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				Writer w = null;
				try {
					String dir = RuleVersionUtil.getRuleVersionFileDirectory(store, RuleEntity.EXCLUDE);
					if (!FileUtil.isDirectoryExist(dir)) {
						FileUtil.createDirectory(dir);
					}
					w = new FileWriter(RuleVersionUtil.getFileNameByDir(dir, ruleId, RuleVersionUtil.getNextVersion(store, RuleEntity.EXCLUDE, ruleId)));
					m.marshal(excludeRuleXml, w);
				} finally {
					try {
						w.close();
					} catch (Exception e) {
					}
				}
				success = true;
			}
		}catch (Exception e) {
			logger.error(e,e);
		} 
		return success;	
	}
	
	public List<Product> readExcludeRuleVersion(String filePath, String store, String server){
		List<Product> list = Collections.emptyList();
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(ExcludeRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();
				ExcludeRuleXml excludeRule = (ExcludeRuleXml) um.unmarshal(new FileReader(filePath));
				for (ExcludedSkuXml e : excludeRule.getExcludedSku()) {
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
	
	public void readExcludeRuleVersion(File file, RuleVersionInfo backup){
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(ExcludeRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				ExcludeRuleXml excludeRule = (ExcludeRuleXml) um.unmarshal(file);
				backup.setReason(excludeRule.getReason());
				backup.setName(excludeRule.getName());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
	}
	
}
