package com.search.converter.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.xml.file.RuleXmlUtil;

public class RuleXMLToFile implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(RuleXMLToFile.class);
	private String store; 
	private String path; 
	private String ruleId; 
	private RuleXml ruleXml;
	private RuleEntity ruleEntity; 

	public RuleXMLToFile(String store, String path, String ruleId, RuleXml ruleXml, RuleEntity ruleEntity){
		this.store = store;
		this.path = path;
		this.ruleId = ruleId;
		this.ruleXml = ruleXml;
		this.ruleEntity = ruleEntity;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void run() {
		
		String dir = RuleXmlUtil.getRuleFileDirectory(path, store, ruleEntity);
		String id = RuleXmlUtil.getRuleId(ruleEntity, ruleId);
		String filename = RuleXmlUtil.getFilenameByDir(dir, id);
		
		FileWriter writer = null;

		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			try {
				FileUtils.forceMkdir(dirFile);
			} catch (IOException e) {
				LOGGER.error("Unable to create directory", e);
			}
		}

		try {
			if (ruleXml != null) {
				JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				writer = new FileWriter(filename);
				
				RuleVersionListXml ruleVersionListXml = new RuleVersionListXml(2);
				ruleXml.setVersion(1);
				ruleVersionListXml.getVersions().add(ruleXml);
				
				m.marshal(ruleVersionListXml, writer);
				LOGGER.info(String.format("%s file created: %s", ruleEntity, filename));
			}
			else{
				LOGGER.info("RuleXml is null. " + filename + " cannot be imported");
			}
		} catch (JAXBException e) {
			LOGGER.error("Unable to create marshaller", e);
		} catch (Exception e) {
			LOGGER.error("Unknown error", e);
		}catch (Throwable e) {
			LOGGER.error("Unknown error", e);
		}
		finally {
			try { if (writer != null) { writer.close(); } } catch (IOException e) { }
		}
	}
}