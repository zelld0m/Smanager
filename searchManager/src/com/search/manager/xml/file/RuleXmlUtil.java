package com.search.manager.xml.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
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
import com.search.manager.report.model.xml.FacetSortItemXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleKeywordXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionValidationEventHandler;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.StringUtil;

public class RuleXmlUtil{

	private static final String XML_FILE_TYPE = ".xml";
	
	private static Logger logger = Logger.getLogger(RuleXmlUtil.class);
	private static final String PREIMPORTPATH = PropsUtils.getValue("pre-importpath");
	private static final String PRERESTOREPATH = PropsUtils.getValue("pre-restorepath");
	@Autowired private static DaoService daoService;

	private static RuleXmlUtil instance = null;

	protected RuleXmlUtil() {
		//Exists only to defeat instantiation.
	}

	public static RuleXmlUtil getInstance() {
		if(instance == null) {
			instance = new RuleXmlUtil();
		}
		return instance;
	}

	private static boolean rollBackToBackUpRuleXml(String path, String ruleId){
		//TODO:
		return true;
	}

	private static boolean createPreBackUpRuleXml(String path, RuleXml xml){
		RuleEntity ruleEntity = xml.getRuleEntity();
		String dir = RuleXmlUtil.getRuleFileDirectory(PREIMPORTPATH, xml.getStore(), ruleEntity);
		String id = getRuleId(ruleEntity, xml.getRuleId());
		String filename = RuleXmlUtil.getFilenameByDir(dir, id);

		File dirFile = new File(dir);
		FileWriter writer = null;
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
				writer = new FileWriter(filename);
				m.marshal(new RuleXml(), writer);
			}

			return true;
		} catch (JAXBException e) {
			logger.error("Failed to marshall rule", e);
			return false;
		} catch (IOException e) {
			logger.error("Failed to create pre-import rule file", e);
			return false;
		} finally {
			try { if (writer != null) writer.close(); } catch (Exception e) {}
		}
	}

	private static boolean restoreElevate(String path, RuleXml xml){
		ElevateRuleXml eXml = (ElevateRuleXml) xml;
		String store = xml.getStore();
		String ruleId = xml.getRuleId();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		ElevateResult model = new ElevateResult(storeKeyword);
		SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(model);

		try {
			List<ElevateResult> preImportlist = daoService.getElevateResultList(criteria).getList();

			if(CollectionUtils.isNotEmpty(preImportlist)){
				if (!RuleXmlUtil.createPreBackUpRuleXml(path, eXml)){
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
				if(!rollBackToBackUpRuleXml(path, ruleId)){
					//TODO: add email notification for manual import
					logger.error("Failed to rollback");
				};
			}

		}catch (Exception e) {
			logger.error("Failed to restore elevate", e);
			return false;
		} 

		return false;
	}

	private static boolean restoreExclude(String path, RuleXml xml){
		ExcludeRuleXml eXml = (ExcludeRuleXml) xml;
		String store = xml.getStore();
		String ruleId = xml.getRuleId();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		ExcludeResult model = new ExcludeResult(storeKeyword);
		SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(model);

		try {
			List<ExcludeResult> preImportlist = daoService.getExcludeResultList(criteria).getList();

			if(CollectionUtils.isNotEmpty(preImportlist)){
				if (!RuleXmlUtil.createPreBackUpRuleXml(path, eXml)){
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
				if(!rollBackToBackUpRuleXml(path, ruleId)){
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

	private static boolean restoreDemote(String path, RuleXml xml){
		DemoteRuleXml dXml = (DemoteRuleXml) xml;
		String store = xml.getStore();
		String ruleId = xml.getRuleId();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		DemoteResult model = new DemoteResult(storeKeyword);
		SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(model);

		try {
			List<DemoteResult> preImportlist = daoService.getDemoteResultList(criteria).getList();

			if(CollectionUtils.isNotEmpty(preImportlist)){
				if (!RuleXmlUtil.createPreBackUpRuleXml(path, dXml)){
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
				if(!rollBackToBackUpRuleXml(path, ruleId)){
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

	private static boolean restoreFacetSort(String path, RuleXml xml){
		//TODO: Verify correctness, old implementation
		FacetSortRuleXml rxml = (FacetSortRuleXml) xml;
		FacetSort restoreVersion = new FacetSort(rxml);
		String store = DAOUtils.getStoreId(restoreVersion.getStore());
		String ruleId = xml.getRuleId();

		try {
			FacetSort rule = new FacetSort();
			rule.setRuleId(ruleId);
			rule.setStore(new Store(store));
			rule.setLastModifiedBy(xml.getLastModifiedBy());
			rule = daoService.getFacetSort(rule);
			
			if(rule != null){
				if (!RuleXmlUtil.createPreBackUpRuleXml(path, xml)){
					logger.error("Failed to create pre-import rule");
					return false;
				};
				daoService.deleteFacetSort(rule);
			}
			
			if (daoService.addFacetSort(restoreVersion) > 0) {
				if (CollectionUtils.isNotEmpty(rxml.getItem())) {
					FacetGroup facetGroup = new FacetGroup();
					facetGroup.setRuleId(ruleId);
					facetGroup.setLastModifiedBy(xml.getLastModifiedBy());
					List<FacetGroupItem> groupItems = new ArrayList<FacetGroupItem>();
					
					int processedItems = 0;
					int totalItems = 0;
					
					for (FacetSortItemXml facetSort: rxml.getItem()) {
						String groupId = DAOUtils.generateUniqueId();
						facetGroup.setFacetGroupType(facetSort.getGroupType());
						facetGroup.setName(facetSort.getGroupName());
						facetGroup.setSortType(facetGroup.getSortType());
						facetGroup.setSequence(facetGroup.getSequence());
						facetGroup.setId(groupId);
						totalItems++;
						processedItems += daoService.addFacetGroup(facetGroup);
						
						groupItems.clear();
						if (CollectionUtils.isNotEmpty(facetSort.getGroupItem())) {
							int i = 0;
							for (String item: facetSort.getGroupItem()) {
								groupItems.add(new FacetGroupItem(ruleId, groupId, DAOUtils.generateUniqueId(), item, ++i, xml.getLastModifiedBy()));
							}
							totalItems += facetSort.getGroupItem().size();
							processedItems += daoService.addFacetGroupItems(groupItems);
						}
					}
					
					if (processedItems == totalItems) {
						return true;
					}

				}
				else {
					return true;
				}
			}
				
			if(!rollBackToBackUpRuleXml(path, ruleId)){
				//TODO: add email notification for manual import
				logger.error("Failed to rollback");
			}
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean restoreQueryCleaning(String path, RuleXml xml) {
		if (xml instanceof RedirectRuleXml) {
			RedirectRuleXml dXml = (RedirectRuleXml) xml;
			String store = xml.getStore();
			String ruleId = xml.getRuleId();

			try {
				RedirectRule redirectRule = new RedirectRule();
				redirectRule.setStoreId(store);
				redirectRule.setRuleId(ruleId);
				redirectRule = daoService.getRedirectRule(redirectRule);

				if(redirectRule != null){
					if (!RuleXmlUtil.createPreBackUpRuleXml(path, dXml)){
						logger.error("Failed to create pre-import rule");
						return false;
					};

					RedirectRule delRel = new RedirectRule();
					delRel.setRuleId(ruleId);
					delRel.setStoreId(store);
					daoService.deleteRedirectRule(delRel);
				}

				int processedItem = 0;
				
				RuleKeywordXml keywordsXml = dXml.getRuleKeyword();
				Boolean includeKeyword = null;
				if (dXml.getRuleCondition() != null) {
					includeKeyword = dXml.getRuleCondition().isIncludeKeyword();
				}
			
				RedirectRule addRel = new RedirectRule(ruleId, dXml.getRedirectType(), dXml.getRuleName(), dXml.getDescription(), store,
						(Integer)null, (String)null, (String)null, dXml.getCreatedBy(), dXml.getLastModifiedBy(),
						dXml.getCreatedDate(), dXml.getLastModifiedDate(), dXml.getReplacementKeyword(), dXml.getDirectHit(), includeKeyword);

				if (daoService.addRedirectRule(addRel) > 0) {

					// add redirect keyword
					RedirectRule rule = new RedirectRule();
					rule.setRuleId(addRel.getRuleId());
					rule.setStoreId(store);
					rule.setLastModifiedBy(dXml.getLastModifiedBy());
					for (String keyword: keywordsXml.getKeyword()) {
						daoService.addKeyword(new StoreKeyword(store, keyword));
						rule.setSearchTerm(keyword);
						processedItem += daoService.addRedirectKeyword(rule);							
					}

					if (processedItem == CollectionUtils.size(keywordsXml.getKeyword())) {
						processedItem = 0;
						if (dXml.getRuleCondition() == null) {
							return true;
						}
						else {
							RedirectRuleCondition condition = new RedirectRuleCondition();
							condition.setRuleId(ruleId);
							condition.setStoreId(store);
							condition.setLastModifiedBy(dXml.getLastModifiedBy());
							for (String cond: dXml.getRuleCondition().getCondition()) {
								condition.setCondition(cond);
								// TODO: temporary workaround until SP is fixed. value being returned is > 1.
								if (daoService.addRedirectCondition(condition) > 0) {
									processedItem++;
								}
							}
							if (processedItem ==  CollectionUtils.size(dXml.getRuleCondition().getCondition())) {
								return true;
							}
						} 
					}
				}
				
				if(!rollBackToBackUpRuleXml(path, ruleId)){
						//TODO: add email notification for manual import
						logger.error("Failed to rollback");
				};
			} catch (Exception e) {
				logger.error("Falied to restore query cleaning", e);
				return false;
			} 
		}
		return false;
	}

	private static boolean restoreRankingRule(String path, RuleXml xml){
		//TODO: Verify correctness, old implementation
		Relevancy restoreVersion = new Relevancy((RankingRuleXml) xml);
		String store = DAOUtils.getStoreId(restoreVersion.getStore());
		String ruleId = xml.getRuleId();
		RelevancyField rf = new RelevancyField();

		try {
			Relevancy rule = new Relevancy();
			rule.setRuleId(ruleId);
			rule.setStore(new Store(store));
			rule.setLastModifiedBy(xml.getLastModifiedBy());
			rule = daoService.getRelevancy(rule);
			
			if(rule != null){
				if (!RuleXmlUtil.createPreBackUpRuleXml(path, xml)){
					logger.error("Failed to create pre-import rule");
					return false;
				};
				daoService.deleteRelevancy(rule);
			}
			
			int processedItem = 0;
			
			if (daoService.addRelevancy(restoreVersion) > 0) {
				Map<String, String> parameters = restoreVersion.getParameters();
				if (MapUtils.isNotEmpty(parameters)) {
					for (String key: parameters.keySet()) {
						rf = new RelevancyField(restoreVersion, key, parameters.get(key), restoreVersion.getCreatedBy(), restoreVersion.getLastModifiedBy(),
								restoreVersion.getCreatedDate(), restoreVersion.getLastModifiedDate());
						processedItem += daoService.addRelevancyField(rf);
					}
					
					if (processedItem == parameters.size()) {
						processedItem = 0;
						if (CollectionUtils.isNotEmpty(restoreVersion.getRelKeyword())) {
							for (RelevancyKeyword keyword : restoreVersion.getRelKeyword()) {
								keyword.setRelevancy(restoreVersion);
								processedItem += daoService.addRelevancyKeyword(keyword);
							}
							if (processedItem == restoreVersion.getRelKeyword().size()) {
								return true;							
							}
						}
						else {
							return true;
						}
					}
				}
			}
			
			if(!rollBackToBackUpRuleXml(path, ruleId)){
				//TODO: add email notification for manual import
				logger.error("Failed to rollback");
			}
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean restoreRule(RuleXml xml) {
		return RuleXmlUtil.restoreRule(xml, true);
	}
	
	public static boolean importRule(RuleXml xml) {
		return RuleXmlUtil.restoreRule(xml, false);
	}
	
	private static boolean restoreRule(RuleXml xml, boolean isVersion) {
		String path = isVersion ? PRERESTOREPATH : PREIMPORTPATH;
		boolean isRestored = false;

		if(xml  == null){
			return isRestored; 
		}

		if (xml instanceof ElevateRuleXml){
			isRestored = RuleXmlUtil .restoreElevate(path, xml);
		}else if(xml instanceof DemoteRuleXml){
			isRestored = RuleXmlUtil.restoreDemote(path, xml);
		}else if(xml instanceof ExcludeRuleXml){
			isRestored = RuleXmlUtil.restoreExclude(path, xml);
		}else if(xml instanceof FacetSortRuleXml){
			isRestored = RuleXmlUtil.restoreFacetSort(path, xml);
		}else if(xml instanceof RedirectRuleXml){
			isRestored = RuleXmlUtil.restoreQueryCleaning(path, xml);
		}else if(xml instanceof RankingRuleXml){
			isRestored = RuleXmlUtil.restoreRankingRule(path, xml);
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
		StringBuilder filePath = new StringBuilder(getRuleFileDirectory(path, store, ruleEntity)).append(File.separator).append(ruleId).append(XML_FILE_TYPE);
		return filePath.toString();
	}

	public static String getFilenameByDir(String dir, String ruleId){
		StringBuilder filePath = new StringBuilder(dir).append(File.separator).append(ruleId).append(XML_FILE_TYPE);
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
		String filename = getFilenameByDir(dir, getRuleId(ruleEntity, ruleId)) + "_backup";
		FileWriter writer = null;
		try {
			JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			writer = new FileWriter(filename);
			m.marshal(rule, writer);
			return true;
		} catch (JAXBException e) {
			logger.error("Unable to create marshaller", e);
			return false;
		} catch (Exception e) {
			logger.error("Unknown error", e);
			return false;
		} finally {
			try { if (writer != null) writer.close(); } catch (Exception e) {}
		}
	}

	public static String getRuleId(RuleEntity ruleEntity, String ruleId) {
		switch(ruleEntity) {
			case ELEVATE:
			case EXCLUDE:
			case DEMOTE: 
				return StringUtil.escapeKeyword(ruleId);
		}
		return ruleId;
	}
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		RuleXmlUtil.daoService = daoService;
	}


}