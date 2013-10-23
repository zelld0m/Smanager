package com.search.manager.core.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.search.manager.dao.DaoException;
import com.search.manager.model.FacetSort;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.ws.ConfigManager;
import com.search.ws.SearchException;
import com.search.ws.SolrConstants;
import com.search.ws.SolrResponseParser;

@Component
public class FacetSortRequestProcessor implements RequestProcessor {
	private static final Logger logger = LoggerFactory.getLogger(FacetSortRequestProcessor.class);
	private ConfigManager configManager = ConfigManager.getInstance();
	private RequestPropertyBean requestPropertyBean;

	private FacetSortRequestProcessor(){
		super();
	}

	public FacetSortRequestProcessor(RequestPropertyBean requestPropertyBean){
		this();
		this.requestPropertyBean = requestPropertyBean;
	}

	@Override
	public boolean isEnabled() {
		return BooleanUtils.toBooleanObject(StringUtils.defaultIfBlank(configManager.getFacetSortProperty(requestPropertyBean.getStoreId(), "facetsort.enable"), "false"));
	}

	private FacetSort getFacetSortRule(StoreKeyword storeKeyword) throws DaoException {
		try {
			return RequestProcessorUtil.getDaoService(requestPropertyBean.isGuiRequest()).getFacetSortRule(storeKeyword);
		} catch (DaoException e) {
			if (!requestPropertyBean.isGuiRequest()) {
				if (!configManager.isSolrImplOnly()) {
					try {
						return RequestProcessorUtil.getDaoService().getFacetSortRule(storeKeyword);
					} catch (DaoException e1) {
						logger.error("Failed to get defaultRelevancyRule {}", e1);
						return null;
					}
				} else {
					return null;
				}
			}
			throw e;
		}
	}

	protected FacetSort getFacetSortRule(Store store, String templateName) throws DaoException {
		try {
			return RequestProcessorUtil.getDaoService().getFacetSortRule(store, templateName);
		} catch (DaoException e) {
			if (!requestPropertyBean.isGuiRequest()) {
				if (!configManager.isSolrImplOnly()) {
					try {
						return RequestProcessorUtil.getDaoService().getFacetSortRule(store, templateName);
					} catch (DaoException e1) {
						logger.error("Failed to get facetSortRule {}", e1);
						return null;
					}
				} else {
					return null;
				}
			}
			throw e;
		}
	}

	@Override
	public void process(HttpServletRequest request, SolrResponseParser solrHelper, List<Map<String, String>> activeRules, Map<String, List<NameValuePair>> paramMap, List<NameValuePair> nameValuePairs) {
		boolean applyRule = false;
		final String storeId = requestPropertyBean.getStoreId();
		final String keyword = requestPropertyBean.getKeyword();
		final Map<String, String> facetMap = RequestProcessorUtil.getFacetMap(storeId);
		String facetTemplate = StringUtils.EMPTY;
		String facetTemplateName = StringUtils.EMPTY;

		if(MapUtils.isNotEmpty(facetMap)){
			facetTemplate = facetMap.get(SolrConstants.SOLR_PARAM_FACET_TEMPLATE);
			facetTemplateName = facetMap.get(SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME);
		}

		logger.info("Processing Facet Rule");
		final ArrayList<NameValuePair> getTemplateNameParams = new ArrayList<NameValuePair>(nameValuePairs);
		final StoreKeyword sk = RequestProcessorUtil.getStoreKeywordOverride(storeId, keyword);

		for (NameValuePair param : nameValuePairs) {
			if (StringUtils.equals(SolrConstants.SOLR_PARAM_SPELLCHECK, param.getName())
					|| StringUtils.equals(SolrConstants.TAG_FACET, param.getName())
					|| StringUtils.equals(SolrConstants.TAG_FACET_MINCOUNT, param.getName())
					|| StringUtils.equals(SolrConstants.TAG_FACET_LIMIT, param.getName())) {
				getTemplateNameParams.remove(param);
			} else if (StringUtils.equals(SolrConstants.TAG_FACET_FIELD, param.getName())) {
				// apply facet sort only if facet.field contains Manufacturer or Category or PCMall_FacetTemplate
				if (StringUtils.equals("Manufacturer", param.getValue())
						|| StringUtils.equals("Category", param.getValue())
						|| StringUtils.equals(facetTemplate, param.getValue())) {
					applyRule = true;
				}
				getTemplateNameParams.remove(param);
			}
		}

		FacetSort facetSort = null;
		// Get facet rule based on keyword
		try {
			logger.info("Attempting to apply facet rule using keyword {}", keyword);
			facetSort = requestPropertyBean.isKeywordPresent()? getFacetSortRule(sk) : null;
			if (facetSort != null) {
				activeRules.add(RequestProcessorUtil.generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_FACET_SORT, facetSort.getRuleId(), facetSort.getRuleName(), !requestPropertyBean.isDisableRule()));
				if (!requestPropertyBean.isDisableRule() && applyRule) {
					solrHelper.setFacetSortRule(facetSort);
				}
			}
		} catch (DaoException e) {
			facetSort = null;
			logger.error("Failed to retrieved facet sort rule {}", e);
		} catch (SearchException e) {
			logger.error("Failed to apply facet sort rule {}", e);
			return;
		}

		// Get facet rule based on template
		if (facetSort == null) {
			logger.info("Attempting to apply facet rule using template name {}", facetTemplateName);
			getTemplateNameParams.add(new BasicNameValuePair(SolrConstants.TAG_FACET, "true"));
			getTemplateNameParams.add(new BasicNameValuePair(SolrConstants.TAG_FACET_MINCOUNT, "1"));
			getTemplateNameParams.add(new BasicNameValuePair(SolrConstants.TAG_FACET_FIELD, facetTemplateName));
			getTemplateNameParams.add(new BasicNameValuePair(SolrConstants.TAG_FACET_LIMIT, "-1"));

			try {
				facetTemplateName = solrHelper.getCommonTemplateName(facetTemplateName, getTemplateNameParams);
				if (StringUtils.isNotBlank(facetTemplateName)) {
					facetSort = getFacetSortRule(sk.getStore(), facetTemplateName);
				}
			} catch (Exception e) {
				logger.error("Failed to get template name {}", e);
			}
		}
	}
}