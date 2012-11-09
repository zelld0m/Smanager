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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.DemoteItemXml;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateItemXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeItemXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionValidationEventHandler;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.StringUtil;

public class RuleXmlUtil {

	private static Logger logger = Logger.getLogger(RuleXmlUtil.class);
	private static final String PREIMPORTPATH = PropsUtils.getValue("pre-importpath");
	@Autowired private static DaoService daoService;

	private static boolean rollBackToPreImportRuleXml(String ruleId){
		return true;
	}

	private static boolean createPreImportRuleXml(RuleXml xml){
		RuleEntity ruleEntity = xml.getRuleEntity();
		String dir = RuleXmlUtil.getRuleFileDirectory(PREIMPORTPATH, xml.getStore(), ruleEntity);
		String id = xml.getRuleId();

		switch(ruleEntity){
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE: id = StringUtil.escapeKeyword(xml.getRuleId()); break;
		}

		String filename = RuleXmlUtil.getFilenameByDir(dir, id);

		File dirFile = new File(dir);

		if (!dirFile.exists()) {
			try {
				FileUtils.forceMkdir(dirFile);
			} catch (IOException e) {
				logger.error("Unable to create directory", e);
				return false;
			}
		}

		try {
			JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setEventHandler(new RuleVersionValidationEventHandler());

			if (!new File(filename).exists()){
				m.marshal(new RuleXml(), new FileWriter(filename));
			}

			return true;
		} catch (JAXBException e) {
			logger.error("Failed to marshall rule", e);
			return false;
		} catch (IOException e) {
			logger.error("Failed to create pre-import rule file", e);
			return false;
		}
	}

	private static boolean restoreElevate(String ruleId, RuleXml xml){
		ElevateRuleXml eXml = (ElevateRuleXml) xml;
		String store = UtilityService.getStoreName();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		ElevateResult model = new ElevateResult(storeKeyword);
		SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(model);

		try {
			List<ElevateResult> preImportlist = daoService.getElevateResultList(criteria).getList();

			if(CollectionUtils.isNotEmpty(preImportlist)){
				if (!RuleXmlUtil.createPreImportRuleXml(eXml)){
					logger.error("Failed to create pre-import rule");
					return false;
				};

				if(!(daoService.clearElevateResult(storeKeyword)>0)){
					logger.error("Failed to clear existing rule");
					return false;
				};
			}
			
			daoService.addKeyword(storeKeyword);
			
			int processedItem = 0;
			List<ElevateItemXml> eItemXmlList = eXml.getItem();
			ElevateResult elevateResult = null;
			
			for (ElevateItemXml eItemXml : eItemXmlList){
				elevateResult = new ElevateResult(storeKeyword, eItemXml);
				processedItem += daoService.addElevateResult(elevateResult)==1? 1 : 0;
			}

			if (processedItem == CollectionUtils.size(eItemXmlList)){
				return true;				
			}else{
				if(!rollBackToPreImportRuleXml(ruleId)){
					//TODO: add email notification for manual import
					logger.error("Failed to rollback");
				};
			}

		}catch (Exception e) {
			logger.error("Falied to restore elevate", e);
			return false;
		} 

		return false;
	}

	private static boolean restoreExclude(String ruleId, RuleXml xml){
		ExcludeRuleXml eXml = (ExcludeRuleXml) xml;
		String store = UtilityService.getStoreName();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		ExcludeResult model = new ExcludeResult(storeKeyword);
		SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(model);

		try {
			List<ExcludeResult> preImportlist = daoService.getExcludeResultList(criteria).getList();

			if(CollectionUtils.isNotEmpty(preImportlist)){
				if (!RuleXmlUtil.createPreImportRuleXml(eXml)){
					logger.error("Failed to create pre-import rule");
					return false;
				};

				if(!(daoService.clearExcludeResult(storeKeyword)>0)){
					logger.error("Failed to clear existing rule");
					return false;
				};
			}
			
			daoService.addKeyword(storeKeyword);
			
			int processedItem = 0;
			List<ExcludeItemXml> eItemXmlList = eXml.getItem();
			ExcludeResult excludeResult = null;
			
			for (ExcludeItemXml eItemXml : eItemXmlList){
				excludeResult = new ExcludeResult(storeKeyword,eItemXml);
				processedItem += daoService.addExcludeResult(excludeResult)==1? 1 : 0;
			}

			if (processedItem == CollectionUtils.size(eItemXmlList)){
				return true;				
			}else{
				if(!rollBackToPreImportRuleXml(ruleId)){
					//TODO: add email notification for manual import
					logger.error("Failed to rollback");
				};
			}

		}catch (Exception e) {
			logger.error("Falied to restore exclude", e);
			return false;
		} 

		return false;
	}

	private static boolean restoreDemote(String ruleId, RuleXml xml){
		DemoteRuleXml dXml = (DemoteRuleXml) xml;
		String store = UtilityService.getStoreName();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		DemoteResult model = new DemoteResult(storeKeyword);
		SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(model);

		try {
			List<DemoteResult> preImportlist = daoService.getDemoteResultList(criteria).getList();

			if(CollectionUtils.isNotEmpty(preImportlist)){
				if (!RuleXmlUtil.createPreImportRuleXml(dXml)){
					logger.error("Failed to create pre-import rule");
					return false;
				};

				if(!(daoService.clearExcludeResult(storeKeyword)>0)){
					logger.error("Failed to clear existing rule");
					return false;
				};
			}
			
			daoService.addKeyword(storeKeyword);
			
			int processedItem = 0;
			List<DemoteItemXml> dItemXmlList = dXml.getItem();
			DemoteResult demoteResult = null;
			
			for (DemoteItemXml dItemXml : dItemXmlList){
				demoteResult = new DemoteResult(storeKeyword, dItemXml);
				processedItem += daoService.addDemoteResult(demoteResult)==1? 1 : 0;
			}

			if (processedItem == CollectionUtils.size(dItemXmlList)){
				return true;				
			}else{
				if(!rollBackToPreImportRuleXml(ruleId)){
					//TODO: add email notification for manual import
					logger.error("Failed to rollback");
				};
			}

		}catch (Exception e) {
			logger.error("Falied to restore exclude", e);
			return false;
		} 

		return false;
	}

	private static boolean restoreFacetSort(String ruleId, RuleXml xml){
		return false;
	}

	private static boolean restoreQueryCleaning(String ruleId, RuleXml xml){
		return false;
	}

	private static boolean restoreRankingRule(String ruleId, RuleXml xml){
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

	public static boolean restoreRule(String ruleId, RuleXml xml) {
		boolean isRestored = false;

		if(StringUtils.isBlank(ruleId) || xml== null){
			return isRestored; 
		}

		if (xml instanceof ElevateRuleXml){
			isRestored = RuleXmlUtil.restoreElevate(ruleId, xml);
		}else if(xml instanceof DemoteRuleXml){
			isRestored = RuleXmlUtil.restoreDemote(ruleId, xml);
		}else if(xml instanceof ExcludeRuleXml){
			isRestored = RuleXmlUtil.restoreExclude(ruleId, xml);
		}else if(xml instanceof FacetSortRuleXml){
			isRestored = RuleXmlUtil.restoreFacetSort(ruleId, xml);
		}else if(xml instanceof RedirectRuleXml){
			isRestored = RuleXmlUtil.restoreQueryCleaning(ruleId, xml);
		}else if(xml instanceof RankingRuleXml){
			isRestored = RuleXmlUtil.restoreRankingRule(ruleId, xml);
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