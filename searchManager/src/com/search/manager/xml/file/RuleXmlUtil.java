package com.search.manager.xml.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.Product;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchResult;
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
import com.search.manager.report.model.xml.ProductDetailsAware;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleItemXml;
import com.search.manager.report.model.xml.RuleKeywordXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.StringUtil;
import com.search.ws.SearchHelper;

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
			synchronized (RuleXmlUtil.class) {
				if (instance == null) {
					instance = new RuleXmlUtil();
				}
			}
		}
		return instance;
	}

	public static RuleXml getLatestVersion(List<RuleXml> ruleVersions) {
		RuleXml latestVersion = null;
		if (CollectionUtils.isNotEmpty(ruleVersions)) {
			for(RuleXml rule : ruleVersions){
				if(latestVersion == null || rule.getVersion() > latestVersion.getVersion()){
					latestVersion = rule;
				}
			}
		}
		return latestVersion;
	}
	
	public static RuleXml ruleToXml(String store, String ruleType, String ruleId){
		RuleXml ruleXml = new RuleXml();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		StoreKeyword sk = new StoreKeyword(store, ruleId);

		switch(ruleEntity){
		case ELEVATE:
			SearchCriteria<ElevateResult> elevateCriteria = new SearchCriteria<ElevateResult>(new ElevateResult(sk));
			List<ElevateItemXml> elevateItemXmlList = new ArrayList<ElevateItemXml>();
			try {
				List<ElevateResult> elevateItemList = daoService.getElevateResultList(elevateCriteria).getList();
				for (ElevateResult elevateResult : elevateItemList) {
					elevateItemXmlList.add(new ElevateItemXml(elevateResult));
				}
			} catch (DaoException e) {
				logger.error("Failed convert elevate rule to rule xml", e);
				return null;
			}	

			ruleXml = new ElevateRuleXml(store, ruleId, elevateItemXmlList);
			break;
		case EXCLUDE:
			SearchCriteria<ExcludeResult> excludeCriteria = new SearchCriteria<ExcludeResult>(new ExcludeResult(sk));
			List<ExcludeItemXml> excludeItemXmlList = new ArrayList<ExcludeItemXml>();
			try {
				List<ExcludeResult> excludeItemList = daoService.getExcludeResultList(excludeCriteria).getList();
				for (ExcludeResult result : excludeItemList) {
					excludeItemXmlList.add(new ExcludeItemXml(result));
				}
			} catch (DaoException e) {
				logger.error("Failed convert exclude rule to rule xml", e);
				return null;
			}	

			ruleXml = new ExcludeRuleXml(store, ruleId, excludeItemXmlList);
			break;
		case DEMOTE: 
			SearchCriteria<DemoteResult> demoteCriteria = new SearchCriteria<DemoteResult>(new DemoteResult(sk));
			List<DemoteItemXml> demoteItemXmlList = new ArrayList<DemoteItemXml>();
			try {
				List<DemoteResult> demoteItemList = daoService.getDemoteResultList(demoteCriteria).getList();
				for (DemoteResult result : demoteItemList) {
					demoteItemXmlList.add(new DemoteItemXml(result));
				}
			} catch (DaoException e) {
				logger.error("Failed convert demote rule to rule xml", e);
				return null;
			}	
			ruleXml = new DemoteRuleXml(store, ruleId, demoteItemXmlList);
			break;
		case FACET_SORT:
			FacetSort facetSort = new FacetSort();

			try {
				facetSort = daoService.getFacetSort(new FacetSort(ruleId, store));
			} catch (DaoException e) {
				logger.error("Failed convert facet sort rule to rule xml", e);
				return null;
			}

			ruleXml = new FacetSortRuleXml(facetSort);
			break;
		case QUERY_CLEANING:
			RedirectRule redirectRule = new RedirectRule();

			try {
				redirectRule = daoService.getRedirectRule(new RedirectRule(ruleId));

			} catch (DaoException e) {
				logger.error("Failed convert query cleaning rule to rule xml", e);
				return null;
			}

			ruleXml = new RedirectRuleXml(store, redirectRule);
			break;
		case RANKING_RULE:
			Relevancy relevancy = new Relevancy();

			try {
				relevancy = daoService.getRelevancyDetails(new Relevancy(ruleId));
				RecordSet<RelevancyKeyword> relevancyKeywords = daoService.getRelevancyKeywords(relevancy);
				relevancy.setRelKeyword(relevancyKeywords.getList());

			} catch (DaoException e) {
				logger.error("Failed convert ranking rule to rule xml", e);
				return null;
			}

			ruleXml = new RankingRuleXml(store, relevancy);
			break;
		}
		return ruleXml;
	}

	public static List<Product> getProductDetails(RuleXml ruleXml){

		LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();
		List<Product> productList = new ArrayList<Product>();
		ProductDetailsAware prodDetails = (ProductDetailsAware) ruleXml;
		List<? extends RuleItemXml> ruleItemList = prodDetails.getItem();
		String mapKey = "";
		String store = ruleXml.getStore();
		String keyword = ruleXml.getRuleId();

		StoreKeyword storeKeyword = new StoreKeyword(store, keyword);

		for (RuleItemXml ruleItem : ruleItemList) {
			mapKey = ruleItem.getMemberType() == MemberTypeEntity.PART_NUMBER ? ruleItem.getEdp() : ruleItem.getMemberId();
			map.put(mapKey, new Product(new SearchResult(storeKeyword, ruleItem)));
		}

		if (MapUtils.isNotEmpty(map)){
			SearchHelper.getProductsIgnoreKeyword(map, store, keyword);
			productList = new ArrayList<Product>(map.values());
		} 

		return productList;
	}

	private static RuleXml xmlFileToRuleXml(String store, String path, RuleEntity ruleEntity, String ruleId){
		String dir = RuleXmlUtil.getRuleFileDirectory(path, store, ruleEntity);
		String id = RuleXmlUtil.getRuleId(ruleEntity, ruleId);
		String filename = RuleXmlUtil.getFilenameByDir(dir, id);
		File dirFile = new File(dir);
		FileReader reader = null;

		if (!dirFile.exists()) {
			logger.error("Unable to locate directory");
			return null;
		}

		try {
			JAXBContext context = JAXBContext.newInstance(RuleXml.class);
			Unmarshaller m = context.createUnmarshaller();
			reader = new FileReader(filename);
			return (RuleXml) m.unmarshal(reader);

			//			RuleXml ruleXml = (RuleXml) m.unmarshal(reader);
			//			StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);
			//
			//			switch (ruleEntity){
			//
			//			case ELEVATE:
			//				ElevateRuleXml elevateRuleXml = (ElevateRuleXml) ruleXml;
			//				List<ElevateResult> elevateResultList = new ArrayList<ElevateResult>();
			//				List<ElevateItemXml> elevateItemXmlList = elevateRuleXml.getItem();
			//
			//				for(ElevateItemXml elevateItemXml: elevateItemXmlList){
			//					elevateResultList.add(new ElevateResult(storeKeyword, elevateItemXml));
			//				}
			//
			//				return elevateResultList;
			//			case EXCLUDE:
			//				ExcludeRuleXml excludeRuleXml = (ExcludeRuleXml) ruleXml;
			//				List<ExcludeResult> excludeResultList = new ArrayList<ExcludeResult>();
			//				List<ExcludeItemXml> excludeItemXmlList = excludeRuleXml.getItem();
			//
			//				for(ExcludeItemXml elevateItemXml: excludeItemXmlList){
			//					excludeResultList.add(new ExcludeResult(storeKeyword, elevateItemXml));
			//				}
			//
			//				return excludeResultList;
			//			case DEMOTE:
			//				DemoteRuleXml demoteRuleXml = (DemoteRuleXml) ruleXml;
			//				List<DemoteResult> demoteResultList = new ArrayList<DemoteResult>();
			//				List<DemoteItemXml> demoteItemXmlList = demoteRuleXml.getItem();
			//
			//				for(DemoteItemXml demoteItemXml: demoteItemXmlList){
			//					demoteResultList.add(new DemoteResult(storeKeyword, demoteItemXml));
			//				}
			//
			//				return demoteResultList;	
			//			case FACET_SORT:
			//				return new FacetSort((FacetSortRuleXml) ruleXml);	
			//			case QUERY_CLEANING:
			//				return new RedirectRule((RedirectRuleXml) ruleXml);	
			//			case RANKING_RULE:	
			//				return new Relevancy((RankingRuleXml) ruleXml);
			//			}

		} catch (JAXBException e) {
			logger.error("Unable to create marshaller", e);
			return null;
		} catch (Exception e) {
			logger.error("Unknown error", e);
			return null;
		}
		finally {
			try { if (reader != null) { reader.close(); } } catch (IOException e) { }
		}

		//		return null;
	}

	public static boolean ruleXmlToFile(String store, RuleEntity ruleEntity, String ruleId, RuleXml rule, String path){
		String dir = RuleXmlUtil.getRuleFileDirectory(path, store, ruleEntity);
		String id = RuleXmlUtil.getRuleId(ruleEntity, ruleId);
		String filename = RuleXmlUtil.getFilenameByDir(dir, id);
		FileWriter writer = null;

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
			if (rule != null) {
				JAXBContext context = JAXBContext.newInstance(RuleXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				writer = new FileWriter(filename);
				m.marshal(rule, writer);
				return true;
			}
		} catch (JAXBException e) {
			logger.error("Unable to create marshaller", e);
		} catch (Exception e) {
			logger.error("Unknown error", e);
		}
		finally {
			try { if (writer != null) { writer.close(); } } catch (IOException e) { }
		}
		return false;
	}

	private static boolean restoreElevate(String path, RuleXml xml, boolean createPreRestore){
		ElevateRuleXml eXml = (ElevateRuleXml) xml;
		String store = xml.getStore();
		String ruleId = xml.getRuleId();
		RuleEntity ruleEntity = xml.getRuleEntity();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		ElevateResult model = new ElevateResult(storeKeyword);
		SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(model);

		try {

			List<ElevateResult> preImportlist = daoService.getElevateResultList(criteria).getList();

			if(CollectionUtils.isNotEmpty(preImportlist)){
				if(createPreRestore){			
					if (!RuleXmlUtil.ruleXmlToFile(store, ruleEntity, ruleId, eXml, path)){
						logger.error("Failed to create pre-import rule");
						return false;
					};
				}

				if(!(daoService.clearElevateResult(storeKeyword)>0)){
					logger.error("Failed to clear existing elevate rule");
					return false;
				};
			}

			daoService.addKeyword(storeKeyword);

			int processedItem = 0;
			List<ElevateItemXml> eItemXmlList = eXml.getItem();
			ElevateResult elevateResult = null;

			for (ElevateItemXml eItemXml : eItemXmlList){
				elevateResult = new ElevateResult(storeKeyword, eItemXml);
				processedItem += daoService.addElevateResult(elevateResult) > 0 ? 1 : 0;
			}

			if (processedItem != CollectionUtils.size(eItemXmlList)){
				daoService.clearElevateResult(storeKeyword);
				RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

				if(backupXml!=null && restoreElevate(path, backupXml, false)){
					logger.info("Rollback elevate succeded");
					return true;
				}else{
					logger.error("Failed to rollback elevate");
					return false;
				}
			}else{
				return true;
			}

		}catch (Exception e) {
			logger.error("Failed to restore elevate", e);
			return false;
		} 
	}

	private static boolean restoreExclude(String path, RuleXml xml, boolean createPreRestore){
		ExcludeRuleXml eXml = (ExcludeRuleXml) xml;
		String store = xml.getStore();
		String ruleId = xml.getRuleId();
		RuleEntity ruleEntity = xml.getRuleEntity();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		ExcludeResult model = new ExcludeResult(storeKeyword);
		SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(model);

		try {
			List<ExcludeResult> preImportlist = daoService.getExcludeResultList(criteria).getList();


			if(CollectionUtils.isNotEmpty(preImportlist)){
				if(createPreRestore){			
					if (!RuleXmlUtil.ruleXmlToFile(store, ruleEntity, ruleId, eXml, path)){
						logger.error("Failed to create pre-import rule");
						return false;
					};
				}

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
				processedItem += daoService.addExcludeResult(excludeResult) > 0 ? 1 : 0;
			}

			if (processedItem != CollectionUtils.size(eItemXmlList)){
				
				daoService.clearExcludeResult(storeKeyword);
				RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

				if(backupXml!=null && restoreExclude(path, backupXml, false)){
					logger.info("Rollback of exclude rule succeded");
					return true;
				}else{
					logger.error("Failed to rollback exclude rule");
					return false;
				}
			}else{
				return true;
			}

		}catch (Exception e) {
			logger.error("Failed to restore exclude", e);
			return false;
		} 
	}

	private static boolean restoreDemote(String path, RuleXml xml, boolean createPreRestore){
		DemoteRuleXml dXml = (DemoteRuleXml) xml;
		String store = xml.getStore();
		String ruleId = xml.getRuleId();
		RuleEntity ruleEntity = xml.getRuleEntity();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		DemoteResult model = new DemoteResult(storeKeyword);
		SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(model);

		try {
			List<DemoteResult> preImportlist = daoService.getDemoteResultList(criteria).getList();

			if(CollectionUtils.isNotEmpty(preImportlist)){
				if(createPreRestore){			
					if (!RuleXmlUtil.ruleXmlToFile(store, ruleEntity, ruleId, dXml, path)){
						logger.error("Failed to create pre-import rule");
						return false;
					};
				}

				if(!(daoService.clearDemoteResult(storeKeyword)>0)){
					logger.error("Failed to clear existing demote rule");
					return false;
				};
			}

			daoService.addKeyword(storeKeyword);

			int processedItem = 0;
			List<DemoteItemXml> dItemXmlList = dXml.getItem();
			DemoteResult demoteResult = null;

			for (DemoteItemXml dItemXml : dItemXmlList){
				demoteResult = new DemoteResult(storeKeyword, dItemXml);
				processedItem += daoService.addDemoteResult(demoteResult) > 0 ? 1 : 0;
			}

			if (processedItem != CollectionUtils.size(dItemXmlList)){
			
				daoService.clearDemoteResult(storeKeyword);
				RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

				if(backupXml!=null && restoreDemote(path, backupXml, false)){
					logger.info("Rollback demote succeded");
					return true;
				}else{
					logger.error("Failed to rollback demote");
					return false;
				}
			}else{
				return true;
			}

		}catch (Exception e) {
			logger.error("Falied to restore exclude", e);
			return false;
		} 
	}

	private static boolean restoreFacetSort(String path, RuleXml xml, boolean createPreRestore){
		FacetSortRuleXml fXml = (FacetSortRuleXml) xml;
		FacetSort restoreVersion = new FacetSort(fXml);
		RuleEntity ruleEntity = xml.getRuleEntity();
		String store = DAOUtils.getStoreId(restoreVersion.getStore());
		String ruleId = xml.getRuleId();

		try {
			FacetSort rule = new FacetSort();
			rule.setRuleId(ruleId);
			rule.setStore(new Store(store));
			rule.setLastModifiedBy(xml.getLastModifiedBy());
			rule = daoService.getFacetSort(rule);

			if(rule != null){
				if(createPreRestore){
					if (!RuleXmlUtil.ruleXmlToFile(store, ruleEntity, ruleId, fXml, path)){
						logger.error("Failed to create pre-import rule");
						return false;
					};
				}
				daoService.deleteFacetSort(rule);
			}

			if (daoService.addFacetSort(restoreVersion) <= 0) {
				daoService.deleteFacetSort(rule);
				RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

				if(backupXml!=null && restoreFacetSort(path, backupXml, false)){
					logger.info("Rollback facet sort rule succeded");
					return true;
				}else{
					logger.error("Failed to rollback facet sort rule");
				}

				return false;
			}

			if (CollectionUtils.isNotEmpty(fXml.getItem())) {
				FacetGroup facetGroup = new FacetGroup();
				facetGroup.setRuleId(ruleId);
				facetGroup.setLastModifiedBy(xml.getLastModifiedBy());
				List<FacetGroupItem> groupItems = new ArrayList<FacetGroupItem>();

				int processedItems = 0;
				int totalItems = 0;

				for (FacetSortItemXml facetSort: fXml.getItem()) {
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

				if (processedItems != totalItems) {
					return false;
				}
			}

			return true;
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean restoreQueryCleaning(String path, RuleXml xml, boolean createPreRestore) {
		if (xml instanceof RedirectRuleXml) {
			RedirectRuleXml qRXml = (RedirectRuleXml) xml;
			String store = xml.getStore();
			String ruleId = xml.getRuleId();
			RuleEntity ruleEntity = xml.getRuleEntity();

			try {
				RedirectRule redirectRule = new RedirectRule();
				redirectRule.setStoreId(store);
				redirectRule.setRuleId(ruleId);
				redirectRule = daoService.getRedirectRule(redirectRule);

				if(redirectRule != null){
					if(createPreRestore){
						if (!RuleXmlUtil.ruleXmlToFile(store, ruleEntity, ruleId, qRXml, path)){
							logger.error("Failed to create pre-import rule");
							return false;
						};
					}

					RedirectRule delRel = new RedirectRule(ruleId);
					delRel.setStoreId(store);
					daoService.deleteRedirectRule(delRel);
				}

				int processedItem = 0;

				RuleKeywordXml keywordsXml = qRXml.getRuleKeyword();
				Boolean includeKeyword = null;
				if (qRXml.getRuleCondition() != null) {
					includeKeyword = qRXml.getRuleCondition().isIncludeKeyword();
				}

				RedirectRule addRel = new RedirectRule(ruleId, qRXml.getRedirectType(), qRXml.getRuleName(), qRXml.getDescription(), store,
						(Integer)null, (String)null, (String)null, qRXml.getCreatedBy(), qRXml.getLastModifiedBy(),
						qRXml.getCreatedDate(), qRXml.getLastModifiedDate(), qRXml.getReplacementKeyword(), qRXml.getDirectHit(), includeKeyword);

				if (daoService.addRedirectRule(addRel) <= 0) {
					RedirectRule delRel = new RedirectRule(ruleId);
					delRel.setStoreId(store);
					daoService.deleteRedirectRule(delRel);
					RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

					if(backupXml!=null && restoreQueryCleaning(path, backupXml, false)){
						logger.info("Rollback query cleaning rule succeded");
						return true;
					}else{
						logger.error("Failed to rollback query cleaning rule");
						return false;
					}
				}

				// add redirect keyword
				RedirectRule rule = new RedirectRule();
				rule.setRuleId(addRel.getRuleId());
				rule.setStoreId(store);
				rule.setLastModifiedBy(qRXml.getLastModifiedBy());
				for (String keyword: keywordsXml.getKeyword()) {
					daoService.addKeyword(new StoreKeyword(store, keyword));
					rule.setSearchTerm(keyword);
					processedItem += daoService.addRedirectKeyword(rule);							
				}

				if (processedItem == CollectionUtils.size(keywordsXml.getKeyword())) {
					processedItem = 0;
					if (qRXml.getRuleCondition() == null) {
						return true;
					}
					else {
						RedirectRuleCondition condition = new RedirectRuleCondition();
						condition.setRuleId(ruleId);
						condition.setStoreId(store);
						condition.setLastModifiedBy(qRXml.getLastModifiedBy());
						for (String cond: qRXml.getRuleCondition().getCondition()) {
							condition.setCondition(cond);
							// TODO: temporary workaround until SP is fixed. value being returned is > 1.
							if (daoService.addRedirectCondition(condition) > 0) {
								processedItem++;
							}
						}
						if (processedItem !=  CollectionUtils.size(qRXml.getRuleCondition().getCondition())) {
							return false;
						}
					} 
				}

				return true;

			} catch (Exception e) {
				logger.error("Falied to restore query cleaning", e);
				return false;
			} 
		}

		return false;
	}

	private static boolean restoreRankingRule(String path, RuleXml xml, boolean createPreRestore){
		RankingRuleXml rRXml = (RankingRuleXml) xml;
		Relevancy restoreVersion = new Relevancy(rRXml);
		String store = DAOUtils.getStoreId(restoreVersion.getStore());
		String ruleId = xml.getRuleId();
		RuleEntity ruleEntity = xml.getRuleEntity();
		RelevancyField rf = new RelevancyField();

		try {
			Relevancy rule = new Relevancy();
			rule.setRuleId(ruleId);
			rule.setStore(new Store(store));
			rule.setLastModifiedBy(xml.getLastModifiedBy());
			rule = daoService.getRelevancy(rule);

			if(rule != null){

				if(createPreRestore){
					if (!RuleXmlUtil.ruleXmlToFile(store, ruleEntity, ruleId, rRXml, path)){
						logger.error("Failed to create pre-import rule");
						return false;
					};
				}
				daoService.deleteRelevancy(rule);
			}

			int processedItem = 0;

			if (daoService.addRelevancy(restoreVersion) <= 0) {
				daoService.deleteRelevancy(rule);

				RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

				if(backupXml!=null && restoreRankingRule(path, backupXml, false)){
					logger.info("Rollback ranking rule succeded");
					return true;
				}else{
					logger.error("Failed to rollback ranking rule");
					return false;
				}				
			}

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
						if (processedItem != restoreVersion.getRelKeyword().size()) {
							return false;							
						}
					}
				}
			}

			return true;
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
		return restoreRule(xml, isVersion, true);
	}

	private static boolean restoreRule(RuleXml xml, boolean isVersion, boolean createPreRestore) {
		String path = isVersion ? PRERESTOREPATH : PREIMPORTPATH;
		boolean isRestored = false;

		if(xml  == null){
			return isRestored; 
		}

		if (xml instanceof ElevateRuleXml){
			isRestored = RuleXmlUtil.restoreElevate(path, xml, createPreRestore);
		}else if(xml instanceof DemoteRuleXml){
			isRestored = RuleXmlUtil.restoreDemote(path, xml, createPreRestore);
		}else if(xml instanceof ExcludeRuleXml){
			isRestored = RuleXmlUtil.restoreExclude(path, xml, createPreRestore);
		}else if(xml instanceof FacetSortRuleXml){
			isRestored = RuleXmlUtil.restoreFacetSort(path, xml, createPreRestore);
		}else if(xml instanceof RedirectRuleXml){
			isRestored = RuleXmlUtil.restoreQueryCleaning(path, xml, createPreRestore);
		}else if(xml instanceof RankingRuleXml){
			isRestored = RuleXmlUtil.restoreRankingRule(path, xml, createPreRestore);
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
