package com.search.manager.xml.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.StringUtil;

public class RuleXmlUtil {

	private static Logger logger = Logger.getLogger(RuleXmlUtil.class);
	@Autowired static DaoService daoService;

	private static boolean restoreElevate(RuleXml xml){
		StoreKeyword storeKeyword = new StoreKeyword(xml.getStore(), xml.getRuleId());
		//ElevateResult elevateResult = new ElevateResult((ElevateRuleXml) xml);

		//SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(model);
//		daoService.getElevateResultList(criteria)
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private static boolean restoreExclude(RuleXml xml){
		return false;
	}

	private static boolean restoreDemote(RuleXml xml){
		return false;
	}
	
	private static boolean restoreFacetSort(RuleXml xml){
		return false;
	}
	
	private static boolean restoreQueryCleaning(RuleXml xml){
		return false;
	}

	private static boolean restoreRankingRule(RuleXml xml) {
		Relevancy restoreVersion = new Relevancy((RankingRuleXml) xml);
		String store = restoreVersion.getStore().toString();
		RelevancyField rf = new RelevancyField();
		boolean isRestored = true;

		try {
			Relevancy currentVersion = RuleXmlUtil.getRelevancy(store , restoreVersion.getRuleId());
			if (currentVersion == null && (isRestored &= daoService.addRelevancy(currentVersion) > 0)){

				//TODO: Check for optimization
				if (MapUtils.isNotEmpty(currentVersion.getParameters())) {
					for (Map.Entry<String, String> entry : currentVersion.getParameters().entrySet()) {
						rf = new RelevancyField(currentVersion,entry.getKey(), entry.getValue(), currentVersion.getCreatedBy(), currentVersion.getLastModifiedBy(),
								currentVersion.getCreatedDate(), currentVersion.getLastModifiedDate());
						isRestored &= daoService.addRelevancyField(rf) > 0;
					}
				}

				if (CollectionUtils.isNotEmpty(currentVersion.getRelKeyword())) {
					for (RelevancyKeyword keyword : currentVersion.getRelKeyword()) {
						keyword.setRelevancy(currentVersion);
						isRestored &= daoService.addRelevancyKeyword(keyword) > 0;
					}
				}

				//TODO: Rollback if isRestored value becomes false

				return isRestored;
			}

			Map<String, String> currParams = currentVersion.getParameters();
			Map<String, String> restParams = restoreVersion.getParameters();

			if (isRestored &= daoService.updateRelevancy(restoreVersion)>0){

				for (Map.Entry<String, String> entry : restParams.entrySet()) {

					if (currParams.get(entry.getKey()) == null) {
						rf = new RelevancyField(currentVersion,entry.getKey(), entry.getValue(), currentVersion.getCreatedBy(), currentVersion.getLastModifiedBy(),
								currentVersion.getCreatedDate(), currentVersion.getLastModifiedDate());

						isRestored &= daoService.addRelevancyField(rf) > 0;

					} else if (!currParams.get(entry.getKey()).equals(restParams.get(entry.getKey()))) {

						rf = new RelevancyField(currentVersion,entry.getKey(), entry.getValue(), currentVersion.getCreatedBy(), currentVersion.getLastModifiedBy(),
								currentVersion.getCreatedDate(), currentVersion.getLastModifiedDate());

						//						
						//						daoService.updateRelevancyField(new RelevancyField(relevancyVersion,entry.getKey(), entry.getValue(), relevancyVersion.getCreatedBy(), relevancyVersion.getLastModifiedBy(),
						//								relevancyVersion.getCreatedDate(), relevancyVersion.getLastModifiedDate()));
					}
				}

				if (CollectionUtils.isNotEmpty(currentVersion.getRelKeyword())) {
					for (RelevancyKeyword keyword : currentVersion.getRelKeyword()) {
						keyword.setRelevancy(currentVersion);
						daoService.deleteRelevancyKeyword(keyword);
					}
				}

				if (CollectionUtils.isNotEmpty(restoreVersion.getRelKeyword())) {
					for (RelevancyKeyword keyword : restoreVersion.getRelKeyword()) {
						keyword.setRelevancy(restoreVersion);
						daoService.addRelevancyKeyword(keyword);
					}
				}
			}

		} catch (DaoException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static Relevancy getRelevancy(String store, String ruleId) throws DaoException {
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

	public static boolean restoreRule(RuleXml xml) {
		boolean isRestored = false;
		
		if(xml==null){

		}else if (xml instanceof ElevateRuleXml){
			isRestored = RuleXmlUtil.restoreElevate(xml);
		}else if(xml instanceof DemoteRuleXml){
			isRestored = RuleXmlUtil.restoreDemote(xml);
		}else if(xml instanceof ExcludeRuleXml){
			isRestored = RuleXmlUtil.restoreExclude(xml);
		}else if(xml instanceof FacetSortRuleXml){
			isRestored = RuleXmlUtil.restoreFacetSort(xml);
		}else if(xml instanceof RedirectRuleXml){
			isRestored = RuleXmlUtil.restoreQueryCleaning(xml);
		}else if(xml instanceof RankingRuleXml){
			isRestored = RuleXmlUtil.restoreRankingRule(xml);
		}

		return isRestored;
	}
	
	public static void deleteFile(String filepath) throws IOException{
		File file = new File(filepath);

		if(file.exists() && !file.delete()){
			file.deleteOnExit();
		}
	}

	public static String getFilename(String path, String store, RuleEntity ruleEntity ,String ruleId){
		StringBuilder filePath = new StringBuilder(getRuleFileDirectory(path, store, ruleEntity)).append(File.separator).append(ruleId).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}

	public static String getFilenameByDir(String dir, String ruleId){
		StringBuilder filePath = new StringBuilder(dir).append(File.separator).append(ruleId).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}
	

	public static String getRuleFileDirectory(String path, String store, RuleEntity ruleEntity){
		StringBuilder sb = new StringBuilder();
		List<String> values = ruleEntity.getValues(); 
		String directory = CollectionUtils.isNotEmpty(values)? values.get(0): ruleEntity.name();
		sb.append(path).append(File.separator).append(store).append(File.separator).append(directory);
		return sb.toString();
	}
	
	public static boolean backUpRule(String path, String store, RuleEntity ruleEntity, String ruleId, Object rule){
		String dir = getRuleFileDirectory(path, store, ruleEntity);
		String id = ruleId;
	
		switch(ruleEntity){
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE: id = StringUtil.escapeKeyword(ruleId); break;
		}
		
		String filename = getFilenameByDir(dir, id) + "_backup";

		try {
			JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(rule, new FileWriter(filename));
			return true;
		} catch (JAXBException e) {
			logger.error("Unable to create marshaller", e);
			return false;
		} catch (Exception e) {
			logger.error("Unknown error", e);
			return false;
		}
	}
}