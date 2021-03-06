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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.Store;
import com.search.manager.core.model.TypeaheadBrand;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.model.TypeaheadSuggestion;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.core.service.BannerRuleService;
import com.search.manager.core.service.ImagePathService;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadBrandService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.core.service.TypeaheadSuggestionService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateProduct;
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
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.SearchResult;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.BannerItemXml;
import com.search.manager.report.model.xml.BannerRuleXml;
import com.search.manager.report.model.xml.DemoteItemXml;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateItemXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeItemXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.FacetSortGroupXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.ProductDetailsAware;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleItemXml;
import com.search.manager.report.model.xml.RuleKeywordXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.TypeaheadBrandXml;
import com.search.manager.report.model.xml.TypeaheadRuleXml;
import com.search.manager.report.model.xml.TypeaheadSuggestionXml;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.PropertiesUtils;
import com.search.manager.utility.StringUtil;
import com.search.manager.utility.Transformers;
import com.search.ws.ConfigManager;
import com.search.ws.SearchHelper;
import com.search.ws.SolrConstants;

@Component
public class RuleXmlUtil {

	private static final Logger logger =
			LoggerFactory.getLogger(RuleXmlUtil.class);

	private static final String XML_FILE_TYPE = ".xml";
	private static final String PREIMPORTPATH = PropertiesUtils.getValue("pre-importpath");
	private static final String PRERESTOREPATH = PropertiesUtils.getValue("pre-restorepath");

	@Autowired
	private DaoService daoService;
	@Autowired
	@Qualifier("ruleStatusServiceSp")
	private RuleStatusService ruleStatusService;
	@Autowired
	@Qualifier("bannerRuleServiceSp")
	private BannerRuleService bannerRuleService;
	@Autowired
	@Qualifier("bannerRuleItemServiceSp")
	private BannerRuleItemService bannerRuleItemService;
	@Autowired
	@Qualifier("imagePathServiceSp")
	private ImagePathService imagePathService;
	@Autowired
	@Qualifier("typeaheadRuleServiceSp")
	private TypeaheadRuleService typeaheadRuleService;
	@Autowired
	@Qualifier("typeaheadBrandServiceSp")
	private TypeaheadBrandService typeaheadBrandService;
	@Autowired
	@Qualifier("typeaheadSuggestionServiceSp")
	private TypeaheadSuggestionService typeaheadSuggestionService;
	@Autowired
	private ConfigManager configManager;
	@Autowired
	private SearchHelper searchHelper;
	@Autowired
	private UtilityService utilityService;

	protected RuleXmlUtil() {
		//Exists only to defeat instantiation.
	}

	// a setter method so that the Spring container can 'inject'
	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	protected void setFacetTemplateValues(String storeId, RedirectRuleCondition condition) {
		if (configManager != null && condition != null) {
			condition.setStoreId(storeId);
			condition.setFacetPrefix(configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_NAME));
			condition.setFacetTemplate(configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE));
			condition.setFacetTemplateName(configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME));
		}
	}

	protected void setFacetTemplateValues(String storeId, List<? extends RuleItemXml> list) {
		if (configManager != null && CollectionUtils.isNotEmpty(list)) {
			String facetPrefix = configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_NAME);
			String facetTemplate = configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE);
			String facetTemplateName = configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME);
			for (RuleItemXml p : list) {
				RedirectRuleCondition condition = p.getRuleCondition();
				if (condition != null) {
					condition.setStoreId(storeId);
					condition.setFacetPrefix(facetPrefix);
					condition.setFacetTemplate(facetTemplate);
					condition.setFacetTemplateName(facetTemplateName);
				}
			}
		}
	}

	public RuleXml getLatestVersion(List<RuleXml> ruleVersions) {
		RuleXml latestVersion = null;
		if (CollectionUtils.isNotEmpty(ruleVersions)) {
			for (RuleXml rule : ruleVersions) {
				if (latestVersion == null || rule.getVersion() > latestVersion.getVersion()) {
					latestVersion = rule;
				}
			}
		}
		if (latestVersion != null && latestVersion instanceof ElevateRuleXml) {
			ElevateRuleXml elevateRuleXml = (ElevateRuleXml) latestVersion;
			if (CollectionUtils.isNotEmpty(elevateRuleXml.getElevateItem())) {
				setFacetTemplateValues(elevateRuleXml.getStore(), elevateRuleXml.getElevateItem());
			}
		}
		return latestVersion;
	}

	public RuleXml currentRuleToXml(String store, String ruleType, String ruleId) {
		RuleXml ruleXml = new RuleXml();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		StoreKeyword sk = new StoreKeyword(store, ruleId);

		switch (ruleEntity) {
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
			List<FacetGroup> groups = null;
			try {
				facetSort = daoService.getFacetSort(new FacetSort(ruleId, store));
				RecordSet<FacetGroup> facetGroups = daoService.searchFacetGroup(new SearchCriteria<FacetGroup>(new FacetGroup(ruleId, "")), MatchType.MATCH_ID);
				groups = (facetGroups != null) ? facetGroups.getList() : null;
			} catch (DaoException e) {
				logger.error("Failed convert facet sort rule to rule xml", e);
				return null;
			}

			ruleXml = new FacetSortRuleXml(facetSort, groups);
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
			ruleXml.setCreatedDate(redirectRule.getCreatedDate());
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
		case BANNER:
			try {
				// Old impl: BannerRule banner = daoService.getBannerRuleById(store, ruleId);
				// // Old impl: RecordSet<BannerRuleItem> ruleItems = daoService.searchBannerRuleItem(new SearchCriteria<BannerRuleItem>(new BannerRuleItem(ruleId, store), 1, Integer.MAX_VALUE));
				BannerRule banner = bannerRuleService.searchById(store, ruleId);
				List<BannerRuleItem> ruleItems = null;
				com.search.manager.core.search.SearchResult<BannerRuleItem> searchResult = bannerRuleItemService.getRuleItemsByRuleId(store, ruleId, 1, Integer.MAX_VALUE);

				if(searchResult.getTotalCount() > 0) {
					ruleItems = searchResult.getResult();
				}

				BannerRuleXml bRuleXml = new BannerRuleXml(banner);
				bRuleXml.setItemXml(Lists.transform(ruleItems, Transformers.bannerItemRuleToXml));
				ruleXml = bRuleXml;
			} catch (CoreServiceException e) {
				logger.error("Failed convert banner rule to rule xml", e);
				return null;
			}
			break;
		case TYPEAHEAD:
			try {
				TypeaheadRule typeahead = typeaheadRuleService.searchById(store, ruleId);

				TypeaheadRuleXml typeaheadXml = new TypeaheadRuleXml(typeahead);

				ruleXml = typeaheadXml;

			} catch (CoreServiceException e) {
				logger.error("Failed convert typeahead rule to rule xml", e);
				return null;
			}
			break;
		}
		return ruleXml;
	}

	public List<Product> getProductDetails(RuleXml ruleXml, String targetStore) {

		LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();
		List<Product> productList = new ArrayList<Product>();
		ProductDetailsAware prodDetails = (ProductDetailsAware) ruleXml;
		List<? extends RuleItemXml> ruleItemList = prodDetails.getItem();
		String mapKey = "";
		String keyword = ruleXml.getRuleId();


		StoreKeyword storeKeyword = new StoreKeyword(targetStore, keyword);

		if (CollectionUtils.isNotEmpty(ruleItemList)) {
			for (RuleItemXml ruleItem : ruleItemList) {
				mapKey = ruleItem.getMemberType() == MemberTypeEntity.PART_NUMBER ? ruleItem.getEdp() : ruleItem.getMemberId();

				if (ruleXml instanceof ElevateRuleXml) {
					map.put(mapKey, new ElevateProduct(new ElevateResult(storeKeyword, (ElevateItemXml) ruleItem)));
				} else if (ruleXml instanceof DemoteRuleXml) {
					map.put(mapKey, new DemoteProduct(new DemoteResult(storeKeyword, (DemoteItemXml) ruleItem)));
				} else {
					map.put(mapKey, new Product(new SearchResult(storeKeyword, ruleItem)));
				}

			}
		}

		if (MapUtils.isNotEmpty(map)) {
			searchHelper.getProductsIgnoreKeyword(map, targetStore, keyword);
			productList = new ArrayList<Product>(map.values());
		}

		return productList;
	}

	private RuleXml xmlFileToRuleXml(String path) {
		FileReader reader = null;

		try {
			JAXBContext context = JAXBContext.newInstance(RuleXml.class);
			Unmarshaller m = context.createUnmarshaller();
			reader = new FileReader(path);
			return (RuleXml) m.unmarshal(reader);
		} catch (JAXBException e) {
			logger.error("Unable to create marshaller", e);
			return null;
		} catch (Exception e) {
			logger.error("Unknown error", e);
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
	}

	private RuleXml xmlFileToRuleXml(String store, String path, RuleEntity ruleEntity, String ruleId) {
		String dir = getRuleFileDirectory(path, store, ruleEntity);
		String id = getRuleId(ruleEntity, ruleId);
		String filename = getFilenameByDir(dir, id);
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
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}

		//		return null;
	}

	public boolean ruleXmlToFile(String store, RuleEntity ruleEntity, String ruleId, RuleXml rule, String path) {
		String dir = getRuleFileDirectory(path, store, ruleEntity);
		String id = getRuleId(ruleEntity, ruleId);
		String filename = getFilenameByDir(dir, id);
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
			} else {
				logger.info("RuleXml is null. " + filename + " cannot be imported.");
			}
		} catch (JAXBException e) {
			logger.error("Unable to create marshaller", e);
		} catch (Exception e) {
			logger.error("Unknown error", e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
			}
		}
		return false;
	}

	private boolean restoreElevate(String path, RuleXml xml, boolean createPreRestore) {
		ElevateRuleXml eXml = (ElevateRuleXml) xml;
		String store = xml.getStore();
		String ruleId = xml.getRuleId();
		RuleEntity ruleEntity = xml.getRuleEntity();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		ElevateResult model = new ElevateResult(storeKeyword);
		SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(model);

		try {

			List<ElevateResult> preImportlist = daoService.getElevateResultList(criteria).getList();

			if (CollectionUtils.isNotEmpty(preImportlist)) {
				if (createPreRestore) {
					//TODO: Save existing rule not the rule to be restored
					if (!ruleXmlToFile(store, ruleEntity, ruleId, eXml, path)) {
						logger.error("Failed to create pre-import rule");
						return false;
					};
				}

				if (!(daoService.clearElevateResult(storeKeyword) > 0)) {
					logger.error("Failed to clear existing elevate rule");
					return false;
				};
			}

			daoService.addKeyword(storeKeyword);

			int processedItem = 0;
			List<ElevateItemXml> eItemXmlList = eXml.getItem();
			ElevateResult elevateResult = null;

			if (CollectionUtils.isNotEmpty(eItemXmlList)) {
				for (ElevateItemXml eItemXml : eItemXmlList) {
					eItemXml.setCreatedBy(eXml.getCreatedBy());
					elevateResult = new ElevateResult(storeKeyword, eItemXml);
					if (elevateResult.getCondition() != null) {
						setFacetTemplateValues(store, elevateResult.getCondition());
					}
					processedItem += daoService.addElevateResult(elevateResult) > 0 ? 1 : 0;
				}

				if (processedItem != CollectionUtils.size(eItemXmlList)) {
					daoService.clearElevateResult(storeKeyword);
					RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

					if (backupXml != null && restoreElevate(path, backupXml, false)) {
						logger.info("Rollback elevate succeded");
						return true;
					} else {
						logger.error("Failed to rollback elevate");
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("Failed to restore elevate", e);
			return false;
		}
	}

	private boolean restoreExclude(String path, RuleXml xml, boolean createPreRestore) {
		ExcludeRuleXml eXml = (ExcludeRuleXml) xml;
		String store = xml.getStore();
		String ruleId = xml.getRuleId();
		RuleEntity ruleEntity = xml.getRuleEntity();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		ExcludeResult model = new ExcludeResult(storeKeyword);
		SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(model);

		try {
			List<ExcludeResult> preImportlist = daoService.getExcludeResultList(criteria).getList();


			if (CollectionUtils.isNotEmpty(preImportlist)) {
				if (createPreRestore) {
					//TODO: Save existing rule not the rule to be restored
					if (!ruleXmlToFile(store, ruleEntity, ruleId, eXml, path)) {
						logger.error("Failed to create pre-import rule");
						return false;
					};
				}

				if (!(daoService.clearExcludeResult(storeKeyword) > 0)) {
					logger.error("Failed to clear existing rule");
					return false;
				};
			}

			daoService.addKeyword(storeKeyword);

			int processedItem = 0;
			List<ExcludeItemXml> eItemXmlList = eXml.getItem();
			ExcludeResult excludeResult = null;

			if (CollectionUtils.isNotEmpty(eItemXmlList)) {
				for (ExcludeItemXml eItemXml : eItemXmlList) {
					eItemXml.setCreatedBy(eXml.getCreatedBy());
					excludeResult = new ExcludeResult(storeKeyword, eItemXml);
					if (excludeResult.getCondition() != null) {
						setFacetTemplateValues(store, excludeResult.getCondition());
					}
					processedItem += daoService.addExcludeResult(excludeResult) > 0 ? 1 : 0;
				}

				if (processedItem != CollectionUtils.size(eItemXmlList)) {

					daoService.clearExcludeResult(storeKeyword);
					RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

					if (backupXml != null && restoreExclude(path, backupXml, false)) {
						logger.info("Rollback of exclude rule succeded");
						return true;
					} else {
						logger.error("Failed to rollback exclude rule");
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("Failed to restore exclude", e);
			return false;
		}
	}

	private boolean restoreDemote(String path, RuleXml xml, boolean createPreRestore) {
		DemoteRuleXml dXml = (DemoteRuleXml) xml;
		String store = xml.getStore();
		String ruleId = xml.getRuleId();
		RuleEntity ruleEntity = xml.getRuleEntity();
		StoreKeyword storeKeyword = new StoreKeyword(store, ruleId);

		DemoteResult model = new DemoteResult(storeKeyword);
		SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(model);

		try {
			List<DemoteResult> preImportlist = daoService.getDemoteResultList(criteria).getList();

			if (CollectionUtils.isNotEmpty(preImportlist)) {
				if (createPreRestore) {
					//TODO: Save existing rule not the rule to be restored
					if (!ruleXmlToFile(store, ruleEntity, ruleId, dXml, path)) {
						logger.error("Failed to create pre-import rule");
						return false;
					};
				}

				if (!(daoService.clearDemoteResult(storeKeyword) > 0)) {
					logger.error("Failed to clear existing demote rule");
					return false;
				};
			}

			daoService.addKeyword(storeKeyword);

			int processedItem = 0;
			List<DemoteItemXml> dItemXmlList = dXml.getItem();
			DemoteResult demoteResult = null;

			if (CollectionUtils.isNotEmpty(dItemXmlList)) {
				for (DemoteItemXml dItemXml : dItemXmlList) {
					dItemXml.setCreatedBy(dXml.getCreatedBy());
					demoteResult = new DemoteResult(storeKeyword, dItemXml);
					if (demoteResult.getCondition() != null) {
						setFacetTemplateValues(store, demoteResult.getCondition());
					}
					processedItem += daoService.addDemoteResult(demoteResult) > 0 ? 1 : 0;
				}

				if (processedItem != CollectionUtils.size(dItemXmlList)) {

					daoService.clearDemoteResult(storeKeyword);
					RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

					if (backupXml != null && restoreDemote(path, backupXml, false)) {
						logger.info("Rollback demote succeded");
						return true;
					} else {
						logger.error("Failed to rollback demote");
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("Falied to restore exclude", e);
			return false;
		}
	}

	private boolean restoreFacetSort(String path, RuleXml xml, boolean createPreRestore) {
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

			if (rule != null) {
				if (createPreRestore) {
					//TODO: Save existing rule not the rule to be restored
					if (!ruleXmlToFile(store, ruleEntity, ruleId, fXml, path)) {
						logger.error("Failed to create pre-import rule");
						return false;
					};
				}
				daoService.deleteFacetSort(rule);
			}

			if (daoService.addFacetSort(restoreVersion) <= 0) {
				daoService.deleteFacetSort(rule);
				RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

				if (backupXml != null && restoreFacetSort(path, backupXml, false)) {
					logger.info("Rollback facet sort rule succeded");
					return true;
				} else {
					logger.error("Failed to rollback facet sort rule");
				}

				return false;
			}

			if (CollectionUtils.isNotEmpty(fXml.getGroups())) {
				FacetGroup facetGroup = new FacetGroup();
				facetGroup.setRuleId(ruleId);
				facetGroup.setLastModifiedBy(xml.getLastModifiedBy());
				List<FacetGroupItem> groupItems = new ArrayList<FacetGroupItem>();

				int processedItems = 0;
				int totalItems = 0;

				for (FacetSortGroupXml facetSort : fXml.getGroups()) {
					String groupId = DAOUtils.generateUniqueId();
					facetGroup.setFacetGroupType(facetSort.getGroupType());
					facetGroup.setName(facetSort.getGroupName());
					facetGroup.setSortType(facetSort.getSortType());
					facetGroup.setId(groupId);
					facetGroup.setCreatedBy(fXml.getCreatedBy());
					totalItems++;
					processedItems += daoService.addFacetGroup(facetGroup);

					groupItems.clear();
					if (CollectionUtils.isNotEmpty(facetSort.getGroupItem())) {
						int i = 0;
						for (String item : facetSort.getGroupItem()) {
							FacetGroupItem facetGroupItem = new FacetGroupItem(ruleId, groupId, DAOUtils.generateUniqueId(), item, ++i, xml.getLastModifiedBy());
							facetGroupItem.setCreatedBy(fXml.getCreatedBy());
							groupItems.add(facetGroupItem);
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

	private boolean restoreQueryCleaning(String path, RuleXml xml, boolean createPreRestore) {
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

				if (redirectRule != null) {
					if (createPreRestore) {
						//TODO: Save existing rule not the rule to be restored
						if (!ruleXmlToFile(store, ruleEntity, ruleId, qRXml, path)) {
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
						(Integer) null, (String) null, (String) null, qRXml.getCreatedBy(), qRXml.getLastModifiedBy(),
						qRXml.getCreatedDate(), qRXml.getLastModifiedDate(), qRXml.getReplacementKeyword(), qRXml.getDirectHit(), includeKeyword,
						qRXml.getReplaceKeywordMessageType(), qRXml.getReplaceKeywordMessageCustomText());

				if (daoService.addRedirectRule(addRel) <= 0) {
					RedirectRule delRel = new RedirectRule(ruleId);
					delRel.setStoreId(store);
					daoService.deleteRedirectRule(delRel);
					RuleXml backupXml = xmlFileToRuleXml(store, path, ruleEntity, ruleId);

					if (backupXml != null && restoreQueryCleaning(path, backupXml, false)) {
						logger.info("Rollback query cleaning rule succeded");
						return true;
					} else {
						logger.error("Failed to rollback query cleaning rule");
						return false;
					}
				}

				// add redirect keyword
				RedirectRule rule = new RedirectRule();
				rule.setRuleId(addRel.getRuleId());
				rule.setStoreId(store);
				rule.setCreatedBy(qRXml.getCreatedBy());
				rule.setLastModifiedBy(qRXml.getLastModifiedBy());
				List<String> keywords = keywordsXml.getKeyword();

				if (CollectionUtils.isNotEmpty(keywords)) {
					for (String keyword : keywords) {
						daoService.addKeyword(new StoreKeyword(store, keyword));
						rule.setSearchTerm(keyword);
						processedItem += daoService.addRedirectKeyword(rule);
					}
				}

				if (processedItem == (keywords == null ? 0 : keywords.size())) {
					processedItem = 0;
					if (qRXml.getRuleCondition() == null) {
						return true;
					} else {
						RedirectRuleCondition condition = new RedirectRuleCondition();
						condition.setRuleId(ruleId);
						condition.setStoreId(store);
						setFacetTemplateValues(store, condition);
						condition.setCreatedBy(qRXml.getCreatedBy());
						condition.setLastModifiedBy(qRXml.getLastModifiedBy());

						if (qRXml.getRuleCondition() != null && CollectionUtils.isNotEmpty(qRXml.getRuleCondition().getCondition())) {
							for (String cond : qRXml.getRuleCondition().getCondition()) {
								condition.setCondition(cond);
								// TODO: temporary workaround until SP is fixed. value being returned is > 1.
								if (daoService.addRedirectCondition(condition) > 0) {
									processedItem++;
								}
							}

							if (processedItem != CollectionUtils.size(qRXml.getRuleCondition().getCondition())) {
								return false;
							}
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

	private boolean restoreRankingRule(String path, RuleXml xml, boolean createPreRestore) {
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

			if (rule != null) {

				if (createPreRestore) {
					if (!ruleXmlToFile(store, ruleEntity, ruleId, rRXml, path)) {
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

				if (backupXml != null && restoreRankingRule(path, backupXml, false)) {
					logger.info("Rollback ranking rule succeded");
					return true;
				} else {
					logger.error("Failed to rollback ranking rule");
					return false;
				}
			}

			Map<String, String> parameters = restoreVersion.getParameters();
			if (MapUtils.isNotEmpty(parameters)) {
				for (String key : parameters.keySet()) {
					rf = new RelevancyField(restoreVersion, key, parameters.get(key), restoreVersion.getCreatedBy(), restoreVersion.getLastModifiedBy(),
							restoreVersion.getCreatedDate(), restoreVersion.getLastModifiedDate());
					processedItem += daoService.addRelevancyField(rf);
				}

				if (processedItem == parameters.size()) {
					processedItem = 0;
					List<RelevancyKeyword> keywords = restoreVersion.getRelKeyword();

					if (CollectionUtils.isNotEmpty(keywords)) {
						for (RelevancyKeyword relKeyword : keywords) {
							daoService.addKeyword(new StoreKeyword(store, relKeyword.getKeyword().getKeywordId()));
							relKeyword.setRelevancy(restoreVersion);
							relKeyword.setCreatedBy(rRXml.getCreatedBy());
							processedItem += daoService.addRelevancyKeyword(relKeyword);
						}
						if (processedItem != keywords.size()) {
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

	private boolean restoreBannerRule(String path, RuleXml xml, boolean createPreRestore) {
		String username = utilityService.getUsername();
		String store = utilityService.getStoreId();

		try {
			BannerRuleXml bannerRuleXml = (BannerRuleXml) xml;
			BannerRule bannerRule = new BannerRule(bannerRuleXml);

			bannerRule.setLastModifiedBy(username);
			List<BannerRuleItem> items = new ArrayList<BannerRuleItem>();

			if (bannerRuleXml.getItemXml() != null) {
				for (BannerItemXml itemXml : bannerRuleXml.getItemXml()) {
					//	Old impl: ImagePath imagePath = daoService.getBannerImagePath(new ImagePath(store, itemXml.getImagePathId(), null));
					ImagePath imagePath = imagePathService.searchById(store, itemXml.getImagePathId());
					items.add(new BannerRuleItem(bannerRule, itemXml.getMemberId(), itemXml.getPriority(), itemXml
							.getStartDate(), itemXml.getEndDate(), itemXml.getImageAlt(), itemXml.getLinkPath(), itemXml
							.getDescription(), imagePath, itemXml.getDisabled(), itemXml.getOpenNewWindow()));
				}
			}

			// get current rules
			// Old impl: BannerRule crule = daoService.getBannerRuleById(store, xml.getRuleId());
			BannerRule crule = bannerRuleService.searchById(store, xml.getRuleId());
			List<BannerRuleItem> citems = null;

			if (crule != null) {
				// Old impl: citems = daoService.searchBannerRuleItem(new SearchCriteria<BannerRuleItem>(new BannerRuleItem(xml.getRuleId(), store))).getList();
				citems = bannerRuleItemService.getRuleItemsByRuleId(store, xml.getRuleId(), -1, -1).getResult();
				// create backup first
				if (createPreRestore) {
					BannerRuleXml cruleXml = new BannerRuleXml(crule);
					cruleXml.setItemXml(Lists.transform(citems, Transformers.bannerItemRuleToXml));

					if (!ruleXmlToFile(store, RuleEntity.BANNER, xml.getRuleId(), cruleXml, path)) {
						logger.error("Failed to create pre-restore for Banner");
						return false;
					}
				}

				// delete current rule items
				for (BannerRuleItem citem : citems) {
					// Old impl: daoService.deleteBannerRuleItem(citem);
					bannerRuleItemService.delete(citem);
				}

				// delete current rule
				// Old impl: daoService.deleteBannerRule(crule);
				bannerRuleService.delete(crule);
			}

			// start re-inserting rules
			try {
				// add version rule
				// Old impl: int updateCount = daoService.addBannerRule(bannerRule);
				int updateCount = 0;
				if(bannerRuleService.add(bannerRule) != null) {
					updateCount++;
				}

				// add all rule items
				for (BannerRuleItem item : items) {
					// Old impl: updateCount += daoService.addBannerRuleItem(item);
					bannerRuleItemService.add(item);
					updateCount++;
				}

				if (updateCount < 1 + items.size()) {
					throw new DaoException("Update count should be " + (1 + items.size()) + " but was only " + updateCount);
				}
				return true;
			} catch (DaoException de) {
				// not successful restoring rule version
				logger.error("Unable to restore version. Rolling back...", de);

				// ROLLBACK
				// delete version
				for (BannerRuleItem item : items) {
					// Old impl: daoService.deleteBannerRuleItem(item);
					bannerRuleItemService.delete(item);
				}
				// Old impl: daoService.deleteBannerRule(bannerRule);
				bannerRuleService.delete(bannerRule);

				// add current version
				// Old impl: daoService.addBannerRule(crule);
				bannerRuleService.add(crule);

				// add current items
				for (BannerRuleItem citem : citems) {
					// Old impl: daoService.addBannerRuleItem(citem);
					bannerRuleItemService.add(citem);
				}
			}
		} catch (CoreServiceException e) {
			logger.error("Error occurred in restoreBannerRule.", e);
		}

		return false;
	}

	private boolean restoreTypeaheadRule(String path, RuleXml xml, boolean createPreRestore) {
		String username = utilityService.getUsername();

		try {
			TypeaheadRuleXml typeaheadRuleXml = (TypeaheadRuleXml) xml;

			String ruleId = xml.getRuleId();

			TypeaheadRule query = new TypeaheadRule();
			query.setStoreId(xml.getStore());
			query.setRuleName(xml.getRuleName());
			
			com.search.manager.core.search.SearchResult<TypeaheadRule> result = typeaheadRuleService.search(query);
			
			TypeaheadRule typeaheadRule = result.getTotalCount() > 0 ? result.getList().get(0) : null;
			
			if(typeaheadRule == null) {
				typeaheadRule = new TypeaheadRule();
				typeaheadRule.setStoreId(xml.getStore());
				typeaheadRule.setRuleName(xml.getRuleName());
				typeaheadRule.setCreatedBy(xml.getCreatedBy());
				typeaheadRule.setRuleId(xml.getRuleId());
				
				typeaheadRuleService.add(typeaheadRule);
			}
			
			//Delete Keyword Attributes
			typeaheadRuleService.deleteSections(typeaheadRule);
			//Insert Keyword Attributes
			typeaheadRule.initSections(typeaheadRuleXml);
			typeaheadRuleService.addSections(typeaheadRule);
			//Update SP will not work if the ruleName is the same.
			typeaheadRule.setRuleName(null);
			typeaheadRule.setPriority(typeaheadRuleXml.getPriority());
			typeaheadRule.setLastModifiedBy(username);
			typeaheadRule.setLastModifiedDate(new DateTime());
			typeaheadRule.setDisabled(typeaheadRuleXml.getDisabled());
			typeaheadRule = typeaheadRuleService.update(typeaheadRule);
			
			return true;
		} catch (CoreServiceException e) {
			logger.error("Error occurred in typeaheadRule.", e);
		}

		return false;
	}

	public boolean restoreRule(RuleXml xml) {
		return restoreRule(xml, true);
	}

	public boolean importRule(RuleXml xml) {
		return restoreRule(xml, false);
	}

	private boolean restoreRule(RuleXml xml, boolean isVersion) {
		return restoreRule(xml, isVersion, true);
	}

	public boolean saveRuleXml(RuleXml xml, String location, long version) {
		FileWriter writer = null;

		try {
			JAXBContext context = JAXBContext.newInstance(RuleXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			writer = new FileWriter(location);
			m.marshal(xml, writer);
			return true;
		} catch (JAXBException e) {
			logger.error("Unable to create marshaller", e);
			return false;
		} catch (Exception e) {
			logger.error("Unknown error", e);
			return false;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public RuleXml loadVersion(String file) {
		return xmlFileToRuleXml(file);
	}

	private boolean restoreRule(RuleXml xml, boolean isVersion, boolean createPreRestore) {
		String path = isVersion ? PRERESTOREPATH : PREIMPORTPATH;
		boolean isRestored = false;

		if (xml == null) {
			return isRestored;
		}
		
		try {
			RuleStatus ruleStatus = ruleStatusService.getRuleStatus(xml.getStore(), xml.getRuleEntity().getName(), xml.getRuleId());
			
			if(ruleStatus != null && ruleStatus.isLocked())
				return isRestored;
			
		} catch (CoreServiceException e) {
			e.printStackTrace();
		}

		if (xml instanceof ElevateRuleXml) {
			isRestored = restoreElevate(path, xml, createPreRestore);
		} else if (xml instanceof DemoteRuleXml) {
			isRestored = restoreDemote(path, xml, createPreRestore);
		} else if (xml instanceof ExcludeRuleXml) {
			isRestored = restoreExclude(path, xml, createPreRestore);
		} else if (xml instanceof FacetSortRuleXml) {
			isRestored = restoreFacetSort(path, xml, createPreRestore);
		} else if (xml instanceof RedirectRuleXml) {
			isRestored = restoreQueryCleaning(path, xml, createPreRestore);
		} else if (xml instanceof RankingRuleXml) {
			isRestored = restoreRankingRule(path, xml, createPreRestore);
		} else if (xml instanceof BannerRuleXml) {
			isRestored = restoreBannerRule(path, xml, createPreRestore);
		} else if (xml instanceof TypeaheadRuleXml) {
			isRestored = restoreTypeaheadRule(path, xml, createPreRestore);
		}
		return isRestored;
	}

	public void deleteFile(String filepath) throws IOException {
		File file = new File(filepath);

		if (file.exists() && !file.delete()) {
			file.deleteOnExit();
		}
	}

	public String getFilename(String path, String store, RuleEntity ruleEntity, String ruleId) {
		StringBuilder filePath = new StringBuilder(getRuleFileDirectory(path, store, ruleEntity)).append(File.separator).append(ruleId).append(XML_FILE_TYPE);
		return filePath.toString();
	}

	public String getFilenameByDir(String dir, String ruleId) {
		StringBuilder filePath = new StringBuilder(dir).append(File.separator).append(ruleId).append(XML_FILE_TYPE);
		return filePath.toString();
	}

	public String getRuleFileDirectory(String path, String store, RuleEntity ruleEntity) {
		StringBuilder sb = new StringBuilder();
		List<String> values = ruleEntity.getValues();
		String directory = CollectionUtils.isNotEmpty(values) ? values.get(0) : ruleEntity.name();
		sb.append(path).append(File.separator).append(store).append(File.separator).append(directory);
		return sb.toString();
	}

	public boolean backUpRule(String path, String store, RuleEntity ruleEntity, String ruleId, Object rule) {
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
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public String getRuleId(RuleEntity ruleEntity, String ruleId) {
		switch (ruleEntity) {
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE:
			return StringUtil.escapeKeyword(ruleId);
		}
		return ruleId;
	}

}
