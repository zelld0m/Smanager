package com.search.converter.utility;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
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
import com.search.manager.report.model.xml.RuleXml;

public class RuleToXMLConverter {
	private static ApplicationContext context;
	private static Properties properties = null;
	private static final Logger logger = Logger.getLogger(RuleToXMLConverter.class);
	private static final ExecutorService execService = Executors.newCachedThreadPool();
	@Autowired private static DaoService daoService;  
	
	public RuleToXMLConverter() {
		this.initConfig();
	}

	public boolean initConfig(){
		try {

			FileInputStream inStream = new FileInputStream("config/converter.properties");
			properties = new Properties(System.getProperties());
			properties.load(inStream);

			String databaseContext = properties.getProperty("database.context");
			String log4jProperties = properties.getProperty("log4j.properties");

			PropertyConfigurator.configure(log4jProperties);
			context = new FileSystemXmlApplicationContext(databaseContext);

			return true;
		} catch (Exception e) {
			logger.error("Initialization of config failed", e);
			return false;
		}	
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

	public static void main(String[] args) {
		RuleToXMLConverter ruleToXMLConverter = new RuleToXMLConverter();

		try {
			
			ruleToXMLConverter.toString();
			RecordSet<StoreKeyword> storeKeywordSet = daoService.getAllKeywords("pcmallcap");
			
			for(StoreKeyword keyword : storeKeywordSet.getList()){
				logger.info(keyword.getKeywordTerm());
			}
			
		} catch (DaoException de) {
			logger.error("DaoException", de);
		}
		
		ExecutorCompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(execService);

		Future<Integer> elevateRule = null;
		Future<Integer> excludeRule = null;
		Future<Integer> demoteRule = null;
		Future<Integer> facetSortRule = null;
		Future<Integer> convertRankingRule = null;

		int tasks = 0;

		try {

			while (tasks > 0) {
				Future<Integer> completed = completionService.take();

				if (completed.equals(elevateRule)) {
					logger.debug("Elevated item count: " + completed.get());
				}

				tasks--;
			}

		} catch (InterruptedException e) {
			logger.error("Interrupted exception", e);
		} catch (Throwable t) {
			logger.error("Throwable", t);
		}
	}
}