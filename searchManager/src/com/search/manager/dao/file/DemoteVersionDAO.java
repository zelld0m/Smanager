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
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.DemoteItemXml;
import com.search.manager.utility.StringUtil;
import com.search.manager.utility.FileUtil;
import com.search.ws.SearchHelper;

@Repository(value="demoteVersionDAO")
public class DemoteVersionDAO {
	
	private static Logger logger = Logger.getLogger(DemoteVersionDAO.class);
	
	@Autowired private DaoService daoService;
	
	public boolean createDemoteRuleVersion(String store, String ruleId, String username, String name, String notes){
		
		boolean success = false;
		DemoteResult demoteFilter = new DemoteResult();
		List<DemoteResult> demotedList = null;
		
		try{
			StoreKeyword sk = new StoreKeyword(store, ruleId);
			demoteFilter.setStoreKeyword(sk); 
			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(demoteFilter,null,null,0,0);
			
			demotedList = daoService.getDemoteResultList(criteria).getList();	
			
			if(CollectionUtils.isNotEmpty(demotedList)){
				DemoteRuleXml demoteRuleXml = new DemoteRuleXml();
				demoteRuleXml.setKeyword(ruleId);
				demoteRuleXml.setNotes(notes);
				demoteRuleXml.setName(name);
				
				List<DemoteItemXml> skuList = new ArrayList<DemoteItemXml>();
				ruleId = StringUtil.escapeKeyword(ruleId);
				for (DemoteResult demoteResult : demotedList) {
					DemoteItemXml sku = new DemoteItemXml();
					sku.setEdp(demoteResult.getEdp());
					sku.setLocation(demoteResult.getLocation());
					sku.setExpiryDate(demoteResult.getExpiryDate());
					sku.setCreatedBy(demoteResult.getCreatedBy());
					sku.setLastModifiedBy(demoteResult.getLastModifiedBy());
					sku.setCreatedDate(demoteResult.getCreatedDate());
					sku.setLastModifiedDate(demoteResult.getLastModifiedDate());
					skuList.add(sku);
				}
				demoteRuleXml.setDemotedSku(skuList);
				JAXBContext context = JAXBContext.newInstance(DemoteRuleXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				Writer w = null;
				try {
					String dir = RuleVersionUtil.getRuleVersionFileDirectory(store, RuleEntity.DEMOTE);
					if (!FileUtil.isDirectoryExist(dir)) {
						FileUtil.createDirectory(dir);
					}
					w = new FileWriter(RuleVersionUtil.getFileNameByDir(dir, ruleId));
					m.marshal(demoteRuleXml, w);
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
	
	public List<DemoteProduct> readDemotedVersion(String filePath, String store, String server){
		List<DemoteProduct> list = Collections.emptyList();
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(DemoteRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				LinkedHashMap<String, DemoteProduct> map = new LinkedHashMap<String, DemoteProduct>();
				DemoteRuleXml demoteRule = (DemoteRuleXml) um.unmarshal(new FileReader(filePath));
				for (DemoteItemXml e : demoteRule.getDemotedSku()) {
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
	
	public void readDemotedVersion(File file, RuleVersionInfo backup){
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(DemoteRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				DemoteRuleXml demoteRule = (DemoteRuleXml) um.unmarshal(file);
				backup.setNotes(demoteRule.getNotes());
				backup.setName(demoteRule.getName());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
	}
	

}
