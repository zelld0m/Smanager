package com.search.manager.core.constant;

public class TypeaheadDaoConstant {

    public static final String COLUMN_TYPEAHEAD_SUGGESTION_ID = "TYPEAHEAD_SUGGESTION_ID";
    public static final String COLUMN_MEMBER_TYPE = "MEMBER_TYPE";
    public static final String COLUMN_MEMBER_VALUE = "MEMBER_VALUE";

    public static final String COLUMN_TYPEAHEAD_BRAND_ID = "TYPEAHEAD_BRAND_ID";
    public static final String COLUMN_BRAND_NAME = "BRAND_NAME";
    public static final String COLUMN_VENDOR_ID = "VENDOR_ID";
    public static final String COLUMN_PRODUCT_COUNT = "PRODUCT_COUNT";
    public static final String COLUMN_PRIORITY = "PRIORITY";
    public static final String COLUMN_DISABLED = "DISABLED";
    public static final String COLUMN_STATUS = "STATUS_ID";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";

    public static final String COLUMN_CATEGORY = "CATEGORY";

    public static final String PARAM_ORDER_BY = "ORDER_BY";

    public static final String SP_ADD_TYPEAHEAD_RULE = "dbo.usp_ADD_TYPEAHEAD_KEYWORD";
    public static final String SP_DELETE_TYPEAHEAD_RULE = "dbo.usp_DELETE_TYPEAHEAD_KEYWORD";
    public static final String SP_UPDATE_TYPEAHEAD_RULE = "dbo.usp_UPDATE_TYPEAHEAD_KEYWORD";
    public static final String SP_GET_TYPEAHEAD_RULE = "dbo.usp_GET_TYPEAHEAD_KEYWORD";

    public static final String SP_ADD_TYPEAHEAD_CATEGORY = "dbo.usp_Add_Typeahead_Category";
    public static final String SP_UPDATE_TYPEAHEAD_CATEGORY = "dbo.usp_Update_Typeahead_Category";
    public static final String SP_GET_TYPEAHEAD_CATEGORY = "dbo.usp_Get_Typeahead_Category";
    
    public static final String SP_ADD_TYPEAHEAD_BRAND = "dbo.usp_Add_Typeahead_Brand";
    public static final String SP_UPDATE_TYPEAHEAD_BRAND = "dbo.usp_Update_Typeahead_Brand";
    public static final String SP_GET_TYPEAHEAD_BRAND = "dbo.usp_Get_Typeahead_Brand";

    public static final String SP_ADD_TYPEAHEAD_SUGGESTION = "dbo.usp_Add_Typeahead_Suggestion";
    public static final String SP_UPDATE_TYPEAHEAD_SUGGESTION = "dbo.usp_Update_Typeahead_Suggestion";
    public static final String SP_GET_TYPEAHEAD_SUGGESTION = "dbo.usp_Get_Typeahead_Suggestion";
}
