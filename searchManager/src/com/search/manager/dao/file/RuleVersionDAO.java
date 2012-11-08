package com.search.manager.dao.file;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionXml;
import com.search.manager.xml.file.RuleRestoreUtil;

public abstract class RuleVersionDAO<T extends RuleVersionXml>{
	
	private Logger logger = Logger.getLogger(RuleVersionDAO.class);
	
	public abstract String getRuleVersionFilename(String store, String ruleId);
	public abstract RuleVersionListXml<T> getRuleVersionList(String store, String ruleId);
	public abstract boolean createRuleVersion(String store, String ruleId, String username, String name, String notes);
	
	public boolean restoreRuleVersion(T xml){
		return RuleRestoreUtil.restoreRule(xml);
	};

	@SuppressWarnings("unchecked")
	public boolean deleteRuleVersion(String store, String ruleId, final String username, final long version){

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
			m.marshal(prefsJaxb, new FileWriter(filename));

		} catch (JAXBException e) {
			logger.error("JAXBException");
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException");
		} catch (SAXException e) {
			logger.error("SAXException");
		} catch (IOException e) {
			logger.error("IOException");
		} 
		return false;
	}

	public List<RuleVersionXml> getRuleVersions(String store, String ruleId) {
		List<RuleVersionXml> ruleVersionInfoList = new ArrayList<RuleVersionXml>();
		RuleVersionListXml<T> ruleVersionListXml = (RuleVersionListXml<T>) getRuleVersionList(store, ruleId);

		if (ruleVersionListXml!=null){
			List<T> ruleXmlList =  (List<T>) ruleVersionListXml.getVersions();
			
			if(CollectionUtils.isNotEmpty(ruleXmlList)){
				for(T ruleVersion: ruleXmlList){
					if(!ruleVersion.isDeleted())
						ruleVersionInfoList.add(ruleVersion);
				}
				
				Collections.sort(ruleVersionInfoList, new Comparator<RuleVersionXml>() {
					@Override
					public int compare(RuleVersionXml r1, RuleVersionXml r2) {
						return r2.getVersion() < r1.getVersion() ? 0 : 1;
					}
				});
			}
		}

		return ruleVersionInfoList;
	}
}