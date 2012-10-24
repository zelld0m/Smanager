package com.search.manager.dao.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.model.Keyword;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.Store;
import com.search.manager.report.model.xml.RankingRuleKeywordXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.utility.FileUtil;

@Repository(value="rankingRuleVersionDAO")
public class RankingRuleVersionDAO {
	
	private static Logger logger = Logger.getLogger(RankingRuleVersionDAO.class);
	
	@Autowired private DaoService daoService;
	
	public boolean restoreRankingRuleVersion(String store, String ruleId, int version) {
		boolean success = true;
		Relevancy relevancyVersion = readRankingRuleVersion(store, ruleId, version);
		try {
			Relevancy currentRelevancy = getRelevancy(store, ruleId);
			if (currentRelevancy == null) {
				daoService.addRelevancy(relevancyVersion);
				if (relevancyVersion.getParameters()!=null) {
					for (Map.Entry<String, String> entry : relevancyVersion.getParameters().entrySet()) {
						daoService.addRelevancyField(new RelevancyField(relevancyVersion,entry.getKey(), entry.getValue(), relevancyVersion.getCreatedBy(), relevancyVersion.getLastModifiedBy(),
								relevancyVersion.getCreatedDate(), relevancyVersion.getLastModifiedDate()));
					}
				}
				if (relevancyVersion.getRelKeyword()!=null) {
					for (RelevancyKeyword keyword : relevancyVersion.getRelKeyword()) {
						keyword.setRelevancy(relevancyVersion);
						daoService.addRelevancyKeyword(keyword);
					}
				}
			} else {
				Map<String, String> currParams = currentRelevancy.getParameters();
				Map<String, String> verParams = relevancyVersion.getParameters();
				
				daoService.updateRelevancy(relevancyVersion);
				for (Map.Entry<String, String> entry : verParams.entrySet()) {
					if (currParams.get(entry.getKey()) == null) {
						daoService.addRelevancyField(new RelevancyField(relevancyVersion,entry.getKey(), entry.getValue(), relevancyVersion.getCreatedBy(), relevancyVersion.getLastModifiedBy(),
								relevancyVersion.getCreatedDate(), relevancyVersion.getLastModifiedDate()));
					} else if (!currParams.get(entry.getKey()).equals(verParams.get(entry.getKey()))) {
						daoService.updateRelevancyField(new RelevancyField(relevancyVersion,entry.getKey(), entry.getValue(), relevancyVersion.getCreatedBy(), relevancyVersion.getLastModifiedBy(),
								relevancyVersion.getCreatedDate(), relevancyVersion.getLastModifiedDate()));
					}
				}
				if (currentRelevancy.getRelKeyword() != null) {
					for (RelevancyKeyword keyword : currentRelevancy.getRelKeyword()) {
						keyword.setRelevancy(currentRelevancy);
						daoService.deleteRelevancyKeyword(keyword);
					}
				}
				if (relevancyVersion.getRelKeyword() != null) {
					for (RelevancyKeyword keyword : relevancyVersion.getRelKeyword()) {
						keyword.setRelevancy(relevancyVersion);
						daoService.addRelevancyKeyword(keyword);
					}
				}
			}
		} catch (DaoException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	
	public boolean createRankingRuleVersion(String store, String ruleId, String username, String name, String notes){
		
		boolean success = false;
		
		try{
			Relevancy relevancy = getRelevancy(store, ruleId);

			if(relevancy != null){
				RankingRuleXml rrXml = new RankingRuleXml();
				rrXml.setRuleId(ruleId);
				rrXml.setRuleName(relevancy.getRuleName());
				rrXml.setDescription(relevancy.getDescription());
				rrXml.setStartDate(relevancy.getStartDate());
				rrXml.setEndDate(relevancy.getEndDate());
				rrXml.setRelevancyFields(relevancy.getParameters());
				rrXml.setCreatedDate(new Date());
				rrXml.setLastModifiedDate(relevancy.getLastModifiedDate());
				rrXml.setModifiedBy(relevancy.getLastModifiedBy());
				rrXml.setCreatedBy(relevancy.getCreatedBy());
				rrXml.setNotes(notes);
				rrXml.setName(name);
				
			    if(CollectionUtils.isNotEmpty(relevancy.getRelKeyword())){
			    	List<RankingRuleKeywordXml> keywords = new ArrayList<RankingRuleKeywordXml>();
			    	for (RelevancyKeyword relevancyKeyword : relevancy.getRelKeyword()) {
			    		RankingRuleKeywordXml keyword = new RankingRuleKeywordXml();
			    		keyword.setKeyword(relevancyKeyword.getKeyword().getKeyword());
			    		keyword.setPriority(relevancyKeyword.getPriority());
			    		keywords.add(keyword);
					}
			    	rrXml.setKeywords(keywords);
			    }

			    JAXBContext context = JAXBContext.newInstance(RankingRuleXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				Writer w = null;
				try {
					String dir =  RuleVersionUtil.getRuleVersionFileDirectory(store, RuleEntity.RANKING_RULE);
					if (!FileUtil.isDirectoryExist(dir)) {
						FileUtil.createDirectory(dir);
					}
					w = new FileWriter(RuleVersionUtil.getFileNameByDir(dir, ruleId));
					m.marshal(rrXml, w);
				} finally {
					try {
						w.close();
					} catch (Exception e) {
					}
				}
				success = true;
			}
		} catch (Exception e) {
			logger.error(e,e);
		} 
		return success;	
	}

	public Relevancy readRankingRuleVersion(String store, String ruleId, int version){
		
		Relevancy relevancy = new Relevancy();
		try {
			JAXBContext context = JAXBContext.newInstance(RankingRuleXml.class);
			Unmarshaller um = context.createUnmarshaller();
			RankingRuleXml rr = (RankingRuleXml) um.unmarshal(new FileReader(RuleVersionUtil.getFileName(store, RuleEntity.RANKING_RULE, ruleId)));
			relevancy.setRuleId(rr.getRuleId());
			relevancy.setStore(new Store(store));
			relevancy.setRuleName(rr.getRuleName());
			relevancy.setDescription(rr.getDescription());
			relevancy.setStartDate(rr.getStartDate());
			relevancy.setEndDate(rr.getEndDate());
			relevancy.setCreatedDate(rr.getCreatedDate());
			relevancy.setLastModifiedDate(rr.getLastModifiedDate());
			relevancy.setLastModifiedBy(rr.getModifiedBy());
			relevancy.setCreatedBy(rr.getCreatedBy());
			relevancy.setFields(rr.getRelevancyFields());
			
			if (rr.getKeywords() != null) {
				List<RelevancyKeyword> relKwList = new ArrayList<RelevancyKeyword>();
				for (int i = 0; i < rr.getKeywords().size(); i++) {
					RankingRuleKeywordXml e = rr.getKeywords().get(i);
					RelevancyKeyword r = new RelevancyKeyword();
					r.setPriority(e.getPriority());
					r.setKeyword(new Keyword(e.getKeyword()));
					relKwList.add(r);
					
				}
				relevancy.setRelKeyword(relKwList);
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
		return relevancy;

	}

	public void readRankingRuleVersion(File file, RuleVersionInfo backup){
		
		try {
			JAXBContext context = JAXBContext.newInstance(RankingRuleXml.class);
			Unmarshaller um = context.createUnmarshaller();
			RankingRuleXml rr = (RankingRuleXml) um.unmarshal(new FileReader(file));
			backup.setNotes(rr.getNotes());
			backup.setName(rr.getName());
		}catch (Exception e) {
			logger.error(e,e);
		}

	}

	private Relevancy getRelevancy(String store, String ruleId) throws DaoException {
		Relevancy relevancy = new Relevancy();
		relevancy.setRelevancyId(ruleId);
		relevancy.setStore(new Store(store));
		relevancy = daoService.getRelevancyDetails(relevancy);
		if (relevancy != null) {
			List<RelevancyKeyword> relKWList = daoService.getRelevancyKeywords(relevancy).getList();
			relevancy.setRelKeyword(relKWList);
		}
		return relevancy;
	}
	
}