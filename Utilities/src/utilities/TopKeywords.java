package utilities;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;



public class TopKeywords implements Runnable {

	HttpClient client = new DefaultHttpClient();
	HttpPost post = null;
	HttpResponse solrResponse = null;
    DocumentBuilder builder = null;
    Document doc = null;
	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    List<Object[]> topKeywordsList = new ArrayList<Object[]>();
    List<String> forReprocess = new ArrayList<String>();
    BlockingQueue<String> toProcess = new ArrayBlockingQueue<String>(500);
    boolean running = false;
    List<Object[]> valuesTop = new ArrayList<Object[]>();
       
    public TopKeywords (String solrURL) {
		post = new HttpPost(solrURL);
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
    public boolean addKeywordToProcess(String keyword) {
    	try {
    		return toProcess.add(keyword);
    	} catch (IllegalStateException ie) {
    		// no capacity at the present
    		return false;
    	}
    }
	
	@Override
	public void run() {
		int retry = 0;
		running = true;
		while (!toProcess.isEmpty()) {
			String keyword = toProcess.peek();
		    parameters.clear();
		    try {
				parameters.add(new BasicNameValuePair("q", URLDecoder.decode(keyword,"UTF-8")));
			    post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
			    solrResponse = client.execute(post);
			    doc = builder.parse(solrResponse.getEntity().getContent());
			    int count = 0;
			    	count =  Integer.parseInt(doc.getElementsByTagName("result").item(0)
							.getAttributes().getNamedItem("numFound").getNodeValue());
			   
			    if(count != 0){
			    String edp = "";
				    edp =  doc.getElementsByTagName("int").item(5).getFirstChild().getNodeValue();
			    	topKeywordsList.add(new Object[] {keyword,edp,count});
			    }
				toProcess.poll();
//System.out.println(keyword + " -> " + count);
			} catch (Exception e) {
//System.err.println(keyword + " -> " + e.getMessage());
				try {
//System.out.println(Thread.currentThread().getId() + "*************retry " + retry++ + " fail" );	        			
        			Thread.sleep(5000);
        		} catch (Exception ex) {
        		}
//        		System.out.println(keyword);
        		if (retry > 10) {
        			retry = 0;
        			toProcess.poll();
        			forReprocess.add(keyword);
        		}
        		
				EntityUtils.consumeQuietly(post.getEntity());
				client.getConnectionManager().shutdown();
				
        		client = new DefaultHttpClient();
				retry++;
        		
				continue;
			}
		}
		running = false;
	}
	
	public List<String[]> processKeywords(HashMap<String, KeyValuePair> map,String solrURL){
		
		StringBuilder log = new StringBuilder();
		ArrayList<String> keywordCopy = new ArrayList<String>(map.keySet());

        // create resource pool. TODO: make this configurable in config file
        int poolSize = 100;
        TopKeywords[] topKeywordsPool = new TopKeywords[poolSize];
        for (int n = 0; n < poolSize; n++) {
        	topKeywordsPool[n] = new TopKeywords(solrURL);
        }
        
        // feed initial data
        int i = 0;
        while (!keywordCopy.isEmpty()) {
        	String keyword = keywordCopy.remove(0);
        	if (!topKeywordsPool[i].addKeywordToProcess(keyword)) {
        		keywordCopy.add(keyword);
        		break;
        	}
        	if (++i >= poolSize) {
        		i = 0;
        	}
        }
        
        // start the process
        for (int n = 0; n < poolSize; n++) {
        	new Thread(topKeywordsPool[n]).start();
        }
        
        // feed the rest of the data
        i = 0;
        while (!keywordCopy.isEmpty()) {
        	String keyword = keywordCopy.remove(0);
        	if (!topKeywordsPool[i].addKeywordToProcess(keyword)) {
        		keywordCopy.add(keyword);
        	}
        	if (++i >= poolSize) {
        		i = 0;
        	}
        }
        
        i = 0;
        while (true) {
        	if (topKeywordsPool[i].running) {
        		i = 0;
        		// wait for 10 secs
        		try {
        			Thread.sleep(10000);
        		} catch (Exception e) {
        		}
        		continue;
        	}
        	else {
        		// restart runnable if more words to process
        		if (!topKeywordsPool[i].toProcess.isEmpty()) {
        			new Thread(topKeywordsPool[i]).start();
        			i = 0;
        			continue;
        		}
        	}
        	i++;
        	if (i >= poolSize) {
        		break;
        	}
        }
        
        
		for (int n = 0; n < poolSize; n++) {
			for (Object arr[]: topKeywordsPool[n].topKeywordsList) {
				valuesTop.add(new Object[] {arr[0],arr[1],arr[2]});					
			}
			forReprocess.addAll(topKeywordsPool[n].forReprocess);
		}
		
		if (!forReprocess.isEmpty()) {
			log.append("Please reprocess the following keywords to check for top keywords:\n");
			for (String key: forReprocess) {
				log.append(key + "\n");
			}
		}
		 List<String[]> valuesTop = new ArrayList<String[]>();
	        List<String> forReprocess = new ArrayList<String>();
			for (int n = 0; n < poolSize; n++) {
				for (Object arr[]: topKeywordsPool[n].topKeywordsList) {
					valuesTop.add(new String[] {arr[0].toString(),arr[1].toString(),arr[2].toString(),Integer.toString(map.get(arr[0]).getValue())});					
				}
				forReprocess.addAll(topKeywordsPool[n].forReprocess);
			}
			
			if (!forReprocess.isEmpty()) {
				log.append("Please reprocess the following keywords to check for top keywords:\n");
				for (String key: forReprocess) {
					log.append(key + "\n");
				}
			}
			return valuesTop;
	}
}
