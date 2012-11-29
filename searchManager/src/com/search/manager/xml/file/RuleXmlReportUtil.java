package com.search.manager.xml.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoService;
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetSort;
import com.search.manager.model.Keyword;
import com.search.manager.model.Product;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.report.model.DemoteReportBean;
import com.search.manager.report.model.ElevateReportBean;
import com.search.manager.report.model.ExcludeReportBean;
import com.search.manager.report.model.FacetSortReportBean;
import com.search.manager.report.model.KeywordReportBean;
import com.search.manager.report.model.KeywordReportModel;
import com.search.manager.report.model.RedirectRuleConditionReportBean;
import com.search.manager.report.model.RedirectRuleConditionReportModel;
import com.search.manager.report.model.RedirectRuleReportBean;
import com.search.manager.report.model.RedirectRuleReportModel;
import com.search.manager.report.model.RelevancyFieldReportBean;
import com.search.manager.report.model.RelevancyFieldReportModel;
import com.search.manager.report.model.RelevancyReportBean;
import com.search.manager.report.model.RelevancyReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.FacetSortGroupXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleConditionXml;
import com.search.manager.report.model.xml.RuleKeywordXml;
import com.search.manager.report.model.xml.RuleXml;

public class RuleXmlReportUtil{
	private static Logger logger = Logger.getLogger(RuleXmlReportUtil.class);
	@Autowired private static DaoService daoService;

	private static RuleXmlReportUtil instance = null;

	protected RuleXmlReportUtil() {
		//Exists only to defeat instantiation.
	}

	public static RuleXmlReportUtil getInstance() {
		logger.info("RuleXmlReportUtil.getInstance()");
		if(instance == null) {
			synchronized (RuleXmlReportUtil.class) {
				if (instance == null) {
					instance = new RuleXmlReportUtil();
				}
			}
		}
		return instance;
	}

	public static List<ElevateReportBean> getElevateProducts(RuleXml ruleXml){
		if(ruleXml != null){
			ElevateRuleXml xml = (ElevateRuleXml) ruleXml;
			List<ElevateProduct> products = xml.getProducts();
			List<ElevateReportBean> elList = new ArrayList<ElevateReportBean>();
			
			if(products != null){
				for (ElevateProduct p: products) {
					elList.add(new ElevateReportBean(p));
				}
			}
			return elList;
		}
		else{
			return new ArrayList<ElevateReportBean>();
		}
	}
	
	public static List<DemoteReportBean> getDemoteProducts(RuleXml ruleXml){
		if(ruleXml != null){
			DemoteRuleXml dXml = (DemoteRuleXml) ruleXml;
			List<DemoteProduct> products = dXml.getProducts();
			List<DemoteReportBean> dList = new ArrayList<DemoteReportBean>();
			
			if(products != null){
				for (DemoteProduct p: products) {
					dList.add(new DemoteReportBean(p));
				}
			}
			return dList;
		}
		else{
			return new ArrayList<DemoteReportBean>();
		}
	}
	
	public static List<ExcludeReportBean> getExcludeProducts(RuleXml ruleXml){
		if(ruleXml != null){
			ExcludeRuleXml dXml = (ExcludeRuleXml) ruleXml;
			List<Product> products = dXml.getProducts();
			List<ExcludeReportBean> dList = new ArrayList<ExcludeReportBean>();
			
			if(products != null){
				for (Product p: products) {
					dList.add(new ExcludeReportBean(p));
				}
			}
			return dList;
		}
		else{
			return new ArrayList<ExcludeReportBean>();
		}
	}
	
	public static List<FacetSortReportBean> getFacetSortReportBeanList(FacetSortRuleXml xml){
		List<FacetSortReportBean> list = new ArrayList<FacetSortReportBean>();
		List<FacetSortGroupXml> groups = xml.getGroups();
		FacetSort fs = new FacetSort(xml);
		int count = 0;
		
		for(FacetSortGroupXml group : groups){
			StringBuilder sb = new StringBuilder();
			List<String> facets = group.getGroupItem();
			FacetGroup fsg = new FacetGroup(group, fs.getId(), count, fs.getStoreId());
			
			if(CollectionUtils.isNotEmpty(facets)){
				for(int i=0 ; i < facets.size(); i++){
					sb.append((i+1) + " - " + facets.get(i) + (char)10);    
				}
			}

			String highlightedFacets = sb.toString();
			if(StringUtils.isNotBlank(highlightedFacets)){
				highlightedFacets = highlightedFacets.substring(0, highlightedFacets.length()-1);
			}
			else{
				highlightedFacets = "No Highlighted Facets";
			}
			
			FacetSortReportBean reportBean = new FacetSortReportBean(fsg, highlightedFacets);
			list.add(reportBean);
			count++;
		}
		
		return list;
	}
	
	public static List<RedirectRuleConditionReportBean> getRedirectRuleConditionReportBeanList(RuleConditionXml ruleConditionXml){
		List<RedirectRuleConditionReportBean> conditions = new ArrayList<RedirectRuleConditionReportBean>();
		
		if(ruleConditionXml != null){
			List<String> ruleConditions = ruleConditionXml.getCondition();
			
			if(ruleConditions != null){
				for(String cond : ruleConditions){
					conditions.add(new RedirectRuleConditionReportBean(cond));
				}
			}
		}
		return conditions;
	}
	
	public static RedirectRuleReportBean getRedirectRuleReportBean(RedirectRuleXml xml){
		RedirectRule redirectRule = new RedirectRule(xml);
		return (redirectRule != null) ? new RedirectRuleReportBean(redirectRule) : null;
	}
	
	public static List<ReportModel<? extends ReportBean<?>>> getRedirectSubReports(RedirectRuleXml xml, ReportHeader reportHeader, SubReportHeader subReportHeader){
		List<ReportModel<? extends ReportBean<?>>> subReports = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		
		RedirectRuleReportBean redirectRule = getRedirectRuleReportBean(xml);
		if(redirectRule != null){
			List<RedirectRuleReportBean> list = new ArrayList<RedirectRuleReportBean>();
			list.add(redirectRule);
			subReports.add( new RedirectRuleReportModel(reportHeader, subReportHeader, list));
		}
		
		RuleKeywordXml ruleKeyword = xml.getRuleKeyword();
		if(ruleKeyword != null){
			subReports.add(new KeywordReportModel(reportHeader, subReportHeader, getKeywordReportBeanList(ruleKeyword)));
		}
			
		RuleConditionXml ruleCondition = xml.getRuleCondition();
		if(ruleCondition != null){
			subReports.add(new RedirectRuleConditionReportModel(reportHeader, subReportHeader, getRedirectRuleConditionReportBeanList(ruleCondition)));
		}
			
		return subReports;
	}
	
	public static RelevancyReportBean getRelevancyReportBean(RankingRuleXml xml){
		Relevancy relevancy = new Relevancy(xml);
		return (relevancy != null) ? new RelevancyReportBean(relevancy) : null;
	}
	
	public static List<KeywordReportBean> getKeywordReportBeanList(RuleKeywordXml ruleKeyword){
		List<KeywordReportBean> keywords = new ArrayList<KeywordReportBean>();
		
		if(ruleKeyword != null){
			List<String> keywordList = ruleKeyword.getKeyword();
			
			for (String kw : keywordList) {
				keywords.add(new KeywordReportBean(new Keyword(kw)));
			}
		}
		return keywords;
	}
	
	public static List<RelevancyFieldReportBean> getRelevancyFieldReportBeanList(Map<String, String> parameters){
		List<RelevancyFieldReportBean> relevancyFields = new ArrayList<RelevancyFieldReportBean>();
		for (String key: parameters.keySet()) {
			String value = parameters.get(key);
			if (value != null) {
				relevancyFields.add(new RelevancyFieldReportBean(new BasicNameValuePair(key, value)));
			}
		}
		return relevancyFields;
	}
	
	public static List<ReportModel<? extends ReportBean<?>>> getRelevancySubReports(RankingRuleXml xml, ReportHeader reportHeader, SubReportHeader subReportHeader){
		List<ReportModel<? extends ReportBean<?>>> subReports = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		
		RelevancyReportBean relevancy = getRelevancyReportBean(xml);
		if(relevancy != null){
			List<RelevancyReportBean> list = new ArrayList<RelevancyReportBean>();
			list.add(relevancy);
			subReports.add( new RelevancyReportModel(reportHeader, subReportHeader,list));
		}
		
		RuleKeywordXml ruleKeyword = xml.getRuleKeyword();
		if(ruleKeyword != null){
			subReports.add(new KeywordReportModel(reportHeader, subReportHeader, getKeywordReportBeanList(ruleKeyword)));
		}
			
		Map<String, String> parameters = xml.getParameters();
		if(parameters != null){
			subReports.add(new RelevancyFieldReportModel(reportHeader, subReportHeader, getRelevancyFieldReportBeanList(parameters)));
		}
			
		return subReports;
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		RuleXmlReportUtil.daoService = daoService;
	}
}
