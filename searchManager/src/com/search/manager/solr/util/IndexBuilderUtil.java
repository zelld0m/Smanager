package com.search.manager.solr.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;

public class IndexBuilderUtil {

	private static final Logger logger = Logger
			.getLogger(IndexBuilderUtil.class);

	private static String DEFAULT_SOLR_URL = "http://localhost:8983/solr";

	public void doCommit(String solrUrl, String core)
			throws ClientProtocolException, IOException {
		if (solrUrl != null) {
			DEFAULT_SOLR_URL = solrUrl;
		}
		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpGet getRequest = new HttpGet(DEFAULT_SOLR_URL + "/" + core
				+ "/update?stream.body=%3Ccommit%2F%3E");
		HttpResponse response = httpClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code :"
					+ response.getStatusLine().getStatusCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		String output;

		while ((output = br.readLine()) != null) {
			logger.info(output);
		}

		httpClient.getConnectionManager().shutdown();
	}

	public static void saveIndexData(String logPath,
			List<SolrInputDocument> solrInputDocuments, String prefix) {
		BufferedWriter out = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
			Date date = new Date();
			int count = 1;
			for (SolrInputDocument solrInputDocument : solrInputDocuments) {
				FileWriter fileWriter = new FileWriter(logPath + prefix + "_"
						+ dateFormat.format(date) + "_" + count + ".xml");
				out = new BufferedWriter(fileWriter);
				ClientUtils.writeXML(solrInputDocument, out);
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveIndexData(String logPath,
			SolrInputDocument solrInputDocument, String prefix) {
		BufferedWriter out = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
			Date date = new Date();

			FileWriter fileWriter = new FileWriter(logPath + prefix + "_"
					+ dateFormat.format(date) + ".xml");
			out = new BufferedWriter(fileWriter);

			ClientUtils.writeXML(solrInputDocument, out);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
