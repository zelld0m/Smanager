package com.search.manager.core.processor;

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
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.ws.ConfigManager;

public class SearchWithinRequestProcessor implements RequestProcessor {
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

	public String getPrefixOperator(String swType){
		return cm.getSearchWithinProperty(storeId, String.format("searchwithin.%s.prefixTypeOperator", swType));
	}

	public boolean isQuoteKeyword(String swType){
		return BooleanUtils.toBooleanObject(StringUtils.defaultIfBlank(cm.getSearchWithinProperty(storeId, String.format("searchwithin.%s.quoteKeyword",swType)), "false"));
	}

	public boolean isSplit(String swType){
		return BooleanUtils.toBooleanObject(StringUtils.defaultIfBlank(cm.getSearchWithinProperty(storeId, String.format("searchwithin.%s.split",swType)), "false"));
	}
	
	public String getSplitRegex(String swType){
		return cm.getSearchWithinProperty(storeId, String.format("searchwithin.%s.splitRegex", swType));
	}
	
	public int getMinLength(){
		return Integer.parseInt(StringUtils.defaultIfBlank(cm.getSearchWithinProperty(storeId, "searchwithin.minLength"),"2"));
	}
	
	@Override
	public boolean isEnabled(){
		return BooleanUtils.toBooleanObject(StringUtils.defaultIfBlank(cm.getSearchWithinProperty(storeId, "searchwithin.enable"), "false"));
	}
	
	public List<String> getTokenizedKeyword(List<String> keywords, String allowedSwParam){
		List<String> qualifiedTokens = new ArrayList<String>();
		List<String> unQualifiedTokens = new ArrayList<String>();
		
		if(isQuoteKeyword(allowedSwParam) || !isSplit(allowedSwParam)){
			return keywords;
		}
		
		for(String keyword: keywords){
			String trimmedKeyword = StringUtils.trimToEmpty(keyword);
			
			if(StringUtils.isNotBlank(trimmedKeyword)){
				int numberOfQuotes = StringUtils.countMatches(trimmedKeyword, "\"");
				
				if(numberOfQuotes > 0 && (numberOfQuotes % 2) == 0 && StringUtils.startsWith(trimmedKeyword, "\"") && StringUtils.endsWith(trimmedKeyword, "\"")){
					logger.info("Qualified token: {}", trimmedKeyword);
					qualifiedTokens.add(trimmedKeyword);
					continue;
				}
				
				String[] tokens = keyword.split(getSplitRegex(allowedSwParam));
				logger.info("Processing keyword for {}: {}", StringUtils.capitalize(allowedSwParam), keyword);
				logger.info("Tokens using {}: {}", getSplitRegex(allowedSwParam), StringUtils.join(tokens, "|"));
				if (ArrayUtils.isNotEmpty(tokens) && tokens.length>0){
					for(String token: tokens){
						if(StringUtils.length(StringUtils.trimToEmpty(token)) >= getMinLength()){
							qualifiedTokens.add(token);
							continue;
						}
						unQualifiedTokens.add(token);
					}
				}
			}
		}
		
		logger.info("UnQualified tokens: {}", StringUtils.join(unQualifiedTokens,"|"));
		logger.info("Qualified tokens: {}", StringUtils.join(qualifiedTokens,"|"));
		return qualifiedTokens;
	}

	public String[] toSolrFq(Map<String, List<String>> swProcessedParams) throws Throwable{
		StringBuilder sbType = new StringBuilder();
		List<String> fields = getFields();

		// Generate template Keyword to Solr fields
		if(CollectionUtils.isNotEmpty(fields) && MapUtils.isNotEmpty(swProcessedParams)){
			String swKeywordTemplate = new String();
			Set<String> keySet = swProcessedParams.keySet();
			String[] perTypeQueryArr = new String[keySet.size()];
			int iteration = 0;

			try {
				for(String swType: keySet){
					sbType = new StringBuilder();
					swKeywordTemplate = String.format(CollectionUtils.size(fields)==1 || CollectionUtils.size(swProcessedParams.get(swType))==1 ? "%s":"(%s)", StringUtils.join(fields, ":%%keyword%% %%operator%% ") + ":%%keyword%%");
					String keywordTemplate = isQuoteKeyword(swType)? "\"%s\"": "%s";
					for(String swKeyword: swProcessedParams.get(swType)){
						sbType.append(sbType.length()>0? String.format(" %s ", getKeywordOperator(swType)): "");
						sbType.append(StringUtils.replaceEach(swKeywordTemplate, new String[]{"%%keyword%%", "%%operator%%"}, new String[]{String.format(keywordTemplate, StringEscapeUtils.escapeJavaScript(swKeyword)), getSolrFieldOperator(swType)}));
					}

					String prefixOperator = getPrefixOperator(swType);
					String perTypeTemplate = String.format("(%s)", StringUtils.isNotBlank(prefixOperator)? StringUtils.trim(prefixOperator) + "(%s)" : "%s");

					perTypeQueryArr[iteration++] = String.format(perTypeTemplate, sbType.toString());
				}

				if(logger.isInfoEnabled()){
					logger.info("Generated Solr Filter: fq={}", StringUtils.join(perTypeQueryArr, String.format("%s", "&fq=")));
				}
				
				if (ArrayUtils.isNotEmpty(perTypeQueryArr)) return perTypeQueryArr;
			} catch (Exception e) {
				throw e;
			} catch (Throwable t){
				throw t;
			}
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void process(HttpServletRequest request, Map<String, List<NameValuePair>> paramMap, List<NameValuePair> nameValuePairs) {
		Map<String, List<String>> swParamsMap = new HashMap<String, List<String>>();
		List<String> swTypeList= new ArrayList<String>();
		String[] paramValues= request.getParameterValues(getRequestParamName());
		JsonSlurper slurper = new JsonSlurper();
		List<String> solrFieldToSearchList = null; 
		String swValues = ""; 

		if(logger.isInfoEnabled()){
			logger.info("Enabled: {}", BooleanUtils.toStringYesNo(isEnabled()));
			logger.info("Param name {} exists? {}", getRequestParamName(), BooleanUtils.toStringYesNo(ArrayUtils.getLength(paramValues)>0));
			if (ArrayUtils.getLength(paramValues)>0){
				logger.info("Values for {} param name: {}", ArrayUtils.getLength(paramValues), StringUtils.join(paramValues,"|"));
			}
		}

		if(!isEnabled() ||  ArrayUtils.getLength(paramValues)==0 || StringUtils.isBlank(swValues = paramValues[paramValues.length-1])){
			logger.info("Skipped: Enabled? {}, Empty params? {}", BooleanUtils.toStringYesNo(isEnabled()),BooleanUtils.toStringYesNo(StringUtils.isBlank(swValues)));
			return;
		}

		if(CollectionUtils.isEmpty(solrFieldToSearchList = getFields()) || CollectionUtils.isEmpty(swTypeList = getSearchWithinType())){
			logger.info("Skipped: Empty search fields? {} , Empty allowed params? {}", BooleanUtils.toStringYesNo(CollectionUtils.isEmpty(solrFieldToSearchList)), BooleanUtils.toStringYesNo(CollectionUtils.isEmpty(getSearchWithinType())));
			return;
		}

		if(logger.isInfoEnabled()){
			logger.info("Solr Fields: {}", StringUtils.join(solrFieldToSearchList,", "));
		}

		String[] solrFqArr = new String[CollectionUtils.size(swTypeList)];

		try {
			JSONObject swJSONParam = (JSONObject)slurper.parseText(swValues);

			if(JSONUtils.isNull(swJSONParam)){
				logger.info("Skipped: Field to convert to JSON using param {}", swValues);
				return;
			}

			for(String swType: swTypeList){
				if(StringUtils.isNotBlank(swType)){
					List<String> swParamList = new ArrayList<String>();
					JSONArray jsonArray = null; 
					if(swJSONParam.containsKey(swType) && !JSONUtils.isNull(jsonArray = swJSONParam.getJSONArray(swType))){
						swParamList = (List<String>) JSONSerializer.toJava(jsonArray);
					}

					List<String> tokensList = new ArrayList<String>();
					if(CollectionUtils.isNotEmpty(swParamList) && CollectionUtils.isNotEmpty(tokensList = getTokenizedKeyword(swParamList, swType))){
						swParamsMap.put(swType, tokensList);
					}
				}
			}

			if(MapUtils.isEmpty(swParamsMap)){
				logger.info("Skipped: Empty processed request param");
				return;
			}

			if(logger.isInfoEnabled()){
				logger.info("Map Request Params: {}", ObjectUtils.toString(swParamsMap));
			}

			solrFqArr = toSolrFq(swParamsMap);
		} catch (JSONException e) {
			logger.info("Skipped: {}", e.getMessage());
			return;
		} catch (Exception e){
			logger.error(e.getMessage());
		} catch (Throwable t) {
			logger.error(t.getMessage());
			logger.info("Skipped: {}", t.getMessage());
		} finally{
			nameValuePairs.remove(new BasicNameValuePair(getRequestParamName(), swValues));
			if(ArrayUtils.getLength(solrFqArr)>0){
				logger.info("Pre-processing List: {} Map: {}", CollectionUtils.size(nameValuePairs), paramMap);
				for (String solrFq: solrFqArr){
					BasicNameValuePair nameValuePair = new BasicNameValuePair("fq", solrFq);
					RequestProcessorUtil.addNameValuePairToMap(paramMap, "fq", nameValuePair);
					nameValuePairs.add(nameValuePair);
				}
				logger.info("Post-processing List: {} Map: {}", CollectionUtils.size(nameValuePairs), paramMap);
			}else{
				logger.error("No search within applied for {}={}", getRequestParamName(), swValues);
			}
		}
	}
}