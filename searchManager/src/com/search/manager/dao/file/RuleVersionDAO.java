package com.search.manager.dao.file;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.Product;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateItemXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.utility.StringUtil;
import com.search.manager.xml.file.RuleXmlUtil;
import com.search.ws.SearchHelper;

public abstract class RuleVersionDAO<T extends RuleXml>{
	
	private Logger logger = Logger.getLogger(RuleVersionDAO.class);
	
	public abstract RuleVersionListXml<T> getRuleVersionList(String store, String ruleId);
	public abstract boolean createRuleVersion(String store, String ruleId, String username, String name, String notes);
	protected static RuleEntity ruleEntity = null;
	
	public boolean restoreRuleVersion(T xml){
		return RuleXmlUtil.restoreRule(xml);
	};

	public String getRuleVersionFilename(String store, String ruleId) {
		return RuleVersionUtil.getFilename(store, ruleEntity, StringUtil.escapeKeyword(ruleId));
	}
	
	@SuppressWarnings("unchecked")
	public boolean deleteRuleVersion(String store, String ruleId, final String username, final long version){

		FileWriter writer = null;
		try {
			String filename = getRuleVersionFilename(store, ruleId);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			Document prefsDom = db.parse(filename);
			prefsDom.setXmlStandalone(true);
			JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
			Binder<Node> binder = context.createBinder();
			RuleVersionListXml<T> prefsJaxb = (RuleVersionListXml<T>) binder.unmarshal(prefsDom);

			List<T> versions = (List<T>) prefsJaxb.getVersions();

			CollectionUtils.forAllDo(versions, new Closure(){
				public void execute(Object o) {
					if(((T)o).getVersion() == version){
						((T)o).setDeleted(true);
						((T)o).setLastModifiedBy(username);
						((T)o).setLastModifiedDate(new Date());
					}
				};
			});

			prefsJaxb.setVersions(versions);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			writer = new FileWriter(filename);
			m.marshal(prefsJaxb, writer);

		} catch (JAXBException e) {
			logger.error("JAXBException");
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException");
		} catch (SAXException e) {
			logger.error("SAXException");
		} catch (IOException e) {
			logger.error("IOException");
		} finally {
			try { if (writer != null) writer.close(); } catch (Exception e) {}
		}
		return false;
	}

//	private List<Product>() getProducts(T xml){
//		if(CollectionUtils.isNotEmpty(elevateItem)){
//			StoreKeyword storeKeyword = new StoreKeyword(getStore(), keyword);
//			LinkedHashMap<String, Product> map =  new LinkedHashMap<String, Product>();
//			List<ElevateResult> itemList = new ArrayList<ElevateResult>();
//			ElevateResult eResult = new ElevateResult();
//			
//			for(ElevateItemXml eItem:elevateItem){
//				eResult = new ElevateResult(storeKeyword, eItem);
//				itemList.add(eResult);
//			}
//
//			map = SearchHelper.getProducts(itemList, getStore(), keyword);
//			
//			if(MapUtils.isNotEmpty(map)){
//				return new ArrayList<Product>(map.values());
//			}
//		}
//		
//		return null;
//	}
//	
	public List<RuleXml> getRuleVersions(String store, String ruleId) {
		List<RuleXml> ruleVersionInfoList = new ArrayList<RuleXml>();
		RuleVersionListXml<T> ruleVersionListXml = (RuleVersionListXml<T>) getRuleVersionList(store, ruleId);

		if (ruleVersionListXml!=null){
			List<T> ruleXmlList =  (List<T>) ruleVersionListXml.getVersions();
			
			if(CollectionUtils.isNotEmpty(ruleXmlList)){
				for(T ruleVersion: ruleXmlList){
					if(!ruleVersion.isDeleted()){
						
						if(ruleVersion instanceof ElevateRuleXml){
							
							
							//((ElevateRuleXml)ruleVersion).setProducts(products)
						}else if(ruleVersion instanceof DemoteRuleXml){
							
						}
						else if(ruleVersion instanceof ExcludeRuleXml){
							
						}
						
						ruleVersionInfoList.add(ruleVersion);
					}
				}
				
				Collections.sort(ruleVersionInfoList, new Comparator<RuleXml>() {
					@Override
					public int compare(RuleXml r1, RuleXml r2) {
						return r2.getVersion() < r1.getVersion() ? 0 : 1;
					}
				});
			}
		}

		return ruleVersionInfoList;
	}
	
	
}
