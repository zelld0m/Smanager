package com.search.manager.dao.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.ElevateItemXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;

@Repository(value="elevateVersionDAO")
public class ElevateVersionDAO {

	private static Logger logger = Logger.getLogger(ElevateVersionDAO.class);

	@Autowired private DaoService daoService;

	@SuppressWarnings("unchecked")
	public boolean createElevateRuleVersion(String store, String ruleId, String username, String name, String notes){
		boolean success = false;
		
		try {
			RuleVersionListXml<ElevateRuleXml> ruleVersionListXml = (RuleVersionListXml<ElevateRuleXml>) RuleVersionUtil.getRuleVersionFile(store, RuleEntity.ELEVATE, ruleId);
			
			int version = ruleVersionListXml.getNextVersion();
			List<ElevateRuleXml> elevateRuleXmlList = ruleVersionListXml.getVersions();
			
			// Get all items
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(new ElevateResult(new StoreKeyword(store, ruleId)));
			List<ElevateResult> elevateItemList= daoService.getElevateResultList(criteria).getList();	

			List<ElevateItemXml> elevateItemXmlList = new ArrayList<ElevateItemXml>();

			for (ElevateResult elevateResult : elevateItemList) {
				elevateItemXmlList.add(new ElevateItemXml(elevateResult));
			}
			
			elevateRuleXmlList.add(new ElevateRuleXml(store, version, name, notes, username, ruleId, elevateItemXmlList));
			
			ruleVersionListXml.setVersions(elevateRuleXmlList);
			
			success = RuleVersionUtil.addRuleVersion(store, RuleEntity.ELEVATE, ruleId, ruleVersionListXml);
			
		} catch (IOException e) {
			logger.error("IOexception createElevateRuleVersion", e);
		}catch (JAXBException e) {
			logger.error("JAXBException createElevateRuleVersion", e);
		} catch (Exception e) {
			logger.error("Failed createElevateRuleVersion", e);
		} finally {

		}

		return success;
	}

	public List<ElevateProduct> readElevateVersion(String filePath, String store, String server){
		List<ElevateProduct> list = Collections.emptyList();


		return list;
	}

	public List<RuleVersionInfo> readElevateVersion(File file){
		List<RuleVersionInfo> list =  Collections.emptyList();

		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
		} catch (Exception e) {
			logger.error(e,e);
		}

		return list;
	}
}