package com.search.manager.dao.sp;

public class DAOConstants {

	/* Result Set identifier */
	public static final String RESULT_SET_1 						= "#result-set-1";
	public static final String RESULT_SET_2 						= "#result-set-2";
	public static final String RESULT_SET_3 						= "#result-set-3";
	public static final String RESULT_SET_TOTAL 					= "#result-set-total";
	public static final String RESULT_SET_RESULT					= "#result-set-result";

	/* Stored Procedure Names */
	// KEYWORD
	public static final String SP_ADD_KEYWORD 						= "usp_Add_Keyword";
	public static final String SP_GET_KEYWORD 						= "usp_Get_Keyword";
	// STORE_KEYWORD
	public static final String SP_ADD_STORE_KEYWORD 				= "usp_Add_Store_Keyword_Relationship";
	public static final String SP_GET_STORE_KEYWORD 				= "usp_Get_Store_Keyword_Relationship";
	// ELEVATE
	public static final String SP_ADD_ELEVATE 						= "usp_Add_Elevate";
	public static final String SP_GET_ELEVATE 						= "usp_Get_Elevate";
	public static final String SP_GET_ELEVATE_ITEM 					= "usp_Get_Elevate_Value";
	public static final String SP_GET_NO_EXPIRY_ELEVATE 			= "usp_Get_Elevate_NoExpiry";
	public static final String SP_UPDATE_ELEVATE 					= "usp_Update_Elevate";
	public static final String SP_UPDATE_ELEVATE_EXPIRY_DATE 		= "usp_Update_Elevate_Expiry_Date";
	public static final String SP_UPDATE_ELEVATE_COMMENT 			= "usp_Update_Elevate_Comment";
	public static final String SP_APPEND_ELEVATE_COMMENT 			= "usp_Append_Elevate_Comment";
	public static final String SP_DELETE_ELEVATE 					= "usp_Delete_Elevate";
	// EXCLUDE
	public static final String SP_ADD_EXCLUDE 						= "usp_Add_Exclude";
	public static final String SP_GET_EXCLUDE 						= "usp_Get_Exclude";
	public static final String SP_GET_EXCLUDE_ITEM 					= "usp_Get_Exclude_Value";
	public static final String SP_UPDATE_EXCLUDE					= "usp_Update_Exclude";
	public static final String SP_DELETE_EXCLUDE 					= "usp_Delete_Exclude";
	public static final String SP_UPDATE_EXCLUDE_COMMENT 			= "usp_Update_Exclude_Comment";
	public static final String SP_APPEND_EXCLUDE_COMMENT 			= "usp_Append_Exclude_Comment";
	public static final String SP_UPDATE_EXCLUDE_EXPIRY_DATE 		= "usp_Update_Exclude_Expiry_Date";
	// DEMOTE
	public static final String SP_ADD_DEMOTE 						= "usp_Add_Demote";
	public static final String SP_GET_DEMOTE 						= "usp_Get_Demote";
	public static final String SP_UPDATE_DEMOTE 					= "usp_Update_Demote";
	public static final String SP_UPDATE_DEMOTE_EXPIRY_DATE 		= "usp_Update_Demote_Expiry_Date";
	public static final String SP_DELETE_DEMOTE 					= "usp_Delete_Demote";

	// FACET SORT
	public static final String SP_ADD_FACET_SORT 					= "usp_Add_Facet_Sort_Rule";
	public static final String SP_DELETE_FACET_SORT 				= "usp_Delete_Facet_Sort_Rule";
	public static final String SP_GET_FACET_SORT 					= "usp_Get_Facet_Sort_Rule";
	public static final String SP_UPDATE_FACET_SORT    				= "usp_Update_Facet_Sort_Rule";
	public static final String SP_ADD_FACET_GROUP					= "usp_Add_Facet_Group";
	public static final String SP_UPDATE_FACET_GROUP				= "usp_Update_Facet_Group";
	public static final String SP_DELETE_FACET_GROUP				= "usp_Delete_Facet_Group";
	public static final String SP_ADD_FACET_GROUP_ITEM				= "usp_Add_Facet_Group_Item";
	public static final String SP_UPDATE_FACET_GROUP_ITEM			= "usp_Update_Facet_Group_Item";
	public static final String SP_DELETE_FACET_GROUP_ITEM			= "usp_Delete_Facet_Group_Item";
	public static final String SP_CLEAR_FACET_GROUP_ITEM			= "usp_Clear_Facet_Group_Item";

	// CAMPAIGN
	public static final String SP_ADD_CAMPAIGN 						= "usp_Add_Campaign";
	public static final String SP_GET_CAMPAIGN 						= "usp_Get_Campaign";
	public static final String SP_SEARCH_CAMPAIGN 					= "usp_Search_Campaign";
	public static final String SP_UPDATE_CAMPAIGN					= "usp_Update_Campaign";
	public static final String SP_DELETE_CAMPAIGN 					= "usp_Delete_Campaign";
	public static final String SP_UPDATE_CAMPAIGN_COMMENT 			= "usp_Update_Campaign_Comment";
	public static final String SP_APPEND_CAMPAIGN_COMMENT 			= "usp_Append_Campaign_Comment";
	// BANNER
	public static final String SP_ADD_BANNER 						= "usp_Add_Banner";
	public static final String SP_GET_BANNER 						= "usp_Get_Banner";
	public static final String SP_SEARCH_BANNER 					= "usp_Search_Banner";
	public static final String SP_UPDATE_BANNER						= "usp_Update_Banner";
	public static final String SP_DELETE_BANNER 					= "usp_Delete_Banner";
	public static final String SP_UPDATE_BANNER_COMMENT 			= "usp_Update_Banner_Comment";
	public static final String SP_APPEND_BANNER_COMMENT 			= "usp_Append_Banner_Comment";
	// CAMPAIGN_BANNER_RELATIONSHIP
	public static final String SP_ADD_CAMPAIGN_BANNER 				= "usp_Add_Campaign_Banner_Relationship";
	public static final String SP_UPDATE_CAMPAIGN_BANNER 			= "usp_Update_Campaign_Banner_Relationship";
	public static final String SP_DELETE_CAMPAIGN_BANNER 			= "usp_Delete_Campaign_Banner_Relationship";
	public static final String SP_GET_CAMPAIGN_BANNER 				= "usp_Get_Campaign_Banner_Relationship";
	public static final String SP_SEARCH_CAMPAIGN_BANNER 			= "usp_Search_Campaign_Banner_Relationship";
	// CAMPAIGN_PROD_KEYWORD_BANNER_RELATIONSHIP
	public static final String SP_ADD_CAMPAIGN_BANNER_KEYWORD 		= "usp_Add_Campaign_Prod_Keyword_Banner_Relationship";
	public static final String SP_DELETE_CAMPAIGN_BANNER_KEYWORD 	= "usp_Delete_Campaign_Prod_Keyword_Banner_Relationship";
	public static final String SP_GET_CAMPAIGN_BANNER_KEYWORD 		= "usp_Get_Campaign_Prod_Keyword_Banner_Relationship";
	public static final String SP_GET_CAMPAIGN_BANNER_WITH_KEYWORD	= "usp_Search_Campaign_Prod_Keyword_Banner_Relationship";
	// AUDIT_TRAIL
	public static final String SP_ADD_AUDIT_TRAIL 					= "usp_Add_Audit_Trail";
	public static final String SP_GET_AUDIT_TRAIL 					= "usp_Get_Audit_Trail";
	// RELEVANCY
	public static final String SP_ADD_RELEVANCY 					= "usp_Add_Relevancy";
	public static final String SP_GET_RELEVANCY 					= "usp_Get_Relevancy";
	public static final String SP_SEARCH_RELEVANCY 					= "usp_Search_Relevancy";
	public static final String SP_UPDATE_RELEVANCY					= "usp_Update_Relevancy";
	public static final String SP_DELETE_RELEVANCY 					= "usp_Delete_Relevancy";
	public static final String SP_UPDATE_RELEVANCY_COMMENT 			= "usp_Update_Relevancy_Comment";
	public static final String SP_APPEND_RELEVANCY_COMMENT 			= "usp_Append_Relevancy_Comment";
	// RELEVANCY_KEYWORD
	public static final String SP_ADD_RELEVANCY_FIELD 				= "usp_Add_Relevancy_Field";
	public static final String SP_GET_RELEVANCY_FIELD 				= "usp_Get_Relevancy_Field";
	public static final String SP_UPDATE_RELEVANCY_FIELD			= "usp_Update_Relevancy_Field";
	public static final String SP_DELETE_RELEVANCY_FIELD			= "usp_Delete_Relevancy_Field";
	// RELEVANCY
	public static final String SP_ADD_RELEVANCY_KEYWORD 			= "usp_Add_Relevancy_Prod_Keyword_Relationship";
	public static final String SP_GET_RELEVANCY_KEYWORD 			= "usp_Get_Relevancy_Prod_Keyword_Relationship";
	public static final String SP_UPDATE_RELEVANCY_KEYWORD 			= "usp_Update_Relevancy_Prod_Keyword_Relationship";
	public static final String SP_SEARCH_RELEVANCY_KEYWORD 			= "usp_Search_Relevancy_Prod_Keyword_Relationship";
	public static final String SP_DELETE_RELEVANCY_KEYWORD 			= "usp_Delete_Relevancy_Prod_Keyword_Relationship";
	//REDIRECT_RULE
	public static final String SP_ADD_REDIRECT						= "usp_Add_Redirect_Rule";
	public static final String SP_UPDATE_REDIRECT					= "usp_Update_Redirect_Rule";
	public static final String SP_DELETE_REDIRECT					= "usp_Delete_Redirect_Rule";
	public static final String SP_GET_REDIRECT						= "usp_Get_Redirect_Rule";
	//REDIRECT_RULE_KEYWORD
	public static final String SP_ADD_REDIRECT_KEYWORD				= "usp_Add_Redirect_Rule_Keyword_Relationship";
	public static final String SP_DELETE_REDIRECT_KEYWORD			= "usp_Delete_Redirect_Rule_Keyword_Relationship";
	//REDIRECT_RULE_CONDITION
	public static final String SP_ADD_REDIRECT_CONDITION			= "usp_Add_Redirect_Rule_Condition_Relationship";
	public static final String SP_UPDATE_REDIRECT_CONDITION			= "usp_Update_Redirect_Rule_Condition_Relationship";
	public static final String SP_DELETE_REDIRECT_CONDITION			= "usp_Delete_Redirect_Rule_Condition_Relationship";

	//RULE STATUS
	public static final String SP_ADD_RULE_STATUS					= "usp_Add_Rule_Status";
	public static final String SP_UPDATE_RULE_STATUS				= "usp_Update_Rule_Status";
	public static final String SP_DELETE_RULE_STATUS				= "usp_Delete_Rule_Status";
	public static final String SP_GET_RULE_STATUS					= "usp_Get_Rule_Status";
	//COMMENT
	public static final String SP_ADD_COMMENT						= "usp_Add_Comment_Status";
	public static final String SP_UPDATE_COMMENT					= "usp_Update_Comment_Status";
	public static final String SP_DELETE_COMMENT					= "usp_Delete_Comment_Status";
	public static final String SP_GET_COMMENT						= "usp_Get_Comment_Status";

	//CATEGORY
	public static final String SP_GET_CATEGORY_BY_FILTER			= "usp_Get_Product_Category_by_Filter";

	//USER
	public static final String SP_GET_USERS							= "usp_Get_Manager_Group_User";
	public static final String SP_ADD_USERS							= "usp_Add_Manager_User";
	public static final String SP_UPDATE_USERS						= "usp_Update_Manager_User";
	public static final String SP_DELETE_USERS						= "usp_Delete_Manager_User";
	public static final String SP_GET_GROUP_SECURITY				= "usp_Get_Manager_Group_Security";

	//EXPORT_RULE_MAP
	public static final String SP_GET_EXPORT_RULE_MAP				= "usp_Get_Export_Rule_Map";
	public static final String SP_ADD_EXPORT_RULE_MAP				= "usp_Add_Export_Rule_Map";
	public static final String SP_UPDATE_EXPORT_RULE_MAP			= "usp_Update_Export_Rule_Map";
	public static final String SP_DELETE_EXPORT_RULE_MAP			= "usp_Delete_Export_Rule_Map";
	
	/* Stored Procedure Parameter Names */
	public static final String PARAM_STORE_ID 						= "store_id";
	public static final String PARAM_KEYWORD_ID						= "keyword_id";
	public static final String PARAM_KEYWORD 						= "keyword";
	public static final String PARAM_SEQUENCE 						= "sequence";
	public static final String PARAM_SEQUENCE_NUM 					= "sequence_num";
	public static final String PARAM_MEMBER_ID 						= "member_id";
	public static final String PARAM_VALUE 							= "value";
	public static final String PARAM_START_ROW 						= "start_row";
	public static final String PARAM_END_ROW 						= "end_row";
	public static final String PARAM_EXACT_MATCH 					= "exact_match";
	public static final String PARAM_CREATED_BY 					= "created_by";
	public static final String PARAM_MODIFIED_BY 					= "modified_by";
	public static final String PARAM_EXPIRY_DATE 					= "expiry_date";
	public static final String PARAM_START_DATE 					= "start_date";
	public static final String PARAM_END_DATE 						= "end_date";
	public static final String PARAM_COMMENT 						= "comment";
	public static final String PARAM_CAMPAIGN 						= "campaign";
	public static final String PARAM_CAMPAIGN_ID 					= "campaign_id";
	public static final String PARAM_CAMPAIGN_NAME 					= "campaign_name";
	public static final String PARAM_USER_NAME   					= "user_name";
	public static final String PARAM_OPERATION   					= "operation";
	public static final String PARAM_ENTITY   						= "entity";
	public static final String PARAM_REFERENCE   					= "reference";
	public static final String PARAM_DATE   						= "date";
	public static final String PARAM_DETAILS   						= "details";
	public static final String PARAM_STORE   						= "store";
	public static final String PARAM_IMAGE_URL						= "image_url";
	public static final String PARAM_LINK_URL  						= "link_url";
	public static final String PARAM_BANNER							= "banner";
	public static final String PARAM_BANNER_ID						= "banner_id";
	public static final String PARAM_BANNER_NAME					= "banner_name";
	public static final String PARAM_MATCH_TYPE_CAMPAIGN 			= "match_type_campaign";
	public static final String PARAM_MATCH_TYPE_BANNER 				= "match_type_banner";
	public static final String PARAM_RELEVANCY_ID 					= "relevancy_id";
	public static final String PARAM_RELEVANCY_NAME 				= "relevancy_name";
	public static final String PARAM_RELEVANCY_DESCRIPTION			= "relevancy_description";
	public static final String PARAM_FIELD_NAME						= "field_name";
	public static final String PARAM_FIELD_VALUE					= "field_value";
	public static final String PARAM_PRIORITY						= "field_value";
	public static final String PARAM_RELEVANCY 						= "relevancy";
	public static final String PARAM_MATCH_TYPE_RELEVANCY 			= "match_type_relevancy";
	public static final String PARAM_CAT_CODE 						= "catcode";
	public static final String PARAM_NAME	 						= "name";
	public static final String PARAM_RULE_ID						= "rule_id";
	public static final String PARAM_RULE_NAME						= "rule_name";
	public static final String PARAM_CONDITION 						= "condition";
	public static final String PARAM_RULE_PRIORITY	 	 			= "rule_priority";
	public static final String PARAM_REDIRECT_URL	 	 			= "redirect_url";
	public static final String PARAM_INCLUDE_KEYWORD 	 			= "include_keyword";
	public static final String PARAM_SEARCH_TERM	 	 			= "search_term";
	public static final String PARAM_ACTIVE_FLAG		 			= "active_flag";
	public static final String PARAM_REDIRECT_TYPE_ID	 			= "redirect_type_id";
	public static final String PARAM_CHANGE_KEYWORD		 			= "change_keyword";
	public static final String PARAM_RESULT 						= "result";

	public static final String PARAM_RULE_STATUS_ID		 			= "rule_status_id";
	public static final String PARAM_RULE_TYPE_ID		 			= "rule_type_id";
	public static final String PARAM_COMMENT_ID		 				= "comment_id";
	public static final String PARAM_REFERENCE_ID		 			= "reference_id";
	public static final String PARAM_PUBLISHED_STATUS	 			= "p_status";
	public static final String PARAM_APPROVED_STATUS	 			= "a_status";
	public static final String PARAM_EVENT_STATUS		 			= "e_status";
	public static final String PARAM_DESCRIPTION 					= "description";
	public static final String PARAM_RULE_NAME_LIKE					= "rule_name_like";
	public static final String PARAM_SEARCH_TERM_LIKE	 	 		= "search_term_like";

	public static final String PARAM_SALUTATION			 	 		= "salutation";
	public static final String PARAM_FULL_NAME			 	 		= "full_name";
	public static final String PARAM_FIRST_NAME			 	 		= "first_name";
	public static final String PARAM_MIDDLE_NAME			 	 	= "middle_name";
	public static final String PARAM_LAST_NAME			 	 		= "last_name";
	public static final String PARAM_PERSONAL_TITLE			 	 	= "personal_title";
	public static final String PARAM_PASSWORD_HINT			 	 	= "password_hint";
	public static final String PARAM_CURRENT_PASSWORD	 	 		= "current_password";
	public static final String PARAM_EMAIL				 	 		= "email";
	public static final String PARAM_ACCT_NON_LOCKED	 	 		= "acct_non_locked";
	public static final String PARAM_REQUIRE_PASSWORD_CHANGE 		= "require_password_change";
	public static final String PARAM_LAST_ACCESS_DATE	 	 		= "last_access_date";
	public static final String PARAM_IP					 	 		= "internet_protocol";
	public static final String PARAM_ACTIVE_USER		 	 		= "Active_User";
	public static final String PARAM_SUCCESSIVE_FAILED_LOGINS 		= "successive_failed_logins";
	public static final String PARAM_HAS_LOGGED_OUT			 		= "has_logged_out";
	public static final String PARAM_THRU_DATE				 		= "thru_date";
	public static final String PARAM_USER_NAMELIKE					= "User_NameLike";
	public static final String PARAM_USER_LOCKED					= "User_Locked";
	public static final String PARAM_START_DATE2 					= "StartDate";
	public static final String PARAM_END_DATE2 						= "EndDate";
	public static final String PARAM_START_ROW2 					= "StartRow";
	public static final String PARAM_END_ROW2 						= "EndRow";

	public static final String PARAM_GROUP_ID			 	 		= "group_id";
	public static final String PARAM_PERMISSION_ID		 			= "Permission_ID";
	public static final String PARAM_ADMIN				 			= "admin";
	public static final String PARAM_MEMBER_TYPE_ID					= "member_type_id";
	public static final String PARAM_FACET_NAME						= "facet_name";
	public static final String PARAM_FORCE_ADD						= "force_add";

	public static final String PARAM_FACET_GROUP_ID					= "facet_group_id";
	public static final String PARAM_FACET_GROUP_NAME				= "facet_group_name";
	public static final String PARAM_FACET_GROUP_TYPE				= "facet_group_type";
	public static final String PARAM_FACET_GROUP_SEQUENCE			= "facet_group_sequence";
	public static final String PARAM_FACET_GROUP_ITEM_NAME			= "group_item_name";
	public static final String PARAM_FACET_GROUP_ITEM_SEQUENCE		= "group_item_sequence";
	public static final String PARAM_RULE_TYPE			 			= "rule_type";
	public static final String PARAM_SORT_TYPE			 			= "sort_type";
	public static final String PARAM_MATCH_TYPE			 			= "match_type";
	public static final String PARAM_RETURN_OPTION			 		= "return_option";

	// additional rule status parameters
	public static final String PARAM_REQUEST_BY						= "request_by";
	public static final String PARAM_LAST_REQUEST_DATE				= "last_request_date";
	public static final String PARAM_APPROVAL_BY					= "approval_by";
	public static final String PARAM_LAST_APPROVAL_DATE				= "last_approval_date";
	public static final String PARAM_PUBLISHED_BY					= "published_by";
	public static final String PARAM_EXPORT_TYPE					= "export_type";
	public static final String PARAM_EXPORT_BY						= "export_by";
	public static final String PARAM_LAST_EXPORT_DATE				= "last_export_date";
	public static final String PARAM_SORT_BY						= "sort_by";
	
	// export_rule_map_parameters
	public static final String PARAM_PROD_STORE_ID_ORIGIN			= "store_id_origin";
	public static final String PARAM_RULE_ID_ORIGIN   				= "rule_id_origin";
	public static final String PARAM_RULE_NAME_ORIGIN     			= "rule_name_origin";
	public static final String PARAM_PROD_STORE_ID_TARGET			= "store_id_target";
	public static final String PARAM_RULE_ID_TARGET   				= "rule_id_target";
	public static final String PARAM_RULE_NAME_TARGET     			= "rule_name_target";
	
	// DO NOT CHANGE CARELESSLY, THERE MIGHT BE OTHER TABLES USING IT! COLUMN NAMES ARE CASE-INSENSITIVE
	public static final String COLUMN_KEYWORD      					= "keyword";
	public static final String COLUMN_PROD_KEYWORD_ID   			= "prod_keyword_id";
	public static final String COLUMN_PRODUCT_STORE_ID     			= "product_store_id";
	public static final String COLUMN_STORE_NAME  					= "store_name";
	public static final String COLUMN_STORE_ID						= "store_id";
	public static final String COLUMN_STORE  						= "store";
	public static final String COLUMN_VALUE       					= "value";
	public static final String COLUMN_SEQUENCE_NUM 					= "sequence_num";
	public static final String COLUMN_ROW_NUMBER   					= "rowno";
	public static final String COLUMN_EXPIRY_DATE   				= "expiry_date";
	public static final String COLUMN_LAST_MODIFIED_BY  			= "modified_by";
	public static final String COLUMN_CREATED_BY   					= "created_by";
	public static final String COLUMN_COMMENT   					= "comment";
	public static final String COLUMN_CREATED_DATE   				= "date_created";
	public static final String COLUMN_LAST_MODIFIED_DATE 			= "date_last_modified";
	public static final String COLUMN_CAMPAIGN_ID   				= "campaign_id";
	public static final String COLUMN_CAMPAIGN_NAME   				= "campaign_name";
	public static final String COLUMN_NAME   						= "name";
	public static final String COLUMN_START_DATE   					= "start_date";
	public static final String COLUMN_END_DATE   					= "end_date";
	public static final String COLUMN_USER_NAME   					= "user_name";
	public static final String COLUMN_OPERATION   					= "operation";
	public static final String COLUMN_ENTITY   						= "entity";
	public static final String COLUMN_REFERENCE   					= "reference";
	public static final String COLUMN_DATE   						= "date";
	public static final String COLUMN_DETAILS   					= "details";
	public static final String COLUMN_BANNER_ID   					= "banner_id";
	public static final String COLUMN_BANNER_NAME  					= "banner_name";
	public static final String COLUMN_IMAGE_URL   					= "image_url";
	public static final String COLUMN_LINK_URL   					= "link_url";
	public static final String COLUMN_RELEVANCY_ID 					= "relevancy_id";
	public static final String COLUMN_RELEVANCY_NAME 				= "relevancy_name";
	public static final String COLUMN_DESCRIPTION 					= "description";
	public static final String COLUMN_FIELD_NAME 					= "field_name";
	public static final String COLUMN_FIELD_VALUE 					= "field_value";
	public static final String COLUMN_PRIORITY 						= "priority";
	public static final String COLUMN_PRODUCT_CATEGORY_ID  			= "product_category_id";
	public static final String COLUMN_CATEGORY_NAME 	 			= "category_name";
	public static final String COLUMN_MANUFACTURER	 	 			= "manufacturer";

	public static final String COLUMN_RULE_ID						= "rule_id";
	public static final String COLUMN_SEARCH_TERM	 	 			= "search_term";
	public static final String COLUMN_CHANGE_KEYWORD 	 			= "change_keyword";
	public static final String COLUMN_CONDITION						= "condition";
	public static final String COLUMN_SEQUENCE_NUMBER				= "sequence_number";
	public static final String COLUMN_ACTIVE_FLAG		 			= "active_flag";
	public static final String COLUMN_REDIRECT_TYPE_ID	 			= "redirect_type_id";
	public static final String COLUMN_REDIRECT_URL 	 				= "redirect_url";
	public static final String COLUMN_INCLUDE_KEYWORD 	 			= "include_keyword";

	public static final String COLUMN_RULE_STATUS_ID		 		= "rule_status_id";
	public static final String COLUMN_RULE_TYPE_ID		 			= "rule_type_id";
	public static final String COLUMN_REFERENCE_ID		 			= "reference_id";
	public static final String COLUMN_COMMENT_ID		 			= "comment_id";
	public static final String COLUMN_PUBLISHED_STATUS	 			= "published_status";
	public static final String COLUMN_APPROVED_STATUS	 			= "approved_status";
	public static final String COLUMN_EVENT_STATUS	 				= "event_status";
	public static final String COLUMN_LAST_PUBLISHED_DATE			= "last_published_date";
	public static final String COLUMN_CREATED_STAMP					= "created_stamp";
	public static final String COLUMN_LAST_UPDATED_STAMP			= "last_updated_stamp";

	public static final String COLUMN_GROUP_ID				 		= "group_id";
	public static final String COLUMN_PERMISSION_ID		 			= "permission_id";

	public static final String COLUMN_FULL_NAME			 	 		= "full_name";
	public static final String COLUMN_CURRENT_PASSWORD	 	 		= "current_password";
	public static final String COLUMN_EMAIL				 	 		= "email";
	public static final String COLUMN_ACCT_NON_LOCKED	 	 		= "acct_non_locked";
	public static final String COLUMN_CRED_NON_EXPIRED	 	 		= "require_password_change";
	public static final String COLUMN_LAST_ACCESS_DATE	 	 		= "last_access_date";
	public static final String COLUMN_IP				 	 		= "internet_protocol";
	public static final String COLUMN_THRU_DATE			 	 		= "thru_date";

	public static final String COLUMN_MEMBER_TYPE_ID	 	 		= "member_type_id";
	public static final String COLUMN_MEMBER_ID 					= "member_id";
	public static final String COLUMN_FORCE_ADD 					= "force_add";

	public static final String COLUMN_FACET_GROUP_ID 				= "facet_group_id";
	public static final String COLUMN_FACET_GROUP_NAME 				= "facet_group_name";
	public static final String COLUMN_FACET_GROUP_TYPE 				= "facet_group_type";
	public static final String COLUMN_FACET_GROUP_SEQUENCE			= "facet_group_seq";
	public static final String COLUMN_GROUP_ID_LIST 				= "group_id_list";
	public static final String COLUMN_GROUP_NAME_LIST 				= "group_name_list";
	public static final String COLUMN_ITEM_NAME_LIST 				= "item_name_list";
	public static final String COLUMN_SORT_ID_LIST 					= "sort_id_list";
	public static final String COLUMN_RULE_NAME 					= "rule_name";
	public static final String COLUMN_SORT_TYPE 					= "sort_type";
	public static final String COLUMN_RULE_TYPE 					= "rule_type";
	public static final String COLUMN_FACET_GROUP_ITEM_NAME 		= "facet_group_item_name";
	public static final String COLUMN_FACET_GROUP_ITEM_SEQUENCE		= "facet_group_item_seq";

	// additional rule status columns
	public static final String COLUMN_REQUEST_BY					= "requested_by";
	public static final String COLUMN_LAST_REQUEST_DATE				= "last_request_date";
	public static final String COLUMN_APPROVAL_BY					= "approved_by";
	public static final String COLUMN_LAST_APPROVAL_DATE			= "last_approval_date";
	public static final String COLUMN_PUBLISHED_BY					= "published_by";
	public static final String COLUMN_EXPORT_TYPE					= "export_type";
	public static final String COLUMN_EXPORT_BY						= "exported_by";
	public static final String COLUMN_LAST_EXPORT_DATE				= "last_export_date";

	// export_rule_map_parameters
	public static final String COLUMN_PROD_STORE_ID_ORIGIN			= "product_store_id_origin";
	public static final String COLUMN_RULE_ID_ORIGIN   				= "rule_id_origin";
	public static final String COLUMN_RULE_NAME_ORIGIN     			= "rule_name_origin";
	public static final String COLUMN_PROD_STORE_ID_TARGET			= "product_store_id_target";
	public static final String COLUMN_RULE_ID_TARGET   				= "rule_id_target";
	public static final String COLUMN_RULE_NAME_TARGET     			= "rule_name_target";
	// GET* SPs total size
	public static final String COLUMN_TOTAL_NUMBER 					= "total_number";

	// ADD/UPDATE/DELETE* SPs total size
	public static final String COLUMN_RESULT 						= "RESULT";

	// will be used in elevate/exclude/etc. once feature for adding by brand/facet is added. Important: existing SP must be changed
	public static final String MEMBER_TYPE_PART 					= "PART_NUMBER";


	// used by settings
	public static final String SETTINGS_AUTO_EXPORT 				= "auto_export";
	public static final String SETTINGS_EXPORT_TARGET				= "export_target";
	
}
