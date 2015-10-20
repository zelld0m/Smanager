package com.search.manager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.manager.core.model.ModelBean;
import com.search.manager.exception.DataException;
import com.search.manager.service.CategoryService;
import com.search.manager.utility.CatCodeUtil.Attribute;
import com.search.ws.SolrConstants;

@DataTransferObject(converter = BeanConverter.class)
public class RedirectRuleCondition extends ModelBean {

    // increment after every change in model
    private static final long serialVersionUID = -6248904441308276236L;
    private static final Logger logger = LoggerFactory.getLogger(RedirectRuleCondition.class);
    private String ruleId;
    private Integer sequenceNumber;
    private String storeId;
    private String facetPrefix;
    private String facetTemplate;
    private String facetTemplateName;
    private Map<String, List<String>> conditionMap = new HashMap<String, List<String>>();

    public RedirectRuleCondition() {
    }

    public RedirectRuleCondition(Map<String, List<String>> filter) {
        conditionMap.putAll(filter);
    }

    public RedirectRuleCondition(String ruleId, String condition) {
        this.ruleId = ruleId;
        setCondition(condition);
    }

    public RedirectRuleCondition(String ruleId, Integer sequenceNumber) {
        this.ruleId = ruleId;
        this.sequenceNumber = sequenceNumber;
    }

    public RedirectRuleCondition(String ruleId, Integer sequenceNumber, String condition) {
        this.ruleId = ruleId;
        this.sequenceNumber = sequenceNumber;
        setCondition(condition);
    }

    public RedirectRuleCondition(String condition) {
        setCondition(condition);
    }

    public String getCondition() {
        return getCondition(false);
    }

    public String getConditionForSolr() {
        return getCondition(true);
    }

    private String getCondition(boolean forSolr) {
        // convert from condition map
        // Category, SubCategory, Class, SubClass, Manufacturer are grouped together
        // e.g. Category:"Systems" AND SubCategory:"Notebook Computers" AND Class:"Intel Core i3 Notebook Computers" AND SubClass:"2.75GHz and up" AND Manufacturer:"Acer"
        // -or- CatCode and Manufacturer are grouped together
        // e.g. CatCode:3F* AND Manufacturer:"Acer"
        // -or- _FacetTemplate is treated as one	
        // (TemplateName or FacetTemplateName) and af* are grouped together (next sprint)
        StringBuilder builder = new StringBuilder();
        Map<String, List<String>> map = null;

        if (isIMSFilter()) {
            map = getIMSFilters();
            for (String key : map.keySet()) {
                builder.append(key).append(":");
                List<String> values = map.get(key);
                if (values.size() == 1) {
                    if (SolrConstants.CAT_CODE.equals(key)) {
                        String value = values.get(0);
                        if (!value.endsWith("*") && value.length() < 4) {
                            value += "*";
                        }
                        builder.append(value);
                    } else {
                        // temp workaround for old data
                        String value = values.get(0);
                        builder.append(forSolr ? ClientUtils.escapeQueryChars(value) : value);
                    }
                } else {
                    // TODO: support for multiple values
                }
                builder.append(SolrConstants.AND);
            }
        } else if (isCNetFilter()) {
            map = getCNetFilters();
            if (CollectionUtils.isNotEmpty(map.get(SolrConstants.LEVEL_1_CATEGORY))) {
                String value = map.get(SolrConstants.LEVEL_1_CATEGORY).get(0);
                builder.append(forSolr ? getFacetTemplate() : SolrConstants.FACET_TEMPLATE).append(":").append(forSolr ? ClientUtils.escapeQueryChars(value) : value);
                if (CollectionUtils.isNotEmpty(map.get(SolrConstants.LEVEL_2_CATEGORY))) {
                    value = map.get(SolrConstants.LEVEL_2_CATEGORY).get(0);
                    builder.append(forSolr ? ClientUtils.escapeQueryChars(" | ") : " | ").append(forSolr ? ClientUtils.escapeQueryChars(value) : value);
                    if (CollectionUtils.isNotEmpty(map.get(SolrConstants.LEVEL_3_CATEGORY))) {
                        value = map.get(SolrConstants.LEVEL_3_CATEGORY).get(0);
                        builder.append(forSolr ? ClientUtils.escapeQueryChars(" | ") : " | ").append(forSolr ? ClientUtils.escapeQueryChars(value) : value);
                    }
                }
                builder.append(forSolr ? "*" : "").append(SolrConstants.AND);
            }

            String key = SolrConstants.MANUFACTURER;
            List<String> values = map.get(key);
            if (values != null && values.size() == 1) {
                String value = values.get(0);
                builder.append("Manufacturer:").append(forSolr ? ClientUtils.escapeQueryChars(value) : value);
                builder.append(SolrConstants.AND);
            } else {
                // TODO: support for multiple values
            }
        }

        // TODO: dynamic attributes
        map = getDynamicAttributes();
        if (MapUtils.isNotEmpty(map)) {
            String templateName = null;
            for (String key : map.keySet()) {
                if (StringUtils.equals(key, SolrConstants.TEMPLATE_NAME)) {
                    templateName = map.get(key).get(0);
                    String value = templateName;
                    builder.append(key).append(":").append(forSolr ? ClientUtils.escapeQueryChars(value) : value).append(SolrConstants.AND);
                    break;
                } else if (StringUtils.equals(key, SolrConstants.FACET_TEMPLATE_NAME)) {
                    templateName = map.get(key).get(0);
                    String value = templateName;
                    builder.append(forSolr ? getFacetTemplateName() : SolrConstants.FACET_TEMPLATE_NAME).append(":").append(forSolr ? ClientUtils.escapeQueryChars(value) : value).append(SolrConstants.AND);
                    break;
                }
            }

            if (StringUtils.isNotBlank(templateName)) {
                for (String key : map.keySet()) {
                    if (!(StringUtils.equals(key, SolrConstants.TEMPLATE_NAME) || StringUtils.equals(key, SolrConstants.FACET_TEMPLATE_NAME))) {
                        List<String> values = map.get(key);
                        if (CollectionUtils.isNotEmpty(values)) {
                            builder.append(key).append(":");

                            if (forSolr) {
                                builder.append("(");
                            }
                            for (String value : values) {
                                builder.append(forSolr ? ClientUtils.escapeQueryChars(value) : value);
                                builder.append(forSolr ? " " : SolrConstants.OR);
                            }
                            if (forSolr) {
                                builder.append(")");
                            } else {
                                builder.replace(builder.length() - 4, builder.length(), "");
                            }
                            builder.append(SolrConstants.AND);
                        }
                    }
                }
            }
        }


        // Platform, Condition, Availability, License, ImageExists are grouped together
        // special processing
        // if Condition == SolrConstants.REFURBISHED set Refurbished_Flag:1
        //				== SolrConstants.OPEN_BOX    set OpenBox_Flag:1
        //              == SolrConstants.CLEARANCE   set Clearance_Flag:1
        // if License == SolrConstants.NON_LICENSE set Licence_Flag:0
        //		      == SolrConstants.LICENSE_ONLY, set Licence_Flag:1
        // if Availability == SolrConstants.IN_STOCK set InStock:1
        //                 == SolrConstants.CALL     set InStock:0
        // if ImageExists == "Products with Image Only" 	set ImageExists:1
        //                == "Products without Image Only"  set ImageExists:0

        map = getFacets();
        if (map.containsKey(SolrConstants.AND)) {
            String value = map.get(SolrConstants.AND).get(0);
            if (value.equals(SolrConstants.REFURBISHED)) {
                builder.append(SolrConstants.IS_REFURB).append(":"+SolrConstants.YES).append(SolrConstants.AND);
            } else if (value.equals(SolrConstants.OPEN_BOX)) {
                builder.append(SolrConstants.IS_OPEN_BOX).append(":"+SolrConstants.YES).append(SolrConstants.AND);
            } else if (value.equals(SolrConstants.CLEARANCE)) {
                builder.append(SolrConstants.IS_CLEARANCE).append(":"+SolrConstants.YES).append(SolrConstants.AND);
            }
        }
        if (map.containsKey(SolrConstants.LICENSE)) {
            String value = map.get(SolrConstants.LICENSE).get(0);
            if (value.equals(SolrConstants.NON_LICENSE_ONLY)) {
                builder.append(SolrConstants.IS_LICENSE).append(":"+SolrConstants.NO).append(SolrConstants.AND);
            } else if (value.equals(SolrConstants.LICENSE_ONLY)) {
                builder.append(SolrConstants.IS_LICENSE).append(":"+SolrConstants.YES).append(SolrConstants.AND);
            }
        }

        if (map.containsKey(SolrConstants.IMAGE_EXISTS)) {
            String value = map.get(SolrConstants.IMAGE_EXISTS).get(0);
            if (value.equals(SolrConstants.NO_IMAGE_ONLY)) {
                builder.append(SolrConstants.IMAGE_EXISTS).append(":0").append(SolrConstants.AND);
            } else if (value.equals(SolrConstants.IMAGE_ONLY)) {
                builder.append(SolrConstants.IMAGE_EXISTS).append(":1").append(SolrConstants.AND);
            }
        }

        if (map.containsKey(SolrConstants.AVAILABILITY)) {
            String value = map.get(SolrConstants.AVAILABILITY).get(0);
            if (value.equals(SolrConstants.CALL)) {
                builder.append(SolrConstants.QTY_AVAILABLE).append(":0").append(SolrConstants.AND);
            } else if (value.equals(SolrConstants.IN_STOCK)) {
                builder.append(SolrConstants.QTY_AVAILABLE).append(":[1 TO *]").append(SolrConstants.AND);
            }
        }
        if (map.containsKey(SolrConstants.PLATFORM)) {
            builder.append(SolrConstants.PLATFORM).append(":").append(map.get(SolrConstants.PLATFORM).get(0)).append(SolrConstants.AND);
        }

        if (map.containsKey(SolrConstants.NAME)) {
            String value = map.get(SolrConstants.NAME).get(0);
            if (forSolr) {
                value = ClientUtils.escapeQueryChars(value);
                builder.append("(").append(facetPrefix).append(SolrConstants.NAME_INDEX_SUFFIX).append(":").append(value).append(SolrConstants.OR);
            }
            builder.append(SolrConstants.NAME).append(":").append(value);
            if (forSolr) {
                builder.append(")");
            }
            builder.append(SolrConstants.AND);
        }
        if (map.containsKey(SolrConstants.DESCRIPTION)) {
            String value = map.get(SolrConstants.DESCRIPTION).get(0);
            if (forSolr) {
                value = ClientUtils.escapeQueryChars(value);
                builder.append("(").append(facetPrefix).append(SolrConstants.DESCRIPTION_SUFFIX).append(":").append(value).append(SolrConstants.OR);
            }
            builder.append(SolrConstants.DESCRIPTION).append(":").append(value);
            if (forSolr) {
                builder.append(")");
            }
            builder.append(SolrConstants.AND);
        }

        if (map.containsKey(SolrConstants.MFR_PN)) {
            String value = map.get(SolrConstants.MFR_PN).get(0);
            builder.append(value);
            builder.append(SolrConstants.AND);
        }
        
        if (builder.length() > 0) {
            builder.replace(builder.length() - 5, builder.length(), "");
        }
        return builder.toString();
    }

    public void setFilter(Map<String, List<String>> filter) {
        // TODO: add validation. no support for this current sprint
        synchronized (conditionMap) {
            conditionMap.clear();
            conditionMap.putAll(filter);
        }
    }

    private String encloseInQuotes(String string) {
        string = StringUtils.trim(string);
        if (StringUtils.isEmpty(string)) {
            return "";
        }
        return (string.startsWith("\"") && string.endsWith("\"")) ? string
                : "\"" + string + "\"";
    }

    public String getReadableString() {
        StringBuilder builder = new StringBuilder();
        // construct from condition
        Map<String, List<String>> map = null;

        if (isIMSFilter()) {
            map = getIMSFilters();

            if (CollectionUtils.isNotEmpty(map.get(SolrConstants.CAT_CODE))) {
                builder.append("Category Code is \"").append(map.get(SolrConstants.CAT_CODE).get(0));
                builder.append("\" and ");
            } else if (CollectionUtils.isNotEmpty(map.get(SolrConstants.CATEGORY))) {
                builder.append("Category is \"").append(map.get(SolrConstants.CATEGORY).get(0));
                if (CollectionUtils.isNotEmpty(map.get(SolrConstants.SUB_CATEGORY))) {
                    builder.append(" > ").append(map.get(SolrConstants.SUB_CATEGORY).get(0));
                    if (CollectionUtils.isNotEmpty(map.get(SolrConstants.CLASS))) {
                        builder.append(" > ").append(map.get(SolrConstants.CLASS).get(0));
                        if (CollectionUtils.isNotEmpty(map.get(SolrConstants.SUB_CLASS))) {
                            builder.append(" > ").append(map.get(SolrConstants.SUB_CLASS).get(0));
                        }
                    }
                }
                builder.append("\" and ");
            }

            String key = SolrConstants.MANUFACTURER;
            if (CollectionUtils.isNotEmpty(map.get(key))) {
                builder.append(key).append(" is ");
                List<String> values = map.get(key);
                if (values.size() == 1) {
                    builder.append(encloseInQuotes(values.get(0)));
                } else {
                    // TODO: support for multiple values
                }
                builder.append(" and ");
            }
        } else if (isCNetFilter()) {
            map = getCNetFilters();
            if (CollectionUtils.isNotEmpty(map.get(SolrConstants.LEVEL_1_CATEGORY))) {
                builder.append("Category is \"").append(map.get(SolrConstants.LEVEL_1_CATEGORY).get(0));
                if (CollectionUtils.isNotEmpty(map.get(SolrConstants.LEVEL_2_CATEGORY))) {
                    builder.append(" > ").append(map.get(SolrConstants.LEVEL_2_CATEGORY).get(0));
                    if (CollectionUtils.isNotEmpty(map.get(SolrConstants.LEVEL_3_CATEGORY))) {
                        builder.append(" > ").append(map.get(SolrConstants.LEVEL_3_CATEGORY).get(0));
                    }
                }
                builder.append("\" and ");
            }

            String key = SolrConstants.MANUFACTURER;
            if (CollectionUtils.isNotEmpty(map.get(key))) {
                builder.append(key).append(" is ");
                List<String> values = map.get(key);
                if (values.size() == 1) {
                    builder.append(encloseInQuotes(values.get(0)));
                } else {
                    // TODO: support for multiple values
                }
                builder.append(" and ");
            }
        }


        map = getDynamicAttributes();
        if (MapUtils.isNotEmpty(map)) {
            boolean isCNET = false;
            String templateName = null;
            for (String key : map.keySet()) {
                if (StringUtils.equals(key, SolrConstants.TEMPLATE_NAME) || StringUtils.equals(key, SolrConstants.FACET_TEMPLATE_NAME)) {
                    isCNET = StringUtils.equals(key, SolrConstants.FACET_TEMPLATE_NAME);
                    templateName = map.get(key).get(0);
                    break;
                }
            }

            // TODO: dynamic attributes
            if (StringUtils.isNotBlank(templateName)) {
                // get readable value
                try {
                    builder.append("Template Name is \"").append(templateName).append("\" AND ");

                    Map<String, Attribute> attributeMap;
                    if (this.storeId != null) {
                    	attributeMap = isCNET
                            ? CategoryService.getCNETTemplateAttributesMap(this.storeId, templateName)
                            : CategoryService.getIMSTemplateAttributesMap(this.storeId, templateName);
                    } else {
                    	attributeMap = new HashMap<String, Attribute>();
                    }
                    for (String key : map.keySet()) {
                        if (!(StringUtils.equals(key, SolrConstants.TEMPLATE_NAME) || StringUtils.equals(key, SolrConstants.FACET_TEMPLATE_NAME))) {
                            Attribute a = attributeMap.get(key);
                            List<String> values = map.get(key);
                            if (CollectionUtils.isNotEmpty(values)) {
                                builder.append(a == null ? key : a.getAttributeDisplayName()).append(" is ");
                                for (String value : values) {
                                    value = value.substring(value.indexOf("|") + 1);
                                    builder.append("\"").append(value).append("\"");
                                    builder.append(" or ");
                                }
                                builder.replace(builder.length() - 4, builder.length(), "");
                                builder.append(SolrConstants.AND);
                            }
                        }
                    }
                } catch (DataException e) {
                    logger.error("Failed to get template attributes", e);
                }
            }
        }

        String[] arrFieldContains = {SolrConstants.NAME, SolrConstants.DESCRIPTION};

        map = getFacets();
        for (String key : map.keySet()) {
            if (SolrConstants.IMAGE_EXISTS.equalsIgnoreCase(key)) {
                builder.append("Product Image");
            } else {
                builder.append(key);
            }

            builder.append(ArrayUtils.contains(arrFieldContains, key) ? " contains " : " is ");
            List<String> values = map.get(key);
            if (values.size() == 1) {
                builder.append(encloseInQuotes(values.get(0)));
            } else {
                // TODO: support for multiple values
            }
            builder.append(" and ");
        }

        if (builder.length() > 0) {
            builder.replace(builder.length() - 5, builder.length(), "");
        }

        return builder.toString();
    }

    private void putListToConditionMap(String key, String values) {
        List<String> list = conditionMap.get(key);
        if (list == null) {
            list = new ArrayList<String>();
            conditionMap.put(key, list);
        }
        for (String value : values.split(SolrConstants.OR)) {
            list.add(value);
        }
    }

    private void putToConditionMap(String key, String value) {
        List<String> list = conditionMap.get(key);
        if (list == null) {
            list = new ArrayList<String>();
            conditionMap.put(key, list);
        }
        list.add(value);
    }

    public void setCondition(String condition) {
        // TODO: create condition map
        // split String into Field:Value tokens

        conditionMap.clear();

        int colonPosition = 0;
        int fieldStart = 0;
        int valueEnd = 0;

        while (fieldStart < condition.length()) {
            // TODO: what if value contains : and AND
            colonPosition = condition.indexOf(":", fieldStart);
            valueEnd = condition.indexOf(SolrConstants.AND, colonPosition + 1);
            if (valueEnd < 0) {
                valueEnd = condition.length();
            }

            String fieldName = condition.substring(fieldStart, colonPosition);
            String fieldValue = condition.substring(colonPosition + 1, valueEnd);

            // special processing for the following:
            // if Refurbished_Flag:1 set Condition to SolrConstants.REFURBISHED 
            //	      OpenBox_Flag:1 set Condition to SolrConstants.OPEN_BOX
            //      Clearance_Flag:1 set Condition to SolrConstants.CLEARANCE
            if (fieldName.equals(SolrConstants.IS_REFURB) && fieldValue.equals(SolrConstants.YES)) {
                putToConditionMap(SolrConstants.AND, SolrConstants.REFURBISHED);
            } else if (fieldName.equals(SolrConstants.IS_OPEN_BOX) && fieldValue.equals(SolrConstants.YES)) {
                putToConditionMap(SolrConstants.AND, SolrConstants.OPEN_BOX);
            } else if (fieldName.equals(SolrConstants.IS_CLEARANCE) && fieldValue.equals(SolrConstants.YES)) {
                putToConditionMap(SolrConstants.AND, SolrConstants.CLEARANCE);
            } // if  Licence_Flag:N set License to SolrConstants.NON_LICENSE
            //                 :Y set License to SolrConstants.LICENSE_ONLY
            else if (fieldName.equals(SolrConstants.IS_LICENSE) && fieldValue.equals(SolrConstants.NO)) {
                putToConditionMap(SolrConstants.LICENSE, SolrConstants.NON_LICENSE_ONLY);
            } else if (fieldName.equals(SolrConstants.IS_LICENSE) && fieldValue.equals(SolrConstants.YES)) {
                putToConditionMap(SolrConstants.LICENSE, SolrConstants.LICENSE_ONLY);
            } // if  ImageExists:0 set ImageExists to SolrConstants.NO_IMAGE_ONLY
            //                 :1 set ImageExists to SolrConstants.IMAGE_ONLY
            else if (fieldName.equals(SolrConstants.IMAGE_EXISTS) && fieldValue.equals("0")) {
                putToConditionMap(SolrConstants.IMAGE_EXISTS, SolrConstants.NO_IMAGE_ONLY);
            } else if (fieldName.equals(SolrConstants.IMAGE_EXISTS) && fieldValue.equals("1")) {
                putToConditionMap(SolrConstants.IMAGE_EXISTS, SolrConstants.IMAGE_ONLY);
            } // CNET
            // TODO: update when MacMall and other stores support CNET Facet Template
            else if (fieldName.endsWith(SolrConstants.FACET_TEMPLATE_SUFFIX) // for legacy values
                    || StringUtils.equals(fieldName, SolrConstants.FACET_TEMPLATE)) {

                if (fieldValue.endsWith("*")) {
                    fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
                }
                String[] facets = fieldValue.split("\\ \\|\\ ");
                if (facets.length > 0) {
                    if (StringUtils.isNotEmpty(facets[0])) {
                        putToConditionMap(SolrConstants.LEVEL_1_CATEGORY, facets[0].replaceAll("\\\\", ""));
                    }
                }
                if (facets.length > 1) {
                    putToConditionMap(SolrConstants.LEVEL_2_CATEGORY, facets[1].replaceAll("\\\\", ""));
                }
                if (facets.length > 2) {
                    putToConditionMap(SolrConstants.LEVEL_3_CATEGORY, facets[2].replaceAll("\\\\", ""));
                }
            } else if (fieldName.endsWith("_FacetTemplateName") // for legacy values
                    || StringUtils.equals(fieldName, SolrConstants.FACET_TEMPLATE_NAME)) {
                putToConditionMap(SolrConstants.FACET_TEMPLATE_NAME, fieldValue);
            } // Dynamic attributes
            else if (fieldName.startsWith("af_")) {
                putListToConditionMap(fieldName, fieldValue);
            } // If InStock:0 set Availability to SolrConstants.IN_STOCK
            //           :1 set Availability to SolrConstants.CALL
            else if (fieldName.equals(SolrConstants.QTY_AVAILABLE) && fieldValue.equals("0")) {
                putToConditionMap(SolrConstants.AVAILABILITY, SolrConstants.CALL);
            } else if (fieldName.equals(SolrConstants.QTY_AVAILABLE) && fieldValue.equals("[1 TO *]")) {
                putToConditionMap(SolrConstants.AVAILABILITY, SolrConstants.IN_STOCK);
            } else {
                putToConditionMap(fieldName, fieldValue);
            }

            fieldStart = valueEnd + 5; // 5 = length of SolrConstants.AND
        }

    }

    public Map<String, List<String>> getCNetFilters() {
        // FacetTemplate
        LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
        if (isCNetFilter()) {
            String[] facetKeys = {SolrConstants.LEVEL_1_CATEGORY, SolrConstants.LEVEL_2_CATEGORY, SolrConstants.LEVEL_3_CATEGORY, SolrConstants.MANUFACTURER};
            for (String key : facetKeys) {
                List<String> value = conditionMap.get(key);
                List<String> newValue = new ArrayList<String>();
                if (CollectionUtils.isNotEmpty(value)) {
                    for (String tmp : value) {
                        newValue.add(tmp.replaceAll("\"", ""));
                    }
                    map.put(key, new ArrayList<String>(newValue));
                }
            }
        }
        return map;
    }

    public Map<String, List<String>> getIMSFilters() {
        // if Category is present return Category, SubCategory, Class, SubClass and Manufacturer fields;
        // else if CatCode is present return CatCode and Manufacturer fields;
        LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();

        if (isIMSFilter()) {
            String[] categoryKeys = {SolrConstants.CATEGORY, SolrConstants.SUB_CATEGORY, SolrConstants.CLASS, SolrConstants.SUB_CLASS, SolrConstants.MANUFACTURER};
            String[] catCodeKeys = {SolrConstants.CAT_CODE, SolrConstants.MANUFACTURER};
            // TODO: update once CNET filters is available
            for (String key : isImsUsingCategory() ? categoryKeys : catCodeKeys) {
                List<String> value = conditionMap.get(key);
                List<String> newValue = new ArrayList<String>();
                if (CollectionUtils.isNotEmpty(value)) {
                    for (String tmp : value) {
                        newValue.add(tmp.replaceAll("\"", ""));
                    }
                    map.put(key, new ArrayList<String>(newValue));
                }
            }
        }
        return map;
    }

    public boolean isImsUsingCatCode() {
        return conditionMap.get(SolrConstants.CAT_CODE) != null && !conditionMap.get(SolrConstants.CAT_CODE).isEmpty() && StringUtils.isNotBlank(conditionMap.get(SolrConstants.CAT_CODE).get(0));
    }

    public boolean isImsUsingCategory() {
        return conditionMap.get(SolrConstants.CATEGORY) != null && !conditionMap.get(SolrConstants.CATEGORY).isEmpty() && StringUtils.isNotBlank(conditionMap.get(SolrConstants.CATEGORY).get(0));
    }

    public boolean isIMSFilter() {
        return (!isCNetFilter()
                && (CollectionUtils.isNotEmpty(conditionMap.get(SolrConstants.MANUFACTURER)) && StringUtils.isNotBlank(conditionMap.get(SolrConstants.MANUFACTURER).get(0))
                || CollectionUtils.isNotEmpty(conditionMap.get(SolrConstants.TEMPLATE_NAME))
                || isImsUsingCatCode()
                || isImsUsingCategory()));
    }

    public boolean isCNetFilter() {
        for (String key : conditionMap.keySet()) {
            if (key.endsWith(SolrConstants.FACET_TEMPLATE_NAME)) {
                return true;
            }
        }
        return (conditionMap.get(SolrConstants.LEVEL_1_CATEGORY) != null && !conditionMap.get(SolrConstants.LEVEL_1_CATEGORY).isEmpty() && StringUtils.isNotBlank(conditionMap.get(SolrConstants.LEVEL_1_CATEGORY).get(0)));
    }

    public Map<String, List<String>> getFacets() {
        // if any of the following fields are present return them;
        // Platform, Condition, Availability, License, ImageExists
        LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
        String[] keys = {SolrConstants.MFR_PN, SolrConstants.PLATFORM, SolrConstants.AND, SolrConstants.AVAILABILITY, SolrConstants.LICENSE, SolrConstants.IMAGE_EXISTS, SolrConstants.NAME, SolrConstants.DESCRIPTION};
        for (String key : keys) {
            List<String> value = conditionMap.get(key);
            if (value != null && !value.isEmpty()) {
                map.put(key, new ArrayList<String>(value));
            }
        }
        return map;
    }

    public Map<String, List<String>> getDynamicAttributes() {
        // if TemplateName or *_FacetTemplateName is present return TemplateName or *_FacetTemplateName and af_* fields and dynamic attributes;
        LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
        // TODO: implement

        // if any of the following fields are present return them;
        // Platform, Condition, Availability, License
        for (String key : conditionMap.keySet()) {
            if (key.contains(SolrConstants.TEMPLATE_NAME) || key.startsWith("af_")) {
                List<String> value = conditionMap.get(key);
                if (value != null && !value.isEmpty()) {
                    map.put(key, new ArrayList<String>(value));
                }
            }
        }
        return map;
    }

//    public static void main(String[] args) {
//        ConfigManager.getInstance("C:\\home\\solr\\conf\\solr.xml");
//
//        // initialize catcodeutil
//        boolean initCatCodeUtil = false;
//
//        if (initCatCodeUtil) {
//            try {
//                CatCodeUtil.init2();
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//
//
//        // if Condition == SolrConstants.REFURBISHED set Refurbished_Flag:1
//        //				== SolrConstants.OPEN_BOX    set OpenBox_Flag:1
//        //              == SolrConstants.CLEARANCE   set Clearance_Flag:1
//        // if License == SolrConstants.LICENSE_ONLY set Licence_Flag:1
//        //		      == SolrConstants.NON_LICENSE, set Licence_Flag:0
//        //            else, set Licence_Flag:0
//        // if Availability == SolrConstants.IN_STOCK set InStock:1
//        //                 == SolrConstants.CALL     set InStock:0
//        // if ImageExists == SolrConstants.NO_IMAGE_ONLY, set ImageExists:0
//        //                == SolrConstants.IMAGE_ONLY, set ImageExists:2
//        String[] conditions = {
//            //				"Category:\"System\" AND SubCategory:\"Notebook Computers\" AND Manufacturer:\"Apple\" AND Refurbished_Flag:1 AND InStock:1",
//            //				"Manufacturer:Microsoft AND PCMall_FacetTemplate:Games | XBOX 360 Games | XBOX 360 Racing Games*",
//            //				"PCMall_FacetTemplate:Electronics | Gaming | PC Games & Accessories",
//            //				"CatCode:31* AND Manufacturer:\"BlackBerry\"",
//            //				"CatCode:3F* AND OpenBox_Flag:1 AND InStock:0 AND Platform:\"Windows\s"",
//            //				"Name:bag ivory AND Description:bag ivory",
//            //				"TemplateName:Notebook Computers AND af_Processor1_Value_Attrib:a2|Core i5 OR a2|Core i7",
//            //				"PCMall_FacetTemplateName:Notebook Computers AND af_Processor1_Value_Attrib:a2|Core i5",
//            //				"Clearance_Flag:1 AND Licence_Flag:0 AND ImageExists:1",
//            "Manufacturer:Apple AND PCMall_FacetTemplate:Data Storage | Network Attached Storage (NAS) AND Description:netbook", //				""
//        };
//
//        for (String condition : conditions) {
//            logger.info("***************");
//            
//            RedirectRuleCondition rr = new RedirectRuleCondition(condition);
//            rr.setStoreId("macmall");
//            
//            logger.info(String.format("text: %s", condition));
//            logger.info(String.format("condition: %s", rr.getCondition()));
//            logger.info(String.format("solr filter: %s", rr.getConditionForSolr()));
//            logger.info(String.format("readable string: %s", rr.getReadableString()));
//            logger.info(String.format("ims filter: %s", rr.getIMSFilters()));
//            logger.info(String.format("cnet filter: %s", rr.getCNetFilters()));
//            logger.info(String.format("facets: %s", rr.getFacets()));
//            logger.info(String.format("dynamic attributes: %s", rr.getDynamicAttributes()));
//            logger.info(String.format("IMS filter: %b", rr.isIMSFilter()));
//            logger.info(String.format("CNET filter: %b", rr.isCNetFilter()));
//        }
//    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getFacetTemplate() {
        return facetTemplate;
    }

    public void setFacetTemplate(String facetTemplate) {
        this.facetTemplate = facetTemplate;
    }

    public String getFacetTemplateName() {
        return facetTemplateName;
    }

    public void setFacetTemplateName(String facetTemplateName) {
        this.facetTemplateName = facetTemplateName;
    }

    public void setFacetPrefix(String facetPrefix) {
        this.facetPrefix = facetPrefix;
    }

    public String getFacetPrefix() {
        return facetPrefix;
    }

    public void setFacetValues(String facetPrefix, String facetTemplate, String facetTemplateName) {
        this.facetPrefix = facetPrefix;
        this.facetTemplate = facetTemplate;
        this.facetTemplateName = facetTemplateName;
    }
}
