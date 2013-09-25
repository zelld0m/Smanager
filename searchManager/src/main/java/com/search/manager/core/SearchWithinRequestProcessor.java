package com.search.manager.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.groovy.JsonSlurper;
import net.sf.json.util.JSONUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.ws.ConfigManager;

public class SearchWithinRequestProcessor extends RequestProcessorUtil implements RequestProcessor {
	private static final Logger logger = LoggerFactory.getLogger(SearchWithinRequestProcessor.class);
	private ConfigManager cm;
	private String storeId;
	
	private SearchWithinRequestProcessor(){
		super();
	}
	
	public SearchWithinRequestProcessor(String storeId){
		this();
		this.cm = ConfigManager.getInstance();
		this.storeId = storeId;
	}
	
	public List<String> getFields(){
		return cm.getListSearchWithinProperty(storeId, "searchwithin.solrfieldlist");
	}

	public List<String> getSearchWithinType(){
		return cm.getListSearchWithinProperty(storeId, "searchwithin.type");
	}

	public String getRequestParamName(){
		return StringUtils.defaultIfBlank(cm.getSearchWithinProperty(storeId, "searchwithin.paramname"),"searchwithin");
	}
	
	public String getKeywordOperator(String swType){
		return StringUtils.defaultIfBlank(cm.getSearchWithinProperty(storeId, String.format("searchwithin.%s.keywordOperator",swType)),"OR");
	}
	
	public String getSolrFieldOperator(String swType){
		return StringUtils.defaultIfBlank(cm.getSearchWithinProperty(storeId, String.format("searchwithin.%s.solrFieldOperator",swType)),"OR");
	}
	
	public String getTypeOperator(){
		return StringUtils.defaultIfBlank(cm.getSearchWithinProperty(storeId, "searchwithin.typeOperator"),"OR");
	}
	
	public String getPrefixOperator(String swType){
		return cm.getSearchWithinProperty(storeId, String.format("searchwithin.%s.prefixTypeOperator", swType));
	}

	@Override
	public boolean isEnabled(){
		return BooleanUtils.toBooleanObject(StringUtils.defaultIfBlank(cm.getSearchWithinProperty(storeId, "searchwithin.enable"), "false"));
	}

	public StringBuilder toSolrFq(Map<String, List<String>> swProcessedParams){
		StringBuilder sbAllType = new StringBuilder();
		StringBuilder sbType = new StringBuilder();
		List<String> fields = getFields();

		// Generate template Keyword to Solr fields
		if(CollectionUtils.isNotEmpty(fields) && MapUtils.isNotEmpty(swProcessedParams)){
			String swKeywordTemplate = new String();

			Set<String> keySet = swProcessedParams.keySet();
			String[] perTypeQueryArr = new String[keySet.size()];
			int iteration = 0;

			for(String swType: keySet){
				sbType = new StringBuilder();
				swKeywordTemplate = String.format(CollectionUtils.size(fields)==1 || CollectionUtils.size(swProcessedParams.get(swType))==1 ? "%s":"(%s)", StringUtils.join(fields, ":%%keyword%% %%operator%% ") + ":%%keyword%%");
				
				for(String swKeyword: swProcessedParams.get(swType)){
					sbType.append(sbType.length()>0? String.format(" %s ", getKeywordOperator(swType)): "");
					sbType.append(StringUtils.replaceEach(swKeywordTemplate, new String[]{"%%keyword%%", "%%operator%%"}, new String[]{swKeyword, getSolrFieldOperator(swType)}));
				}
				
				String prefixOperator = getPrefixOperator(swType);
				String perTypeTemplate = String.format("(%s)", StringUtils.isNotBlank(prefixOperator)? StringUtils.trim(prefixOperator) + "(%s)" : "%s");
				
				perTypeQueryArr[iteration++] = String.format(perTypeTemplate, sbType.toString());
			}

			sbAllType.append(StringUtils.join(perTypeQueryArr, String.format(" %s ", getTypeOperator())));

			if(logger.isDebugEnabled()){
				logger.debug("Solr Filter: {}", sbAllType.toString());
			}
		}

		return sbAllType;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void process(HttpServletRequest request, Map<String, List<NameValuePair>> paramMap) {
		Map<String, List<String>> swParamsMap = new HashMap<String, List<String>>();
		String[] paramValues= request.getParameterValues(getRequestParamName());
		JsonSlurper slurper = new JsonSlurper();
		List<String> solrFieldToSearchList = null; 
		String swValues = ""; 

		if(logger.isDebugEnabled()){
			logger.debug("Enabled: {}", BooleanUtils.toStringYesNo(isEnabled()));
			logger.debug("Request Param Name: {}", getRequestParamName());
			logger.debug("Request Param Name Count: {}", ArrayUtils.getLength(paramValues));
		}

		if(!isEnabled() ||  ArrayUtils.getLength(paramValues)==0 || StringUtils.isBlank(swValues = paramValues[paramValues.length-1])){
			logger.debug("Skipped: Enabled? {}, Empty params? {}", BooleanUtils.toStringYesNo(isEnabled()),BooleanUtils.toStringYesNo(StringUtils.isBlank(swValues)));
			return;
		}

		if(CollectionUtils.isEmpty(solrFieldToSearchList = getFields()) || CollectionUtils.isEmpty(getSearchWithinType())){
			logger.debug("Skipped: Empty search fields? {} , Empty allowed params? {}", BooleanUtils.toStringYesNo(CollectionUtils.isEmpty(solrFieldToSearchList)), BooleanUtils.toStringYesNo(CollectionUtils.isEmpty(getSearchWithinType())));
			return;
		}

		if(logger.isDebugEnabled()){
			logger.debug("Solr Fields: {}", StringUtils.join(solrFieldToSearchList,", "));
		}

		try {
			JSONObject swJSONParam = (JSONObject)slurper.parseText(swValues);

			for(String allowedSWParam: getSearchWithinType()){
				if(StringUtils.isNotBlank(allowedSWParam)){
					List<String> swParamList = new ArrayList<String>();
					JSONArray jsonArray = null; 
					if(swJSONParam.containsKey(allowedSWParam) && !JSONUtils.isNull(jsonArray=swJSONParam.getJSONArray(allowedSWParam))){
						swParamList = (List<String>) JSONSerializer.toJava(jsonArray);
					}

					if(CollectionUtils.isNotEmpty(swParamList)){
						swParamsMap.put(allowedSWParam, swParamList);
					}
				}
			}

			if(MapUtils.isEmpty(swParamsMap)){
				logger.debug("Skipped: Empty processed request param");
				return;
			}

			if(logger.isDebugEnabled()){
				logger.debug("Map Request Params: {}", ObjectUtils.toString(swParamsMap));
			}
		} catch (JSONException e) {
			logger.error("Skipped: {}", e.getMessage());
			return;
		} catch (Exception e){
			logger.error("Skipped: {}", e.getMessage());
			return;
		}		

		StringBuilder solrFq = toSolrFq(swParamsMap);
		if(solrFq.length()>0){
			logger.debug("Pre-processing: {}", paramMap);
			RequestProcessorUtil.addNameValuePairToMap(paramMap, "fq", new BasicNameValuePair("fq", solrFq.toString()));
			logger.debug("Post-processing: {}", paramMap);
		}
	}
}