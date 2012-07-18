package com.search.manager.utility;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.enums.CatCodes;
import com.search.manager.exception.DataException;
import com.search.manager.model.Category;
import com.search.manager.model.CategoryCNET;
import com.search.manager.model.SolrAttribute;
import com.search.manager.model.SolrAttributeRange;

public class CatCodeUtil {
	
	private static Logger logger = Logger.getLogger(CatCodeUtil.class);
	private static ConcurrentHashMap<String,CacheModel<?>> cache = new ConcurrentHashMap<String,CacheModel<?>>();
	private static final String SOLR_OBJECTS_DEFINITION_XLSX = "catcodeexcel";
	private static final String ALTERNATIVE_CNET_CATEGORIZATION_XLSX = "catcodeexcel_cnetalternate";
	private static final String SUB_CAT_NAME = "SubCategory Name";
	private static final String TEMPLATE_NAME = "Template Name";
	private static final String CLASS_NAME = "Class Name";
	private static final String CATEGORY_NAME = "Category Name";
	private static final String STATUS_ACTIVE = "1";
	private static final String ERROR_MSG = "Error while loading ";
	private static final String DYNAMIC_FACET_PREFIX = "af_";
	
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

	/** Retrieve category listing by IMS */
	public static List<Category> getCategoriesByIMSCatCode(String catCode) throws DataException{
		
		List<Category> catList = new ArrayList<Category>();
		Set<String> filterCat = new TreeSet<String>();
		Set<String> filterSubCat = new TreeSet<String>();
		Set<String> filterClass = new TreeSet<String>();
		String code = "";
		String subCode = "";
		String subCol = "";
		
		Vector<String[]> row = getCatCodesFmCache(CatCodes.CATEGORY_CODES.getCodeStr());

		if(CollectionUtils.isNotEmpty(row)){
			
			for(String[] col : row){

				switch (catCode.length()) {	
					case 0:
						subCode = col[1];
						subCol = col[5];
						break;
					case 1:
						code = col[1];
						subCode = col[1]+col[2];
						subCol = col[6];
						break;
					case 2:
						code = col[1]+col[2];
						subCode = col[1]+col[2]+col[3];
						subCol = col[7];
						break;	
					case 3:
						code = col[1]+col[2]+col[3];
						subCode = col[1]+col[2]+col[3]+col[4];
						subCol = col[8];
						break;	
					case 4:
						code = col[1]+col[2]+col[3]+col[4];
						subCode = code;
						subCol = col[8];
						break;	
					default:
						break;
				}
				
				if(catCode.equalsIgnoreCase(code) || catCode.length() == 0){
					String template = getTemplate(subCode);
					
					if(SUB_CAT_NAME.equalsIgnoreCase(template)){
						if(filterSubCat.add(col[6]))
							catList.add(new Category(subCode,col[6]));
					}else if(TEMPLATE_NAME.equalsIgnoreCase(template)){
						Vector<String[]> row_ = getCatCodesFmCache(CatCodes.TEMPLATE_USED.getCodeStr());
						
						if(CollectionUtils.isNotEmpty(row_)){
							for(String[] col_ : row_){
								if((subCode).equalsIgnoreCase(col_[2])){
									if(filterCat.add(subCode))
										catList.add(new Category(subCode,col_[1]));
								}
							}
						}
						if(filterCat.add(subCode))
							catList.add(new Category(subCode,subCol));
						
					}else if(CLASS_NAME.equalsIgnoreCase(template)){
						if(filterClass.add(col[7]))
							catList.add(new Category(subCode,col[7]));
					}else if(CATEGORY_NAME.equalsIgnoreCase(template)){
						if(filterCat.add(subCode))
							catList.add(new Category(subCode,col[5]));
					}else{
						if(filterCat.add(subCode))
							catList.add(new Category(subCode,subCol));
					}
				}
			}	
		}

		filterCat = null;
		filterSubCat = null;
		filterClass = null;
		SortUtil.sort(catList,"catName");
		return catList;
	}
	
	/** Retrieve category listing by IMS */
	public static List<Category> getCategoriesByIMSCatName(String catName) throws DataException{
		if(StringUtils.isNotEmpty(catName)){
			catName = StringUtil.decodeHtml(catName);
			String catCode = getCatCodeByIMSCatName(catName);
			if(StringUtils.isNotEmpty(catCode))
				return getCategoriesByIMSCatCode(catCode);
		}
		return Collections.EMPTY_LIST;
	}
	
	/** Retrieve category code by category name */
	public static String getCatCodeByIMSCatName(String catName) throws DataException{
		
		String catCode = "";
		
		if(StringUtils.isNotEmpty(catName)){
			// Search @ First Level
			catCode = getCatCodeByIMSCatName(catName, CatCodes.LEVEL1);	
			if(StringUtils.isNotEmpty(catCode))
				return catCode;
			// Search @ Second Level
			catCode = getCatCodeByIMSCatName(catName, CatCodes.LEVEL2);
			if(StringUtils.isNotEmpty(catCode))
				return catCode;
			// Search @ Third Level
			catCode = getCatCodeByIMSCatName(catName, CatCodes.LEVEL3);
			if(StringUtils.isNotEmpty(catCode))
				return catCode;
			// Search @ Forth Level
			catCode = getCatCodeByIMSCatName(catName, CatCodes.LEVEL4);
			if(StringUtils.isNotEmpty(catCode))
				return catCode;
		}
		
		return catCode;
	}
	
	/** Retrieve category name by category code */
	public static String getCatNameByIMSCatCode(String catCode) throws DataException{
		
		String code = "";
		String subCol = "";
		String catName = "";
		Set<String> filter = new TreeSet<String>();
		
		Vector<String[]> row = getCatCodesFmCache(CatCodes.CATEGORY_CODES.getCodeStr());

		if(CollectionUtils.isNotEmpty(row)){
			
			for(String[] col : row){

				switch (catCode.length()) {	
					case 0:
						return "";
					case 1:
						code = col[1];
						subCol = col[5];
						break;
					case 2:
						code = col[1]+col[2];
						subCol = col[6];
						break;	
					case 3:
						code = col[1]+col[2]+col[3];
						subCol = col[7];
						break;	
					case 4:
						code = col[1]+col[2]+col[3]+col[4];
						subCol = col[8];
						break;	
					default:
						return "";
				}
				
				if(catCode.equalsIgnoreCase(code)){
					String template = getTemplate(code);
					
					if(SUB_CAT_NAME.equalsIgnoreCase(template)){
						if(filter.add(col[6])){
							if(StringUtils.isNotEmpty(catName))
								catName += ";"+ col[6];
							else
								catName = col[6];
						}
					}else if(TEMPLATE_NAME.equalsIgnoreCase(template)){
						Vector<String[]> row_ = getCatCodesFmCache(CatCodes.TEMPLATE_USED.getCodeStr());
						
						if(CollectionUtils.isNotEmpty(row_)){
							for(String[] col_ : row_){
								if((code).equalsIgnoreCase(col_[2])){
									if(filter.add(col_[1])){
										if(StringUtils.isNotEmpty(catName))
											catName += ";"+ col_[1];
										else
											catName = col_[1];
									}
								}
							}
						}
						if(filter.add(subCol)){
							if(StringUtils.isNotEmpty(catName))
								catName += ";"+ subCol;
							else
								catName = subCol;
						}
					}else if(CLASS_NAME.equalsIgnoreCase(template)){
						if(filter.add(col[7])){
							if(StringUtils.isNotEmpty(catName))
								catName += ";"+ col[7];
							else
								catName = col[7];
						}
					}else if(CATEGORY_NAME.equalsIgnoreCase(template)){
						if(filter.add(col[5])){
							if(StringUtils.isNotEmpty(catName))
								catName += ";"+ col[5];
							else
								catName = col[5];
						}
					}else{
						if(filter.add(subCol)){
							if(StringUtils.isNotEmpty(catName))
								catName += ";"+ subCol;
							else
								catName = subCol;
						}
					}
				}
			}	
		}
		return catName;
	}

	/** Retrieve category code by category name and catcode entity */
	public static String getCatCodeByIMSCatName(String catName, CatCodes catCodes) throws DataException{

		String subCode = "";
		String subCol = "";
		
		Vector<String[]> row = getCatCodesFmCache(CatCodes.CATEGORY_CODES.getCodeStr());

		if(StringUtils.isNotEmpty(catName) && CollectionUtils.isNotEmpty(row)){
			
			for(String[] col : row){

				switch (catCodes) {	
					case LEVEL1:
						subCode = col[1];
						subCol = col[5];
						break;
					case LEVEL2:
						subCode = col[1]+col[2];
						subCol = col[6];
						break;
					case LEVEL3:
						subCode = col[1]+col[2]+col[3];
						subCol = col[7];
						break;	
					case LEVEL4:
						subCode = col[1]+col[2]+col[3]+col[4];
						subCol = col[8];
						break;		
					default:
						break;
				}
				
				String template = getTemplate(subCode);
				
				if(SUB_CAT_NAME.equalsIgnoreCase(template)){
					if(catName.equalsIgnoreCase(col[6]))
						return col[1]+col[2];
				}else if(TEMPLATE_NAME.equalsIgnoreCase(template)){
					Vector<String[]> row_ = getCatCodesFmCache(CatCodes.TEMPLATE_USED.getCodeStr());
					
					if(CollectionUtils.isNotEmpty(row_)){
						for(String[] col_ : row_){
							if((subCode).equalsIgnoreCase(col_[2])){
								if(catName.equalsIgnoreCase(col_[1]))
									return subCode;
							}
						}
					}
					
					if(catName.equalsIgnoreCase(subCol))
						return subCode;	
				}else if(CLASS_NAME.equalsIgnoreCase(template)){	
					if(catName.equalsIgnoreCase(col[7]))
						return col[1]+col[2]+col[3];
				}else if(CATEGORY_NAME.equalsIgnoreCase(template)){	
					if(catName.equalsIgnoreCase(col[5]))
						return col[1];	
				}else{
					if(catName.equalsIgnoreCase(subCol))
						return subCode;	
				}
			
			}	
		}

		return "";
	}

	/** Retrieve category listing by CNET */
	public static List<CategoryCNET> getCategoriesByCNET(String searchName, String store) throws DataException{
		
		List<CategoryCNET> catList = new ArrayList<CategoryCNET>();
		
		if(StringUtils.isNotEmpty(searchName)){
			searchName = StringUtil.decodeHtml(searchName);
			Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_SEARCH_NAV.getCodeStr());
			
			List<CategoryCNET> searchIds = getCNETSearchId(row, searchName, store);
			
			for(CategoryCNET searchId : searchIds){
				boolean hasKey = false;
					for(String[] col : row){
							if(searchId.getId().equalsIgnoreCase(getWholeNumber(col[3]))){
								catList.add(new CategoryCNET(getWholeNumber(col[0]), col[1], col[2], getWholeNumber(col[3]), col[4]));
								hasKey = true;
							}	
					}
					if(!hasKey)
						catList.add(searchId);			
			}	
		}
		
		return catList;
	}
	
	/** Retrieve category listing by alternate CNET */
	public static List<CategoryCNET> getCategoriesByCNETAlternate(String catName) throws DataException{
		
		List<CategoryCNET> catList = new ArrayList<CategoryCNET>();
		
		if(StringUtils.isNotEmpty(catName)){
			catName = StringUtil.decodeHtml(catName);
			Vector<String[]> row = getCatCodesFmCache(CatCodes.ALTERNATE_CNET.getCodeStr());
			
			List<CategoryCNET> catIds = getCNETAlternateCatId(row, catName);
			
			
			for(CategoryCNET catId : catIds){
				boolean hasKey = false;
					for(String[] col : row){
							if(catId.getId().equalsIgnoreCase(getWholeNumber(col[4]))){
								catList.add(new CategoryCNET(getWholeNumber(col[1]), col[2], col[2], getWholeNumber(col[4]), null));
								hasKey = true;
							}	
					}
					if(!hasKey)
						catList.add(catId);			
			}	
		}
		
		return catList;
	}
	
	/** Retrieve categories alternative cnet by category name given worksheet values */
	public static List<CategoryCNET> getCNETAlternateCatId(Vector<String[]> row, String catName) throws DataException{
		
		List<CategoryCNET> searchList = new ArrayList<CategoryCNET>();
		
		if(StringUtils.isNotEmpty(catName)){
			if(CollectionUtils.isNotEmpty(row)){	
				for(String[] col : row){
					if(catName.equalsIgnoreCase(col[2])){
						searchList.add(new CategoryCNET(getWholeNumber(col[1]), col[2], col[2], getWholeNumber(col[4]),null));
					}
				}
			}
		}		
		return searchList;
	}
	
	/** Retrieve categories cnet by category  name and store given worksheet values */
	public static List<CategoryCNET> getCNETSearchId(Vector<String[]> row, String searchName, String store) throws DataException{
		
		List<CategoryCNET> searchList = new ArrayList<CategoryCNET>();
		
		if(StringUtils.isNotEmpty(searchName) && StringUtils.isNotEmpty(store)){
			if(CollectionUtils.isNotEmpty(row)){	
				for(String[] col : row){
					if(searchName.equalsIgnoreCase(col[1]) && store.equalsIgnoreCase(col[4])){
						searchList.add(new CategoryCNET(getWholeNumber(col[0]), col[1], col[2], getWholeNumber(col[3]), col[4]));
					}
				}
			}
		}		
		return searchList;
	}
	
	/** Retrieve categories cnet by category name and store */
	public static List<CategoryCNET> getCNETSearchId(String searchName, String store) throws DataException{
		
		List<CategoryCNET> searchList = new ArrayList<CategoryCNET>();
		
		if(StringUtils.isNotEmpty(searchName) && StringUtils.isNotEmpty(store)){
			Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_SEARCH_NAV.getCodeStr());
			
			if(CollectionUtils.isNotEmpty(row)){
				for(String[] col : row){
					if(searchName.equalsIgnoreCase(col[1]) && store.equalsIgnoreCase(col[4])){
						searchList.add(new CategoryCNET(getWholeNumber(col[0]), col[1], col[2], getWholeNumber(col[3]), col[4]));
					}
				}
			}
		}
		
		return searchList;
	}

	/** Retrieve category override template IMS by category code */
	public static String getTemplate(String catCode) throws DataException{
		Vector<String[]> row = getCatCodesFmCache(CatCodes.CATEGORY_OVERRIDE_RULES.getCodeStr());
		
		if(CollectionUtils.isNotEmpty(row) && StringUtils.isNotEmpty(catCode)){
			for(String[] col : row){
				if(catCode.equalsIgnoreCase(col[0]))
					return col[1];
			}
		}
		return "";
	}
	
	/** Retrieve attribute template number by template name */
	public static String getAttributeTemplateNo(String name) throws DataException{
		Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_TEMPLATE_MASTER.getCodeStr());
		if(CollectionUtils.isNotEmpty(row) && StringUtils.isNotEmpty(name)){
			for(String[] col : row){
				if(name.equalsIgnoreCase(col[1]) && STATUS_ACTIVE.equals(getWholeNumber(col[2])))
					return getWholeNumber(col[0]);
			}
		}
		return "";
	}
	
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
	
	/** Retrieve dynamic facet listing given a template name */
	public static List<SolrAttribute> getDynamicFacetListByTemplateName(String name) throws DataException{
		
		List<SolrAttribute> attrList = new ArrayList<SolrAttribute>();

		if(StringUtils.isNotEmpty(name)){
			name = StringUtil.decodeHtml(name);
			String template = getAttributeTemplateNo(name);
			List<String[]> attrTemplate = getAttributeTemplateAttribute(template);
	
			Map<String, SolrAttribute> attrMap = getSolrAttributes();
	
			if(CollectionUtils.isNotEmpty(attrTemplate) && attrMap.size() > 0){	
				for(String[] col : attrTemplate){
					if(attrMap.containsKey(col[2]) && DYNAMIC_FACET_PREFIX.equalsIgnoreCase(attrMap.get(col[2]).getName().substring(0, 3)) && !STATUS_ACTIVE.equalsIgnoreCase(attrMap.get(col[2]).getIsRange()))
						attrList.add(attrMap.get(col[2]));
				}
			}
		}
		return attrList;
	}

	/** Retrieve list of attributes given a Template Name */
	public static List<SolrAttribute> getAttributeByTemplateName(String name) throws DataException{
		
		List<SolrAttribute> attrList = new ArrayList<SolrAttribute>();

		if(StringUtils.isNotEmpty(name)){
			name = StringUtil.decodeHtml(name);
			String template = getAttributeTemplateNo(name);
			List<String[]> attrTemplate = getAttributeTemplateAttribute(template);
	
			Map<String, SolrAttribute> attrMap = getSolrAttributes();
	
			if(CollectionUtils.isNotEmpty(attrTemplate) && attrMap.size() > 0){	
				for(String[] col : attrTemplate){
					if(attrMap.containsKey(col[2]))
						attrList.add(attrMap.get(col[2]));
				}
			}
		}
		return attrList;
	}

	/** Store solr attributes to cache */
	public static void loadSolrAttributesToCache() throws DataException{
		Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_ATTRIBUTE_MASTER.getCodeStr());
		Map<String,SolrAttribute> map = new HashMap<String, SolrAttribute>();
		for(String[] col : row){
			map.put(col[0], new SolrAttribute(col[0],col[1],col[2],getWholeNumber(col[3]),getWholeNumber(col[4]),getWholeNumber(col[6]),getWholeNumber(col[9]),getSolrAttributeRange(col[0])));
		}

		putCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, CatCodes.SOLR_ATTRIBUTES.getCodeStr()), new CacheModel<SolrAttribute>(map));
	}
	
	/** Retrieve solr attributes from cache */
	public static Map<String,SolrAttribute> getSolrAttributes() throws DataException{
		CacheModel<SolrAttribute> cache = getCache(CacheConstants.getCacheKey(CacheConstants.CATEGORY_CODES, CatCodes.SOLR_ATTRIBUTES.getCodeStr()));
		return cache.getMap();
	}
	
	/** Retrieve list of attribute values given an attribute */
	public static List<String> getSolrAttributeValuesByTemplateName(String name) throws DataException{

		Map<String, List<SolrAttributeRange>> map = getSolrAttributeRangeListByTemplateName(name);
		List<String> list = new ArrayList<String>();
		
		if(map.size() > 0 && StringUtils.isNotEmpty(name)){	
				
			for(String key : map.keySet()){
				list.addAll(getSolrAttributeValuesByAttribute(key));
			}		
		}
		return list;
	}
	
	/** Retrieve list of attribute values given an attribute */
	public static List<String> getSolrAttributeValuesByAttribute(String attrId) throws DataException{
		Map<String, SolrAttribute> attrMap = getSolrAttributes();
		List<String> list = new ArrayList<String>();
		
		if(attrMap.size() > 0 && StringUtils.isNotEmpty(attrId)){	
				if(attrMap.containsKey(attrId)){
					for(SolrAttributeRange range : attrMap.get(attrId).getList()){
						list.addAll(range.getRangevalues());
					}
				}
		}
		
		return list;
	}
	
	/** Retrieve list of attribute range values given an template name */
	public static Map<String, List<SolrAttributeRange>> getSolrAttributeRangeListByTemplateName(String name) throws DataException{
		
		Map<String, List<SolrAttributeRange>> range = new HashMap<String, List<SolrAttributeRange>>();
		List<SolrAttribute> attrs = getAttributeByTemplateName(name);
		
		if(StringUtils.isNotEmpty(name) && CollectionUtils.isNotEmpty(attrs)){
			name = StringUtil.decodeHtml(name);
			for(SolrAttribute attr : attrs){
				range.put(attr.getId(), attr.getList());
			}
		}
		
		return range;
	}
	
	/** Retrieve list of attribute range values given an attribute */
	public static List<SolrAttributeRange> getSolrAttributeRangeListByAttribute(String attrId) throws DataException{
		Map<String, SolrAttribute> attrMap = getSolrAttributes();
		
		if(attrMap.size() > 0 && StringUtils.isNotEmpty(attrId)){	
				if(attrMap.containsKey(attrId))
					return attrMap.get(attrId).getList();
		}
		
		return Collections.EMPTY_LIST;
	}
	
	/** Retrieve solr attribute range values by attribute id */
	public static List<SolrAttributeRange> getSolrAttributeRange(String attrId) throws DataException{
		Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_ATTRIBUTE_RANGE.getCodeStr());
		List<SolrAttributeRange> attrRanges = null;
		Set<String> keys = new TreeSet<String>();
		
		// Get all the key
		for(String[] col : row){
			keys.add(col[0]);
		}
		
		// For each key get attr range
		if(StringUtils.isNotEmpty(attrId)){
			attrRanges = new ArrayList<SolrAttributeRange>();
			for(String[] col : row){
				if(attrId.equalsIgnoreCase(col[0]))
					attrRanges.add(new SolrAttributeRange(getWholeNumber(col[1]), getWholeNumber(col[2]), col[3], col[4], getSolrAttributeValues(getWholeNumber(col[1]))));	
			}
		}

		return attrRanges;
	}
	
	/** Retrieve solr attribute values by range id */
	public static List<String> getSolrAttributeValues(String rangeId) throws DataException{
		Vector<String[]> row = getCatCodesFmCache(CatCodes.SOLR_ATTRIBUTE_RANGE_XREF.getCodeStr());
		List<String> list = new ArrayList<String>();
		
		if(StringUtils.isNotEmpty(rangeId)){
			for(String[] col : row){
				if(rangeId.equalsIgnoreCase(getWholeNumber(col[3])))
					list.add(col[2]);	
			}
		}
		return list;
	}

	private static <E> void putCache(String key, CacheModel<E> v){
		cache.put(key, v);
	}
	
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
	
	public static Vector<String[]> getXlsxDataCategoryOverride(XSSFWorkbook workbook, int sheetNum) throws IOException, DataException{
		
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
		
		String tmpLevel = ""; 
		String tmpLevel2 = ""; 
		String parentID = "";
		if(StringUtils.isBlank(level1)){
			for(String[] col:categoryRow){
				if(col[3].equalsIgnoreCase("0"))
					if(!list.contains(col[2]))
						list.add(col[2]);
			}
		}else if(StringUtils.isBlank(level2)){
			for(String[] col:categoryRow){
				tmpLevel = col[0].substring(col[0].length()-9 > 0 ? col[0].length()-9 : 0,col[0].length()-6);
				if(col[1].equalsIgnoreCase(level1) && tmpLevel.contains("1") ){
					for(String[] coll:categoryRow){
						if(col[0].equalsIgnoreCase(coll[3])){
							if(!list.contains(coll[2]))
								list.add(coll[2]);
						}
					}
				}
			}
		}else{
			for(String[] col:categoryRow){
				tmpLevel = col[0].substring(col[0].length()-9 > 0 ? col[0].length()-9 : 0,col[0].length()-6);
				
				if(col[1].equalsIgnoreCase(level1) && tmpLevel.contains("1")){
					parentID = col[0];
					for(String[] coll:categoryRow){
						tmpLevel2 = coll[0].substring(coll[0].length()-6,coll[0].length()-3);
						if(coll[3].equalsIgnoreCase(parentID) && tmpLevel2.contains("1")){
							for(String[] colll:categoryRow){
								if(coll[0].equalsIgnoreCase(colll[3])){
									if(!list.contains(colll[2]))
										list.add(colll[2]);
								}
							}
						}
					}
				}
			}
		}
		
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
				
		return list;
	}

	
	/** Initialized Category code utility when startup */
	public static void init() throws Exception {
		try {
			CatCodeUtil.loadXlsxWorkbook(PropsUtils.getValue(SOLR_OBJECTS_DEFINITION_XLSX),CatCodes.WORKBOOK_OBJECTS.getCodeStr());
			CatCodeUtil.loadXlsxWorkbook(PropsUtils.getValue(ALTERNATIVE_CNET_CATEGORIZATION_XLSX),CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr());		
			loadToCache();
		} catch (DataException e) {
			logger.error(ERROR_MSG+CatCodeUtil.CLASS_NAME+" ERROR - " +e);
		} catch (IOException e) {
			logger.error(ERROR_MSG+CatCodeUtil.CLASS_NAME+" ERROR - " +e);
		}
	}
	
	private static void init2() throws Exception {
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
			
			Thread td10 = new Thread(){
				@Override
				public void run() {
					try {
						CatCodeUtil.loadCatCodesToCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr(),CatCodes.SOLR_ATTRIBUTE_RANGE_XREF.getCode(),CatCodes.SOLR_ATTRIBUTE_RANGE_XREF.getCodeStr());
					} catch (DataException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_ATTRIBUTE_RANGE_XREF.getValue(),e);
					} catch (IOException e) {
						logger.error(ERROR_MSG+CatCodes.SOLR_ATTRIBUTE_RANGE_XREF.getValue(),e);
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
			td10.start();
			while(true){
				if(!td2.isAlive() && !td3.isAlive()){
					td1.start();
					break;
				}
			}
			while(true){
				if(!td1.isAlive() && !td2.isAlive() && !td3.isAlive() && !td4.isAlive() && !td5.isAlive() && !td6.isAlive() && !td7.isAlive() && !td8.isAlive() && !td9.isAlive() && !td10.isAlive()){
					CatCodeUtil.removeFmCache(CatCodes.WORKBOOK_OBJECTS.getCodeStr());
					CatCodeUtil.removeFmCache(CatCodes.WORKBOOK_OBJECTS_CNET_ALTERNATE.getCodeStr());
					CatCodeUtil.loadSolrAttributesToCache();
					break;
				}
			}

		} catch (DataException e) {
			logger.error(ERROR_MSG+CatCodeUtil.CLASS_NAME+" ERROR - " +e);
		}
	}
	
	public static void main(String args[])	throws Exception
    {
		init2();
		String strCategory = "";
		String strSubCategory = "";
		String strClass = "";
		boolean repeat = true;
		List<String> list = new ArrayList<String>();
		List<String> listCNET = new ArrayList<String>();
		
		while(repeat){
			list = new ArrayList<String>();
			Scanner in = new Scanner(System.in);
			System.out.println("Please enter category : ");
			strCategory = in.nextLine();  
			System.out.println("Please enter sub category : ");
			strSubCategory = in.nextLine(); 
			System.out.println("Please enter class : ");
			strClass = in.nextLine();
			
			list = getIMSCategoryNextLevel(strCategory,strSubCategory,strClass);
			listCNET = getCNETNextLevel(strCategory,strSubCategory);
			for(String field : list){
				System.out.println(field);
			}
			for(String field : listCNET){
				System.out.println(field);
			}
			
			System.out.println("Again?(y/n) : ");
			if(in.nextLine().equalsIgnoreCase("y")){
				repeat = true;
			}else{
				repeat = false;
			}
		}
		
		Vector<String[]> categoryRow = getCatCodesFmCache(CatCodes.CATEGORY_CODES.getCodeStr());
		
		for(String[] col : categoryRow){
			System.out.println(col[0]+" : "+col[5]+" : "+col[6]+" : "+col[7]+" : "+col[8]);
		}
    }
	
}
