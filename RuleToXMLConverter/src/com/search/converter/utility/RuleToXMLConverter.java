package com.search.converter.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
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
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
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
import com.search.manager.report.model.xml.RuleXml;

public class RuleToXMLConverter {

	private static final Logger LOGGER = Logger.getLogger(RuleToXMLConverter.class);
	private static DaoService daoService; 
	private static DaoService daoServiceRuleStatus; 
	private static Properties properties = new Properties(System.getProperties());
	private static ExecutorService produceExec = Executors.newCachedThreadPool();
	private static ExecutorService consumeExec = Executors.newCachedThreadPool();
	private static ApplicationContext context;
	private static String store;  

	public static RuleXml ruleToXml(String store, RuleEntity ruleEntity, String ruleId){
		RuleXml ruleXml = new RuleXml();
		StoreKeyword sk = new StoreKeyword(store, ruleId);

		switch(ruleEntity){

		case ELEVATE:
			SearchCriteria<ElevateResult> elevateCriteria = new SearchCriteria<ElevateResult>(new ElevateResult(sk));
			List<ElevateItemXml> elevateItemXmlList = new ArrayList<ElevateItemXml>();
			try {
				List<ElevateResult> elevateItemList = daoService.getElevateResultList(elevateCriteria).getList();
				if(CollectionUtils.isNotEmpty(elevateItemList)){
					for (ElevateResult elevateResult : elevateItemList) {
						elevateItemXmlList.add(new ElevateItemXml(elevateResult));
					}
				}else{
					return null;
				}
			} catch (DaoException e) {
				LOGGER.error("Failed convert elevate rule to rule xml", e);
				return null;
			}	

			ruleXml = new ElevateRuleXml(store, ruleId, elevateItemXmlList);
			break;

		case EXCLUDE:
			SearchCriteria<ExcludeResult> excludeCriteria = new SearchCriteria<ExcludeResult>(new ExcludeResult(sk));
			List<ExcludeItemXml> excludeItemXmlList = new ArrayList<ExcludeItemXml>();
			try {
				List<ExcludeResult> excludeItemList = daoService.getExcludeResultList(excludeCriteria).getList();
				if(CollectionUtils.isNotEmpty(excludeItemList)){
					for (ExcludeResult result : excludeItemList) {
						excludeItemXmlList.add(new ExcludeItemXml(result));
					}
				}else{
					return null;
				}
			} catch (DaoException e) {
				LOGGER.error("Failed convert exclude rule to rule xml", e);
				return null;
			}	

			ruleXml = new ExcludeRuleXml(store, ruleId, excludeItemXmlList);
			break;

		case DEMOTE: 
			SearchCriteria<DemoteResult> demoteCriteria = new SearchCriteria<DemoteResult>(new DemoteResult(sk));
			List<DemoteItemXml> demoteItemXmlList = new ArrayList<DemoteItemXml>();
			try {
				List<DemoteResult> demoteItemList = daoService.getDemoteResultList(demoteCriteria).getList();
				if(CollectionUtils.isNotEmpty(demoteItemList)){
					for (DemoteResult result : demoteItemList) {
						demoteItemXmlList.add(new DemoteItemXml(result));
					}
				}else{
					return null;
				}
			} catch (DaoException e) {
				LOGGER.error("Failed convert demote rule to rule xml", e);
				return null;
			}	
			ruleXml = new DemoteRuleXml(store, ruleId, demoteItemXmlList);
			break;

		case FACET_SORT:
			FacetSort facetSort = new FacetSort();

			try {
				facetSort = daoService.getFacetSort(new FacetSort(ruleId, store));
			} catch (DaoException e) {
				LOGGER.error("Failed convert facet sort rule to rule xml", e);
				return null;
			}

			ruleXml = new FacetSortRuleXml(facetSort); // check constructor
			ruleXml.setRuleId(ruleId); //TODO: Why is not set?? 
			ruleXml.setRuleName(facetSort.getRuleName());
			ruleXml.setStore(store);
			break;

		case QUERY_CLEANING:
			RedirectRule redirectRule = new RedirectRule();

			try {
				redirectRule = daoService.getRedirectRule(new RedirectRule(ruleId));

			} catch (DaoException e) {
				LOGGER.error("Failed convert query cleaning rule to rule xml", e);
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
				LOGGER.error("Failed convert ranking rule to rule xml", e);
				return null;
			}

			ruleXml = new RankingRuleXml(store, relevancy);
			break;
		}

		return ruleXml;
	}

	public static void main(String[] args) {

		//Initialize
		try {
			PropertyConfigurator.configure("config/log4j.properties");
			properties.load(new FileInputStream("config/converter.properties"));
			context = new FileSystemXmlApplicationContext(
					"/WebContent/WEB-INF/spring/db-rule-context.xml",
					"/WebContent/WEB-INF/spring/db-rulestatus-context.xml");
			
			daoService = (DaoService) context.getBean("daoService");
			daoServiceRuleStatus = (DaoService) context.getBean("daoServiceRuleStatus");
			
			store = properties.getProperty("store");
		} catch (FileNotFoundException e) {
			LOGGER.error("File not found", e);
			System.exit(1);
		} catch (IOException e) {
			LOGGER.error("I/O Exception", e);
			System.exit(1);
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			System.exit(1);
		}	

		ExecutorCompletionService<List<RuleXml>> completionService = new ExecutorCompletionService<List<RuleXml>>(produceExec);

		Future<List<RuleXml>> futureElevateRule = null;
		Future<List<RuleXml>> futureExcludeRule = null;
		Future<List<RuleXml>> futureDemoteRule = null;
		Future<List<RuleXml>> futureFacetSortRule = null;
		Future<List<RuleXml>> futureQueryCleaningRule = null;
		Future<List<RuleXml>> futureRankingRule = null;

		try {
			final List<StoreKeyword> keywordList = daoService.getAllKeywords(store).getList();

			SearchCriteria<FacetSort> facetSortCriteria = new SearchCriteria<FacetSort>(new FacetSort("", "", store));
			final List<FacetSort> facetSortList = daoService.searchFacetSort(facetSortCriteria, MatchType.LIKE_NAME).getList();

			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setStoreId(store);
			redirectRule.setRuleName("");
			SearchCriteria<RedirectRule> queryCleaningCriteria = new SearchCriteria<RedirectRule>(redirectRule);
			final List<RedirectRule> queryCleaningList = daoService.searchRedirectRule(queryCleaningCriteria, MatchType.LIKE_NAME).getList();

			Relevancy relevancy = new Relevancy();
			relevancy.setStore(new Store(store));
			relevancy.setRelevancyName("");
			SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(relevancy);
			final List<Relevancy> rankingRuleList = daoService.searchRelevancy(criteria, MatchType.LIKE_NAME).getList();

			int tasks = 0;

			//Transform to RuleXml
			if(CollectionUtils.isNotEmpty(rankingRuleList)){
				futureRankingRule = completionService.submit(new Callable<List<RuleXml>>() {
					@Override
					public List<RuleXml> call() throws Exception {
						List<RuleXml> ruleXmlList = new ArrayList<RuleXml>();
						int total = rankingRuleList.size();
						for(int i=0; i< total; i++){
							RuleXml rxml = RuleToXMLConverter.ruleToXml(store, RuleEntity.RANKING_RULE, rankingRuleList.get(i).getRuleId());
							if(rxml!=null){
								LOGGER.info(String.format("Ranking Rule to RuleXML %d of %d : %s (%s)", i+1, total, rankingRuleList.get(i).getRuleName(), rankingRuleList.get(i).getRuleId()));
								ruleXmlList.add(rxml);
							}else{
								LOGGER.info(String.format("No Ranking Rule to RuleXML %d of %d : %s (%s)", i+1, total, rankingRuleList.get(i).getRuleName(), rankingRuleList.get(i).getRuleId()));
							}
						}
						return ruleXmlList;
					}
				});
				tasks++;
			}else{
				LOGGER.info(String.format("No Ranking Rule retrieved"));
			}	

			if(CollectionUtils.isNotEmpty(facetSortList)){
				futureFacetSortRule = completionService.submit(new Callable<List<RuleXml>>() {
					@Override
					public List<RuleXml> call() throws Exception {
						List<RuleXml> ruleXmlList = new ArrayList<RuleXml>();
						int total = facetSortList.size();
						for(int i=0; i< total; i++){
							RuleXml rxml = RuleToXMLConverter.ruleToXml(store, RuleEntity.FACET_SORT, facetSortList.get(i).getRuleId());
							if(rxml!=null){
								LOGGER.info(String.format("Facet Sort to RuleXML %d of %d : %s (%s)", i+1, total, facetSortList.get(i).getRuleName(), facetSortList.get(i).getRuleId()));
								ruleXmlList.add(rxml);
							}else{
								LOGGER.info(String.format("No Facet Sort to RuleXML %d of %d : %s (%s)", i+1, total, facetSortList.get(i).getRuleName(), facetSortList.get(i).getRuleId()));
							}
						}
						return ruleXmlList;
					}
				});
				tasks++;
			}else{
				LOGGER.info(String.format("No Facet Sort rule retrieved"));
			}	

			if(CollectionUtils.isNotEmpty(queryCleaningList)){
				futureQueryCleaningRule = completionService.submit(new Callable<List<RuleXml>>() {
					@Override
					public List<RuleXml> call() throws Exception {
						List<RuleXml> ruleXmlList = new ArrayList<RuleXml>();
						int total = queryCleaningList.size();
						for(int i=0; i< total; i++){
							RuleXml rxml = RuleToXMLConverter.ruleToXml(store, RuleEntity.QUERY_CLEANING, queryCleaningList.get(i).getRuleId());
							if(rxml!=null){
								LOGGER.info(String.format("Query Cleaning to RuleXML %d of %d : %s (%s)", i+1, total, queryCleaningList.get(i).getRuleName(), queryCleaningList.get(i).getRuleId()));
								ruleXmlList.add(rxml);
							}else{
								LOGGER.info(String.format("No Query Cleaning to RuleXML %d of %d : %s (%s)", i+1, total, queryCleaningList.get(i).getRuleName(), queryCleaningList.get(i).getRuleId()));
							}
						}
						return ruleXmlList;
					}
				});
				tasks++;
			}else{
				LOGGER.info(String.format("No Query Cleaning rule retrieved"));
			}		

			if(CollectionUtils.isNotEmpty(keywordList)){
				futureElevateRule = completionService.submit(new Callable<List<RuleXml>>() {
					@Override
					public List<RuleXml> call() throws Exception {
						List<RuleXml> ruleXmlList = new ArrayList<RuleXml>();
						try {
							int total = keywordList.size();

							for(int i=0; i< total; i++)
							{

								RuleXml rxml = RuleToXMLConverter.ruleToXml(store, RuleEntity.ELEVATE, keywordList.get(i).getKeywordTerm());
								if(rxml!=null){
									LOGGER.info(String.format("Elevate to RuleXML %d of %d : %s", i+1, total, keywordList.get(i).getKeywordTerm()));
									ruleXmlList.add(rxml);
								}else{
									LOGGER.info(String.format("No Elevate to RuleXML %d of %d : %s", i+1, total, keywordList.get(i).getKeywordTerm()));
								}
							}
						} catch (Exception e) {
							LOGGER.error(e);
						}
						catch (Throwable e) {
							LOGGER.error(e);
						}
						return ruleXmlList;
					}
				});
				tasks++;

				futureExcludeRule = completionService.submit(new Callable<List<RuleXml>>() {
					@Override
					public List<RuleXml> call() throws Exception {
						List<RuleXml> ruleXmlList = new ArrayList<RuleXml>();
						int total = keywordList.size();
						for(int i=0; i< total; i++){
							RuleXml rxml = RuleToXMLConverter.ruleToXml(store, RuleEntity.EXCLUDE, keywordList.get(i).getKeywordTerm());
							if(rxml!=null){
								LOGGER. info(String.format("Exclude to RuleXML %d of %d : %s", i+1, total, keywordList.get(i).getKeywordTerm()));
								ruleXmlList.add(rxml);	
							}else{
								LOGGER. info(String.format("No Exclude to RuleXML %d of %d : %s", i+1, total, keywordList.get(i).getKeywordTerm()));								
							}
						}
						return ruleXmlList;
					}
				});
				tasks++;

				futureDemoteRule = completionService.submit(new Callable<List<RuleXml>>() {
					@Override
					public List<RuleXml> call() throws Exception {
						List<RuleXml> ruleXmlList = new ArrayList<RuleXml>();
						int total = keywordList.size();
						for(int i=0; i< total; i++){
							RuleXml rxml = RuleToXMLConverter.ruleToXml(store, RuleEntity.DEMOTE, keywordList.get(i).getKeywordTerm());
							if(rxml!=null){
								LOGGER.info(String.format("Demote to RuleXML %d of %d : %s", i+1, total, keywordList.get(i).getKeywordTerm()));
								ruleXmlList.add(rxml);
							}else{
								LOGGER.info(String.format("No Demote to RuleXML %d of %d : %s", i+1, total, keywordList.get(i).getKeywordTerm()));
							}
						}
						return ruleXmlList;
					}
				});
				tasks++;
			}

			String path = properties.getProperty("output.folder");

			while (tasks > 0) {
				Future<List<RuleXml>> completed = completionService.take();
				if (completed.equals(futureElevateRule)) {
					RuleToXMLConverter.generateFile(path, completed.get(), RuleEntity.ELEVATE);
				}else if (completed.equals(futureExcludeRule)) {
					RuleToXMLConverter.generateFile(path, completed.get(), RuleEntity.EXCLUDE);
				}else if (completed.equals(futureDemoteRule)) {
					RuleToXMLConverter.generateFile(path, completed.get(), RuleEntity.DEMOTE);
				}else if (completed.equals(futureFacetSortRule)) {
					RuleToXMLConverter.generateFile(path, completed.get(), RuleEntity.FACET_SORT);
				}else if (completed.equals(futureQueryCleaningRule)) {
					RuleToXMLConverter.generateFile(path, completed.get(), RuleEntity.QUERY_CLEANING);
				}else if (completed.equals(futureRankingRule)) {
					RuleToXMLConverter.generateFile(path, completed.get(), RuleEntity.RANKING_RULE);
				}
				tasks--;
			}

		} catch (DaoException de) {
			LOGGER.error("DaoException", de);
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted exception", e);
		} catch (Throwable t) {
			LOGGER.error("Throwable", t);
		}

		LOGGER.info(String.format("End of processing"));
		System.exit(0);
	}

	public static void generateFile(String path, List<RuleXml> ruleXmlList, RuleEntity ruleEntity){
		if(CollectionUtils.isNotEmpty(ruleXmlList)){
			for(RuleXml ruleXml: ruleXmlList){
				ruleXml.setRuleStatus(getRuleStatus(ruleEntity, store, ruleXml.getRuleId()));
				consumeExec.execute(new RuleXMLToFile(store, path, ruleXml.getRuleId(), ruleXml, ruleEntity));
			}
		}else{
			LOGGER.info(String.format("No record of %s to convert", ruleEntity.name()));
		}
	}

	public static RuleStatus getRuleStatus(RuleEntity ruleEntity, String store, String ruleId){
		RuleStatus ruleStatus = new RuleStatus(ruleEntity.getCode(), store, ruleId);
		SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(ruleStatus);

		try {

			RecordSet<RuleStatus> approvedRset = daoServiceRuleStatus.getRuleStatus(searchCriteria);

			if (approvedRset.getTotalSize() > 0) {
				ruleStatus = approvedRset.getList().get(0);
				LOGGER.info(String.format("Rule status found for %s %s", ruleEntity, ruleId));
			}else {
				LOGGER.info(String.format("No rule status found for %s %s", ruleEntity, ruleId));
			}

		}catch (DaoException e) {
			LOGGER.error(String.format("Failed to retrieve rule status for  %s %s", ruleEntity, ruleId), e);
		}

		return ruleStatus;
	}
}