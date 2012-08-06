package com.search.manager.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.search.ws.ConfigManager;

@Service(value = "linguisticsService")
@RemoteProxy(
		name = "LinguisticsServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "linguisticsService")
	)
public class LinguisticsService {
	private static final Logger logger = Logger.getLogger(LinguisticsService.class);
	
	
	public HttpResponse getDocument(String fileName,String server) throws ParserConfigurationException {
		
		String url="";
		HttpClient client = new DefaultHttpClient();
		HttpPost post = null;
		HttpResponse response = null;
		
		url = ConfigManager.getInstance().getServerParameter(server,"url").replace("(store)", UtilityService.getStoreName())+"admin/file/?file="+fileName;
		post = new HttpPost(url);
		try {
			response = client.execute(post);
		} catch (Exception e) {
			logger.error("ERROR: no document found!\n"+e.getMessage());
		} 
		
		return response;
	}
	
	@RemoteMethod
	public TreeMap<Character,List<String>> getProtStopWord(String fileName,String server) throws ParserConfigurationException, IllegalStateException, IOException{
		List<String> list = new ArrayList<String>();
		TreeMap<Character,List<String>> map = new TreeMap<Character,List<String>>();
		HttpResponse response = getDocument(fileName,server);
		BufferedReader reader= new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		Character tmpKey = null;
		while ((line = reader.readLine()) != null) {
			String tmp = line.trim();
			
			if(!StringUtils.isBlank(tmp) && tmp.charAt(0)!='#'){
				tmpKey = tmp.charAt(0);
				if(map.containsKey(tmpKey)){
					list = map.get(tmpKey);
					map.remove(tmpKey);
				}else{
					list = new ArrayList<String>();
				}
				list.add(tmp);
				map.put(tmpKey,list);
			}
		}
		
		
		return map;
		
	}
	@RemoteMethod
	public TreeMap<String,List<String>> getSynonyms(String fileName,String server) throws ParserConfigurationException, IllegalStateException, IOException{
		List<String> list = new ArrayList<String>();
		TreeMap<String,List<String>> map = new TreeMap<String,List<String>>();
		HttpResponse response = getDocument(fileName,server);
		BufferedReader reader= new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		String tmpKey = "";
		String tmpString = "";
		while ((line = reader.readLine()) != null) {
			String tmp = line.trim();
			
			if(!StringUtils.isBlank(tmp) && tmp.charAt(0)!='#' && tmp.indexOf("=>")!=-1){
				list = new ArrayList<String>();
				tmpKey = tmp.substring(0,tmp.indexOf("=>"));
				tmpString = tmp.substring(tmp.indexOf("=>")+2);
				list.addAll(Arrays.asList(tmpString.split(",")));
				map.put(tmpKey,list);
			}
		}
		
		
		return map;
		
	}
	
	@RemoteMethod
	public FileTransfer downloadFile(String file,String customFileName)  {
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = null;
		HttpResponse urlResponse = null;
	    String url = "http://afs-pl-schpd07.afservice.org:8080/solr14/"+UtilityService.getStoreName()+"/admin/file/?file="+file+".txt";
		FileTransfer fileTransfer = null;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		BufferedInputStream bis = null;
		post = new HttpPost(url);
		
		try {
			try {
				urlResponse = client.execute(post);
				bis = new BufferedInputStream(urlResponse.getEntity().getContent());
				byte[] buf = new byte[1024];
				int n = 0;
				while ((n=bis.read(buf)) != -1) {
					buffer.write(buf, 0, n);
				}			
				fileTransfer = new FileTransfer(StringUtils.isBlank(customFileName) ? file : customFileName + ".txt", "text/plain", buffer.toByteArray());
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
			} finally {
				bis.close();
				buffer.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return fileTransfer;
	}
	
}
