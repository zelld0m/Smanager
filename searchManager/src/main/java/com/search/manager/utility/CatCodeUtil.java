package com.search.manager.utility;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.enums.CatCodes;
import com.search.manager.exception.DataException;

public class CatCodeUtil {

    private static final Logger logger =
            LoggerFactory.getLogger(CatCodeUtil.class);
    
    private static ConcurrentHashMap<String, CacheModel<?>> cache = new ConcurrentHashMap<String, CacheModel<?>>();
    private static final String TEMPEST_STORE_MAP = "tempestStoreMapping";
    private static final String SOLR_OBJECTS_DEFINITION_XLSX = "catcodeexcel";
    private static final String ALTERNATIVE_CNET_CATEGORIZATION_XLSX = "catcodeexcel_cnetalternate";
    private static final String SUB_CAT_NAME = "SubCategory Name";
    private static final String TEMPLATE_NAME = "Template Name";
    private static final String CLASS_NAME = "Class Name";
    private static final String STATUS_ACTIVE = "1";
    private static final String ERROR_MSG = "Error while loading ";
//	private final static String VALUE_ATTRIBUTE = "_Value_Attrib";

    @DataTransferObject(converter = BeanConverter.class)
    public static class Attribute {

        public Attribute(String attributeName, String attributeDisplayName) {
            this.attributeName = attributeName;
            this.attributeDisplayName = attributeDisplayName;
        }

        public Attribute(String attributeNumber, String attributeName, String attributeDisplayName) {
            this.attributeName = attributeName;
            this.attributeNumber = attributeNumber;
            this.attributeDisplayName = attributeDisplayName;
        }

        public Attribute(String attributeNumber, String attributeName, String attributeDisplayName, boolean range) {
            this.attributeName = attributeName;
            this.attributeNumber = attributeNumber;
            this.attributeDisplayName = attributeDisplayName;
            this.range = range;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        public String getAttributeDisplayName() {
            return attributeDisplayName;
        }

        public void setAttributeDisplayName(String attributeDisplayName) {
            this.attributeDisplayName = attributeDisplayName;
        }

        public boolean isRange() {
			return range;
		}

		public void setRange(boolean range) {
			this.range = range;
		}

		public void addAttributeValue(String value) {
            attributeValues.add(value);
        }

        public List<String> getAttributeValues() {
            return attributeValues;
        }
        String attributeName;
        String attributeNumber;
        String attributeDisplayName;
        boolean range;
        List<String> attributeValues = new ArrayList<String>();
    }

    @DataTransferObject(converter = BeanConverter.class)
    public static class Template {

        Template(String templateNumber, String templateName, String store) {
            this.templateName = templateName;
            this.templateNumber = templateNumber;
            this.store = store;
        }
        String templateName;
        String templateNumber;
        String store;

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public String getTemplateNumber() {
            return templateNumber;
        }

        public void setTemplateNumber(String templateNumber) {
            this.templateNumber = templateNumber;
        }

        public String getStore() {
			return store;
		}

		public void setStore(String store) {
			this.store = store;
		}

		public List<Attribute> getAttributeList() {
            return attributeList;
        }
        List<Attribute> attributeList = new ArrayList<Attribute>();
    }
    // key = TemplateNumber
    private static Map<String, Template> templateMap = new LinkedHashMap<String, Template>();
    private static Map<String, Attribute> attributeMap = new HashMap<String, Attribute>();

    private static Map<String, Map<String, Template>> storeIMSTemplateMapMap= new LinkedHashMap<String, Map<String, Template>>();
    private static Map<String, Map<String, Template>> storeCNETTemplateMapMap= new LinkedHashMap<String, Map<String, Template>>();

    /**
     * Store workbook to cache
     */
    public static void loadXlsxWorkbook(String location, String workBook) throws IOException, DataException {
        CacheModel<XSSFWorkbook> workbook = new CacheModel<XSSFWorkbook>();
        workbook.setObj(XlsxUtil.getXlsxWorkbook(location));
        putCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, workBook), workbook);
    }

    /**
     * Retrieve workbook from cache
     */
    public static XSSFWorkbook getXlsxWorkbook(String wbObject) throws IOException, DataException {
        CacheModel<XSSFWorkbook> workbook = getCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, wbObject));
        return workbook.getObj();
    }

    /**
     * Retrieve workbook from xlsx file
     */
    public static XSSFSheet getXlsxWorksheet(String wbObject, int sheetNum) throws IOException, DataException {
        return XlsxUtil.getXlsxWorksheet(getXlsxWorkbook(wbObject), sheetNum);
    }

    /**
     * Store worksheet to cache
     */
    public static void loadCatCodesToCache(String wbObject, int sheetNum, String cacheKey) throws DataException, IOException {
        CacheModel<String[]> cat = new CacheModel<String[]>();
        cat.setList(XlsxUtil.getXlsxData(getXlsxWorkbook(wbObject), sheetNum));
        putCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, cacheKey), cat);
    }

    public static void loadCatCodesToCacheCategoryOverride(String wbObject, int sheetNum, String cacheKey) throws DataException, IOException {
        CacheModel<String[]> cat = new CacheModel<String[]>();
        cat.setList(getXlsxDataCategoryOverride(getXlsxWorkbook(wbObject), sheetNum));
        putCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, cacheKey), cat);
    }

    /**
     * Retrieve worksheet from cache
     */
    public static Vector<String[]> getCatCodesFmCache(String cacheKey) throws DataException {
        CacheModel<String[]> cat = getCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, cacheKey));
        return (Vector<String[]>) cat.getList();
    }

    /**
     * Remove object from cache
     */
    public static void removeFmCache(String cacheKey) throws DataException {
        removeCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, cacheKey));
    }

	/** Retrieve attribute template number by template name */
	private static String getAttributeTemplateNo(String name) throws DataException{
		Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_TEMPLATE_MASTER.getCodeStr());
		if(CollectionUtils.isNotEmpty(row) && StringUtils.isNotEmpty(name)){
			for(String[] col : row){
				if(name.equalsIgnoreCase(col[1]) && STATUS_ACTIVE.equals(getWholeNumber(col[2])))
					return getWholeNumber(col[0]);
			}
		}
		return "";
	}
    /**
     * Retrieve attribute template attributes by template name
     */
    public static List<String[]> getAttributeTemplateAttribute(String template) throws DataException {
        Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getCodeStr());
        List<String[]> attr = new ArrayList<String[]>();
        String templateNo = getAttributeTemplateNo(template);
        if (CollectionUtils.isNotEmpty(row) && StringUtils.isNotEmpty(templateNo)) {
            for (String[] col : row) {
                if (templateNo.equalsIgnoreCase(getWholeNumber(col[1])) && STATUS_ACTIVE.equals(getWholeNumber(col[3]))) {
                    attr.add(col);
                }
            }
        }
        return attr;
    }

    private static <E> void putCache(String key, CacheModel<E> v) {
        cache.put(key, v);
    }

    @SuppressWarnings("unchecked")
    private static <E> CacheModel<E> getCache(String key) {
        return (CacheModel<E>) cache.get(key);
    }

    private static void removeCache(String key) {
        cache.remove(key);
    }

    private static String getWholeNumber(String str) {
        if (StringUtils.isNotEmpty(str)) {
            return BigDecimal.valueOf(Double.parseDouble(str)).toPlainString().replace(".0", "");
        }
        return "0";
    }

    private static Vector<String[]> getXlsxDataCategoryOverride(XSSFWorkbook workbook, int sheetNum) throws IOException, DataException {

        Vector<String[]> list = new Vector<String[]>();

        XSSFSheet sheet = XlsxUtil.getXlsxWorksheet(workbook, sheetNum);
        Iterator<Row> rows = sheet.rowIterator();
        int rowcnt = 0;
        int colcnt = 0;
        String[] data = null;


        while (rows.hasNext()) {
            XSSFRow row = ((XSSFRow) rows.next());
            Iterator<Cell> cells = row.cellIterator();
            String category = "";
            String catCode = "";
            String overrideRule = "";
            int cellcnt = 0;
            if (rowcnt == 0) {
                while (cells.hasNext()) {
                    cells.next();
                    colcnt++;
                }
                rowcnt++;
                continue;
            } else {
                data = new String[colcnt];

                while (cells.hasNext()) {
                    XSSFCell cell = (XSSFCell) cells.next();

                    if (colcnt < cell.getColumnIndex() + 1) {
                        break;
                    }
                    if (cellcnt == 5) {
                        category = "";
                        catCode = "";
                        overrideRule = "";
                        catCode = data[0];
                        Vector<String[]> overrideRow = getCatCodesFmCache(CatCodes.CATEGORY_OVERRIDE_RULES.getCodeStr());

                        for (int x = catCode.length(); x != 0; x--) {
                            for (String[] col : overrideRow) {
                                if (catCode.substring(0, x).equalsIgnoreCase(col[0])) {
                                    overrideRule = col[1];
                                    break;
                                }
                            }
                            if (!overrideRule.equals("")) {
                                break;
                            }
                        }

                        if (SUB_CAT_NAME.equalsIgnoreCase(overrideRule)) {
                            category = SUB_CAT_NAME;
                        } else if (TEMPLATE_NAME.equalsIgnoreCase(overrideRule)) {
                            Vector<String[]> templateRow = getCatCodesFmCache(CatCodes.TEMPLATE_USED.getCodeStr());
                            for (int x = catCode.length(); x != 0; x--) {
                                for (String[] col : templateRow) {
                                    if (catCode.substring(0, x).equalsIgnoreCase(col[2])) {
                                        category = col[1];
                                        break;
                                    }
                                }
                                if (!category.equals("")) {
                                    break;
                                }
                            }
                            if (category.equals("")) {
                                category = XlsxUtil.getCellValue(cell);
                            }
                        } else {
                            category = XlsxUtil.getCellValue(cell);
                        }
                        data[cellcnt] = category;
                    } else if (cellcnt == 6 && category.equals(SUB_CAT_NAME)) {
                        data[cellcnt - 1] = XlsxUtil.getCellValue(cell);
                        data[cellcnt] = XlsxUtil.getCellValue(cell);
                    } else {
                        data[cellcnt] = XlsxUtil.getCellValue(cell);
                    }
                    cellcnt++;
                }
            }

            if (data != null) {
                list.add(data);
            }
            rowcnt++;
        }
        return list;
    }

    public static List<String> getCNETNextLevel(String level1, String level2, String store) throws DataException {
        List<String> list = new ArrayList<String>();
        Vector<String[]> categoryRow = getCatCodesFmCache(CatCodes.NAV_LEVELS.getCodeStr());
        String checkedStore = checkStoreMapping(store);

        if (StringUtils.isBlank(level1)) {
            for (String[] col : categoryRow) {
                if (StringUtils.isNotBlank(col[0]) && !list.contains(col[0]) && col[3].equalsIgnoreCase(checkedStore)) {
                    list.add(col[0]);
                }
            }
        } else if (StringUtils.isBlank(level2)) {
            for (String[] col : categoryRow) {
                if (col[0].equalsIgnoreCase(level1) && StringUtils.isNotBlank(col[1]) && !list.contains(col[1])
                		&& col[3].equalsIgnoreCase(checkedStore)) {
                    list.add(col[1]);
                }
            }
        } else {
            for (String[] col : categoryRow) {
            	if (col[0].equalsIgnoreCase(level1) && col[1].equalsIgnoreCase(level2) && StringUtils.isNotBlank(col[2])
            			&& !list.contains(col[2]) && col[3].equalsIgnoreCase(checkedStore)) {
                    list.add(col[2]);
                }
            }
        }
        Collections.sort(list);
        return list;

    }

    public static List<String> getIMSCategoryNextLevel(String strCategory, String strSubcategory, String strClass) throws DataException {
        List<String> list = new ArrayList<String>();
        Vector<String[]> categoryRow = getCatCodesFmCache(CatCodes.CATEGORY_CODES.getCodeStr());

        if (StringUtils.isBlank(strCategory)) {
            for (String[] col : categoryRow) {
                if (!list.contains(col[5])) {
                    list.add(col[5]);
                }
            }
        } else if (StringUtils.isBlank(strSubcategory)) {
            for (String[] col : categoryRow) {
                if (col[5].equalsIgnoreCase(strCategory)) {
                    if (!list.contains(col[6])) {
                        list.add(col[6]);
                    }
                }
            }
        } else if (StringUtils.isBlank(strClass)) {
            for (String[] col : categoryRow) {
                if (col[5].equalsIgnoreCase(strCategory) && col[6].equalsIgnoreCase(strSubcategory)) {
                    if (!list.contains(col[7])) {
                        list.add(col[7]);
                    }
                }
            }
        } else {
            for (String[] col : categoryRow) {
                if (col[5].equalsIgnoreCase(strCategory) && col[6].equalsIgnoreCase(strSubcategory) && col[7].equalsIgnoreCase(strClass)) {
                    if (!list.contains(col[8])) {
                        list.add(col[8]);
                    }
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    // pcmallbd <--> pcmall / macmallbd <--> macmall mapping
    private static final Map<String, String> storesMapping;
    static {
        Map<String, String> _storesMapping = new HashMap<String, String>();
        for (String keyVal : StringUtils.trim(PropertiesUtils.getValue(TEMPEST_STORE_MAP)).split(";")) {
            String[] storeMaps = keyVal.split(":");
            if(storeMaps.length > 1) {
                _storesMapping.put(storeMaps[0].toLowerCase(), storeMaps[1].toLowerCase());
            }
        }
        storesMapping = Collections.unmodifiableMap(_storesMapping);
    }
    private static String checkStoreMapping(String store) {
    	String _store = store.toLowerCase();
    	return storesMapping.get(_store) == null ? _store : storesMapping.get(_store);
    }

    public static List<String> getAllIMSTemplatesByStore(String store) throws DataException {
        return new ArrayList<String>(storeIMSTemplateMapMap.get(checkStoreMapping(store)).keySet()); 
    }

    public static List<String> getAllCNETTemplatesByStore(String store) throws DataException {
        return new ArrayList<String>(storeCNETTemplateMapMap.get(checkStoreMapping(store)).keySet()); 
    }

    private static List<Attribute> getTemplateAttribute(String templateName, Map<String, Template> templateMap) throws DataException {
        List<Attribute> list = new ArrayList<Attribute>();
        Template template = templateMap.get(templateName);
        if (template != null) {
            list.addAll(template.attributeList);
        }
        return list;
    }

    public static List<Attribute> getIMSTemplateAttributeByStore(String store, String templateName) throws DataException {
        return getTemplateAttribute(templateName, storeIMSTemplateMapMap.get(checkStoreMapping(store)));
    }

    public static List<Attribute> getCNETTemplateAttributeByStore(String store, String templateName) throws DataException {
        return getTemplateAttribute(templateName, storeCNETTemplateMapMap.get(checkStoreMapping(store)));
    }

    /**
     * Initialized Category code utility when startup
     */
    public static void init() throws Exception {
        try {
            CatCodeUtil.loadXlsxWorkbook(PropertiesUtils.getValue(SOLR_OBJECTS_DEFINITION_XLSX), CatCodes.WORKBOOK_OBJECTS.getCodeStr());
            CatCodeUtil.loadXlsxWorkbook(PropertiesUtils.getValue(ALTERNATIVE_CNET_CATEGORIZATION_XLSX), CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr());
            loadToCache();
        } catch (DataException e) {
            logger.error(ERROR_MSG + CatCodeUtil.CLASS_NAME + " ERROR - " + e);
        } catch (IOException e) {
            logger.error(ERROR_MSG + CatCodeUtil.CLASS_NAME + " ERROR - " + e);
        }
    }

    public static void init2() throws Exception {
        try {
            CatCodeUtil.loadXlsxWorkbook("/home/solr/utilities/catcodes/Solr SQL Objects and Definitions.xlsx", CatCodes.WORKBOOK_OBJECTS.getCodeStr());
            CatCodeUtil.loadXlsxWorkbook("/home/solr/utilities/catcodes/AlternativeCNETCategorization_Structure.xlsx", CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr());
            loadToCache();
        } catch (DataException e) {
            logger.error(ERROR_MSG + CatCodeUtil.CLASS_NAME + " ERROR - " + e);
        } catch (IOException e) {
            logger.error(ERROR_MSG + CatCodeUtil.CLASS_NAME + " ERROR - " + e);
        }
    }

    /**
     * Initialized Category code utility when startup
     */
    private static void loadToCache() throws Exception {
        try {
            Thread td1 = new Thread() {
                @Override
                public void run() {
                    try {
                        CatCodeUtil.loadCatCodesToCacheCategoryOverride(CatCodes.WORKBOOK_OBJECTS.getCodeStr(), CatCodes.CATEGORY_CODES.getCode(), CatCodes.CATEGORY_CODES.getCodeStr());
                    } catch (DataException e) {
                        logger.error(ERROR_MSG + CatCodes.CATEGORY_CODES.getValue(), e);
                    } catch (IOException e) {
                        logger.error(ERROR_MSG + CatCodes.CATEGORY_CODES.getValue(), e);
                    }
                }
            };

            Thread td2 = new Thread() {
                @Override
                public void run() {
                    try {
                        CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(), CatCodes.CATEGORY_OVERRIDE_RULES.getCode(), CatCodes.CATEGORY_OVERRIDE_RULES.getCodeStr());
                    } catch (DataException e) {
                        logger.error(ERROR_MSG + CatCodes.CATEGORY_OVERRIDE_RULES.getValue(), e);
                    } catch (IOException e) {
                        logger.error(ERROR_MSG + CatCodes.CATEGORY_OVERRIDE_RULES.getValue(), e);
                    }
                }
            };

            Thread td3 = new Thread() {
                @Override
                public void run() {
                    try {
                        CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(), CatCodes.TEMPLATE_USED.getCode(), CatCodes.TEMPLATE_USED.getCodeStr());
                    } catch (DataException e) {
                        logger.error(ERROR_MSG + CatCodes.TEMPLATE_USED.getValue(), e);
                    } catch (IOException e) {
                        logger.error(ERROR_MSG + CatCodes.TEMPLATE_USED.getValue(), e);
                    }
                }
            };

            Thread td4 = new Thread() {
                @Override
                public void run() {
                    try {
                        CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(), CatCodes.NAV_LEVELS.getCode(), CatCodes.NAV_LEVELS.getCodeStr());
                    } catch (DataException e) {
                        logger.error(ERROR_MSG + CatCodes.NAV_LEVELS.getValue(), e);
                    } catch (IOException e) {
                        logger.error(ERROR_MSG + CatCodes.NAV_LEVELS.getValue(), e);
                    }
                }
            };

            Thread td5 = new Thread() {
                @Override
                public void run() {
                    try {
                        CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr(), CatCodes.ALTERNATE_CNET.getCode(), CatCodes.ALTERNATE_CNET.getCodeStr());
                    } catch (DataException e) {
                        logger.error(ERROR_MSG + CatCodes.ALTERNATE_CNET.getValue(), e);
                    } catch (IOException e) {
                        logger.error(ERROR_MSG + CatCodes.ALTERNATE_CNET.getValue(), e);
                    }
                }
            };

            Thread td6 = new Thread() {
                @Override
                public void run() {
                    try {
                        CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(), CatCodes.SOLR_TEMPLATE_MASTER.getCode(), CatCodes.SOLR_TEMPLATE_MASTER.getCodeStr());
                    } catch (DataException e) {
                        logger.error(ERROR_MSG + CatCodes.SOLR_TEMPLATE_MASTER.getValue(), e);
                    } catch (IOException e) {
                        logger.error(ERROR_MSG + CatCodes.SOLR_TEMPLATE_MASTER.getValue(), e);
                    }
                }
            };

            Thread td7 = new Thread() {
                @Override
                public void run() {
                    try {
                        CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(), CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getCode(), CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getCodeStr());
                    } catch (DataException e) {
                        logger.error(ERROR_MSG + CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getValue(), e);
                    } catch (IOException e) {
                        logger.error(ERROR_MSG + CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getValue(), e);
                    }
                }
            };

            Thread td8 = new Thread() {
                @Override
                public void run() {
                    try {
                        CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(), CatCodes.SOLR_ATTRIBUTE_MASTER.getCode(), CatCodes.SOLR_ATTRIBUTE_MASTER.getCodeStr());
                    } catch (DataException e) {
                        logger.error(ERROR_MSG + CatCodes.SOLR_ATTRIBUTE_MASTER.getValue(), e);
                    } catch (IOException e) {
                        logger.error(ERROR_MSG + CatCodes.SOLR_ATTRIBUTE_MASTER.getValue(), e);
                    }
                }
            };

            Thread td9 = new Thread() {
                @Override
                public void run() {
                    try {
                        CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(), CatCodes.SOLR_ATTRIBUTE_RANGE.getCode(), CatCodes.SOLR_ATTRIBUTE_RANGE.getCodeStr());
                    } catch (DataException e) {
                        logger.error(ERROR_MSG + CatCodes.SOLR_ATTRIBUTE_RANGE.getValue(), e);
                    } catch (IOException e) {
                        logger.error(ERROR_MSG + CatCodes.SOLR_ATTRIBUTE_RANGE.getValue(), e);
                    }
                }
            };

            td2.start();
            td3.start();
            td4.start();
            td5.start();
            td6.start();
            td7.start();
            td8.start();
            td9.start();

            while (true) {
                if (!td2.isAlive() && !td3.isAlive()) {
                    td1.start();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // discard
                }
            }

            while (true) {
                if (!td1.isAlive() && !td2.isAlive() && !td3.isAlive() && !td4.isAlive() && !td5.isAlive() && !td6.isAlive() && !td7.isAlive() && !td8.isAlive() && !td9.isAlive()) {
                    CatCodeUtil.removeFmCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr());
                    CatCodeUtil.removeFmCache(CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr());

                    /* generate Template and Attributes data */

                    // load template data
//System.out.println(new Date() + "***TEMPLATE***");
                    Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_TEMPLATE_MASTER.getCodeStr());
                    for (String[] values : row) {
                        boolean isAdd = BooleanUtils.toBoolean(values[2] /* Active */, "1", "0");
                        if (isAdd) {
                            String templateNo = values[0];
                            Template template = new Template(templateNo, values[1], values[3]);
                            try {
                                templateMap.put(templateNo, template);
                            } catch (Exception e) {
                                logger.error("Failed to load template " + values[0], e);
                            }
                        }
                    }

                    // load attribute data
//System.out.println(new Date() + "***ATRIBUTE***");			
                    row = getCatCodesFmCache(CatCodes.SOLR_ATTRIBUTE_MASTER.getCodeStr());
                    for (String[] values : row) {
                        boolean isAdd = BooleanUtils.toBoolean(values[7] /* Status */, "1", "0");
                        if (isAdd) {
                        	boolean isRange = BooleanUtils.toBoolean(values[5] /* Status */, "1", "0");
                            attributeMap.put(values[0], new Attribute(values[0], String.format("%1$s_Value_Attrib", values[1]), values[2], isRange));
                        }
                    }

//System.out.println(new Date() + "***ATRIBUTE_VALUES***");				
                    // load attribute values
                    row = getCatCodesFmCache(CatCodes.SOLR_ATTRIBUTE_RANGE.getCodeStr());
                    for (String[] values : row) {
                        Attribute attribute = attributeMap.get(values[0]);
                        if (attribute != null && attribute.isRange()) {
                            attribute.attributeValues.add(String.format("%1$s|%2$s", values[3], values[2]));
                        }
                    }

                    // TODO: check if need to sort attribute values

                    // map attributes to templates
//System.out.println(new Date() + "***ATRIBUTE_TEMPLATE_MAP***");					
                    row = getCatCodesFmCache(CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getCodeStr());
                    for (String[] values : row) {
                        boolean isAdd = BooleanUtils.toBoolean(values[3] /* Active */, "1", "0");
                        if (isAdd) {
                            Template template = templateMap.get(values[1]);
                            Attribute attribute = attributeMap.get(values[2]);
                            if (template != null && attribute != null) {
                                template.attributeList.add(attribute);
                            }
                        }
                    }

                    for (Template template : templateMap.values()) {
                    	if (Integer.valueOf(template.templateNumber) < 1000) { // IMS
                    		if (!storeIMSTemplateMapMap.containsKey(template.getStore())) {
                    			storeIMSTemplateMapMap.put(template.getStore(), new LinkedHashMap<String, Template>());
                    		}
                    		storeIMSTemplateMapMap.get(template.getStore()).put(template.templateName, template);
                        } else { // CNET
                        	if (!storeCNETTemplateMapMap.containsKey(template.getStore())) {
                        		storeCNETTemplateMapMap.put(template.getStore(), new LinkedHashMap<String, Template>());
                    		}
                        	storeCNETTemplateMapMap.get(template.getStore()).put(template.templateName, template);
                        }
                    }

                    Iterator<Entry<String, Map<String, Template>>> it = storeIMSTemplateMapMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Map<String, Template>> pair = (Map.Entry<String, Map<String, Template>>) it.next();

                        // sort template alphabetically
						List<String> templateKeys = new ArrayList<String>(pair.getValue().keySet());
                        Collections.sort(templateKeys, new Comparator<String>() {
                            @Override
                            public int compare(String arg0, String arg1) {
                                int val = arg0.compareTo(arg1);
                                if (val == 0) {
                                    val = arg0.compareTo(arg1);
                                }
                                return val;
                            }
                        });
                        for (String key : templateKeys) {
                        	pair.getValue().put(key, ((LinkedHashMap<String, Template>) pair.getValue()).remove(key));
                        }
                    }

                    it = storeCNETTemplateMapMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Map<String, Template>> pair = (Map.Entry<String, Map<String, Template>>) it.next();

                        // sort template alphabetically
						List<String> templateKeys = new ArrayList<String>(pair.getValue().keySet());
                        Collections.sort(templateKeys, new Comparator<String>() {
                            @Override
                            public int compare(String arg0, String arg1) {
                                int val = arg0.compareTo(arg1);
                                if (val == 0) {
                                    val = arg0.compareTo(arg1);
                                }
                                return val;
                            }
                        });
                        for (String key : templateKeys) {
                        	pair.getValue().put(key, ((LinkedHashMap<String, Template>) pair.getValue()).remove(key));
                        }
                    }

                    break;
                }
                logger.info("*");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // discard
                }
            }

            CatCodeUtil.removeFmCache(CatCodes.SOLR_TEMPLATE_MASTER.getCodeStr());
            CatCodeUtil.removeFmCache(CatCodes.SOLR_ATTRIBUTE_MASTER.getCodeStr());
            CatCodeUtil.removeFmCache(CatCodes.SOLR_ATTRIBUTE_RANGE.getCodeStr());
            CatCodeUtil.removeFmCache(CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getCodeStr());

        } catch (DataException e) {
            logger.error(String.format("%s%s ERROR - ", ERROR_MSG, CatCodeUtil.CLASS_NAME), e);
        }
    }

    public static void main(String args[]) throws Exception {
        init2();
    }
}
