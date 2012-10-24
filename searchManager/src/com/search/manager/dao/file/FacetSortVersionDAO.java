package com.search.manager.dao.file;

import java.io.File;
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
import org.springframework.stereotype.Repository;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.utility.FileUtil;

@Repository(value="facetSortVersionDAO")
public class FacetSortVersionDAO {
	
	private static Logger logger = Logger.getLogger(FacetSortVersionDAO.class);
	
	public boolean createFacetSortRuleVersion(String store, String ruleId, String username, String name, String notes){
		
		boolean success = false;
		List<FacetSort> facetSortList = null;
		
		try{
			if(CollectionUtils.isNotEmpty(facetSortList)){
				FacetSortRuleXml facetSortRuleXml = new FacetSortRuleXml();
				facetSortRuleXml.setNotes(notes);
				facetSortRuleXml.setName(name);
				
				JAXBContext context = JAXBContext.newInstance(FacetSortRuleXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				Writer w = null;
				try {
					String dir = RuleVersionUtil.getRuleVersionFileDirectory(store, RuleEntity.FACET_SORT);
					if (!FileUtil.isDirectoryExist(dir)) {
						FileUtil.createDirectory(dir);
					}
					w = new FileWriter(RuleVersionUtil.getFileNameByDir(dir, ruleId));
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
		List<FacetSort> list = Collections.emptyList();
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(FacetSortRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				LinkedHashMap<String, FacetSort> map = new LinkedHashMap<String, FacetSort>();
				
				//TODO:
				
				list = new ArrayList<FacetSort>(map.values());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return list;
	}
	
	public void readFacetSortVersion(File file, RuleVersionInfo backup){
		try {
			try {
				JAXBContext context = JAXBContext.newInstance(FacetSortRuleXml.class);
				Unmarshaller um = context.createUnmarshaller();
				FacetSortRuleXml facetSortRule = (FacetSortRuleXml) um.unmarshal(file);
				backup.setNotes(facetSortRule.getNotes());
				backup.setName(facetSortRule.getName());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
	}
	

}
