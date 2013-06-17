package com.search.manager.utility;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

@Deprecated
public class SolrRequestDispatcher {

	private static Logger logger = Logger.getLogger(SolrRequestDispatcher.class);
	
	@Deprecated
	// those creating the connection is responsible for releasing it. This class will be deleted.
	public static HttpResponse dispatchRequest(String url, List<NameValuePair> parameters)
		throws UnsupportedEncodingException, ClientProtocolException, IOException
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		logger.debug("URL: " + post.getURI());
		post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
		logger.debug("Parameter: " + parameters);
		return client.execute(post);
	}
	
}
