package com.search.ws;

public class SolrConstants {
	
	public final static String TEMP_FOLDER = "tmp-folder";
	
	public final static String SOLR_URL = "solrUrl";
	
	public final static String SOLR_PARAM_KEYWORD = "q";
	public final static String SOLR_PARAM_SPELLCHECK = "spellcheck";
	public final static String SOLR_PARAM_ROWS = "rows";
	public final static String SOLR_PARAM_START = "start";
	public final static String SOLR_PARAM_FIELDS = "fl";
	public final static String SOLR_PARAM_FIELD_QUERY = "fq";
	public final static String SOLR_PARAM_WRITER_TYPE = "wt";
	public final static String SOLR_PARAM_JSON_WRAPPER_FUNCTION = "json.wrf";
	public final static String SOLR_PARAM_SORT = "sort";
	public final static String SOLR_PARAM_QUERY_TYPE = "qt";

	public final static String SOLR_PARAM_GUI = "gui";

	public final static String SOLR_PARAM_VALUE_JSON = "json";
	public final static String SOLR_PARAM_VALUE_STANDARD = "standard";
	public final static String SOLR_PARAM_VALUE_XML = "xml";
	
	/* customized parameters */
	public final static String SOLR_PARAM_RELEVANCY_ID = "relevancyId";
	
	public final static String REDIRECT_URL = "redirectUrl";

	public final static String TAG_RESPONSE = "response";
	public final static String TAG_LIST = "lst";
	
	public final static String TAG_RESULT = "result";
	public final static String TAG_STR = "str";
	public final static String TAG_DOC = "doc";
	public final static String TAG_DOCS = "docs";
	public final static String TAG_INT = "int";
	public final static String TAG_ARR = "arr";
	
	public final static String TAG_ELEVATE = "Elevate";
	public final static String TAG_ELEVATE_TYPE = "ElevateType";
	public final static String TAG_ELEVATE_ID = "ElevateId";
	public final static String TAG_ELEVATE_CONDITION = "ElevateCondition";
	
	public final static String TAG_DEMOTE = "Demote";
	public final static String TAG_DEMOTE_TYPE = "DemoteType";
	public final static String TAG_DEMOTE_ID = "DemoteId";
	public final static String TAG_DEMOTE_CONDITION = "DemoteCondition";
	
	public final static String TAG_EXPIRED = "Expired";
	public final static String TAG_EXCLUDE = "Exclude";
	public final static String TAG_REDIRECT = "Redirect";
	public final static String TAG_BANNER = "Banner";
	public final static String TAG_REDIRECT_ID = "Redirect_Id";
	public final static String TAG_REDIRECT_PARAM = "Redirect_Param";
	public final static String TAG_INCLUDE_KEYWORD = "Include_Keyword";
	public final static String TAG_FORCE_ADD = "ForceAdd";
		
	public final static String TAG_FACET_COUNTS = "facet_counts";
	public final static String TAG_FACET_FIELDS = "facet_fields";
	public final static String TAG_FACET_FIELD = "facet.field";
	
	public final static String ATTR_NUM_FOUND = "numFound";
	public final static String ATTR_MAX_SCORE = "maxScore";
	
	public final static String ATTR_NAME = "name";
	public final static String ATTR_NAME_VALUE_EDP = "EDP";
	public final static String ATTR_NAME_VALUE_QTIME = "QTime";
	public final static String ATTR_NAME_VALUE_RESPONSE_HEADER = "responseHeader";
	public final static String ATTR_NAME_VALUE_FACET_COUNTS = "facet_counts";
	public final static String ATTR_NAME_VALUE_RESPONSE = "response";
	public final static String ATTR_NAME_VALUE_PARAMS = "params";
	public final static String ATTR_NAME_VALUE_DEBUG = "debug";
	public final static String ATTR_NAME_VALUE_EXPLAIN = "explain";
	
	// active rules
	// params submitted by simulator
	public final static String SOLR_PARAM_DISABLE_ELEVATE = "disableElevate";
	public final static String SOLR_PARAM_DISABLE_EXCLUDE = "disableExclude";
	public final static String SOLR_PARAM_DISABLE_DEMOTE = "disableDemote";
	public final static String SOLR_PARAM_DISABLE_REDIRECT = "disableRedirect";
	public final static String SOLR_PARAM_DISABLE_RELEVANCY = "disableRelevancy";
	// returned as part of solr response
	public final static String TAG_SEARCH_RULES = "search_rules";
	public final static String TAG_RULE = "rule";
	public final static String TAG_RULE_ID = "id";
	public final static String TAG_RULE_TYPE = "type";
	public final static String TAG_RULE_NAME = "name";
	public final static String TAG_RULE_ACTIVE = "active";
	// rule types
	public final static String TAG_VALUE_RULE_TYPE_ELEVATE = "Elevate";
	public final static String TAG_VALUE_RULE_TYPE_EXCLUDE = "Exclude";
	public final static String TAG_VALUE_RULE_TYPE_DEMOTE  = "Demote";
	public final static String TAG_VALUE_RULE_TYPE_REDIRECT = "Query Cleaning";
	public final static String TAG_VALUE_RULE_TYPE_RELEVANCY = "Ranking Rule";

	
	
	
}
