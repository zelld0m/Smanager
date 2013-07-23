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
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.enums.CatCodes;
import com.search.manager.exception.DataException;

public class CatCodeUtil {
	
	private static Logger logger = Logger.getLogger(CatCodeUtil.class);
	private static ConcurrentHashMap<String,CacheModel<?>> cache = new ConcurrentHashMap<String,CacheModel<?>>();
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
		
		public void addAttributeValue(String value) {
			attributeValues.add(value);
		}
		
		public List<String> getAttributeValues() {
			return attributeValues;
		}	

		String attributeName;
		String attributeNumber;
		String attributeDisplayName;
		List<String> attributeValues = new ArrayList<String>();
		
	}
	
	@DataTransferObject(converter = BeanConverter.class)
	public static class Template {
		
		Template (String templateNumber, String templateName) {
			this.templateName = templateName;
			this.templateNumber = templateNumber;
		}
		
		String templateName;
		String templateNumber;
		
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
		
		public List<Attribute> getAttributeList() {
			return attributeList;
		}

		List<Attribute> attributeList = new ArrayList<Attribute>();
		
	}
	
	// key = TemplateNumber
	private static Map<String, Template> templateMap 		= new LinkedHashMap<String, Template>();
	private static Map<String, Template> imsTemplateMap 	= new LinkedHashMap<String, Template>();
	private static Map<String, Template> cnetTemplateMap 	= new LinkedHashMap<String, Template>();
	private static Map<String, Attribute> attributeMap 		= new HashMap<String, Attribute>();
	
	
	/** Store workbook to cache */
	public static void loadXlsxWorkbook(String location, String workBook) throws IOException, DataException{
		CacheModel<XSSFWorkbook> workbook = new CacheModel<XSSFWorkbook>();
		workbook.setObj(XlsxUtil.getXlsxWorkbook(location));
		putCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, workBook), workbook);
	}

	/** Retrieve workbook from cache */
	public static XSSFWorkbook getXlsxWorkbook(String wbObject) throws IOException, DataException{
		CacheModel<XSSFWorkbook> workbook = getCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, wbObject));
		return workbook.getObj();
	}
	
	/** Retrieve workbook from xlsx file */
	public static XSSFSheet getXlsxWorksheet(String wbObject, int sheetNum) throws IOException, DataException{
		return XlsxUtil.getXlsxWorksheet(getXlsxWorkbook(wbObject), sheetNum);
	}
	
	/** Store worksheet to cache */
	public static void loadCatCodesToCache(String wbObject, int sheetNum, String cacheKey) throws DataException, IOException{
		CacheModel<String[]> cat = new CacheModel<String[]>();
		cat.setList(XlsxUtil.getXlsxData(getXlsxWorkbook(wbObject),sheetNum));
		putCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, cacheKey), cat);
	}
	
	public static void loadCatCodesToCacheCategoryOverride(String wbObject, int sheetNum, String cacheKey) throws DataException, IOException{
		CacheModel<String[]> cat = new CacheModel<String[]>();
		cat.setList(getXlsxDataCategoryOverride(getXlsxWorkbook(wbObject),sheetNum));
		putCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, cacheKey), cat);
	}
	
	/** Retrieve worksheet from cache */
	public static Vector<String[]> getCatCodesFmCache(String cacheKey) throws DataException{
		CacheModel<String[]> cat = getCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, cacheKey));
		return (Vector<String[]>) cat.getList();
	}
	
	/** Remove object from cache */
	public static void removeFmCache(String cacheKey) throws DataException{
		removeCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, cacheKey));
	}
	
//	/** Retrieve attribute template number by template name */
//	private static String getAttributeTemplateNo(String name) throws DataException{
//		Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_TEMPLATE_MASTER.getCodeStr());
//		if(CollectionUtils.isNotEmpty(row) && StringUtils.isNotEmpty(name)){
//			for(String[] col : row){
//				if(name.equalsIgnoreCase(col[1]) && STATUS_ACTIVE.equals(getWholeNumber(col[2])))
//					return getWholeNumber(col[0]);
//			}
//		}
//		return "";
//	}
	
	/** Retrieve attribute template attributes by template name */
	public static List<String[]> getAttributeTemplateAttribute(String template) throws DataException{
		Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getCodeStr());
		List<String[]> attr = new ArrayList<String[]>();
		
		if(CollectionUtils.isNotEmpty(row) && StringUtils.isNotEmpty(template)){
			for(String[] col : row){
				if(template.equalsIgnoreCase(getWholeNumber(col[1])) && STATUS_ACTIVE.equals(getWholeNumber(col[4])))
					attr.add(col);
			}
		}
		return attr;
	}
	
	private static <E> void putCache(String key, CacheModel<E> v){
		cache.put(key, v);
	}
	
	@SuppressWarnings("unchecked")
	private static <E> CacheModel<E> getCache(String key){
		return (CacheModel<E>) cache.get(key);
	}
	
	private static void removeCache(String key){
		cache.remove(key);
	}
	
	private static String getWholeNumber(String str){
		if(StringUtils.isNotEmpty(str))
			return BigDecimal.valueOf(Double.parseDouble(str)).toPlainString().replace(".0", "");
		return "0";
	}
	
	private static Vector<String[]> getXlsxDataCategoryOverride(XSSFWorkbook workbook, int sheetNum) throws IOException, DataException{
		
		Vector<String[]> list = new Vector<String[]>();
	    
	    XSSFSheet sheet = XlsxUtil.getXlsxWorksheet(workbook, sheetNum);
	    Iterator<Row> rows = sheet.rowIterator();
	    int rowcnt = 0;
	    int colcnt = 0;
	    String[] data = null;
	    
	        
        while (rows.hasNext()){
            XSSFRow row = ((XSSFRow) rows.next());
            Iterator<Cell> cells = row.cellIterator();
        	String category = "";
    	    String catCode = "";
    	    String overrideRule="";
            int cellcnt = 0;      
            if(rowcnt == 0){
            	while(cells.hasNext()){
            		cells.next();
            		colcnt++;
                }
             	rowcnt++;
             	continue;
            }else{
            	data = new String[colcnt];
            	
            	while(cells.hasNext()){
	            	XSSFCell cell = (XSSFCell) cells.next();
	            	
	            	if(colcnt < cell.getColumnIndex() + 1)
	            			 break;
	            	if(cellcnt==5){
	            		category = "";
	            	    catCode = "";
	            	    overrideRule="";
		            	catCode = data[0];
		            	Vector<String[]> overrideRow = getCatCodesFmCache(CatCodes.CATEGORY_OVERRIDE_RULES.getCodeStr());
		            	
		            	for(int x=catCode.length();x!=0;x--){
			            	for(String[] col:overrideRow){
				            	if(catCode.substring(0,x).equalsIgnoreCase(col[0])){
				            		overrideRule=col[1];
				            		break;
				            	}
			            	}
			            	if(!overrideRule.equals(""))
			            		break;
		            	}
		            	
		            	if(SUB_CAT_NAME.equalsIgnoreCase(overrideRule)){
		            		category = SUB_CAT_NAME;
		     			}else if(TEMPLATE_NAME.equalsIgnoreCase(overrideRule)){
		     				Vector<String[]> templateRow = getCatCodesFmCache(CatCodes.TEMPLATE_USED.getCodeStr());
		     				for(int x=catCode.length();x!=0;x--){
				            	for(String[] col:templateRow){
					            	if(catCode.substring(0,x).equalsIgnoreCase(col[2])){
					            		category=col[1];
					            		break;
					            	}					            	
				            	}
				            	if(!category.equals(""))
				            		break;
			            	}
		     				if(category.equals(""))
		     					category = XlsxUtil.getCellValue(cell);		     					
		     			}else
		     				category = XlsxUtil.getCellValue(cell);
		            	data[cellcnt] =	category;
	            	}else if(cellcnt==6 && category.equals(SUB_CAT_NAME)){
	            		data[cellcnt-1] = XlsxUtil.getCellValue(cell);
	            		data[cellcnt] = XlsxUtil.getCellValue(cell);
	            	}else{	 
	            		data[cellcnt] = XlsxUtil.getCellValue(cell);
	            	}
	            	cellcnt++;	 
                }
            }
            
            if(data != null)
            	list.add(data);
            rowcnt++; 
        }
		return list;   
	}
	public static List<String> getCNETNextLevel(String level1,String level2) throws DataException{
		List<String> list = new ArrayList<String>();
		Vector<String[]> categoryRow = getCatCodesFmCache(CatCodes.SOLR_SEARCH_NAV.getCodeStr());
		
		String navID = ""; 
		Pattern pat =  Pattern.compile("[1-9]");
		if(StringUtils.isBlank(level1)){
			for(String[] col:categoryRow){
				if(col[3].equalsIgnoreCase("0"))
					if(!list.contains(col[2]))
						list.add(col[2]);
			}
		}else if(StringUtils.isBlank(level2)){
			for(String[] col:categoryRow){
				navID = col[0].substring(col[0].length()-9 > 0 ? col[0].length()-9 : 0,col[0].length()-6);
				Matcher m = pat.matcher(navID);
				if(col[1].equalsIgnoreCase(level1) && m.find() ){
					for(String[] coll:categoryRow){
						navID = coll[0].substring(coll[0].length()-6,coll[0].length()-3);
						Matcher m2 = pat.matcher(navID);
						if(col[0].equalsIgnoreCase(coll[3]) && m2.find()){
							if(!list.contains(coll[2]))
								list.add(coll[2]);
						}
					}
				}
			}
		}else{
			for(String[] col:categoryRow){
				navID = col[0].substring(col[0].length()-9 > 0 ? col[0].length()-9 : 0,col[0].length()-6);
				Matcher m = pat.matcher(navID);
				if(col[1].equalsIgnoreCase(level1) && m.find()){
					for(String[] coll:categoryRow){
						navID = coll[0].substring(coll[0].length()-6,coll[0].length()-3);
						Matcher m2 = pat.matcher(navID);
						if(coll[1].equalsIgnoreCase(level2) && m2.find()){
								for(String[] colll:categoryRow){
									navID = colll[0].substring(colll[0].length()-3,colll[0].length());
									Matcher m3 = pat.matcher(navID);
									if(coll[0].equalsIgnoreCase(colll[3]) && m3.find()){
										if(!list.contains(colll[2]))
											list.add(colll[2]);
									}
								}
						}
					}
				}
			}
		}
		Collections.sort(list);
		return list;
		
	}
	public static List<String> getIMSCategoryNextLevel(String strCategory, String strSubcategory, String strClass) throws DataException {
		List<String> list = new ArrayList<String>();
		Vector<String[]> categoryRow = getCatCodesFmCache(CatCodes.CATEGORY_CODES.getCodeStr());
		
		if(StringUtils.isBlank(strCategory)){
			for(String[] col:categoryRow){
				if(!list.contains(col[5]))
					list.add(col[5]);
			}
		}else if(StringUtils.isBlank(strSubcategory)){
			for(String[] col:categoryRow){
				if(col[5].equalsIgnoreCase(strCategory))
					if(!list.contains(col[6]))
						list.add(col[6]);
			}
		}else if(StringUtils.isBlank(strClass)){
			for(String[] col:categoryRow){
				if(col[5].equalsIgnoreCase(strCategory) && col[6].equalsIgnoreCase(strSubcategory))
					if(!list.contains(col[7]))
						list.add(col[7]);
			}
		}else{
			for(String[] col:categoryRow){
				if(col[5].equalsIgnoreCase(strCategory) && col[6].equalsIgnoreCase(strSubcategory) && col[7].equalsIgnoreCase(strClass))
					if(!list.contains(col[8]))
						list.add(col[8]);
			}
		}
		Collections.sort(list);
		return list;
	}
	
	public static List<String> getAllIMSTemplates() throws DataException {
		return new ArrayList<String>(imsTemplateMap.keySet());
	}
	
	public static List<String> getAllCNETTemplates() throws DataException {
		return new ArrayList<String>(cnetTemplateMap.keySet());
	}
	
	private static List<Attribute> getTemplateAttribute(String templateName, Map<String, Template> templateMap) throws DataException {
		List<Attribute> list = new ArrayList<Attribute>();
		Template template = templateMap.get(templateName);
		if (template != null) {
			list.addAll(template.attributeList);
		}
		return list;
	}
	
	public static List<Attribute> getIMSTemplateAttribute(String templateName) throws DataException {
		return getTemplateAttribute(templateName, imsTemplateMap);
	}

	public static List<Attribute> getCNETTemplateAttribute(String templateName) throws DataException {
		return getTemplateAttribute(templateName, cnetTemplateMap);		
	}
	
	/** Initialized Category code utility when startup */
	public static void init() throws Exception {
		try {
			CatCodeUtil.loadXlsxWorkbook(PropertiesUtils.getValue(SOLR_OBJECTS_DEFINITION_XLSX),CatCodes.WORKBOOK_OBJECTS.getCodeStr());
			CatCodeUtil.loadXlsxWorkbook(PropertiesUtils.getValue(ALTERNATIVE_CNET_CATEGORIZATION_XLSX),CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr());		
			loadToCache();
		} catch (DataException e) {
			logger.error(ERROR_MSG+CatCodeUtil.CLASS_NAME+" ERROR - " +e);
		} catch (IOException e) {
			logger.error(ERROR_MSG+CatCodeUtil.CLASS_NAME+" ERROR - " +e);
		}
	}
	
	public static void init2() throws Exception {
		try {
			CatCodeUtil.loadXlsxWorkbook("/home/solr/utilities/catcodes/Solr SQL Objects and Definitions.xlsx",CatCodes.WORKBOOK_OBJECTS.getCodeStr());
			CatCodeUtil.loadXlsxWorkbook("/home/solr/utilities/catcodes/AlternativeCNETCategorization_Structure.xlsx",CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr());
			loadToCache();
		} catch (DataException e) {
			logger.error(ERROR_MSG+CatCodeUtil.CLASS_NAME+" ERROR - " +e);
		} catch (IOException e) {
			logger.error(ERROR_MSG+CatCodeUtil.CLASS_NAME+" ERROR - " +e);
		}
	}
	
	/** Initialized Category code utility when startup */
	private static void loadToCache() throws Exception {
		try {
			Thread td1 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCacheCategoryOverride(CatCodes.WORKBOOK_OBJECTS.getCodeStr(),CatCodes.CATEGORY_CODES.getCode(),CatCodes.CATEGORY_CODES.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.CATEGORY_CODES.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.CATEGORY_CODES.getValue(),e);
					}
				}
			};
			
			Thread td2 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(),CatCodes.CATEGORY_OVERRIDE_RULES.getCode(),CatCodes.CATEGORY_OVERRIDE_RULES.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.CATEGORY_OVERRIDE_RULES.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.CATEGORY_OVERRIDE_RULES.getValue(),e);
					}
				}
			};
			
			Thread td3 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(),CatCodes.TEMPLATE_USED.getCode(),CatCodes.TEMPLATE_USED.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.TEMPLATE_USED.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.TEMPLATE_USED.getValue(),e);
					}
				}
			};
			
			Thread td4 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(),CatCodes.SOLR_SEARCH_NAV.getCode(),CatCodes.SOLR_SEARCH_NAV.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_SEARCH_NAV.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_SEARCH_NAV.getValue(),e);
					}
				}
			};
			
			Thread td5 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr(),CatCodes.ALTERNATE_CNET.getCode(),CatCodes.ALTERNATE_CNET.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.ALTERNATE_CNET.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.ALTERNATE_CNET.getValue(),e);
					}
				}
			};
			
			Thread td6 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(),CatCodes.SOLR_TEMPLATE_MASTER.getCode(),CatCodes.SOLR_TEMPLATE_MASTER.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_TEMPLATE_MASTER.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_TEMPLATE_MASTER.getValue(),e);
					}
				}
			};
			
			Thread td7 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(),CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getCode(),CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getValue(),e);
					}
				}
			};
			
			Thread td8 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(),CatCodes.SOLR_ATTRIBUTE_MASTER.getCode(),CatCodes.SOLR_ATTRIBUTE_MASTER.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_ATTRIBUTE_MASTER.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_ATTRIBUTE_MASTER.getValue(),e);
					}
				}
			};
			
			Thread td9 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(),CatCodes.SOLR_ATTRIBUTE_RANGE.getCode(),CatCodes.SOLR_ATTRIBUTE_RANGE.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_ATTRIBUTE_RANGE.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_ATTRIBUTE_RANGE.getValue(),e);
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
			
			while(true){
				if(!td2.isAlive() && !td3.isAlive()){
					td1.start();
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) { 
					// discard
				}
			}
			
			while(true){
				if(!td1.isAlive() && !td2.isAlive() && !td3.isAlive() && !td4.isAlive() && !td5.isAlive() && !td6.isAlive() && !td7.isAlive() && !td8.isAlive() && !td9.isAlive()){
					CatCodeUtil.removeFmCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr());
					CatCodeUtil.removeFmCache(CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr());
					
					/* generate Template and Attributes data */
					
					// load template data
//System.out.println(new Date() + "***TEMPLATE***");
					Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_TEMPLATE_MASTER.getCodeStr());
					for (String[] values: row) {
						boolean isAdd = BooleanUtils.toBoolean(values[2] /* Active */, "1", "0");
						if (isAdd) {
							String templateNo = values[0];
							Template template = new Template(templateNo, values[1]);
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
					for (String[] values: row) {
						boolean isAdd = BooleanUtils.toBoolean(values[6] /* Status */, "1", "0");
						if (isAdd) {
							attributeMap.put(values[0], new Attribute(values[0], String.format("%1$s_Value_Attrib", values[1]), values[2]));
						}
					}
					
//System.out.println(new Date() + "***ATRIBUTE_VALUES***");				
					// load attribute values
					row = getCatCodesFmCache(CatCodes.SOLR_ATTRIBUTE_RANGE.getCodeStr());
					for (String[] values: row) {
						Attribute attribute = attributeMap.get(values[0]);
						if (attribute != null) {
							attribute.attributeValues.add(String.format("%1$s|%2$s", values[4], values[3]));
						}
					}
					
					// TODO: check if need to sort attribute values
					
					// map attributes to templates
//System.out.println(new Date() + "***ATRIBUTE_TEMPLATE_MAP***");					
					row = getCatCodesFmCache(CatCodes.SOLR_TEMPLATE_ATTRIBUTE.getCodeStr());
					for (String[] values: row) {
						boolean isAdd = BooleanUtils.toBoolean(values[4] /* Active */, "1", "0");
						if (isAdd) {
							Template template = templateMap.get(values[1]);
							Attribute attribute = attributeMap.get(values[2]);
							if (template != null && attribute != null) {
								template.attributeList.add(attribute);
							}
						}
					}

					// sort template alphabetically
					for (Template template: templateMap.values()) {
						if (Integer.valueOf(template.templateNumber) < 1000) {
							imsTemplateMap.put(template.templateName, template);
						}
						else {
							cnetTemplateMap.put(template.templateName, template);
						}						
					}
					

//System.out.println(new Date() + "***SORT***");					
					// TODO: put into function
					List<String> templateKeys = new ArrayList<String>(imsTemplateMap.keySet());
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
					for (String key: templateKeys) {
						imsTemplateMap.put(key, imsTemplateMap.remove(key));
					}
					
					templateKeys = new ArrayList<String>(cnetTemplateMap.keySet());
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
					for (String key: templateKeys) {
						cnetTemplateMap.put(key, cnetTemplateMap.remove(key));
					}
					
					break;
				}
				System.out.println("*");
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
			logger.error(ERROR_MSG+CatCodeUtil.CLASS_NAME+" ERROR - " +e);
		}
	}
	
	public static void main(String args[])	throws Exception
    {
		init2();
//		String strCategory = "";
//		String strSubCategory = "";
//		String strClass = "";
//		String template = "";
//		String attribute = "";
//		boolean repeat = true;
//		List<String> list = new ArrayList<String>();
//		List<String> listCNET = new ArrayList<String>();
		
		for (Template t: imsTemplateMap.values()) {
			System.out.println(t.templateNumber + ":" + t.templateName);
			for (Attribute a: t.attributeList) {
				System.out.println("\t" + a.attributeDisplayName + " -> " + a.attributeName);
				for (String values: a.attributeValues) {
					System.out.println("\t\t" + values);
				}
			}
		}

		System.out.println("****************CNET");
		for (Template t: cnetTemplateMap.values()) {
			System.out.println(t.templateNumber + ":" + t.templateName);
			for (Attribute a: t.attributeList) {
				System.out.println("\t" + a.attributeDisplayName + " -> " + a.attributeName);
				for (String values: a.attributeValues) {
					System.out.println("\t\t" + values);
				}
			}
		}

//		while(repeat){
//			list = new ArrayList<String>();
//			Scanner in = new Scanner(System.in);
////			System.out.println("Please enter category : ");
////			strCategory = in.nextLine();  
////			System.out.println("Please enter sub category : ");
////			strSubCategory = in.nextLine(); 
////			System.out.println("Please enter class : ");
////			strClass = in.nextLine();			
//			
////			list = getIMSCategoryNextLevel(strCategory,strSubCategory,strClass);
////			listCNET = getCNETNextLevel(strCategory,strSubCategory);
//			
//			System.out.println("Please enter template : ");
//			template = in.nextLine();  
//			System.out.println("Please enter attribute : ");
//			attribute = in.nextLine(); 
//			list = getTemplateAttribute(template);
//			
//			for(String field : list){
//				System.out.println(field);
//			}
//			for(String field : listCNET){
//				System.out.println(field);
//			}
//			
//			System.out.println("Again?(y/n) : ");
//			if(in.nextLine().equalsIgnoreCase("y")){
//				repeat = true;
//			}else{
//				repeat = false;
//			}
//		}
		
//		Vector<String[]> categoryRow = getCatCodesFmCache(CatCodes.CATEGORY_CODES.getCodeStr());
//		
//		for(String[] col : categoryRow){
//			System.out.println(col[0]+" : "+col[5]+" : "+col[6]+" : "+col[7]+" : "+col[8]);
//		}
    }
	
}
