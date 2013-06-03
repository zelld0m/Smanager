package com.search.ws;

public class SolrConstants {
	
	public final static String TEMP_FOLDER = "tmp-folder";
	
	public final static String SOLR_URL = "solrUrl";
	
	public final static String SOLR_PARAM_KEYWORD = "q";
	public final static String SOLR_PARAM_SPELLCHECK = "spellcheck";
	public final static String SOLR_PARAM_SPELLCHECK_COUNT = "spellcheck.count";
	public final static String SOLR_PARAM_ROWS = "rows";
	public final static String SOLR_PARAM_START = "start";
	public final static String SOLR_PARAM_FIELDS = "fl";
	public final static String SOLR_PARAM_FIELD_QUERY = "fq";
	public final static String SOLR_PARAM_WRITER_TYPE = "wt";
	public final static String SOLR_PARAM_JSON_WRAPPER_FUNCTION = "json.wrf";
	public final static String SOLR_PARAM_SORT = "sort";
	public final static String SOLR_PARAM_QUERY_TYPE = "qt";
	public final static String SOLR_PARAM_FACET_FIELD = "facet.field";

	public final static String SOLR_PARAM_GUI = "gui";

	public final static String SOLR_PARAM_VALUE_JSON = "json";
	public final static String SOLR_PARAM_VALUE_STANDARD = "standard";
	public final static String SOLR_PARAM_VALUE_XML = "xml";
	
	/* customized parameters */
	public final static String SOLR_PARAM_RELEVANCY_ID = "relevancyId";
	
	public final static String REDIRECT_URL = "redirectUrl";

	public final static String TAG_RESPONSE = "response";
	public final static String TAG_LIST = "lst";
	public final static String TAG_SPELLCHECK = "spellcheck";
	
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
	public final static String TAG_ELEVATE_EXPIRED = "ElevateExpired";
	
	public final static String TAG_DEMOTE = "Demote";
	public final static String TAG_DEMOTE_TYPE = "DemoteType";
	public final static String TAG_DEMOTE_ID = "DemoteId";
	public final static String TAG_DEMOTE_CONDITION = "DemoteCondition";
	public final static String TAG_DEMOTE_EXPIRED = "DemoteExpired";
	
	public final static String TAG_EXCLUDE = "Exclude";
	
	public final static String TAG_REDIRECT = "redirect_keyword";
	public final static String TAG_REDIRECT_ORIGINAL_KEYWORD = "original_keyword";
	public final static String TAG_REDIRECT_REPLACEMENT_KEYWORD = "replacement_keyword";
	public final static String TAG_REDIRECT_REPLACEMENT_TYPE = "message_type";
	public final static String TAG_REDIRECT_CUSTOM_TEXT = "custom_text";
	
	public final static String TAG_BANNER = "Banner";
	public final static String TAG_REDIRECT_ID = "Redirect_Id";
	public final static String TAG_REDIRECT_PARAM = "Redirect_Param";
	public final static String TAG_INCLUDE_KEYWORD = "Include_Keyword";
	public final static String TAG_FORCE_ADD = "ForceAdd";
	
	public final static String TAG_BANNERS = "banners";
	public final static String TAG_BANNER_RULE = "banner";
	public final static String TAG_BANNER_OPEN_NEW_WINDOW = "openNewWindow";
	public final static String TAG_BANNER_IMAGE_ALT = "imageAlt";
	public final static String TAG_BANNER_LINK_PATH = "linkPath";
	public final static String TAG_BANNER_IMAGE_PATH = "imagePath";
	
	public final static String TAG_FACET_COUNTS = "facet_counts";
	public final static String TAG_FACET_FIELDS = "facet_fields";
	public final static String TAG_FACET_FIELD = "facet.field";
	public final static String TAG_FACET_MINCOUNT = "facet.mincount";
	public final static String TAG_FACET_LIMIT = "facet.limit";
	public final static String TAG_FACET = "facet";
	
	public final static String TAG_DID_YOU_MEAN = "did_you_mean";
	
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

	public final static String ATTR_NAME_VALUE_SPELLCHECK = "spellcheck";
    public static final String ATTR_NAME_VALUE_SPELLCHECK_SUGGESTION = "suggestion";  
    public static final String ATTR_NAME_VALUE_SPELLCHECK_SUGGESTIONS = "suggestions";  
    public static final String ATTR_NAME_VALUE_SPELLCHECK_START_OFFSET = "startOffset";  
    public static final String ATTR_NAME_VALUE_SPELLCHECK_END_OFFSET = "endOffset";
    public static final String ATTR_NAME_VALUE_SPELLCHECK_COLLATION = "collation";
    public static final String ATTR_NAME_VALUE_SPELLCHECK_NUMFOUND = "numFound";
	
	// active rules
	// params submitted by simulator
	public final static String SOLR_PARAM_DISABLE_ELEVATE = "disableElevate";
	public final static String SOLR_PARAM_DISABLE_EXCLUDE = "disableExclude";
	public final static String SOLR_PARAM_DISABLE_DEMOTE = "disableDemote";
	public final static String SOLR_PARAM_DISABLE_REDIRECT = "disableRedirect";
	public final static String SOLR_PARAM_DISABLE_RELEVANCY = "disableRelevancy";
	public final static String SOLR_PARAM_DISABLE_FACET_SORT = "disableFacetSort";
	public final static String SOLR_PARAM_DISABLE_DID_YOU_MEAN = "disableDidYouMean";
	public final static String SOLR_PARAM_DISABLE_BANNER = "disableBanner";
	
	public final static String SOLR_PARAM_FACET_NAME 			= "facet-name";
	public final static String SOLR_PARAM_FACET_TEMPLATE 		= "facet-template";
	public final static String SOLR_PARAM_FACET_TEMPLATE_NAME 	= "facet-template-name";
	
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
	public final static String TAG_VALUE_RULE_TYPE_FACET_SORT = "Facet Sort";
	public final static String TAG_VALUE_RULE_TYPE_DID_YOU_MEAN = "Did You Mean";
	public final static String TAG_VALUE_RULE_TYPE_BANNER = "Banner";
	
	// Enterprise Search overrides
	public final static String REQUEST_ATTRIB_ELEVATE_OVERRIDE_MAP 		= "elevateOverrideMap";
	public final static String REQUEST_ATTRIB_EXCLUDE_OVERRIDE_MAP 		= "excludeOverrideMap";
	public final static String REQUEST_ATTRIB_DEMOTE_OVERRIDE_MAP 		= "demoteOverrideMap";
	public final static String REQUEST_ATTRIB_REDIRECT_OVERRIDE_MAP 	= "redirectOverrideMap";	
	public final static String REQUEST_ATTRIB_RELEVANCY_OVERRIDE_MAP 	= "relevancyOverrideMap";
}
