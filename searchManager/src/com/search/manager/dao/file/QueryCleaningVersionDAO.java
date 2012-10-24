package com.search.manager.dao.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.model.RedirectRule;
import com.search.manager.report.model.xml.QueryCleaningRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.utility.FileUtil;

@Repository(value="queryCleaningVersionDAO")
public class QueryCleaningVersionDAO {
	
	private static Logger logger = Logger.getLogger(QueryCleaningVersionDAO.class);
	
	@Autowired private DaoService daoService;
	
	public boolean createQueryCleaningRuleVersion(String store, String ruleId, String username, String name, String notes) {

		boolean success = false;

		try {
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setRuleId(ruleId);
			redirectRule.setStoreId(store);
			redirectRule = daoService.getRedirectRule(redirectRule);

			if (redirectRule != null) {
				QueryCleaningRuleXml qcrXml = new QueryCleaningRuleXml();
				qcrXml.setRuleId(redirectRule.getRuleId());
				qcrXml.setDescription(redirectRule.getDescription());
				qcrXml.setRuleName(redirectRule.getRuleName());
				qcrXml.setRedirectType(redirectRule.getRedirectTypeId());
				qcrXml.setPriority(redirectRule.getPriority());
				qcrXml.setSearchTerm(redirectRule.getSearchTerm());
				qcrXml.setCondition(redirectRule.getCondition());
				qcrXml.setChangeKeyword(redirectRule.getChangeKeyword());
				qcrXml.setNotes(notes);
				qcrXml.setName(name);
				
				JAXBContext context = JAXBContext.newInstance(QueryCleaningRuleXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				Writer w = null;
				try {
					String dir = RuleVersionUtil.getRuleVersionFileDirectory(store, RuleEntity.QUERY_CLEANING);
					if (!FileUtil.isDirectoryExist(dir)) {
						FileUtil.createDirectory(dir);
					}
					w = new FileWriter(RuleVersionUtil.getFileNameByDir(dir, ruleId));
					m.marshal(qcrXml, w);
				} finally {
					try {
						w.close();
					} catch (Exception e) {
					}
				}
				success = true;
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return success;
	}
	
	public RedirectRule readQueryCleaningVersion(String store, String ruleId, int version){
		RedirectRule rr = new RedirectRule();
		
		try {
			JAXBContext context = JAXBContext.newInstance(QueryCleaningRuleXml.class);
			Unmarshaller um = context.createUnmarshaller();
			QueryCleaningRuleXml qcr = (QueryCleaningRuleXml) um.unmarshal(new FileReader(RuleVersionUtil.getFileName(store, RuleEntity.QUERY_CLEANING, ruleId)));
			rr.setRuleId(ruleId);
			rr.setStoreId(store);
			rr.setRuleName(qcr.getRuleName());
			rr.setDescription(qcr.getDescription());
			rr.setRedirectTypeId(qcr.getRedirectType());
			rr.setPriority(qcr.getPriority());
			rr.setSearchTerm(qcr.getSearchTerm());
			rr.setCondition(qcr.getCondition());
			rr.setChangeKeyword(qcr.getChangeKeyword());
		}catch (Exception e) {
			logger.error(e,e);
		}
		return rr;
	}

	public void readQueryCleaningVersion(File file, RuleVersionInfo backup){
		
		try {
			JAXBContext context = JAXBContext.newInstance(RankingRuleXml.class);
			Unmarshaller um = context.createUnmarshaller();
			QueryCleaningRuleXml rr = (QueryCleaningRuleXml)um.unmarshal(new FileReader(file));
			backup.setNotes(rr.getNotes());
			backup.setName(rr.getName());
		}catch (Exception e) {
			logger.error(e,e);
		}

	}
}
