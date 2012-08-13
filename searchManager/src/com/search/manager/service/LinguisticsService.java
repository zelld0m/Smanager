package com.search.manager.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Service;

import com.search.ws.ConfigManager;

@Service(value = "linguisticsService")
@RemoteProxy(
		name = "LinguisticsServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "linguisticsService")
	)
public class LinguisticsService {
	private static final Logger logger = Logger.getLogger(LinguisticsService.class);
	
	
	public HttpResponse getDocument(String fileName) throws ParserConfigurationException {
		
		String url="";
		HttpClient client = new DefaultHttpClient();
		HttpPost post = null;
		HttpResponse response = null;
		url = ConfigManager.getInstance().getServerParameter(UtilityService.getServerName(),"url").replace("(store)", UtilityService.getStoreName())+"admin/file/?file="+fileName;
		post = new HttpPost(url);
		try {
			response = client.execute(post);
		} catch (Exception e) {
			logger.error("ERROR: no document found!\n"+e.getMessage());
		} 
		
		return response;
	}
	
	@RemoteMethod
	public TreeMap<Character,List<String>> getProtStopWord(String fileName) throws ParserConfigurationException, IllegalStateException, IOException{
		List<String> list = new ArrayList<String>();
		TreeMap<Character,List<String>> map = new TreeMap<Character,List<String>>();
		HttpResponse response = getDocument(fileName);
		BufferedReader reader= new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"ISO-8859-1"));
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
	public List<String> getSynonyms(String fileName) throws ParserConfigurationException, IllegalStateException, IOException{
		List<BasicNameValuePair> vpList = new ArrayList<BasicNameValuePair>(); 
		List<String> list = new ArrayList<String>();
		HttpResponse response = getDocument(fileName);
		BufferedReader reader= new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"ISO-8859-1"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = StringUtils.trimToEmpty(line.trim());
			if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "#")) {
				int i = line.indexOf("=>");
				String key = (i >= 0) ? StringUtils.trim(line.substring(0, i)) : line;
				String value = (i >= 0) ? StringUtils.trim(line.substring(i + 2)) : null;
				key = StringUtils.join(key.split("\\s*,\\s*"), ", ");
				if (value != null) {
					value = StringUtils.trimToEmpty(StringUtils.join(value.split("\\s*,\\s*"), ", "));					
				}
				vpList.add(new BasicNameValuePair(key, value));
			}
		}
		// sort
		Collections.sort(vpList, new Comparator<BasicNameValuePair>() {
			@Override
			public int compare(BasicNameValuePair arg0, BasicNameValuePair arg1) {
				return StringUtils.lowerCase(arg0.getName()).compareTo(StringUtils.lowerCase(arg1.getName()));
			}
		});
		// add to list
		for (BasicNameValuePair b: vpList) {
			if (b.getValue() == null) {
				list.add(b.getName());
			}
			else {
				list.add(b.getName() + " => " + b.getValue());				
			}
		}
		return list;
	}
	
	@RemoteMethod
	public FileTransfer downloadFile(int type,String fileName,String customFileName)  {
		FileTransfer fileTransfer = null;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] newline = "\r\n".getBytes();
		
		try {
			try {
				switch(type){
				case 1:
					TreeMap<Character,List<String>> map2 = getProtStopWord(fileName+".txt");
					for (Entry<Character, List<String>> entry : map2.entrySet())
					{
						for(String tmp : entry.getValue()){
							buffer.write(tmp.getBytes());
							buffer.write(newline);
						}
					}
					break;
				case 2:
					List<String> list = getSynonyms(fileName+".txt");
					for (String value : list) {
						buffer.write(value.getBytes());
						buffer.write(newline);
					}
					break;
				}
				fileTransfer = new FileTransfer(StringUtils.isBlank(customFileName) ? fileName : customFileName + ".txt", "text/plain", buffer.toByteArray());
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				buffer.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return fileTransfer;
	}
	
}
