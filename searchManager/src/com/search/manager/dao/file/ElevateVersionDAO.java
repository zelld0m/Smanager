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
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ElevatedSkuXml;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.FileUtil;
import com.search.ws.SearchHelper;

@Repository(value="elevateVersionDAO")
public class ElevateVersionDAO {
	
	private static Logger logger = Logger.getLogger(ElevateVersionDAO.class);
	
	@Autowired private DaoService daoService;
	
	public boolean createElevateRuleVersion(String store, String ruleId, String reason){
		
		boolean success = false;
		ElevateResult elevateFilter = new ElevateResult();
		List<ElevateResult> elevatedList = null;
		
		try{
			StoreKeyword sk = new StoreKeyword(store, ruleId);
			elevateFilter.setStoreKeyword(sk); 
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(elevateFilter,null,null,0,0);
			
			elevatedList = daoService.getElevateResultList(criteria).getList();	
			
			if(CollectionUtils.isNotEmpty(elevatedList)){
				ElevateRuleXml elevateRuleXml = new ElevateRuleXml();
				elevateRuleXml.setKeyword(ruleId);
				elevateRuleXml.setReason(reason);
				
				List<ElevatedSkuXml> skuList = new ArrayList<ElevatedSkuXml>();
				ruleId = UtilityService.escapeKeyword(ruleId);
				for (ElevateResult elevateResult : elevatedList) {
					ElevatedSkuXml sku = new ElevatedSkuXml();
					sku.setEdp(elevateResult.getEdp());
					sku.setLocation(elevateResult.getLocation());
					sku.setExpiryDate(elevateResult.getExpiryDate());
					sku.setCreatedBy(elevateResult.getCreatedBy());
					sku.setLastModifiedBy(elevateResult.getLastModifiedBy());
					sku.setCreatedDate(elevateResult.getCreatedDate());
					sku.setLastModifiedDate(elevateResult.getLastModifiedDate());
					skuList.add(sku);
				}
				elevateRuleXml.setElevatedSku(skuList);
				JAXBContext context = JAXBContext.newInstance(ElevateRuleXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				Writer w = null;
				try {
					String dir = RuleVersionUtil.getFileDirectory(store, RuleEntity.ELEVATE.getCode());
					if (!FileUtil.isDirectoryExist(dir)) {
						FileUtil.createDirectory(dir);
					}
					w = new FileWriter(RuleVersionUtil.getFileNameByDir(dir, ruleId, RuleVersionUtil.getNextVersion(store, RuleEntity.ELEVATE.getCode(), ruleId)));
					m.marshal(elevateRuleXml, w);
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
	
	public List<ElevateProduct> readElevatedVersion(String filePath, String store, String server){
		List<ElevateProduct> list = Collections.emptyList();
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(ElevateRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				LinkedHashMap<String, ElevateProduct> map = new LinkedHashMap<String, ElevateProduct>();
				ElevateRuleXml elevateRule = (ElevateRuleXml) um.unmarshal(new FileReader(filePath));
				for (ElevatedSkuXml e : elevateRule.getElevatedSku()) {
					ElevateProduct ep = new ElevateProduct();
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
				SearchHelper.getProducts(map, store, UtilityService.getServerName(), elevateRule.getKeyword());
				list = new ArrayList<ElevateProduct>(map.values());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return list;
	}
	
	public String readElevatedVersion(File file){
		String reason = null;
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(ElevateRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				ElevateRuleXml elevateRule = (ElevateRuleXml) um.unmarshal(file);
				reason = elevateRule.getReason();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return reason;
	}
	

}
