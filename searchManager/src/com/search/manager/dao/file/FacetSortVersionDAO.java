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
import com.search.manager.model.BackupInfo;
import com.search.manager.model.FacetSort;
import com.search.manager.model.SearchCriteria;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.utility.StringUtil;
import com.search.manager.utility.FileUtil;
import com.search.ws.SearchHelper;

@Repository(value="facetSortVersionDAO")
public class FacetSortVersionDAO {
	
	private static Logger logger = Logger.getLogger(FacetSortVersionDAO.class);
	
	@Autowired private DaoService daoService;
	
	public boolean createFacetSortRuleVersion(String store, String ruleId, String name, String reason){
		
		boolean success = false;
		List<FacetSort> facetSortList = null;
		
		try{
			if(CollectionUtils.isNotEmpty(facetSortList)){
				FacetSortRuleXml facetSortRuleXml = new FacetSortRuleXml();
				facetSortRuleXml.setReason(reason);
				facetSortRuleXml.setName(name);
				
				JAXBContext context = JAXBContext.newInstance(FacetSortRuleXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				Writer w = null;
				try {
					String dir = RuleVersionUtil.getFileDirectory(store, RuleEntity.FACET_SORT.getCode());
					if (!FileUtil.isDirectoryExist(dir)) {
						FileUtil.createDirectory(dir);
					}
					w = new FileWriter(RuleVersionUtil.getFileNameByDir(dir, ruleId, RuleVersionUtil.getNextVersion(store, RuleEntity.FACET_SORT.getCode(), ruleId)));
					m.marshal(facetSortRuleXml, w);
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
	
	public List<FacetSort> readFacetSortVersion(String filePath, String store, String server){
		//TODO
		return null;
	}
	
	public void readFacetSortVersion(File file, BackupInfo backup){
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(FacetSortRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				FacetSortRuleXml facetSortRule = (FacetSortRuleXml) um.unmarshal(file);
				backup.setReason(facetSortRule.getReason());
				backup.setName(facetSortRule.getName());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
	}
	

}
