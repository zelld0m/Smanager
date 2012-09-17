package com.search.manager.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utilities.KeyValuePair;
import utilities.TopKeywords;
import utilities.ZeroResults;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.search.manager.mail.ReportNotificationMailService;
import com.search.manager.utility.PropsUtils;
import com.search.ws.ConfigManager;


@Service(value = "reportGeneratorService")
@RemoteProxy(
		name = "ReportGeneratorServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "reportGeneratorService")
)	
	
public class ReportGeneratorService {
		
	private static final Logger logger = Logger.getLogger(ReportGeneratorService.class);
	private final String[] topKeywordHeader = {"Search Terms","Searches","Number of Search Results","First SKU"};
	private final String[] zeroResultsHeader = {"Search Terms","Searches"};
	@Autowired ReportNotificationMailService reportNotificationMailService;	
	
	@RemoteMethod
	public FileTransfer generateZeroResults(String content,String format) throws IOException  {
		
		String solrParams="select?rows=1&"+ConfigManager.getInstance().getDefaultSolrParameters(UtilityService.getStoreName())+"e&wt=xml";
	    String url = ConfigManager.getInstance().getServerParameter(UtilityService.getServerName(), "url")
		.replace("(store)", UtilityService.getStoreName()).replace("http://",PropsUtils.getValue("browsejssolrurl")) + solrParams;
	    
	    ZeroResults zero = new ZeroResults(url);
		
	    List<KeyValuePair> valuesZero = zero.processKeywords(processFile(content,format), url);
    	    	
    	if (valuesZero.size() > 0) {		
  		
    		CSVWriter writer = null;
			File outFile = new File("topKeywords.csv");
			try {
				outFile.createNewFile();
				writer = new CSVWriter(new FileWriter(outFile));
				writer.writeNext(zeroResultsHeader);
				for (KeyValuePair kvp: valuesZero) {
					writer.writeNext(new String[] {kvp.getKey(),URLDecoder.decode(Integer.toString(kvp.getValue()), "UTF-8")});
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
			
			InputStream inStream = new FileInputStream(outFile);

    		FileTransfer fileTransfer = new FileTransfer("zeroResults.csv","application/csv",inStream);
    		return fileTransfer;
    	}
    	
		return null;
	}
	
	
	@RemoteMethod
	public FileTransfer generateTopKeywords(String content,String format) throws IOException  {
		
        String solrParams="select?rows=1&"+ConfigManager.getInstance().getDefaultSolrParameters(UtilityService.getStoreName())+"e&wt=xml";
    	String url = ConfigManager.getInstance().getServerParameter(UtilityService.getServerName(), "url")
		.replace("(store)", UtilityService.getStoreName()).replace("http://",PropsUtils.getValue("browsejssolrurl")) + solrParams;
    	 
        TopKeywords top = new TopKeywords(url);
		List<String[]> valuesTop = top.processKeywords(processFile(content,format), url);
	
    	if (valuesTop.size() > 0) {	
    		CSVWriter writer = null;
			File outFile = new File("topKeywords.csv");
			try {
				outFile.createNewFile();
				writer = new CSVWriter(new FileWriter(outFile));
				writer.writeNext(topKeywordHeader);
				for (String[] obj: valuesTop) {
					writer.writeNext(obj);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
    		
    		InputStream inStream = new FileInputStream(outFile);
    		
    		FileTransfer fileTransfer = new FileTransfer("topKeywords.csv","application/csv",inStream);
    		return fileTransfer;
    	}
    	   	
		return null;
	}
	
	private HashMap<String, KeyValuePair> processFile(String content,String format){
		
		CSVReader reader = null;
		InputStream is = new ByteArrayInputStream( content.getBytes() );
		HashMap<String, KeyValuePair> map = new HashMap<String,KeyValuePair>();
	    int noOfSearches = 0;
	    String keyword = "";
			reader = new CSVReader(new InputStreamReader(is));
			try {
				String[] entries;
				while ((entries = reader.readNext()) != null){
					if(entries[0].contains("#") || StringUtils.isBlank(entries[0]))
			        	continue;
			        if(format.equalsIgnoreCase("Search Manager") && StringUtils.isNumeric(entries[0])){
			        	noOfSearches = Integer.parseInt(entries[0]);
			        	keyword = entries[1];
			        }
			        else if(format.equalsIgnoreCase("Adobe") && StringUtils.isNumeric(entries[2])){
			        	noOfSearches = Integer.parseInt(entries[2]);
			        	keyword = StringEscapeUtils.unescapeHtml(entries[1]);
			        }
			        else
			        	continue;
			        
			        if (keyword != null && keyword.contains("!dismax")) {
			        	// ignore
			        	continue;										
					}
					if (map.containsKey(keyword)) {
						map.get(keyword).setValue(map.get(keyword).getValue() + noOfSearches);
					}
					else {
						map.put(keyword, new KeyValuePair(keyword,noOfSearches));										
					}	
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			     
        return map;
	}
}
